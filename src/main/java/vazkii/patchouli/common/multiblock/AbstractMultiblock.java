package vazkii.patchouli.common.multiblock;

import com.mojang.datafixers.util.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.level.ColorResolver;

import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.TriPredicate;
import vazkii.patchouli.common.util.RotationUtil;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMultiblock implements IMultiblock, BlockRenderView {
	public Identifier id;
	protected int offX, offY, offZ;
	protected int viewOffX, viewOffY, viewOffZ;
	private boolean symmetrical;
	World world;

	private final transient Map<BlockPos, BlockEntity> teCache = new HashMap<>();

	@Override
	public IMultiblock offset(int x, int y, int z) {
		return setOffset(offX + x, offY + y, offZ + z);
	}

	public IMultiblock setOffset(int x, int y, int z) {
		offX = x;
		offY = y;
		offZ = z;
		return setViewOffset(x, y, z);
	}

	void setViewOffset() {
		setViewOffset(offX, offY, offZ);
	}

	@Override
	public IMultiblock offsetView(int x, int y, int z) {
		return setViewOffset(viewOffX + x, viewOffY + y, viewOffZ + z);
	}

	public IMultiblock setViewOffset(int x, int y, int z) {
		viewOffX = x;
		viewOffY = y;
		viewOffZ = z;
		return this;
	}

	@Override
	public IMultiblock setSymmetrical(boolean symmetrical) {
		this.symmetrical = symmetrical;
		return this;
	}

	@Override
	public Identifier getID() {
		return id;
	}

	@Override
	public IMultiblock setId(Identifier res) {
		this.id = res;
		return this;
	}

	@Override
	public void place(World world, BlockPos pos, BlockRotation rotation) {
		setWorld(world);
		simulate(world, pos, rotation, false).getSecond().forEach(r -> {
			BlockPos placePos = r.getWorldPosition();
			BlockState targetState = r.getStateMatcher().getDisplayedState((int) world.getTimeOfDay()).rotate(rotation);

			if (!targetState.isAir() && targetState.canPlaceAt(world, placePos) && world.getBlockState(placePos).getMaterial().isReplaceable()) {
				world.setBlockState(placePos, targetState);
			}
		});
	}

	@Override
	public BlockRotation validate(World world, BlockPos pos) {
		if (isSymmetrical() && validate(world, pos, BlockRotation.NONE)) {
			return BlockRotation.NONE;
		} else {
			for (BlockRotation rot : BlockRotation.values()) {
				if (validate(world, pos, rot)) {
					return rot;
				}
			}
		}
		return null;
	}

	@Override
	public boolean validate(World world, BlockPos pos, BlockRotation rotation) {
		setWorld(world);
		Pair<BlockPos, Collection<SimulateResult>> sim = simulate(world, pos, rotation, false);

		return sim.getSecond().stream().allMatch(r -> {
			BlockPos checkPos = r.getWorldPosition();
			TriPredicate<BlockView, BlockPos, BlockState> pred = r.getStateMatcher().getStatePredicate();
			BlockState state = world.getBlockState(checkPos).rotate(RotationUtil.fixHorizontal(rotation));

			return pred.test(world, checkPos, state);
		});
	}

	@Override
	public boolean isSymmetrical() {
		return symmetrical;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	@Override
	@Nullable
	public BlockEntity getBlockEntity(BlockPos pos) {
		BlockState state = getBlockState(pos);
		if (state.getBlock().hasBlockEntity()) {
			return teCache.computeIfAbsent(pos.toImmutable(), p -> ((BlockEntityProvider) state.getBlock()).createBlockEntity(world));
		}
		return null;
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return Fluids.EMPTY.getDefaultState();
	}

	public abstract Vec3i getSize();

	@Override
	public float getBrightness(Direction direction, boolean shaded) {
		return 1.0F;
	}

	@Override
	public LightingProvider getLightingProvider() {
		return null;
	}

	@Override
	public int getColor(BlockPos pos, ColorResolver color) {
		return color.getColor(Biomes.PLAINS, pos.getX(), pos.getZ());
	}

	@Override
	public int getLightLevel(LightType type, BlockPos pos) {
		return 15;
	}

	@Override
	public int getBaseLightLevel(BlockPos pos, int ambientDarkening) {
		return 15 - ambientDarkening;
	}
}
