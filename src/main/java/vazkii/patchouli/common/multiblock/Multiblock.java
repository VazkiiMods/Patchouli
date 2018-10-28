package vazkii.patchouli.common.multiblock;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.patchouli.common.util.RotationUtil;

public class Multiblock {

	final String[][] pattern;

	public ResourceLocation res;
	public StateMatcher[][][] stateTargets;
	public int sizeX, sizeY, sizeZ;
	public int offX, offY, offZ;
	public int viewOffX, viewOffY, viewOffZ;
	int centerX, centerY, centerZ;
	boolean symmetrical;

	public Multiblock(String[][] pattern, Object... targets) {
		this.pattern = pattern;
		build(targets, getPatternDimensions());
	}

	public Multiblock offset(int x, int y, int z) {
		return setOffset(offX + x, offY + y, offZ + z);
	}

	public Multiblock setOffset(int x, int y, int z) {
		offX = x;
		offY = y;
		offZ = z;
		return setViewOffset(x, y, z);
	}

	void setViewOffset() {
		setViewOffset(offX, offY, offZ);
	}

	public Multiblock offsetView(int x, int y, int z) {
		return setViewOffset(viewOffX + x, viewOffY + y, viewOffZ + z);
	}

	public Multiblock setViewOffset(int x, int y, int z) {
		viewOffX = x;
		viewOffY = y;
		viewOffZ = z;
		return this;
	}

	public Multiblock setSymmetrical(boolean symmetrical) {
		this.symmetrical = symmetrical;
		return this;
	}

	public Multiblock setResourceName(ResourceLocation res) {
		this.res = res;
		return this;
	}

	public void place(World world, BlockPos pos, Rotation rotation) {
		BlockPos start = pos.add(RotationUtil.x(rotation, -offX, -offZ), -offY, RotationUtil.z(rotation, -offX, -offZ));
		for(int x = 0; x < sizeX; x++)
			for(int y = 0; y < sizeY; y++)
				for(int z = 0; z < sizeZ; z++) {
					BlockPos placePos = start.add(RotationUtil.x(rotation, x, z), y, RotationUtil.z(rotation, x, z));
					IBlockState targetState = stateTargets[x][y][z].displayState.withRotation(rotation);
					Block targetBlock = targetState.getBlock();
					if(!targetBlock.isAir(targetState, world, placePos) && targetBlock.canPlaceBlockAt(world, placePos) && world.getBlockState(placePos).getBlock().isReplaceable(world, placePos))
						world.setBlockState(placePos, targetState);
				}
	}

	public void forEach(World world, BlockPos pos, Rotation rotation, char c, Consumer<BlockPos> action) {
		forEachMatcher(world, pos, rotation, c, (start, actionPos, x, y, z, ch, matcher) -> {
		    action.accept(actionPos);
		    return true;
		});
	}

	public boolean forEachMatcher(World world, BlockPos pos, Rotation rotation, char c, MatcherAcceptor acceptor){
		BlockPos start = pos.add(RotationUtil.x(rotation, -offX, -offZ), -offY, RotationUtil.z(rotation, -offX, -offZ));
		for(int x = 0; x < sizeX; x++)
			for(int y = 0; y < sizeY; y++)
				for(int z = 0; z < sizeZ; z++) {
					if(c == 0 || c == pattern[y][x].charAt(z)) {
						BlockPos actionPos = start.add(RotationUtil.x(rotation, x, z), y, RotationUtil.z(rotation, x, z));
						if(!acceptor.accepts(start, actionPos, x, y, z, c, this.stateTargets[x][y][z])){
							return false;
						}
					}
				}
        return true;
	}

	public boolean validate(World world, BlockPos pos) {
		if(symmetrical)
			return validate(world, pos, Rotation.NONE);

		else return validate(world, pos, Rotation.NONE)
			|| validate(world, pos, Rotation.CLOCKWISE_90)
			|| validate(world, pos, Rotation.CLOCKWISE_180)
			|| validate(world, pos, Rotation.COUNTERCLOCKWISE_90);
	}


	public boolean validate(World world, BlockPos pos, Rotation rotation) {
		BlockPos start = pos.add(RotationUtil.x(rotation, -offX, -offZ), -offY, RotationUtil.z(rotation, -offX, -offZ));
		if(!test(world, start, centerX, centerY, centerZ, rotation))
			return false;

		for(int x = 0; x < sizeX; x++)
			for(int y = 0; y < sizeY; y++)
				for(int z = 0; z < sizeZ; z++)
					if(!test(world, start, x, y, z, rotation))
						return false;

		return true;
	}

