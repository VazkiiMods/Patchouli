package vazkii.patchouli.client.book.template;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.VariableHolder;

public class VariableAssigner {

	private static final Map<Class<?>, Assigner> ASSIGNERS = new HashMap<Class<?>, Assigner>() {{
		put(String.class, VariableAssigner::assignStringField);
		put(String[].class, VariableAssigner::assignStringArrayField);
		put(List.class, VariableAssigner::assignList);
		put(Map.class, VariableAssigner::assignMap);
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
		
		System.out.println("Assigning: " + f.getName() + " as " + type.getName());

		try {
			if(ASSIGNERS.containsKey(type)) {
				ASSIGNERS.get(type).assign(f, c);
			} else {
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
		System.out.println(map);

		for(Entry e : entries) {
			Object v = e.getValue();
			System.out.println(v);

			if(v instanceof String) {
				Object k = e.getKey();
				String res = resolveString((String) v, c);
				System.out.println("Resolved " + v + " => " + res);
				if(res != null)
					e.setValue(res);
			}
		}
	}

	private static String resolveString(String curr, Context c) {
		if(curr == null || curr.isEmpty())
			return curr;

		String cached = c.getCached(curr);
		if(cached != null)
			return cached;

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
