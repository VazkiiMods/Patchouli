package vazkii.patchouli.client.book.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.base.Supplier;
import com.google.gson.annotations.SerializedName;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.template.component.ComponentEntity;
import vazkii.patchouli.client.book.template.component.ComponentFrame;
import vazkii.patchouli.client.book.template.component.ComponentHeader;
import vazkii.patchouli.client.book.template.component.ComponentImage;
import vazkii.patchouli.client.book.template.component.ComponentItemStack;
import vazkii.patchouli.client.book.template.component.ComponentSeparator;
import vazkii.patchouli.client.book.template.component.ComponentText;
import vazkii.patchouli.client.book.template.test.EntityTestProcessor;
import vazkii.patchouli.client.book.template.test.RecipeTestProcessor;

public class BookTemplate {
	
	public static final HashMap<String, Class<? extends TemplateComponent>> componentTypes = new HashMap();
	
	static {
		registerComponent("text", ComponentText.class);
		registerComponent("item", ComponentItemStack.class);
		registerComponent("image", ComponentImage.class);
		registerComponent("header", ComponentHeader.class);
		registerComponent("separator", ComponentSeparator.class);
		registerComponent("frame", ComponentFrame.class);
		registerComponent("entity", ComponentEntity.class);
	}

	List<TemplateComponent> components = new ArrayList();
	@SerializedName("processor")
	String processorClass;
	
	transient IComponentProcessor processor;
	transient boolean compiled = false;
	transient boolean attemptedCreatingProcessor = false;
	
	public void compile(IVariableProvider variables) {
		if(compiled)
			return;
		
		createProcessor();
		components.removeIf(c -> c == null);
		
		if(processor != null)
			processor.setup(variables);
		
		for(TemplateComponent c : components)
			c.compile(variables, processor);
		
		compiled = true;
	}
	
	public void build(BookPage page, BookEntry entry, int pageNum) {
		if(compiled)
			components.forEach(c -> c.build(page, entry, pageNum));
	}
	
	public void onDisplayed(BookPage page, GuiBookEntry parent, int left, int top) {
		if(compiled) {
			if(processor != null)
				processor.refresh(parent, left, top);

			components.forEach(c -> c.isVisible = c.getVisibleStatus(processor));
			components.forEach(c -> c.onDisplayed(page, parent, left, top));
		}
	}
	
	public void render(BookPage page, int mouseX, int mouseY, float pticks) { 
		if(compiled)
			components.forEach(c -> {
				if(c.isVisible) 
					c.render(page, mouseX, mouseY, pticks);
			});
	}
	
	public void mouseClicked(BookPage page, int mouseX, int mouseY, int mouseButton) {
		if(compiled)
			components.forEach(c -> {
				if(c.isVisible)
					c.mouseClicked(page, mouseX, mouseY, mouseButton);
			});
	}
	
	public static void registerComponent(String name, Class<? extends TemplateComponent> clazz) {
		componentTypes.put(name, clazz);
	}
	
	private void createProcessor() {
		if(!attemptedCreatingProcessor) {
			if(processorClass != null && !processorClass.isEmpty()) try {
				Class<?> clazz = Class.forName(processorClass);
				if(clazz != null)
					processor = (IComponentProcessor) clazz.newInstance();
			} catch(Exception e) {
				throw new RuntimeException("Failed to create component processor " + processorClass, e);
			}
			
			attemptedCreatingProcessor = true;
		}
	}

}
