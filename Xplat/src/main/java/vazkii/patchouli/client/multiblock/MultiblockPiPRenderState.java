package vazkii.patchouli.client.multiblock;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import vazkii.patchouli.common.multiblock.AbstractMultiblock;

public record MultiblockPiPRenderState(
		AbstractMultiblock multiblock,
		Vector3f translation,
		Quaternionf rotation,
		int x0,
		int y0,
		int x1,
		int y1,
		float scale,
		Matrix3x2f pose,
		@Nullable ScreenRectangle scissorArea,
		@Nullable ScreenRectangle bounds
) implements PictureInPictureRenderState {
	public MultiblockPiPRenderState(
			AbstractMultiblock multiblock,
			Vector3f translation,
			Quaternionf rotation,
			int x0,
			int y0,
			int x1,
			int y1,
			float scale,
			Matrix3x2f pose,
			@Nullable ScreenRectangle scissorArea
	) {
		this(
			multiblock,
			translation,
			rotation,
			x0,
			y0,
			x1,
			y1,
			scale,
			pose,
			scissorArea,
			PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea)
		);
	}
}
