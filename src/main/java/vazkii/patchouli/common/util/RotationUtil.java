package vazkii.patchouli.common.util;

import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

public class RotationUtil {

	public static BlockRotation rotationFromFacing(Direction facing) {
		switch (facing) {
		case EAST:
			return BlockRotation.CLOCKWISE_90;
		case SOUTH:
			return BlockRotation.CLOCKWISE_180;
		case WEST:
			return BlockRotation.COUNTERCLOCKWISE_90;
		default:
			return BlockRotation.NONE;
		}
	}

	// TODO figure out why this is needed and document it.
	public static BlockRotation fixHorizontal(BlockRotation rot) {
		switch (rot) {
		case CLOCKWISE_90:
			return BlockRotation.COUNTERCLOCKWISE_90;
		case COUNTERCLOCKWISE_90:
			return BlockRotation.CLOCKWISE_90;
		default:
			return rot;
		}
	}

}
