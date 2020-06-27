package vazkii.patchouli.api.stub;

import com.mojang.datafixers.util.Pair;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

import vazkii.patchouli.api.IMultiblock;

import java.util.Collection;
import java.util.Collections;

public class StubMultiblock implements IMultiblock {

	public static final StubMultiblock INSTANCE = new StubMultiblock();

	private StubMultiblock() {}

	@Override
	public IMultiblock offset(int x, int y, int z) {
		return this;
	}

	@Override
	public IMultiblock offsetView(int x, int y, int z) {
		return this;
	}

	@Override
	public IMultiblock setSymmetrical(boolean symmetrical) {
		return this;
	}

	@Override
	public ResourceLocation getID() {
		return new ResourceLocation("patchouli", "stub");
	}

	@Override
	public IMultiblock setId(ResourceLocation res) {
		return this;
	}

	@Override
	public boolean isSymmetrical() {
		return false;
	}

	@Override
	public void place(World world, BlockPos pos, Rotation rotation) {
		// NO-OP
	}

	@Override
	public Pair<BlockPos, Collection<SimulateResult>> simulate(World world, BlockPos anchor, Rotation rotation, boolean forView) {
		return Pair.of(BlockPos.ZERO, Collections.emptyList());
	}

	@Override
	public Rotation validate(World world, BlockPos pos) {
		return null;
	}

	@Override
	public boolean validate(World world, BlockPos pos, Rotation rotation) {
		return false;
	}

	@Override
	public boolean test(World world, BlockPos start, int x, int y, int z, Rotation rotation) {
		return false;
	}

	@Override
	public Vector3i getSize() {
		return Vector3i.NULL_VECTOR;
	}

}
