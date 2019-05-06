package vazkii.patchouli.client.book.page;

import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.template.BookTemplate;
import vazkii.patchouli.client.book.template.JsonVariableWrapper;
import vazkii.patchouli.common.book.Book;

public class PageTemplate extends BookPage {

	transient BookTemplate template = null;
	transient boolean resolved = false;
	
	@Override
	public void build(BookEntry entry, int pageNum) {
		super.build(entry, pageNum);
		
		JsonVariableWrapper wrapper = new JsonVariableWrapper(sourceObject);
		
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
		if(!resolved) {
			template = BookTemplate.createTemplate(book, type, null);
			resolved = true;
		}
		
		return template != null && super.canAdd(book);
	}
}
