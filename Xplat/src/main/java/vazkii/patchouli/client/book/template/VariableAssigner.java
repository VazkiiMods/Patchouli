package vazkii.patchouli.client.book.template;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

import org.apache.commons.lang3.text.WordUtils;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.IVariablesAvailableCallback;
import vazkii.patchouli.common.util.EntityUtil;

import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.common.util.RecipeUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class VariableAssigner {

	private static final Pattern INLINE_VAR_PATTERN = Pattern.compile("([^#]*)(#[^#]+)#(.*)");
	private static final Pattern FUNCTION_PATTERN = Pattern.compile("(.+)->(.+)");

	private static final Map<String, BiFunction<IVariable, HolderLookup.Provider, IVariable>> FUNCTIONS = new HashMap<>();
	static {
		FUNCTIONS.put("iname", VariableAssigner::iname);
		FUNCTIONS.put("icount", VariableAssigner::icount);
		FUNCTIONS.put("ename", wrapStringFunc(VariableAssigner::ename));
		FUNCTIONS.put("lower", wrapStringFunc(String::toLowerCase));
		FUNCTIONS.put("upper", wrapStringFunc(String::toUpperCase));
		FUNCTIONS.put("trim", wrapStringFunc(String::trim));
		FUNCTIONS.put("capital", wrapStringFunc(WordUtils::capitalize));
		FUNCTIONS.put("fcapital", wrapStringFunc(WordUtils::capitalizeFully));
		FUNCTIONS.put("i18n", wrapStringFunc(I18n::get));
		FUNCTIONS.put("exists", VariableAssigner::exists);
		FUNCTIONS.put("iexists", VariableAssigner::iexists);
		FUNCTIONS.put("inv", VariableAssigner::inv);
//		FUNCTIONS.put("stacks", VariableAssigner::stacks);
	}

	public static void assignVariableHolders(IVariablesAvailableCallback object, IVariableProvider variables, IComponentProcessor processor, TemplateInclusion encapsulation) {
		Context c = new Context(variables, processor, encapsulation);
		object.onVariablesAvailable(input -> {
			if (input == null) {
				return IVariable.empty();
			}
			if (input.unwrap().isJsonPrimitive() && input.unwrap().getAsJsonPrimitive().isString()) {
				IVariable resolved = resolveString(input.asString(), c);
				if (resolved != null) {
					return resolved;
				}
			}
			return input;
		}, RecipeUtil.getRegistryAccess().orElseThrow());
	}

	private static IVariable resolveString(@Nullable String curr, Context c) {
		if (curr == null || curr.isEmpty()) {
			return null;
		}

		String s = curr;
		Matcher m = INLINE_VAR_PATTERN.matcher(s);
		while (m.matches()) {
			String before = m.group(1);
			String var = m.group(2);
			String after = m.group(3);

			String resolved = resolveStringFunctions(var, c).asString();

			s = String.format("%s%s%s", before, resolved, after);
			m = INLINE_VAR_PATTERN.matcher(s);
		}

		return resolveStringFunctions(s, c);
	}

	private static IVariable resolveStringFunctions(String curr, Context c) {
		IVariable cached = c.getCached(curr);
		if (cached != null) {
			return cached;
		}

		Matcher m = FUNCTION_PATTERN.matcher(curr);

		if (m.matches()) {
			String funcStr = m.group(2);
			String arg = m.group(1);

			if (FUNCTIONS.containsKey(funcStr)) {
				BiFunction<IVariable, HolderLookup.Provider, IVariable> func = FUNCTIONS.get(funcStr);
				IVariable parsedArg = resolveStringFunctions(arg, c);
				return c.cache(curr, func.apply(parsedArg, RecipeUtil.getRegistryAccess().orElseThrow()));
			} else {
				throw new IllegalArgumentException("Invalid Function " + funcStr);
			}
		}

		IVariable ret = resolveStringVar(curr, c);

		return c.cache(curr, ret);
	}

	private static IVariable resolveStringVar(String original, Context c) {
		String curr = original;
		IVariable val = null;

		if (curr == null) {
			return IVariable.empty();
		}

		if (curr.startsWith("#")) {
			if (c.encapsulation != null) {
				val = c.encapsulation.attemptVariableLookup(curr, RecipeUtil.getRegistryAccess().orElseThrow());
				if (val != null) {
					return val;
				} else {
					curr = c.encapsulation.qualifyName(curr);
				}
			}

			String key = curr.startsWith("#") ? curr.substring(1) : curr;
			String originalKey = original.substring(1);

			if (c.processor != null) {
				val = c.processor.process(originalKey);
			}

			if (val == null && c.variables.has(key)) {
				val = c.variables.get(key, RecipeUtil.getRegistryAccess().orElseThrow());
			}

			return val == null ? IVariable.empty() : val;
		}
		return IVariable.wrap(curr, RecipeUtil.getRegistryAccess().orElseThrow());
	}

	private static BiFunction<IVariable, HolderLookup.Provider, IVariable> wrapStringFunc(Function<String, String> inner) {
		return (x, r) -> IVariable.wrap(inner.apply(x.asString()), r);
	}

	private static IVariable iname(IVariable arg, HolderLookup.Provider registries) {
		ItemStack stack = arg.as(ItemStack.class);
		return IVariable.wrap(stack.getHoverName().getString(), registries);
	}

	private static IVariable icount(IVariable arg, HolderLookup.Provider registries) {
		ItemStack stack = arg.as(ItemStack.class);
		return IVariable.wrap(stack.getCount(), registries);
	}

	private static IVariable exists(IVariable arg, HolderLookup.Provider registries) {
		return IVariable.wrap(!arg.unwrap().isJsonNull(), registries);
	}

	private static IVariable iexists(IVariable arg, HolderLookup.Provider registries) {
		ItemStack stack = arg.as(ItemStack.class);
		return IVariable.wrap(stack != null && !stack.isEmpty(), registries);
	}

	private static IVariable inv(IVariable arg, HolderLookup.Provider registries) {
		return IVariable.wrap(!arg.unwrap().getAsBoolean(), registries);
	}

//	private static IVariable stacks(IVariable arg, HolderLookup.Provider registries) {
//		return IVariable.from(arg.as(Ingredient.class).getItems(), registries);
//	}

	private static String ename(String arg) {
		return EntityUtil.getEntityName(arg);
	}

	private static class Context {

		final IVariableProvider variables;
		final IComponentProcessor processor;
		final TemplateInclusion encapsulation;
		final Map<String, IVariable> cachedVars = new HashMap<>();

		Context(IVariableProvider variables, IComponentProcessor processor, TemplateInclusion encapsulation) {
			this.variables = variables;
			this.processor = processor;
			this.encapsulation = encapsulation;
		}

		IVariable getCached(String s) {
			return cachedVars.get(s);
		}

		IVariable cache(String k, IVariable v) {
			cachedVars.put(k, v);
			return v;
		}

	}

}
