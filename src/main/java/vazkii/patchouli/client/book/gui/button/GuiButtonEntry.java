package vazkii.patchouli.client.book.gui.button;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonEntry extends Button {
	
	private static final int ANIM_TIME = 5;

	GuiBook parent;
	BookEntry entry;
	int i;
	float timeHovered;

	public GuiButtonEntry(GuiBook parent, int x, int y, BookEntry entry, int i, Button.IPressable onPress) {
		super(x, y, GuiBook.PAGE_WIDTH, 10, "", onPress);
		this.parent = parent;
		this.entry = entry;
		this.i = i;
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		if(active && visible) {
			isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			
			if(isHovered)
				timeHovered = Math.min(ANIM_TIME, timeHovered + ClientTicker.delta);
			else timeHovered = Math.max(0, timeHovered - ClientTicker.delta);
			
			float time = Math.max(0, Math.min(ANIM_TIME, timeHovered + (isHovered ? partialTicks : -partialTicks)));
			float widthFract = time / ANIM_TIME;
			boolean locked = entry.isLocked();
			
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			AbstractGui.fill(x * 2, y * 2, (x + (int) ((float) width * widthFract)) * 2, (y + height) * 2, 0x22000000);
			GlStateManager.enableBlend();

			if(locked) {
				GlStateManager.color4f(1F, 1F, 1F, 0.7F);
				GuiBook.drawLock(parent.book, x * 2 + 2, y * 2 + 2); 
			} else
				entry.getIcon().render(x * 2 + 2, y * 2 + 2);
			
			GlStateManager.scalef(2F, 2F, 2F);

			int color = parent.book.textColor;
			String name = (entry.isPriority() ? TextFormatting.ITALIC : "") + entry.getName();
			if(locked) {
				name = I18n.format("patchouli.gui.lexicon.locked");
				color = 0x77000000 | (parent.book.textColor & 0x00FFFFFF);
			}
			if(entry.isSecret())
				color = 0xAA000000 | (parent.book.textColor & 0x00FFFFFF); 
			
			Minecraft mc = parent.getMinecraft();
//			boolean unicode = mc.fontRenderer.getUnicodeFlag(); TODO unicode
//			if(!parent.book.useBlockyFont)
//				mc.fontRenderer.setUnicodeFlag(true);
			mc.fontRenderer.drawString(name, x + 12, y, color);
//			mc.fontRenderer.setUnicodeFlag(unicode);
			
			if(!entry.isLocked())
				GuiBook.drawMarking(parent.book, x + width - 5, y + 1, entry.hashCode(), entry.getReadState());
		}
	}
	
	@Override
    public void playDownSound(SoundHandler soundHandlerIn) {
		if(entry != null && !entry.isLocked())
			GuiBook.playBookFlipSound(parent.book);
	}
	
	public BookEntry getEntry() {
		return entry;
	}

}
