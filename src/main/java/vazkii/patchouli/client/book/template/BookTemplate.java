package vazkii.patchouli.client.book.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.base.Supplier;

import vazkii.patchouli.client.book.BookEntry;
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
	
	public void compile(IVariableProvider variables, IComponentInflater inflater) {
		if(compiled)
			return;
		
		components.removeIf(c -> c == null);
		for(TemplateComponent c : components)
			c.compile(variables, inflater);
		
		compiled = true;
	}
	
	public void build(BookPage page, BookEntry entry, int pageNum) {
		if(compiled)
			components.forEach(c -> c.build(page, entry, pageNum));
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
