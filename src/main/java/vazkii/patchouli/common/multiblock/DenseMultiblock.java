package vazkii.patchouli.common.multiblock;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.TriPredicate;
import vazkii.patchouli.common.util.RotationUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DenseMultiblock extends AbstractMultiblock {

	private final String[][] pattern;
	private IStateMatcher[][][] stateTargets;
	private final Vec3i size;

	public DenseMultiblock(String[][] pattern, Object... targets) {
		this.pattern = pattern;
		this.size = build(targets, getPatternDimensions());
	}

	@Override
	public Pair<BlockPos, Collection<SimulateResult>> simulate(World world, BlockPos anchor, BlockRotation rotation, boolean forView) {
		BlockPos center = forView
				? anchor.add(RotationUtil.x(rotation, -viewOffX, -viewOffZ), -viewOffY + 1, RotationUtil.z(rotation, -viewOffX, -viewOffZ))
				: anchor.add(RotationUtil.x(rotation, -offX, -offZ), -offY, RotationUtil.z(rotation, -offX, -offZ));
		List<SimulateResult> ret = new ArrayList<>();
		for(int x = 0; x < size.getX(); x++)
			for(int y = 0; y < size.getY(); y++)
				for(int z = 0; z < size.getZ(); z++) {
					BlockPos actionPos = center.add(RotationUtil.x(rotation, x, z), y, RotationUtil.z(rotation, x, z));
					char currC = pattern[y][x].charAt(z);
					ret.add(new SimulateResultImpl(actionPos, stateTargets[x][y][z], currC));
				}
		return Pair.of(center, ret);
	}

	@Override
	public boolean test(World world, BlockPos start, int x, int y, int z, BlockRotation rotation) {
		setWorld(world);
		if (x < 0 || y < 0 || z < 0 || x >= size.getX() || y >= size.getY() || z >= size.getZ()) {
			return false;
		}
		BlockPos checkPos = start.add(RotationUtil.x(rotation, x, z), y, RotationUtil.z(rotation, x, z));
		TriPredicate<BlockView, BlockPos, BlockState> pred = stateTargets[x][y][z].getStatePredicate();
		BlockState state = world.getBlockState(checkPos).rotate(RotationUtil.fixHorizontal(rotation));

		return pred.test(world, checkPos, state);
	}

	private Vec3i build(Object[] targets, int[] dimensions) {
		if(targets.length % 2 == 1)
			throw new IllegalArgumentException("Illegal argument length for targets array " + targets.length);

		Map<Character, IStateMatcher> stateMap = new HashMap<>();
		for(int i = 0; i < targets.length / 2; i++) {
			char c = (Character) targets[i * 2];
			Object o = targets[i * 2 + 1];
			IStateMatcher state;

			if(o instanceof Block)
				state = StateMatcher.fromBlockLoose((Block) o);
			else if(o instanceof BlockState)
				state = StateMatcher.fromState((BlockState) o);
			else if(o instanceof String)
				try {
					state = StringStateMatcher.fromString((String) o);
				} catch (CommandSyntaxException e) {
					throw new RuntimeException(e);
				}
			else if(o instanceof IStateMatcher)
				state = (IStateMatcher) o;
			else throw new IllegalArgumentException("Invalid target " + o);

			stateMap.put(c, state);
		}

		if(!stateMap.containsKey('_'))
			stateMap.put('_', StateMatcher.ANY);
		if(!stateMap.containsKey(' '))
			stateMap.put(' ', StateMatcher.AIR);
		if(!stateMap.containsKey('0'))
			stateMap.put('0', StateMatcher.AIR);

		boolean foundCenter = false;

		stateTargets = new IStateMatcher[dimensions[1]][dimensions[0]][dimensions[2]];
		for(int y = 0; y < dimensions[0]; y++)
			for(int x = 0; x < dimensions[1]; x++)
				for(int z = 0; z < dimensions[2]; z++) {
					char c = pattern[y][x].charAt(z);
					if(!stateMap.containsKey(c))
						throw new IllegalArgumentException("Character " + c + " isn't mapped");

					IStateMatcher matcher = stateMap.get(c);
					if(c == '0') {
						if(foundCenter)
							throw new IllegalArgumentException("A structure can't have two centers");
						foundCenter = true;
						offX = x;
						offY = dimensions[0] - y - 1;
						offZ = z;
						setViewOffset();
					}

					stateTargets[x][dimensions[0] - y - 1][z] = matcher;
				}

		if(!foundCenter)
			throw new IllegalArgumentException("A structure can't have no center");
		return new Vec3i(dimensions[1], dimensions[0], dimensions[2]);
	}

	private int[] getPatternDimensions() {
		int expectedLenX = -1;
		int expectedLenZ = -1;
		for(String[] arr : pattern) {
			if(expectedLenX == -1)
				expectedLenX = arr.length;
			if(arr.length != expectedLenX)
				throw new IllegalArgumentException("Inconsistent array length. Expected" + expectedLenX + ", got " + arr.length);

			for(String s : arr) {
				if(expectedLenZ == -1)
					expectedLenZ = s.length();
				if(s.length() != expectedLenZ)
					throw new IllegalArgumentException("Inconsistent array length. Expected" + expectedLenX + ", got " + arr.length);
			}
		}

		return new int[] { pattern.length, expectedLenX, expectedLenZ };
	}

    @Override
    public BlockState getBlockState(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (x < 0 || y < 0 || z < 0 || x >= size.getX() || y >= size.getY() || z >= size.getZ()) {
            return Blocks.AIR.getDefaultState();
        }
        int ticks = world != null ? (int) world.getTimeOfDay() : 0;
        return stateTargets[x][y][z].getDisplayedState(ticks);
    }

	@Override
	public Vec3i getSize() {
		return size;
	}
}
