package vazkii.patchouli.common.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;

public final class RotationUtil {

	private RotationUtil() {}

	public static Rotation rotationFromFacing(Direction facing) {
		return switch (facing) {
		case EAST -> Rotation.CLOCKWISE_90;
		case SOUTH -> Rotation.CLOCKWISE_180;
		case WEST -> Rotation.COUNTERCLOCKWISE_90;
		default -> Rotation.NONE;
		};
	}

	// TODO figure out why this is needed and document it.
	public static Rotation fixHorizontal(Rotation rot) {
		return switch (rot) {
		case CLOCKWISE_90 -> Rotation.COUNTERCLOCKWISE_90;
		case COUNTERCLOCKWISE_90 -> Rotation.CLOCKWISE_90;
		default -> rot;
		};
	}
}
