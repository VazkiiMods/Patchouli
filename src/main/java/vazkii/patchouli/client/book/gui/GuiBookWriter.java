package vazkii.patchouli.client.book.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookResize;
import vazkii.patchouli.common.book.Book;

public class GuiBookWriter extends GuiBook {

	BookTextRenderer text, editableText;
	GuiTextField textfield;
	
	private static String savedText = "";
	private static boolean drawHeader;

	public GuiBookWriter(Book book) {
		super(book);
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		text = new BookTextRenderer(this, I18n.translateToLocal("patchouli.gui.lexicon.editor.info"), LEFT_PAGE_X, TOP_PADDING + 20);
		textfield = new GuiTextField(0, fontRenderer, 10, FULL_HEIGHT - 40, PAGE_WIDTH, 20);
		textfield.setMaxStringLength(Integer.MAX_VALUE);
		textfield.setText(savedText);
		
		buttonList.add(new GuiButtonBookResize(this, bookLeft + 115, bookTop + PAGE_HEIGHT - 36, false));
		
		Keyboard.enableRepeatEvents(true);
		refreshText();
	}
	
	@Override
	void drawForegroundElements(int mouseX, int mouseY, float partialTicks) {
		super.drawForegroundElements(mouseX, mouseY, partialTicks);
		
		drawCenteredStringNoShadow(I18n.translateToLocal("patchouli.gui.lexicon.editor"), LEFT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, book.headerColor);
		drawSeparator(book, LEFT_PAGE_X, TOP_PADDING + 12);

		if(drawHeader) {
			drawCenteredStringNoShadow(I18n.translateToLocal("patchouli.gui.lexicon.editor.mock_header"), RIGHT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, book.headerColor);
			drawSeparator(book, RIGHT_PAGE_X, TOP_PADDING + 12);
		}
		
		textfield.drawTextBox();
		text.render(mouseX, mouseY);
		editableText.render(mouseX, mouseY);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		textfield.mouseClicked(mouseX - bookLeft, mouseY - bookTop, mouseButton);
		
		text.click(mouseX, mouseY, mouseButton);
		editableText.click(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		
		textfield.textboxKeyTyped(typedChar, keyCode);
		refreshText();
	}
	
	@Override
	public void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		
		if(button instanceof GuiButtonBookResize) {
			drawHeader = !drawHeader;
			refreshText();
		}
	}
	
	public void refreshText() {
		int yPos = TOP_PADDING + (drawHeader ? 22 : -4);
		
		boolean unicode = fontRenderer.getUnicodeFlag();
		savedText = textfield.getText();
		try {
			editableText = new BookTextRenderer(this, savedText, RIGHT_PAGE_X, yPos);
		} catch(Throwable e) {
			editableText = new BookTextRenderer(this, "[ERROR]", RIGHT_PAGE_X, yPos);
			e.printStackTrace();
		} finally {
			fontRenderer.setUnicodeFlag(unicode); // if there's an error the state might not be reset
		}
	}
}
