package vazkii.patchouli.client.book.gui.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.BookIcon;
import vazkii.patchouli.client.book.gui.GuiBook;

import org.jetbrains.annotations.Nullable;

public class GuiButtonCategory extends Button {

	private static final int ANIM_TIME = 5;

	private final GuiBook parent;
	@Nullable private BookCategory category;
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
	public void renderButton(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
		if (active) {
			if (isHoveredOrFocused()) {
				timeHovered = Math.min(ANIM_TIME, timeHovered + ClientTicker.delta);
			} else {
				timeHovered = Math.max(0, timeHovered - ClientTicker.delta);
			}

			float time = Math.max(0, Math.min(ANIM_TIME, timeHovered + (isHoveredOrFocused() ? partialTicks : -partialTicks)));
			float transparency = 0.5F - (time / ANIM_TIME) * 0.5F;
			boolean locked = category != null && category.isLocked();

			if (locked) {
				RenderSystem.setShaderColor(1F, 1F, 1F, 0.7F);
				GuiBook.drawLock(ms, parent.book, getX() + 2, getY() + 2);
			} else {
				icon.render(ms, getX() + 2, getY() + 2);
			}

			ms.pushPose();
			RenderSystem.enableBlend();
			RenderSystem.setShaderColor(1F, 1F, 1F, transparency);
			ms.translate(0, 0, 200);
			GuiBook.drawFromTexture(ms, parent.book, getX(), getY(), u, v, width, height);
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

			if (category != null && !category.isLocked()) {
				GuiBook.drawMarking(ms, parent.book, getX(), getY(), 0, category.getReadState());
			}
			ms.popPose();

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

	public BookCategory getCategory() {
		return category;
	}

}
