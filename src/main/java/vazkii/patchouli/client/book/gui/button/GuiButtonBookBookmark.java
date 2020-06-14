package vazkii.patchouli.client.book.gui.button;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import vazkii.patchouli.client.base.PersistentData.DataHolder.BookData.Bookmark;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.book.Book;

public class GuiButtonBookBookmark extends GuiButtonBook {

	private final Book book;

	public final Bookmark bookmark;
	public final boolean multiblock;

	public GuiButtonBookBookmark(GuiBook parent, int x, int y, Bookmark bookmark) {
		this(parent, x, y, bookmark, false);
	}

	public GuiButtonBookBookmark(GuiBook parent, int x, int y, Bookmark bookmark, boolean multiblock) {
		super(parent, x, y, 272, bookmark == null ? 170 : 160, 13, 10, parent::handleButtonBookmark, getTooltip(parent.book, bookmark, multiblock));
		this.book = parent.book;
		this.bookmark = bookmark;
		this.multiblock = multiblock;
	}

	@Override
	public void renderButton(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
		super.renderButton(ms, mouseX, mouseY, partialTicks);

		BookEntry entry = bookmark == null ? null : bookmark.getEntry(book);
		if (bookmark != null && entry != null) {
			ms.push();
			ms.scale(0.5F, 0.5F, 0.5F);
			int px = x * 2 + (isHovered() ? 6 : 2);
			int py = y * 2 + 2;
			entry.getIcon().render(ms, px, py);

			RenderSystem.disableDepthTest();
			String s = Integer.toString(bookmark.page + 1);
			if (multiblock) {
				s = I18n.format("patchouli.gui.lexicon.visualize_letter");
			}
			parent.getMinecraft().textRenderer.drawWithShadow(ms, s, px + 12, py + 10, 0xFFFFFF);
			RenderSystem.enableDepthTest();
			ms.pop();
		}
	}

	private static ITextComponent[] getTooltip(Book book, Bookmark bookmark, boolean multiblock) {
		BookEntry entry = bookmark == null ? null : bookmark.getEntry(book);

		if (bookmark == null || entry == null) {
			return new ITextComponent[] { new TranslationTextComponent("patchouli.gui.lexicon.add_bookmark") };
		}

		return new ITextComponent[] {
				entry.getName(),
				new TranslationTextComponent(multiblock
						? "patchouli.gui.lexicon.multiblock_bookmark"
						: "patchouli.gui.lexicon.remove_bookmark").applyTextStyle(TextFormatting.GRAY)
		};
	}

}
