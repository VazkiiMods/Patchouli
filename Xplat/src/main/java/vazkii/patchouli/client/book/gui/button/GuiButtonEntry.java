package vazkii.patchouli.client.book.gui.button;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonEntry extends Button {

	private static final int ANIM_TIME = 5;

	private final GuiBook parent;
	private final BookEntry entry;
	private float timeHovered;

	public GuiButtonEntry(GuiBook parent, int x, int y, BookEntry entry, Button.OnPress onPress) {
		super(x, y, GuiBook.PAGE_WIDTH, 10, entry.getName(), onPress, DEFAULT_NARRATION);
		this.parent = parent;
		this.entry = entry;
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		if (!active) {
			return;
		}
		if (isHoveredOrFocused()) {
			timeHovered = Math.min(ANIM_TIME, timeHovered + ClientTicker.delta);
		} else {
			timeHovered = Math.max(0, timeHovered - ClientTicker.delta);
		}

		float time = Math.max(0, Math.min(ANIM_TIME, timeHovered + (isHoveredOrFocused() ? partialTicks : -partialTicks)));
		float widthFract = time / ANIM_TIME;
		boolean locked = entry.isLocked();

		graphics.pose().scale(0.5F, 0.5F, 0.5F);
		graphics.fill(getX() * 2, getY() * 2, (getX() + (int) ((float) width * widthFract)) * 2, (getY() + height) * 2, 0x22000000);
		RenderSystem.enableBlend();

		if (locked) {
			graphics.setColor(1F, 1F, 1F, 0.7F);
			GuiBook.drawLock(graphics, parent.book, getX() * 2 + 2, getY() * 2 + 2);
		} else {
			entry.getIcon().render(graphics, getX() * 2 + 2, getY() * 2 + 2);
		}

		graphics.pose().scale(2F, 2F, 2F);

		MutableComponent name;
		if (locked) {
			name = Component.translatable("patchouli.gui.lexicon.locked");
		} else {
			name = entry.getName();
			if (entry.isPriority()) {
				name = name.withStyle(ChatFormatting.ITALIC);
			}
		}

		name = name.withStyle(entry.getBook().getFontStyle());
		graphics.drawString(Minecraft.getInstance().font, name, getX() + 12, getY(), getColor(), false);

		if (!entry.isLocked()) {
			GuiBook.drawMarking(graphics, parent.book, getX() + width - 5, getY() + 1, entry.hashCode(), entry.getReadState());
		}
	}

	private int getColor() {
		if (entry.isSecret()) {
			return 0xAA000000 | (parent.book.textColor & 0x00FFFFFF);
		}
		if (entry.isLocked()) {
			return 0x77000000 | (parent.book.textColor & 0x00FFFFFF);
		}
		return entry.getEntryColor();
	}

	@Override
	public void playDownSound(SoundManager soundHandlerIn) {
		if (entry != null && !entry.isLocked()) {
			GuiBook.playBookFlipSound(parent.book);
		}
	}

	public BookEntry getEntry() {
		return entry;
	}

}
