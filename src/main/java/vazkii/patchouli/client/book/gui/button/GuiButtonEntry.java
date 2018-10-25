package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonEntry extends GuiButton {
	
	private static final int ANIM_TIME = 5;

	GuiBook parent;
	BookEntry entry;
	int i;
	float timeHovered;
	boolean unread;

	public GuiButtonEntry(GuiBook parent, int x, int y, BookEntry entry, int i) {
		super(0, x, y, GuiBook.PAGE_WIDTH, 10, "");
		this.parent = parent;
		this.entry = entry;
		this.i = i;
		unread = entry.isUnread();
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if(enabled && visible) {
			hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			
			if(hovered)
				timeHovered = Math.min(ANIM_TIME, timeHovered + ClientTicker.delta);
			else timeHovered = Math.max(0, timeHovered - ClientTicker.delta);
			
			float time = Math.max(0, Math.min(ANIM_TIME, timeHovered + (hovered ? partialTicks : -partialTicks)));
			float widthFract = (float) time / ANIM_TIME;
			boolean locked = entry.isLocked();
			
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			Gui.drawRect(x * 2, y * 2, (x + (int) ((float) width * widthFract)) * 2, (y + height) * 2, 0x22000000);
			GlStateManager.enableBlend();

			if(locked) {
				GlStateManager.color(1F, 1F, 1F, 0.7F);
				GuiBook.drawLock(parent.book, x * 2 + 2, y * 2 + 2); 
			} else {
				RenderHelper.enableGUIStandardItemLighting();
				mc.getRenderItem().renderItemIntoGUI(entry.getIconItem(), x * 2 + 2, y * 2 + 2);	
			}
			GlStateManager.scale(2F, 2F, 2F);

			int color = 0;
			String name = (entry.isPriority() ? TextFormatting.ITALIC : "") + entry.getName();
			if(locked) {
				name = I18n.translateToLocal("patchouli.gui.lexicon.locked");
				color = 0x77000000;
			}
			if(entry.isSecret())
				color = 0xAA000000; 
			
			boolean unicode = mc.fontRenderer.getUnicodeFlag();
			mc.fontRenderer.setUnicodeFlag(true);
			mc.fontRenderer.drawString(name, x + 12, y, color);
			mc.fontRenderer.setUnicodeFlag(unicode);
			
			if(unread)
				parent.drawWarning(parent.book, x + width - 5, y, entry.hashCode());
		}
	}
	
	@Override
    public void playPressSound(SoundHandler soundHandlerIn) {
		GuiBook.playBookFlipSound();
	}
	
	public BookEntry getEntry() {
		return entry;
	}

}
