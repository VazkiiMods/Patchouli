package vazkii.patchouli.client.book.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.base.Supplier;

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
import vazkii.patchouli.client.book.template.test.TestProcessor;

public class BookTemplate {
	
	public static final HashMap<String, Class<? extends TemplateComponent>> componentTypes = new HashMap();
	public static final HashMap<String, Supplier<IComponentProcessor>> processorTypes = new HashMap(); 
	
	static {
		registerComponent("text", ComponentText.class);
		registerComponent("item", ComponentItemStack.class);
		registerComponent("image", ComponentImage.class);
		registerComponent("header", ComponentHeader.class);
		registerComponent("separator", ComponentSeparator.class);
		registerComponent("frame", ComponentFrame.class);
		registerComponent("entity", ComponentEntity.class);

		registerProcessorType("patchouli:recipetest", TestProcessor::new);
		registerProcessorType("patchouli:mob_kill_display", EntityTestProcessor::new);
	}

	List<TemplateComponent> components = new ArrayList();
	IComponentProcessor processor;
	boolean compiled = false;
	
	public void compile(IVariableProvider variables, IComponentProcessor processor) {
		if(compiled)
			return;
		
		components.removeIf(c -> c == null);
		
		if(processor != null) {
			this.processor = processor;
			processor.setup(variables);
		}
		
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
			if(processor != null) {
				processor.refresh(parent, left, top);
				components.forEach(c -> c.isVisible = c.group == null || processor.allowRender(c.group));
			}
				
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

	public static void registerProcessorType(String name, Class<? extends IComponentProcessor> provider) {
		processorTypes.put(name, () -> {
			try {
				return provider.newInstance();
			} catch(Exception e) {
				throw new RuntimeException("Failed to instantiate component processor for " + name, e);
			}
		});
	}
	
	public static void registerProcessorType(String name, Supplier<IComponentProcessor> provider) {
		processorTypes.put(name, provider);
	}
	
}
