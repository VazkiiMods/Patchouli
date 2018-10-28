package vazkii.patchouli.client.book.template;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBookEntry;

public abstract class TemplateComponent {

	public String group = "";
	public int x, y;
	
	public final void compile(IVariableProvider variables, IComponentInflater inflater) {
		Class<?> clazz = getClass();
		Field[] fields = clazz.getFields();
		
		for(Field f : fields) {
			if(f.getType() == String.class && f.getAnnotation(VariableHolder.class) != null) try {
				f.setAccessible(true);
				String curr = (String) f.get(this);
				
				if(curr != null && curr.startsWith("#")) {
					String key = curr.substring(1);
					String val = null;
					if(inflater != null)
						val = inflater.getInflatedValue(key);
					
					if(val == null && variables.has(key))
						val = variables.get(key);
					if(val == null)
						val = "[FAILED TO LOAD " + key + "]";
					
					f.set(this, val);
				}
			} catch(IllegalAccessException e) {
				throw new RuntimeException("Error compiling component", e);
			}
		}
	}
	
	public void build(BookPage page, BookEntry entry, int pageNum) {
		// NO-OP
	}
	
	public void onDisplayed(BookPage page, GuiBookEntry parent, int left, int top) {
		// NO-OP
	}
	
	public void render(BookPage page, int mouseX, int mouseY, float pticks) { 
		// NO-OP
	}
	
	public void mouseClicked(BookPage page, int mouseX, int mouseY, int mouseButton) {
		// NO-OP
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface VariableHolder {}
	
}
