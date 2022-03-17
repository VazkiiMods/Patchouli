package vazkii.patchouli.forge.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ForgeHooksClient;

import vazkii.patchouli.xplat.IClientXplatAbstractions;

import java.util.Random;

public class ForgeClientXplatImpl implements IClientXplatAbstractions {
	@Override
	public void renderForMultiblock(BlockState state, BlockPos pos, BlockAndTintGetter multiblock, PoseStack ps, MultiBufferSource buffers, Random rand) {
		for (var layer : RenderType.chunkBufferLayers()) {
			if (ItemBlockRenderTypes.canRenderInLayer(state, layer)) {
				ForgeHooksClient.setRenderType(layer);
				var buffer = buffers.getBuffer(layer);
				Minecraft.getInstance().getBlockRenderer()
						.renderBatched(state, pos, multiblock, ps, buffer, false, rand);
				ForgeHooksClient.setRenderType(null);
			}
		}
	}
}
