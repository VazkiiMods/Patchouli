package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.client.base.PersistentData.DataHolder.Bookmark;
import vazkii.patchouli.client.book.gui.GuiLexicon;

public class GuiButtonLexiconBookmark extends GuiButtonLexicon {

	public final Bookmark bookmark;
	public final boolean multiblock;

	public GuiButtonLexiconBookmark(GuiLexicon parent, int x, int y, Bookmark bookmark) {
		this(parent, x, y, bookmark, false);
	}

	public GuiButtonLexiconBookmark(GuiLexicon parent, int x, int y, Bookmark bookmark, boolean multiblock) {
		super(parent, x, y, 272, bookmark == null ? 170 : 160, 13, 10, getTooltip(bookmark, multiblock));
		this.bookmark = bookmark;
		this.multiblock = multiblock;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		super.drawButton(mc, mouseX, mouseY, partialTicks);

		if(visible && bookmark != null && bookmark.getEntry() != null) {
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			int px = x * 2 + (hovered ? 6 : 2);
			int py = y * 2 + 2;
			mc.getRenderItem().renderItemIntoGUI(bookmark.getEntry().getIconItem(), px, py);

			GlStateManager.disableDepth();
			String s = Integer.toString(bookmark.page + 1);
			if(multiblock)
				s = I18n.translateToLocal("alquimia.gui.lexicon.visualize_letter");
			mc.fontRenderer.drawStringWithShadow(s, px + 12, py + 10, 0xFFFFFF);
			GlStateManager.popMatrix();
		}
	}

	private static String[] getTooltip(Bookmark bookmark, boolean multiblock) {
		if(bookmark == null || bookmark.getEntry() == null)
			return new String[] { I18n.translateToLocal("alquimia.gui.lexicon.add_bookmark") };

		return new String[] {
				bookmark.getEntry().getName(),
				TextFormatting.GRAY + I18n.translateToLocal(multiblock 
						? "alquimia.gui.lexicon.multiblock_bookmark"
								: "alquimia.gui.lexicon.remove_bookmark")
		};
	}

}
