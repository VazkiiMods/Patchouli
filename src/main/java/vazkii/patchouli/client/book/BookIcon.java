package vazkii.patchouli.client.book;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.common.util.ItemStackUtil;

public class BookIcon {

	private final IconType type;
	private final ItemStack stack;
	private final ResourceLocation res;
	
	public BookIcon(String str) {
		if(str.endsWith(".png")) {
			type = IconType.RESOURCE;
			stack = null;
			res = new ResourceLocation(str);
		} else {
			type = IconType.STACK;
			stack = ItemStackUtil.loadStackFromString(str);
			res = null;
		}
	}
	
	public BookIcon(ItemStack stack) {
		type = IconType.STACK;
		this.stack = stack;
		res = null;
	}
	
	public BookIcon(ResourceLocation res) {
		type = IconType.RESOURCE;
		stack = null;
		this.res = res;
	}
	
	public void render(int x, int y) {
		Minecraft mc = Minecraft.getInstance();
		switch(type) {
		case STACK:
			RenderHelper.enableGUIStandardItemLighting();
			mc.getItemRenderer().renderItemIntoGUI(stack, x, y);	
			break;
			
		case RESOURCE:
			GlStateManager.color4f(1F, 1F, 1F, 1F);
			mc.textureManager.bindTexture(res);
			AbstractGui.blit(x, y, 0, 0, 16, 16, 16, 16, 16, 16);
			break;
		}
	}
	
	private enum IconType {
		STACK, RESOURCE
	}
	
}
