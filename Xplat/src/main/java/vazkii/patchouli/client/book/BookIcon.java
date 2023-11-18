package vazkii.patchouli.client.book;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.util.ItemStackUtil;

public sealed interface BookIcon permits BookIcon.StackIcon,BookIcon.TextureIcon {
	void render(PoseStack ms, int x, int y);

	record StackIcon(ItemStack stack) implements BookIcon {
		@Override
		public void render(PoseStack ms, int x, int y) {
			Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(ms, stack(), x, y);
		}
	}

	record TextureIcon(ResourceLocation texture) implements BookIcon {
		@Override
		public void render(PoseStack ms, int x, int y) {
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
			RenderSystem.setShaderTexture(0, texture());
			GuiComponent.blit(ms, x, y, 0, 0, 16, 16, 16, 16);
		}
	}

	static BookIcon from(String str) {
		if (str.endsWith(".png")) {
			return new TextureIcon(new ResourceLocation(str));
		} else {
			try {
				ItemStack stack = ItemStackUtil.loadStackFromString(str);
				return new StackIcon(stack);
			} catch (Exception e) {
				PatchouliAPI.LOGGER.warn("Invalid icon item stack: {}", e.getMessage());
				return new StackIcon(ItemStack.EMPTY);
			}
		}
	}

}
