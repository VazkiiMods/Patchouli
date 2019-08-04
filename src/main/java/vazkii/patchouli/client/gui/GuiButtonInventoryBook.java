package vazkii.patchouli.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.client.book.EntryDisplayState;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.Book;

public class GuiButtonInventoryBook extends Button {

	Book book;
	
	public GuiButtonInventoryBook(Book book, int buttonId, int x, int y) {
		super(buttonId, x, y, 20, 20, "");
		this.book = book;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		mc.renderEngine.bindTexture(new ResourceLocation(Patchouli.MOD_ID, "textures/gui/inventory_button.png"));
		GlStateManager.color(1F, 1F, 1F);
		hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
		AbstractGui.drawModalRectWithCustomSizedTexture(x, y, (hovered ? 20 : 0), 0, width, height, 64, 64);
		
		ItemStack stack = book.getBookItem();
		RenderHelper.enableGUIStandardItemLighting();
		mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x + 2, y + 2);
		
		EntryDisplayState readState = book.contents.getReadState();
		if(readState.hasIcon && readState.showInInventory)
			GuiBook.drawMarking(book, x, y, 0, readState);
	}
	
	public Book getBook() {
		return book;
	}
	
}
