package vazkii.patchouli.client.book.gui;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import vazkii.patchouli.client.book.text.BookTextParser;
import vazkii.patchouli.client.book.text.Word;
import vazkii.patchouli.common.book.Book;

public class BookTextRenderer {
	final Book book;
	final GuiBook gui;
	final FontRenderer font;
	final String text;
	final int x, y, width;
	final int spaceWidth;
	final int lineHeight;
	final boolean defaultUnicode;
	final int baseColor;

	List<Word> words;
	
	public BookTextRenderer(GuiBook gui, String text, int x, int y) {
		this(gui, text, x, y, GuiBook.PAGE_WIDTH, GuiBook.TEXT_LINE_HEIGHT, gui.book.textColor);
	}
	
	public BookTextRenderer(GuiBook gui, String text, int x, int y, int width, int lineHeight, int baseColor) {
		this.book = gui.book;
		this.gui = gui;
		this.font = gui.getMinecraft().fontRenderer;
		this.text = text;
		this.x = x;
		this.y = y;
		this.width = width;
		this.spaceWidth = font.getStringWidth(" ");
		this.lineHeight = lineHeight;
		this.defaultUnicode = false; //font.getUnicodeFlag(); TODO figure unicode out
		this.baseColor = baseColor;
		
		build();
	}
	
	private void build() {
		BookTextParser parser = new BookTextParser(gui, book, x, y, width, lineHeight, baseColor);
		words = parser.parse(text);
	}
	
	public void render(int mouseX, int mouseY) {
//		if(!book.useBlockyFont) TODO yep unicode
//			font.setUnicodeFlag(true);
		words.forEach(word -> word.render(mouseX, mouseY));
//		font.setUnicodeFlag(defaultUnicode);
	}
	
	public boolean click(double mouseX, double mouseY, int mouseButton) {
		for(Word word : words)
			if(word.click(mouseX, mouseY, mouseButton))
				return true;
		
		return false;
	}
}
