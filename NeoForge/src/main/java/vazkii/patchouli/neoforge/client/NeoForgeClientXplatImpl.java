package vazkii.patchouli.neoforge.client;

import com.mojang.blaze3d.vertex.*;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.state.BlockState;

import org.joml.Matrix3x2f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import vazkii.patchouli.client.multiblock.MultiblockPiPRenderState;
import vazkii.patchouli.common.multiblock.AbstractMultiblock;
import vazkii.patchouli.xplat.IClientXplatAbstractions;

import java.util.function.Function;

public class NeoForgeClientXplatImpl implements IClientXplatAbstractions {
	public void renderForMultiblock(BlockState state, BlockPos pos, BlockAndLightGetter multiblock, PoseStack poseStack, Function<ChunkSectionLayer, VertexConsumer> bufferLookup, RandomSource rand) {
		blockRenderer.renderBatched(state, pos, multiblock, poseStack, bufferLookup, true, blockRenderer.getBlockModel(state).collectParts(multiblock, pos, state, rand));
	}

	@Override
	public void submitGuiElement(GuiGraphicsExtractor graphics, GuiElementRenderState renderState) {
		graphics.submitGuiElementRenderState(renderState);
	}

	@Override
	public void submitMultiblockPiP(GuiGraphicsExtractor graphics, AbstractMultiblock multiblock, float scale, Vector3f translation, Quaternionf rotation, int x0, int y0, int x1, int y1) {
		graphics.submitPictureInPictureRenderState(new MultiblockPiPRenderState(multiblock, translation, rotation, x0, y0, x1, y1, scale, new Matrix3x2f(graphics.pose()), graphics.peekScissorStack()));
	}
}
