package vazkii.patchouli.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import scala.actors.threadpool.Arrays;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.Book;

public class GuiButtonInventoryBook extends GuiButton {

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
		Gui.drawModalRectWithCustomSizedTexture(x, y, (hovered ? 20 : 0), 0, width, height, 64, 64);
		
		ItemStack stack = book.getBookItem();
		mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x + 2, y + 2);
	}
	
	public Book getBook() {
		return book;
	}
	
}
