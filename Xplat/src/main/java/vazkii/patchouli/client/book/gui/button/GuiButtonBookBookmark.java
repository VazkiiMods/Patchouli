package vazkii.patchouli.client.book.gui.button;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import vazkii.patchouli.client.base.PersistentData.Bookmark;
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
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.renderWidget(graphics, mouseX, mouseY, partialTicks);

		BookEntry entry = bookmark == null ? null : bookmark.getEntry(book);
		if (bookmark != null && entry != null) {
			graphics.pose().pushPose();
			graphics.pose().scale(0.5F, 0.5F, 0.5F);
			int px = getX() * 2 + (isHoveredOrFocused() ? 6 : 2);
			int py = getY() * 2 + 2;
			entry.getIcon().render(graphics, px, py);

			RenderSystem.disableDepthTest();
			String s = Integer.toString(bookmark.spread + 1);
			if (multiblock) {
				s = I18n.get("patchouli.gui.lexicon.visualize_letter");
			}
			graphics.drawString(parent.getMinecraft().font, s, px + 12, py + 10, 0xFFFFFF, true);
			RenderSystem.enableDepthTest();
			graphics.pose().popPose();
		}
	}

	private static Component[] getTooltip(Book book, Bookmark bookmark, boolean multiblock) {
		BookEntry entry = bookmark == null ? null : bookmark.getEntry(book);

		if (bookmark == null || entry == null) {
			return new Component[] { Component.translatable("patchouli.gui.lexicon.add_bookmark") };
		}

		return new Component[] {
				entry.getName(),
				Component.translatable(multiblock
						? "patchouli.gui.lexicon.multiblock_bookmark"
						: "patchouli.gui.lexicon.remove_bookmark").withStyle(ChatFormatting.GRAY)
		};
	}

}
