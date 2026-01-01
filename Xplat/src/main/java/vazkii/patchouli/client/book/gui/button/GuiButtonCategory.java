package vazkii.patchouli.client.book.gui.button;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

import org.jspecify.annotations.Nullable;

import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.BookIcon;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonCategory extends Button {

	private static final int ANIM_TIME = 5;

	private final GuiBook parent;
	private @Nullable BookCategory category;
	private final BookIcon icon;
	private final Component name;
	private final int u, v;
	private float timeHovered;

	public GuiButtonCategory(GuiBook parent, int x, int y, BookCategory category, Button.OnPress onPress) {
		this(parent, x, y, category.getIcon(), category.getName(), onPress);
		this.category = category;
	}

	public GuiButtonCategory(GuiBook parent, int x, int y, BookIcon icon, Component name, Button.OnPress onPress) {
		super(parent.bookLeft + x, parent.bookTop + y, 20, 20, name, onPress, DEFAULT_NARRATION);
		this.parent = parent;
		this.u = x;
		this.v = y;
		this.icon = icon;
		this.name = name;
	}

	@Override
	protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		if (active) {
			if (isHoveredOrFocused()) {
				timeHovered = Math.min(ANIM_TIME, timeHovered + ClientTicker.delta);
			} else {
				timeHovered = Math.max(0, timeHovered - ClientTicker.delta);
			}

			float time = Math.max(0, Math.min(ANIM_TIME, timeHovered + (isHoveredOrFocused() ? partialTicks : -partialTicks)));
			float transparency = 0.5F - (time / ANIM_TIME) * 0.5F;
			boolean locked = category != null && category.isLocked();

			graphics.pose().pushMatrix();
			graphics.pose().translate(getX(), getY());
			if (locked) {
				GuiBook.drawLock(graphics, parent.book, 2, 2, ARGB.color(0.7F, 0xffffff));
			} else {
				icon.render(graphics, 2, 2);
			}

			graphics.pose().pushMatrix();
			GuiBook.drawFromTexture(graphics, parent.book, 0, 0, u, v, width, height, ARGB.color(transparency, 0xffffff));

			if (category != null && !category.isLocked()) {
				GuiBook.drawMarking(graphics, parent.book, 0, 0, 0, category.getReadState());
			}
			graphics.pose().popMatrix();
			graphics.pose().popMatrix();

			if (isHoveredOrFocused()) {
				parent.setTooltip(locked
						? Component.translatable("patchouli.gui.lexicon.locked").withStyle(ChatFormatting.GRAY)
						: name);
			}
		}
	}

	@Override
	public void playDownSound(SoundManager soundHandlerIn) {
		if (category != null && !category.isLocked()) {
			GuiBook.playBookFlipSound(parent.book);
		}
	}

	public @Nullable BookCategory getCategory() {
		return category;
	}

}
