package vazkii.patchouli.client.book.page;

import java.util.function.Supplier;

import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.template.BookTemplate;
import vazkii.patchouli.client.book.template.JsonVariableWrapper;
import vazkii.patchouli.common.book.Book;

public class PageTemplate extends BookPage {

	transient String processedType = "";
	transient BookTemplate template = null;
	
	@Override
	public void build(BookEntry entry, int pageNum) {
		super.build(entry, pageNum);
		
		JsonVariableWrapper wrapper = new JsonVariableWrapper(sourceObject);
		IComponentProcessor processor = null;
		
		template.compile(wrapper);
		template.build(this, entry, pageNum);
	}
	
	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);
		
		template.onDisplayed(this, parent, left, top);
	}
	
	@Override
	public void render(int mouseX, int mouseY, float pticks) {
		template.render(this, mouseX, mouseY, pticks);
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		template.mouseClicked(this, mouseX, mouseY, mouseButton);
	}
	
	@Override
	public boolean canAdd(Book book) {
		resolveTemplate(book);
		return template != null && super.canAdd(book);
	}
	
	void resolveTemplate(Book book) {
		ResourceLocation key;
		
		if(type.contains(":"))
			key = new ResourceLocation(type);
		else key = new ResourceLocation(book.getModNamespace(), type);
		
		Supplier<BookTemplate> supplier = book.contents.templates.get(key);
		if(supplier == null)
			throw new IllegalArgumentException("Template " + key + " does not exist");

		processedType = key.toString();
		template = supplier.get();
	}
	
}
