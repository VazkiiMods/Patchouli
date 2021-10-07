package vazkii.patchouli.client.book;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import vazkii.patchouli.client.RenderHelper;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.util.ItemStackUtil;

public class BookIcon {
	private static final BookIcon EMPTY = new BookIcon(ItemStack.EMPTY);

	private final IconType type;
	private final ItemStack stack;
	private final ResourceLocation res;

	public static BookIcon from(String str) {
		if (str.endsWith(".png")) {
			return new BookIcon(new ResourceLocation(str));
		} else {
			try {
				ItemStack stack = ItemStackUtil.loadStackFromString(str);
				return new BookIcon(stack);
			} catch (Exception e) {
				Patchouli.LOGGER.warn("Invalid icon item stack: {}", e.getMessage());
				return EMPTY;
			}
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

	public void render(PoseStack ms, int x, int y) {
		switch (type) {
		case STACK:
			RenderHelper.renderItemStackInGui(ms, stack, x, y);
			break;

		case RESOURCE:
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
			RenderSystem.setShaderTexture(0, res);
			GuiComponent.blit(ms, x, y, 0, 0, 16, 16, 16, 16);
			break;
		}
	}

	private enum IconType {
		STACK, RESOURCE
	}

}
