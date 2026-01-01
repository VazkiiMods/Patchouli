package vazkii.patchouli.client.book.gui.button;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import org.jspecify.annotations.Nullable;

import vazkii.patchouli.client.base.PersistentData.Bookmark;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.book.Book;

public class GuiButtonBookBookmark extends GuiButtonBook {

	private final Book book;

	public final @Nullable Bookmark bookmark;
	public final boolean multiblock;

	public GuiButtonBookBookmark(GuiBook parent, int x, int y, @Nullable Bookmark bookmark) {
		this(parent, x, y, bookmark, false);
	}

	public GuiButtonBookBookmark(GuiBook parent, int x, int y, @Nullable Bookmark bookmark, boolean multiblock) {
		super(parent, x, y, 272, bookmark == null ? 170 : 160, 13, 10, parent::handleButtonBookmark, getTooltip(parent.book, bookmark, multiblock));
		this.book = parent.book;
		this.bookmark = bookmark;
		this.multiblock = multiblock;
	}

	@Override
	protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.renderContents(graphics, mouseX, mouseY, partialTicks);

		BookEntry entry = bookmark == null ? null : bookmark.getEntry(book);
		if (bookmark != null && entry != null) {
			graphics.pose().pushMatrix();
			graphics.pose().scale(0.5F, 0.5F);
			int px = getX() * 2 + (isHoveredOrFocused() ? 6 : 2);
			int py = getY() * 2 + 2;
			entry.getIcon().render(graphics, px, py);

			String s = Integer.toString(bookmark.spread() + 1);
			if (multiblock) {
				s = I18n.get("patchouli.gui.lexicon.visualize_letter");
			}
			graphics.drawString(parent.getMinecraft().font, s, px + 12, py + 10, 0xFFFFFF, true);
			graphics.pose().popMatrix();
		}
	}

	private static Component[] getTooltip(Book book, @Nullable Bookmark bookmark, boolean multiblock) {
		if (bookmark == null) {
			return new Component[] { Component.translatable("patchouli.gui.lexicon.add_bookmark") };
		}

		BookEntry entry = bookmark.getEntry(book);

		if (entry == null) {
			return new Component[0];
		}

		return new Component[] {
				entry.getName(),
				Component.translatable(multiblock
						? "patchouli.gui.lexicon.multiblock_bookmark"
						: "patchouli.gui.lexicon.remove_bookmark").withStyle(ChatFormatting.GRAY)
		};
	}

}
