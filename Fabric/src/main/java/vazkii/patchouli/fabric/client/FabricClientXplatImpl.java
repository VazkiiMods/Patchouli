package vazkii.patchouli.fabric.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

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

import vazkii.patchouli.client.book.LiquidBlockVertexConsumer;
import vazkii.patchouli.xplat.IClientXplatAbstractions;

public class FabricClientXplatImpl implements IClientXplatAbstractions {
	@Override
	public void renderForMultiblock(BlockState state, BlockPos pos, BlockAndTintGetter multiblock, PoseStack ps, MultiBufferSource buffers, RandomSource rand) {
		final BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
		final FluidState fluidState = state.getFluidState();
		if (!fluidState.isEmpty()) {
			final RenderType layer = ItemBlockRenderTypes.getRenderLayer(fluidState);
			final VertexConsumer buffer = buffers.getBuffer(layer);
			blockRenderer.renderLiquid(pos, multiblock, new LiquidBlockVertexConsumer(buffer, ps, pos), state, fluidState);
		}
		if (state.getRenderShape() != RenderShape.INVISIBLE) {
			final RenderType layer = ItemBlockRenderTypes.getChunkRenderType(state);
			final VertexConsumer buffer = buffers.getBuffer(layer);
			blockRenderer.renderBatched(state, pos, multiblock, ps, buffer, false, rand);
		}
	}
}