	public boolean test(World world, BlockPos start, int x, int y, int z, Rotation rotation) {
		BlockPos checkPos = start.add(RotationUtil.x(rotation, x, z), y, RotationUtil.z(rotation, x, z));
		Predicate<IBlockState> pred = stateTargets[x][y][z].statePredicate;
		IBlockState state = world.getBlockState(checkPos).withRotation(RotationUtil.fixHorizontal(rotation));

		return pred.test(state);
	}

	void build(Object[] targets, int[] dimensions) {
		if(targets.length % 2 == 1)
			throw new IllegalArgumentException("Illegal argument length for targets array " + targets.length);

		Map<Character, StateMatcher> stateMap = new HashMap();
		for(int i = 0; i < targets.length / 2; i++) {
			char c = (Character) targets[i * 2];
			Object o = targets[i * 2 + 1];
			StateMatcher state = null;

			if(o instanceof Block)
				state = StateMatcher.fromBlockLoose((Block) o);
			else if(o instanceof IBlockState)
				state = StateMatcher.fromState((IBlockState) o);
			else if(o instanceof StateMatcher)
				state = (StateMatcher) o;
			else throw new IllegalArgumentException("Invalid target " + o);

			stateMap.put(c, state);
		}

		if(!stateMap.containsKey(' '))
			stateMap.put(' ', StateMatcher.AIR);
		if(!stateMap.containsKey('0'))
			stateMap.put('0', StateMatcher.AIR);

		boolean foundCenter = false;

		sizeX = dimensions[1];
		sizeY = dimensions[0];
		sizeZ = dimensions[2];
		stateTargets = new StateMatcher[dimensions[1]][dimensions[0]][dimensions[2]];
		for(int y = 0; y < dimensions[0]; y++)
			for(int x = 0; x < dimensions[1]; x++)
				for(int z = 0; z < dimensions[2]; z++) {
					char c = pattern[y][x].charAt(z);
					if(!stateMap.containsKey(c))
						throw new IllegalArgumentException("Character " + c + " isn't mapped");

					StateMatcher matcher = stateMap.get(c);
					if(c == '0') {
						if(foundCenter)
							throw new IllegalArgumentException("A structure can't have two centers");
						foundCenter = true;
						offX = centerX = x;
						offY = centerY = sizeY - y - 1;
						offZ = centerZ = z;
						setViewOffset();
					}

					stateTargets[x][sizeY - y - 1][z] = matcher;
				}

		if(!foundCenter)
			throw new IllegalArgumentException("A structure can't have no center");
	}

	int[] getPatternDimensions() {
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

	public boolean isSymmetrical() {
		return symmetrical;
	}

	public static class StateMatcher {

		public static final StateMatcher ANY = displayOnly(Blocks.AIR.getDefaultState());
		public static final StateMatcher AIR = fromState(Blocks.AIR.getDefaultState());

		public final IBlockState displayState;
		public final Predicate<IBlockState> statePredicate;

		private StateMatcher(IBlockState displayState, Predicate<IBlockState> statePredicate) {
			this.displayState = displayState;
			this.statePredicate = statePredicate;
		}

		public static StateMatcher fromPredicate(IBlockState display, Predicate<IBlockState> predicate) {
			return new StateMatcher(display, predicate);
		}


		public static StateMatcher fromPredicate(Block display, Predicate<IBlockState> predicate) {
			return fromPredicate(display.getDefaultState(), predicate);
		}

		public static StateMatcher fromState(IBlockState displayState, boolean strict) {
			return new StateMatcher(displayState,
					strict ? ((state) -> state.getBlock() == displayState.getBlock() && state.getProperties().equals(displayState.getProperties()))
							: ((state) -> state.getBlock() == displayState.getBlock()));
		}

		public static StateMatcher fromState(IBlockState displayState) {
			return fromState(displayState, true);
		}

		public static StateMatcher fromBlockLoose(Block block) {
			return fromState(block.getDefaultState(), false);
		}

		public static StateMatcher fromBlockStrict(Block block) {
			return fromState(block.getDefaultState(), true);
		}

		public static StateMatcher displayOnly(IBlockState state) {
			return new StateMatcher(state, (s) -> true);
		}

		public static StateMatcher displayOnly(Block block) {
			return displayOnly(block.getDefaultState());
		}

	}

	public interface MatcherAcceptor {
		boolean accepts(BlockPos start, BlockPos actionPos, int x, int y, int z, char c, StateMatcher matcher);
	}
}
