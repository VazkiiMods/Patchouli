package vazkii.patchouli.client.book.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Supplier;

import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.template.component.ComponentText;

public class BookTemplate {
	
	public static final HashMap<String, Class<? extends TemplateComponent>> componentTypes = new HashMap();
	public static final HashMap<String, Supplier<IComponentInflater>> inflaterTypes = new HashMap(); // TODO implement
	
	static {
		componentTypes.put("text", ComponentText.class);
	}

	List<TemplateComponent> components = new ArrayList();
	boolean compiled = false;
	
	public void compile(Map<String, String> variables, IComponentInflater inflater) {
		if(compiled)
			return;
		
		for(TemplateComponent c : components)
			c.compile(variables, inflater);
		
		compiled = true;
	}
	
	public void build(BookPage page, GuiBookEntry parent, int pageNum) {
		if(compiled)
			components.forEach(c -> c.build(page, parent, pageNum));
	}
	
	public void onDisplayed(BookPage page, GuiBookEntry parent, int left, int top) {
		if(compiled)
			components.forEach(c -> c.onDisplayed(page, parent, left, top));
	}
	
	public void render(BookPage page, int mouseX, int mouseY, float pticks) { 
		if(compiled)
			components.forEach(c -> c.render(page, mouseX, mouseY, pticks));
	}
	
	public void mouseClicked(BookPage page, int mouseX, int mouseY, int mouseButton) {
		if(compiled)
			components.forEach(c -> c.mouseClicked(page, mouseX, mouseY, mouseButton));
	}

}
