package vazkii.patchouli.client.book.template;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.item.ItemStack;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.common.util.EntityUtil;
import vazkii.patchouli.common.util.ItemStackUtil;

public class VariableAssigner {
	
	private static final Pattern INLINE_VAR_PATTERN = Pattern.compile("([^#]*)(#[^#]+)#(.*)");
	private static final Pattern FUNCTION_PATTERN = Pattern.compile("(.+)->(.+)");

	private static final Map<Class<?>, Assigner> ASSIGNERS = new HashMap<Class<?>, Assigner>() {{
		put(String.class, VariableAssigner::assignStringField);
		put(String[].class, VariableAssigner::assignStringArrayField);
		put(List.class, VariableAssigner::assignList);
		put(Map.class, VariableAssigner::assignMap);
	}};
	
	private static final Map<String, Function<String, String>> FUNCTIONS = new HashMap<String, Function<String, String>>() {{
		put("iname", VariableAssigner::iname);
		put("icount", VariableAssigner::icount);
		put("ename", VariableAssigner::ename);
		put("lower", String::toLowerCase);
		put("upper", String::toUpperCase);
		put("trim", String::trim);
		put("capital", WordUtils::capitalize);
		put("fcapital", WordUtils::capitalizeFully);
		put("exists", VariableAssigner::exists);
		put("iexists", VariableAssigner::iexists);
		put("inv", VariableAssigner::inv);
	}};

	public static void assignVariableHolders(Object object, IVariableProvider<String> variables, IComponentProcessor processor, TemplateInclusion encapsulation) {
		assignVariableHolders(new Context(object, variables, processor, encapsulation));
	}

	public static void assignVariableHolders(Context context) {
		Class<?> clazz = context.object.getClass();
		Field[] fields = clazz.getFields();

		for(Field f : fields)
			if(f.getAnnotation(VariableHolder.class) != null)
				assignField(f, context);
	}

	private static void assignField(Field f, Context c) {
		Class<?> type = f.getType();
		f.setAccessible(true);
		
		try {
			if(ASSIGNERS.containsKey(type)) {
				ASSIGNERS.get(type).assign(f, c);
			} else if(c.object != null) {
				Object o = f.get(c.object);
				assignVariableHolders(c.rewrap(o));
			}
		} catch(IllegalAccessException e) {
			throw new RuntimeException("Error assigning variables to component", e);
		}
	}

	private static void assignStringField(Field f, Context c) throws IllegalAccessException {
		String s = (String) f.get(c.object);
		String res = resolveString(s, c);
		if(res != null)
			f.set(c.object, res);
	}

	private static void assignStringArrayField(Field f, Context c)  throws IllegalAccessException {
		String[] arr = (String[]) f.get(c.object);

		for(int i = 0; i < arr.length; i++) {
			String s = arr[i];
			String res = resolveString(s, c); 
			if(res != null)
				arr[i] = res;
		}
	}
	
	private static void assignList(Field f, Context c) throws IllegalAccessException {
		List list = (List) f.get(c.object);
		
		for(int i = 0; i < list.size(); i++) {
			Object o = list.get(i);
			
			if(o instanceof String) {
				String res = resolveString((String) o, c);
				list.set(i, res);
			}
		}
	}

	private static void assignMap(Field f, Context c)  throws IllegalAccessException {
		Map map = (Map) f.get(c.object);
		Collection<Entry> entries = map.entrySet();

		for(Entry e : entries) {
			Object v = e.getValue();

			if(v instanceof String) {
				Object k = e.getKey();
				String res = resolveString((String) v, c);
				if(res != null)
					e.setValue(res);
			}
		}
	}
	
	private static String resolveString(String curr, Context c) {
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
			this(object, variables, processor, encapsulation, new HashMap());
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
