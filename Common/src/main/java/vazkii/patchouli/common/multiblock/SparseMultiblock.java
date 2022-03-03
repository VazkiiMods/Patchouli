package vazkii.patchouli.common.multiblock;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.common.util.RotationUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SparseMultiblock extends AbstractMultiblock {
	private final Map<BlockPos, IStateMatcher> data;
	private final Vec3i size;

	public SparseMultiblock(Map<BlockPos, IStateMatcher> data) {
		Preconditions.checkArgument(!data.isEmpty(), "No data given to sparse multiblock!");
		this.data = ImmutableMap.copyOf(data);
		this.size = calculateSize();
	}

	@Override
	public Vec3i getSize() {
		return size;
	}

	private Vec3i calculateSize() {
		int minX = data.keySet().stream().mapToInt(BlockPos::getX).min().getAsInt();
		int maxX = data.keySet().stream().mapToInt(BlockPos::getX).max().getAsInt();
		int minY = data.keySet().stream().mapToInt(BlockPos::getY).min().getAsInt();
		int maxY = data.keySet().stream().mapToInt(BlockPos::getY).max().getAsInt();
		int minZ = data.keySet().stream().mapToInt(BlockPos::getZ).min().getAsInt();
		int maxZ = data.keySet().stream().mapToInt(BlockPos::getZ).max().getAsInt();
		return new Vec3i(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		long ticks = world != null ? world.getGameTime() : 0L;
		return data.getOrDefault(pos, StateMatcher.AIR).getDisplayedState(ticks);
	}

	@Override
	public Pair<BlockPos, Collection<SimulateResult>> simulate(Level world, BlockPos anchor, Rotation rotation, boolean forView) {
		BlockPos disp = forView
				? new BlockPos(-viewOffX, -viewOffY + 1, -viewOffZ).rotate(rotation)
				: new BlockPos(-offX, -offY, -offZ).rotate(rotation);
		// the local origin of this multiblock, in world coordinates
		BlockPos origin = anchor.offset(disp);
		List<SimulateResult> ret = new ArrayList<>();
		for (Entry<BlockPos, IStateMatcher> e : data.entrySet()) {
			BlockPos currDisp = e.getKey().rotate(rotation);
			BlockPos actionPos = origin.offset(currDisp);
			ret.add(new SimulateResultImpl(actionPos, e.getValue(), null));
		}
		return Pair.of(origin, ret);
	}

	@Override
	public boolean test(Level world, BlockPos start, int x, int y, int z, Rotation rotation) {
		setWorld(world);
		BlockPos checkPos = start.offset(new BlockPos(x, y, z).rotate(rotation));
		BlockState state = world.getBlockState(checkPos).rotate(RotationUtil.fixHorizontal(rotation));
		IStateMatcher matcher = data.getOrDefault(new BlockPos(x, y, z), StateMatcher.ANY);
		return matcher.getStatePredicate().test(world, checkPos, state);
	}

	// These heights were assumed based being derivative of old behavior, but it may be ideal to change
	@Override
	public int getHeight() {
		return 255;
	}

	@Override
	public int getMinBuildHeight() {
		return 0;
	}
}
