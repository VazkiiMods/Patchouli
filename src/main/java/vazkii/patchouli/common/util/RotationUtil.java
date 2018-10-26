package vazkii.patchouli.common.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;

public class RotationUtil {

	public static int x(Rotation rot, int x, int z) {
		switch(rot) {
		case NONE: return x;
		case CLOCKWISE_180: return -x;
		case CLOCKWISE_90: return z;
		default: return -z;
		}
	}
	
	public static int z(Rotation rot, int x, int z) {
		switch(rot) {
		case NONE: return z;
		case CLOCKWISE_180: return -z;
		case CLOCKWISE_90: return x;
		default: return -x;
		}
	}
	
	public static Rotation rotationFromFacing(EnumFacing facing) {
		switch(facing) {
		case EAST: return Rotation.CLOCKWISE_90;
		case SOUTH: return Rotation.CLOCKWISE_180;
		case WEST: return Rotation.COUNTERCLOCKWISE_90;
		default: return Rotation.NONE;
		}
	}
	
	public static Rotation fixHorizontal(Rotation rot) {
		switch(rot) {
		case CLOCKWISE_90: return Rotation.COUNTERCLOCKWISE_90;
		case COUNTERCLOCKWISE_90: return Rotation.CLOCKWISE_90;
		default: return rot;
		}
	}

}
