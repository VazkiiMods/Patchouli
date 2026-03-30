package vazkii.patchouli.client.multiblock;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.core.BlockPos;

import org.joml.*;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public record MultiblockPiPRenderState(
		int x0,
		int y0,
		int x1,
		int y1,
		float scale,
		@Nullable ScreenRectangle scissorArea,
		@Nullable ScreenRectangle bounds,
		Matrix4f viewMatrix,
		List<BlockRenderState> multiblock) implements PictureInPictureRenderState {

	public MultiblockPiPRenderState(
			int x0,
			int y0,
			int x1,
			int y1,
			float scale,
			@Nullable ScreenRectangle scissorArea,
			Matrix4f viewMatrix,
			List<BlockRenderState> multiblock) {
		this(
				x0,
				y0,
				x1,
				y1,
				scale,
				scissorArea,
				PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea),
				viewMatrix,
				multiblock
		);
	}

	public record BlockRenderState(BlockPos pos, BlockModelRenderState blockModelRenderState) {
	}
}
