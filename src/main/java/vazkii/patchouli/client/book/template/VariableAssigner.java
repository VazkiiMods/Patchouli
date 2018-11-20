package vazkii.patchouli.client.book.template;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.VariableHolder;

public class VariableAssigner {

	private static final Map<Class<?>, Assigner> ASSIGNERS = new HashMap<Class<?>, Assigner>() {{
		put(String.class, VariableAssigner::assignStringField);
		put(String[].class, VariableAssigner::assignStringArrayField);
	}};

	public static void assignVariableHolders(Object object, IVariableProvider<String> variables, IComponentProcessor processor, TemplateInclusion encapsulation) {
		Context c = new Context(object, variables, processor, encapsulation);
		Class<?> clazz = object.getClass();
		Field[] fields = clazz.getFields();

		for(Field f : fields)
			if(f.getAnnotation(VariableHolder.class) != null)
				assignField(f, c);
	}

	private static void assignField(Field f, Context c) {
		Class<?> type = f.getType();
		if(ASSIGNERS.containsKey(type)) {
			f.setAccessible(true);
			
			try {
				ASSIGNERS.get(type).assign(f, c);
			} catch(IllegalAccessException e) {
				throw new RuntimeException("Error assigning variables to component", e);
			}
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
		boolean changed = false;
		
		for(int i = 0; i < arr.length; i++) {
			String s = arr[i];
			String res = resolveString(s, c); 
			if(res != null) {
				arr[i] = res;
				changed = true;
			}
		}
		
		if(changed)
			f.set(c.object, arr);
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
			this.object = object;
			this.variables = variables;
			this.processor = processor;
			this.encapsulation = encapsulation;
			
			cachedVars = new HashMap();
		}
		
		String getCached(String s) {
			return cachedVars.get(s);
		}
		
		void cache(String k, String v) {
			cachedVars.put(k, v);
		}

	}
	
	private static interface Assigner {
		void assign(Field f, Context c) throws IllegalAccessException;
	}

}
