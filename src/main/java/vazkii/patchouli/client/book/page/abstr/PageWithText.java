package vazkii.patchouli.client.book.page.abstr;

import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.BookTextRenderer;
import vazkii.patchouli.client.book.gui.GuiBookEntry;

public abstract class PageWithText extends BookPage {

	String text;

	transient BookTextRenderer textRender;
	
	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);
		
		if(text == null)
			text = "";
		
		textRender = new BookTextRenderer(parent, text, 0, getTextHeight());
	}
	
	public abstract int getTextHeight();
	
	@Override
	public void render(int mouseX, int mouseY, float pticks) {
		if(shouldRenderText())
			textRender.render(mouseX, mouseY);
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if(shouldRenderText())
			textRender.click(mouseX, mouseY, mouseButton);
	}
	
	public boolean shouldRenderText() {
		return true;
	}

}
