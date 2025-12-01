package vazkii.patchouli.fabric.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.patchouli.xplat.IClientXplatAbstractions;

public class FabricClientXplatImpl implements IClientXplatAbstractions {
	@Override
	public void renderForMultiblock(BlockState state, BlockPos pos, BlockAndTintGetter multiblock, PoseStack ps, MultiBufferSource buffers, RandomSource rand) {
		/*final BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
		if (state.getRenderShape() != RenderShape.INVISIBLE) {
			final RenderType layer = ItemBlockRenderTypes.getChunkRenderType(state);
			final VertexConsumer buffer = buffers.getBuffer(layer);
			blockRenderer.renderBatched(state, pos, multiblock, ps, buffer, false, rand);
		}*/
	}

	@Override
	public void submitGuiElement(GuiGraphics graphics, GuiElementRenderState renderState) {
		graphics.guiRenderState.submitGuiElement(renderState);
	}
}
