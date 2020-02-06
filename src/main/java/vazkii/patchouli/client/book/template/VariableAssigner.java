package vazkii.patchouli.client.book.template;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.resources.I18n;
import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.item.ItemStack;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariablesAvailableCallback;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.EntityUtil;
import vazkii.patchouli.common.util.ItemStackUtil;

import javax.annotation.Nullable;

public class VariableAssigner {
	
	private static final Pattern INLINE_VAR_PATTERN = Pattern.compile("([^#]*)(#[^#]+)#(.*)");
	private static final Pattern FUNCTION_PATTERN = Pattern.compile("(.+)->(.+)");

	private static final Map<String, Function<String, String>> FUNCTIONS = new HashMap<>();
	static {
		FUNCTIONS.put("iname", VariableAssigner::iname);
		FUNCTIONS.put("icount", VariableAssigner::icount);
		FUNCTIONS.put("ename", VariableAssigner::ename);
		FUNCTIONS.put("lower", String::toLowerCase);
		FUNCTIONS.put("upper", String::toUpperCase);
		FUNCTIONS.put("trim", String::trim);
		FUNCTIONS.put("capital", WordUtils::capitalize);
		FUNCTIONS.put("fcapital", WordUtils::capitalizeFully);
		FUNCTIONS.put("exists", VariableAssigner::exists);
		FUNCTIONS.put("iexists", VariableAssigner::iexists);
		FUNCTIONS.put("inv", VariableAssigner::inv);
		FUNCTIONS.put("i18n", I18n::format);
	}

	public static void assignVariableHolders(IVariablesAvailableCallback object, IVariableProvider<String> variables, IComponentProcessor processor, TemplateInclusion encapsulation) {
		Context c = new Context(object, variables, processor, encapsulation);
		object.onVariablesAvailable(key -> resolveString(key, c));
	}

	private static String resolveString(@Nullable String curr, Context c) {
		if(curr == null || curr.isEmpty())
			return null;
		
		String s = curr;
		Matcher m = INLINE_VAR_PATTERN.matcher(s);
		while(m.matches()) {
			String before = m.group(1);
			String var = m.group(2);
			String after = m.group(3);
		
			String resolved = resolveStringFunctions(var, c);
			
			s = String.format("%s%s%s", before, resolved, after);
			m = INLINE_VAR_PATTERN.matcher(s);
		}

		return resolveStringFunctions(s, c);
	}

	private static String resolveStringFunctions(String curr, Context c) {
		String cached = c.getCached(curr);
		if(cached != null)
			return cached;
		
		Matcher m = FUNCTION_PATTERN.matcher(curr);
		
		if(m.matches()) {
			String funcStr = m.group(2);
			String arg = m.group(1);
			
			if(FUNCTIONS.containsKey(funcStr)) {
				Function<String, String> func = FUNCTIONS.get(funcStr);
				String parsedArg = resolveStringFunctions(arg, c);
				return func.apply(parsedArg);
			} else throw new IllegalArgumentException("Invalid Function " + funcStr);
		} 
		
		String ret = resolveStringVar(curr, c);
		c.cache(curr, ret);
		
		return ret;
	}
	
	private static String resolveStringVar(String curr, Context c) {
		String original = curr;

		if(curr != null && !curr.isEmpty() && c.encapsulation != null)
			curr = c.encapsulation.transform(curr, true);

		if(curr != null) {
			String val = curr;
			if(curr.startsWith("#")) {
				val = null;
				String key = curr.substring(1);
				String originalKey = original.substring(1);

				if(c.processor != null)
					val = c.processor.process(originalKey);

				if(val == null && c.variables.has(key))
					val = c.variables.get(key);

				if(val == null)
					val = "";
			}

			c.cache(original, val);
			return val;
		}

		return curr;
	}

	private static String iname(String arg) {
		ItemStack stack = ItemStackUtil.loadStackFromString(arg);
		return stack.getDisplayName().getFormattedText();
	}
	
	private static String icount(String arg) {
		ItemStack stack = ItemStackUtil.loadStackFromString(arg);
		return Integer.toString(stack.getCount());
	}
	
	private static String ename(String arg) {
		return EntityUtil.getEntityName(arg);
	}
	
	private static String exists(String arg) {
		return arg.isEmpty() ? "false" : "true";
	}
	
	private static String iexists(String arg) {
		if(arg.isEmpty())
			return "false";
		
		ItemStack stack = ItemStackUtil.loadStackFromString(arg);
		if(stack.isEmpty())
			return "false";
		
		return "true";
	}
	
	private static String inv(String arg) {
		return arg.equalsIgnoreCase("false") ? "true" : "false"; 
	}
	
	private static class Context {

		final Object object;
		final IVariableProvider<String> variables;
		final IComponentProcessor processor;
		final TemplateInclusion encapsulation;

		final Map<String, String> cachedVars;

		Context(Object object, IVariableProvider<String> variables, IComponentProcessor processor, TemplateInclusion encapsulation) {
			this(object, variables, processor, encapsulation, new HashMap<>());
		}

		Context(Object object, IVariableProvider<String> variables, IComponentProcessor processor, TemplateInclusion encapsulation, Map<String, String> cachedVars) {
			this.object = object;
			this.variables = variables;
			this.processor = processor;
			this.encapsulation = encapsulation;
			this.cachedVars = cachedVars;
		}

		String getCached(String s) {
			return cachedVars.get(s);
		}

		void cache(String k, String v) {
			cachedVars.put(k, v);
		}

		Context rewrap(Object object) {
			return new Context(object, variables, processor, encapsulation, cachedVars);
		}

	}

	private static interface Assigner {
		void assign(Field f, Context c) throws IllegalAccessException;
	}

}
