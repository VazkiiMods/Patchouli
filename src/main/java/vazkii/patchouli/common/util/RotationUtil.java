package vazkii.patchouli.common.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class RotationUtil {

	public static int x(Rotation rot, int x, int z) {
		switch (rot) {
		case NONE:
			return x;
		case CLOCKWISE_180:
			return -x;
		case CLOCKWISE_90:
			return z;
		default:
			return -z;
		}
	}

	public static int z(Rotation rot, int x, int z) {
		switch (rot) {
		case NONE:
			return z;
		case CLOCKWISE_180:
			return -z;
		case CLOCKWISE_90:
			return x;
		default:
			return -x;
		}
	}

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

	public static BlockState rotateState(BlockState state, IWorld world, BlockPos pos, Rotation rotation) {
		return state.getBlock().rotate(state, world, pos, rotation);
	}

}
