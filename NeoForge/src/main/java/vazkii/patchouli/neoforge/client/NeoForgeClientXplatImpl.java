package vazkii.patchouli.neoforge.client;

import com.mojang.blaze3d.vertex.*;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.patchouli.xplat.IClientXplatAbstractions;

public class NeoForgeClientXplatImpl implements IClientXplatAbstractions {
	@Override
	public void renderForMultiblock(BlockState state, BlockPos pos, BlockAndTintGetter multiblock, PoseStack ps, MultiBufferSource buffers, RandomSource rand) {
		/*final BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
		if (state.getRenderShape() != RenderShape.INVISIBLE) {
			final BakedModel model = blockRenderer.getBlockModel(state);
			for (RenderType layer : model.getRenderTypes(state, rand, ModelData.EMPTY)) {
				final VertexConsumer buffer = buffers.getBuffer(layer);
				blockRenderer.renderBatched(state, pos, multiblock, ps, buffer, false, rand, ModelData.EMPTY, layer);
			}
		}*/
	}

	@Override
	public void submitGuiElement(GuiGraphics graphics, GuiElementRenderState renderState) {
		graphics.submitGuiElementRenderState(renderState);
	}
}
