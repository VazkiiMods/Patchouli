package vazkii.patchouli.client.book.template;

import java.lang.reflect.Field;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.common.base.PatchouliConfig;

public abstract class TemplateComponent {

	@VariableHolder
	public String group = "";
	public int x, y;

	@VariableHolder
	public String flag = "";

	@VariableHolder
	public String advancement = "";
	@SerializedName("negate_advancement")
	boolean negateAdvancement = false; 

	transient boolean isVisible = true;
	transient boolean compiled = false;
	
	public transient JsonObject sourceObject;
	protected transient IVariableProvider<String> variables;
	protected transient IComponentProcessor processor;
	protected transient TemplateInclusion encapsulation;

	public final void compile(IVariableProvider<String> variables, IComponentProcessor processor, TemplateInclusion encapsulation) {
		if(compiled)
			return;

		if(encapsulation != null) {
			x += encapsulation.x;
			y += encapsulation.y;
		}

		this.variables = variables;
		this.processor = processor;
		this.encapsulation = encapsulation;
		
		compileVariableHolders(this, variables, processor, encapsulation);
		compiled = true;
	}
	
	public static void compileVariableHolders(Object o, IVariableProvider<String> variables, IComponentProcessor processor, TemplateInclusion encapsulation) {
		Class<?> clazz = o.getClass();
		Field[] fields = clazz.getFields();

		for(Field f : fields) {
			if(f.getType() == String.class && f.getAnnotation(VariableHolder.class) != null) try {
				f.setAccessible(true);
				String curr = (String) f.get(o);
				String original = curr;

				if(curr != null && !curr.isEmpty() && encapsulation != null)
					curr = encapsulation.transform(curr, true);

				if(curr != null) {
					String val = curr;
					if(curr.startsWith("#")) {
						val = null;
						String key = curr.substring(1);
						String originalKey = original.substring(1);
						
						if(processor != null)
							val = processor.process(originalKey);

						if(val == null && variables.has(key))
							val = variables.get(key);

						if(val == null)
							val = "[FAILED TO LOAD " + key + "]";
					}

					f.set(o, val);
				}
			} catch(IllegalAccessException e) {
				throw new RuntimeException("Error compiling component", e);
			}
		}
	}

	public boolean getVisibleStatus(IComponentProcessor processor) {
		if(processor != null && group != null && !group.isEmpty() && !processor.allowRender(group))
			return false;

		if(!flag.isEmpty() && !PatchouliConfig.getConfigFlag(flag))
			return false;

		if(!advancement.isEmpty())
			return ClientAdvancements.hasDone(advancement) != negateAdvancement;

		return true;
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

}
