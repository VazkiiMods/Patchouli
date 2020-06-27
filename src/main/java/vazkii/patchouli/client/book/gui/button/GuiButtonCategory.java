package vazkii.patchouli.client.book.gui.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.BookIcon;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonCategory extends Button {

	private static final int ANIM_TIME = 5;

	final GuiBook parent;
	BookCategory category;
	final BookIcon icon;
	final ITextComponent name;
	final int u, v;
	float timeHovered;

	public GuiButtonCategory(GuiBook parent, int x, int y, BookCategory category, Button.IPressable onPress) {
		this(parent, x, y, category.getIcon(), category.getName(), onPress);
		this.category = category;
	}

	public GuiButtonCategory(GuiBook parent, int x, int y, BookIcon icon, ITextComponent name, Button.IPressable onPress) {
		super(parent.bookLeft + x, parent.bookTop + y, 20, 20, name, onPress);
		this.parent = parent;
		this.u = x;
		this.v = y;
		this.icon = icon;
		this.name = name;
	}

	@Override
	public void func_230431_b_(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
		if (field_230693_o_) {
			if (func_230449_g_()) {
				timeHovered = Math.min(ANIM_TIME, timeHovered + ClientTicker.delta);
			} else {
				timeHovered = Math.max(0, timeHovered - ClientTicker.delta);
			}

			float time = Math.max(0, Math.min(ANIM_TIME, timeHovered + (func_230449_g_() ? partialTicks : -partialTicks)));
			float transparency = 0.5F - (time / ANIM_TIME) * 0.5F;
			boolean locked = category != null && category.isLocked();

			if (locked) {
				RenderSystem.color4f(1F, 1F, 1F, 0.7F);
				GuiBook.drawLock(ms, parent.book, field_230690_l_ + 2, field_230691_m_ + 2);
			} else {
				icon.render(ms, field_230690_l_ + 2, field_230691_m_ + 2);
			}

			ms.push();
			RenderSystem.enableBlend();
			RenderSystem.color4f(1F, 1F, 1F, transparency);
			ms.translate(0, 0, 200);
			GuiBook.drawFromTexture(ms, parent.book, field_230690_l_, field_230691_m_, u, v, field_230688_j_, field_230689_k_);
			RenderSystem.color4f(1F, 1F, 1F, 1F);

			if (category != null && !category.isLocked()) {
				GuiBook.drawMarking(ms, parent.book, field_230690_l_, field_230691_m_, 0, category.getReadState());
			}
			ms.pop();

			if (func_230449_g_()) {
				parent.setTooltip(locked
						? new TranslationTextComponent("patchouli.gui.lexicon.locked").func_240699_a_(TextFormatting.GRAY)
						: name);
			}
		}
	}

	@Override
	public void func_230988_a_(SoundHandler soundHandlerIn) {
		if (category != null && !category.isLocked()) {
			GuiBook.playBookFlipSound(parent.book);
		}
	}

	public BookCategory getCategory() {
		return category;
	}

}
