package vazkii.patchouli.fabric.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

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

public class FabricClientXplatImpl implements IClientXplatAbstractions {
	@Override
	public void renderForMultiblock(BlockState state, BlockPos pos, BlockAndLightGetter multiblock, PoseStack poseStack, Function<ChunkSectionLayer, VertexConsumer> bufferLookup, RandomSource rand) {
		//blockRenderer.renderBatched(state, pos, multiblock, poseStack, bufferLookup.apply(ItemBlockRenderTypes.getChunkRenderType(state)), false, blockRenderer.getBlockModel(state).collectParts(rand));
	}

	@Override
	public void submitGuiElement(GuiGraphicsExtractor graphics, GuiElementRenderState renderState) {
		graphics.guiRenderState.addGuiElement(renderState);
	}

	@Override
	public void submitPiPRenderState(GuiGraphicsExtractor graphics, Function<@Nullable ScreenRectangle, PictureInPictureRenderState> factory) {
		graphics.guiRenderState.addPicturesInPictureState(factory.apply(graphics.scissorStack.peek()));
	}

	@Override
	public void submitPiPRenderState(GuiGraphicsExtractor graphics, PictureInPictureRenderState renderState) {
		graphics.guiRenderState.addPicturesInPictureState(renderState);
	}
}
