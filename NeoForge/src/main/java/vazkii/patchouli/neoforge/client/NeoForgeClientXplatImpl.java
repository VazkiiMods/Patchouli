package vazkii.patchouli.neoforge.client;

import com.mojang.blaze3d.vertex.*;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.state.BlockState;

import org.jspecify.annotations.Nullable;

import vazkii.patchouli.xplat.IClientXplatAbstractions;

import java.util.function.Function;

public class NeoForgeClientXplatImpl implements IClientXplatAbstractions {
	public void renderForMultiblock(BlockState state, BlockPos pos, BlockAndLightGetter multiblock, PoseStack poseStack, Function<ChunkSectionLayer, VertexConsumer> bufferLookup, RandomSource rand) {
		//blockRenderer.renderBatched(state, pos, multiblock, poseStack, bufferLookup, true, blockRenderer.getBlockModel(state).collectParts(multiblock, pos, state, rand));
	}

	@Override
	public void submitGuiElement(GuiGraphicsExtractor graphics, GuiElementRenderState renderState) {
		graphics.submitGuiElementRenderState(renderState);
	}

	@Override
	public void submitPiPRenderState(GuiGraphicsExtractor graphics, Function<@Nullable ScreenRectangle, PictureInPictureRenderState> factory) {
		graphics.submitPictureInPictureRenderState(factory.apply(graphics.peekScissorStack()));
	}

	@Override
	public void submitPiPRenderState(GuiGraphicsExtractor graphics, PictureInPictureRenderState renderState) {
		graphics.submitPictureInPictureRenderState(renderState);
	}
}
