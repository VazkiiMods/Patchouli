package vazkii.patchouli.forge.client;

import com.mojang.blaze3d.vertex.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;

import vazkii.patchouli.client.book.LiquidBlockVertexConsumer;
import vazkii.patchouli.xplat.IClientXplatAbstractions;

public class ForgeClientXplatImpl implements IClientXplatAbstractions {
	@Override
	public void renderForMultiblock(BlockState state, BlockPos pos, BlockAndTintGetter multiblock, PoseStack ps, MultiBufferSource buffers, RandomSource rand) {
		final BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
		final FluidState fluidState = state.getFluidState();
		for (RenderType layer : RenderType.chunkBufferLayers()) {
			ForgeHooksClient.setRenderType(layer);
			final VertexConsumer buffer = buffers.getBuffer(layer);
			if (!fluidState.isEmpty() && ItemBlockRenderTypes.canRenderInLayer(fluidState, layer)) {
				blockRenderer.renderLiquid(pos, multiblock, new LiquidBlockVertexConsumer(buffer, ps, pos), state, fluidState);
			}
			if (state.getRenderShape() != RenderShape.INVISIBLE && ItemBlockRenderTypes.canRenderInLayer(state, layer)) {
				blockRenderer.renderBatched(state, pos, multiblock, ps, buffer, false, rand, EmptyModelData.INSTANCE);
			}
			ForgeHooksClient.setRenderType(null);
		}
	}
}
