package vazkii.patchouli.client.book;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import vazkii.patchouli.client.RenderHelper;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.util.ItemStackUtil;

public class BookIcon {
	private static final BookIcon EMPTY = new BookIcon(ItemStack.EMPTY);

	private final IconType type;
	private final ItemStack stack;
	private final Identifier res;

	public static BookIcon from(JsonElement json) {
		if (json.isJsonPrimitive() && json.getAsString().endsWith(".png")) {
			return new BookIcon(new Identifier(json.getAsString()));
		} else {
			try {
				return new BookIcon(ItemStackUtil.loadStackFromJson(json));
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

	public BookIcon(Identifier res) {
		type = IconType.RESOURCE;
		stack = null;
		this.res = res;
	}

	public void render(MatrixStack ms, int x, int y) {
		MinecraftClient mc = MinecraftClient.getInstance();
		switch (type) {
		case STACK:
			RenderHelper.renderItemStackInGui(ms, stack, x, y);
			break;

		case RESOURCE:
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
			RenderSystem.setShaderTexture(0, res);
			DrawableHelper.drawTexture(ms, x, y, 0, 0, 16, 16, 16, 16);
			break;
		}
	}

	private enum IconType {
		STACK, RESOURCE
	}

}
