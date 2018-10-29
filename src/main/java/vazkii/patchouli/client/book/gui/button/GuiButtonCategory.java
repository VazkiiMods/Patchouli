package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.BookIcon;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonCategory extends GuiButton {

	private static final int ANIM_TIME = 5;

	GuiBook parent;
	BookCategory category;
	BookIcon icon;
	String name;
	int u, v;
	float timeHovered;
	boolean unread;
	
	public GuiButtonCategory(GuiBook parent, int x, int y, BookCategory category) {
		this(parent, x, y, category.getIcon(), category.getName());
		this.category = category;
		unread = category.isUnread();
	}	
	
	public GuiButtonCategory(GuiBook parent, int x, int y, BookIcon icon, String name) {
		super(0, parent.bookLeft + x, parent.bookTop + y, 20, 20, "");
		this.parent = parent;
		this.u = x;
		this.v = y;
		this.icon = icon;
		this.name = name;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if(enabled && visible) {
			hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			
			if(hovered)
				timeHovered = Math.min(ANIM_TIME, timeHovered + ClientTicker.delta);
			else timeHovered = Math.max(0, timeHovered - ClientTicker.delta);
			
			float time = Math.max(0, Math.min(ANIM_TIME, timeHovered + (hovered ? partialTicks : -partialTicks)));
			float transparency = 0.5F - ((float) time / ANIM_TIME) * 0.5F;
			boolean locked = category != null && category.isLocked();

			if(locked) {
				GlStateManager.color(1F, 1F, 1F, 0.7F);
				GuiBook.drawLock(parent.book, x + 2, y + 2); 
			} else
				icon.render(x + 2, y + 2);
			
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.color(1F, 1F, 1F, transparency);
			GlStateManager.translate(0, 0, 200);
			GuiBook.drawFromTexture(parent.book, x, y, u, v, width, height);
			GlStateManager.color(1F, 1F, 1F, 1F);
			
			if(unread) 
				parent.drawWarning(parent.book, x, y, 0);
			GlStateManager.popMatrix();
			
			if(hovered)
				parent.setTooltip(locked ? (TextFormatting.GRAY + I18n.translateToLocal("patchouli.gui.lexicon.locked")) : name);		
			}
	}
	
	@Override
    public void playPressSound(SoundHandler soundHandlerIn) {
		GuiBook.playBookFlipSound();
	}
	
	public BookCategory getCategory() {
		return category;
	}

}
	