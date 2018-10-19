package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.gui.GuiLexicon;

public class GuiButtonCategory extends GuiButton {

	private static final int ANIM_TIME = 5;

	GuiLexicon parent;
	BookCategory category;
	ItemStack stack;
	String name;
	int u, v;
	float timeHovered;
	boolean unread;
	
	public GuiButtonCategory(GuiLexicon parent, int x, int y, BookCategory category) {
		this(parent, x, y, category.getIconItem(), category.getName());
		this.category = category;
		unread = category.isUnread();
	}
	
	public GuiButtonCategory(GuiLexicon parent, int x, int y, ItemStack stack, String name) {
		super(0, parent.bookLeft + x, parent.bookTop + y, 20, 20, "");
		this.parent = parent;
		this.u = x;
		this.v = y;
		this.stack = stack;
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
				GuiLexicon.drawLock(x + 2, y + 2); 
			} else {
				RenderHelper.enableGUIStandardItemLighting();
				mc.getRenderItem().renderItemIntoGUI(stack, x + 2, y + 2);	
			}
			
			GlStateManager.pushMatrix();
			GlStateManager.color(1F, 1F, 1F, transparency);
			GlStateManager.translate(0, 0, 200);
			GuiLexicon.drawFromTexture(x, y, u, v, width, height);
			GlStateManager.color(1F, 1F, 1F, 1F);
			
			if(unread) 
				parent.drawWarning(x, y, 0);
			GlStateManager.popMatrix();
			
			if(hovered)
				parent.setTooltip(locked ? (TextFormatting.GRAY + I18n.translateToLocal("alquimia.gui.lexicon.locked")) : name);		
			}
	}
	
	@Override
    public void playPressSound(SoundHandler soundHandlerIn) {
		GuiLexicon.playBookFlipSound();
	}
	
	public BookCategory getCategory() {
		return category;
	}

}
	