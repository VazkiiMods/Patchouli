package vazkii.patchouli.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.RenderHelper;
import vazkii.patchouli.client.book.BookContents;
import vazkii.patchouli.client.book.EntryDisplayState;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.book.Book;

public class GuiButtonInventoryBook extends Button {

	private Book book;

	public GuiButtonInventoryBook(Book book, int x, int y) {
		super(x, y, 20, 20, Component.empty(), (b) -> {
			BookContents contents = book.getContents();
			contents.openLexiconGui(contents.getCurrentGui(), false);
		}, DEFAULT_NARRATION);
		this.book = book;
	}

	@Override
	public void renderButton(PoseStack ms, int mouseX, int mouseY, float pticks) {
		Minecraft mc = Minecraft.getInstance();
		RenderSystem.setShaderTexture(0, new ResourceLocation(PatchouliAPI.MOD_ID, "textures/gui/inventory_button.png"));
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

		boolean hovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
		GuiComponent.blit(ms, getX(), getY(), (hovered ? 20 : 0), 0, width, height, 64, 64);

		ItemStack stack = book.getBookItem();
		RenderHelper.renderItemStackInGui(ms, stack, getX() + 2, getY() + 2);

		EntryDisplayState readState = book.getContents().getReadState();
		if (readState.hasIcon && readState.showInInventory) {
			GuiBook.drawMarking(ms, book, getX(), getY(), 0, readState);
		}
	}

	public Book getBook() {
		return book;
	}

}
