package vazkii.patchouli.api;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

/**
 * This event is fired on the {@link MinecraftForge#EVENT_BUS} after any
 * book gui {@link #gui} draws the content of a book {@link #book} with
 * the book gui scale still applied to GL. This is useful if additional
 * custom compoenents should be drawn independently of what page a book
 * is currently on.
 */
public class BookDrawScreenEvent
		extends BookEvent {

	public final Screen gui;
	public final int mouseX;
	public final int mouseY;
	public final float partialTicks;
	public final MatrixStack matrixStack;

	public BookDrawScreenEvent(Screen gui, ResourceLocation book, int mouseX, int mouseY, float partialTicks, MatrixStack matrixStack) {
		super(book);
		this.gui = gui;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.partialTicks = partialTicks;
		this.matrixStack = matrixStack;
	}
}
