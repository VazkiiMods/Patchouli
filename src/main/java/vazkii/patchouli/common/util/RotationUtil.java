package vazkii.patchouli.common.util;

import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;

public class RotationUtil {

	public static Rotation rotationFromFacing(Direction facing) {
		switch (facing) {
		case EAST:
			return Rotation.CLOCKWISE_90;
		case SOUTH:
			return Rotation.CLOCKWISE_180;
		case WEST:
			return Rotation.COUNTERCLOCKWISE_90;
		default:
			return Rotation.NONE;
		}
	}

}
