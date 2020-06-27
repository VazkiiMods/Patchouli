package vazkii.patchouli.client.book.gui.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonEntry extends Button {

	private static final int ANIM_TIME = 5;

	private final GuiBook parent;
	private final BookEntry entry;
	private float timeHovered;

	public GuiButtonEntry(GuiBook parent, int x, int y, BookEntry entry, Button.IPressable onPress) {
		super(x, y, GuiBook.PAGE_WIDTH, 10, entry.getName(), onPress);
		this.parent = parent;
		this.entry = entry;
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
			float widthFract = time / ANIM_TIME;
			boolean locked = entry.isLocked();

			ms.scale(0.5F, 0.5F, 0.5F);
			AbstractGui.func_238467_a_(ms, field_230690_l_ * 2, field_230691_m_ * 2, (field_230690_l_ + (int) ((float) field_230688_j_ * widthFract)) * 2, (field_230691_m_ + field_230689_k_) * 2, 0x22000000);
			RenderSystem.enableBlend();

			if (locked) {
				RenderSystem.color4f(1F, 1F, 1F, 0.7F);
				GuiBook.drawLock(ms, parent.book, field_230690_l_ * 2 + 2, field_230691_m_ * 2 + 2);
			} else {
				entry.getIcon().render(ms, field_230690_l_ * 2 + 2, field_230691_m_ * 2 + 2);
			}

			ms.scale(2F, 2F, 2F);

			IFormattableTextComponent name;
			if (locked) {
				name = new TranslationTextComponent("patchouli.gui.lexicon.locked");
			} else {
				name = entry.getName();
				if (entry.isPriority()) {
					name = name.func_240699_a_(TextFormatting.ITALIC);
				}
			}

			name = name.func_240703_c_(entry.getBook().getFontStyle());
			Minecraft.getInstance().fontRenderer.func_238422_b_(ms, name, field_230690_l_ + 12, field_230691_m_, getColor());

			if (!entry.isLocked()) {
				GuiBook.drawMarking(ms, parent.book, field_230690_l_ + field_230688_j_ - 5, field_230691_m_ + 1, entry.hashCode(), entry.getReadState());
			}
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
	public void func_230988_a_(SoundHandler soundHandlerIn) {
		if (entry != null && !entry.isLocked()) {
			GuiBook.playBookFlipSound(parent.book);
		}
	}

	public BookEntry getEntry() {
		return entry;
	}

}
