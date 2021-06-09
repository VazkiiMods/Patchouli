package vazkii.patchouli.common.util;

import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

public final class RotationUtil {

	private RotationUtil() {}

	public static BlockRotation rotationFromFacing(Direction facing) {
		return switch (facing) {
		case EAST -> BlockRotation.CLOCKWISE_90;
		case SOUTH -> BlockRotation.CLOCKWISE_180;
		case WEST -> BlockRotation.COUNTERCLOCKWISE_90;
		default -> BlockRotation.NONE;
		};
	}

	// TODO figure out why this is needed and document it.
	public static BlockRotation fixHorizontal(BlockRotation rot) {
		return switch (rot) {
		case CLOCKWISE_90 -> BlockRotation.COUNTERCLOCKWISE_90;
		case COUNTERCLOCKWISE_90 -> BlockRotation.CLOCKWISE_90;
		default -> rot;
		};
	}
}
