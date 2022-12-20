package vazkii.patchouli.common.multiblock;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.TriPredicate;
import vazkii.patchouli.common.util.RotationUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMultiblock implements IMultiblock, BlockAndTintGetter {
	public ResourceLocation id;
	protected int offX, offY, offZ;
	protected int viewOffX, viewOffY, viewOffZ;
	private boolean symmetrical;
	Level world;

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
	public ResourceLocation getID() {
		return id;
	}

	@Override
	public IMultiblock setId(ResourceLocation res) {
		this.id = res;
		return this;
	}

	@Override
	public void place(Level world, BlockPos pos, Rotation rotation) {
		setWorld(world);
		simulate(world, pos, rotation, false).getSecond().forEach(r -> {
			BlockPos placePos = r.getWorldPosition();
			BlockState targetState = r.getStateMatcher().getDisplayedState(world.getGameTime()).rotate(rotation);

			if (!targetState.isAir() && targetState.canSurvive(world, placePos) && world.getBlockState(placePos).getMaterial().isReplaceable()) {
				world.setBlockAndUpdate(placePos, targetState);
			}
		});
	}

	@Override
	public Rotation validate(Level world, BlockPos pos) {
		if (isSymmetrical() && validate(world, pos, Rotation.NONE)) {
			return Rotation.NONE;
		} else {
			for (Rotation rot : Rotation.values()) {
				if (validate(world, pos, rot)) {
					return rot;
				}
			}
		}
		return null;
	}

	@Override
	public boolean validate(Level world, BlockPos pos, Rotation rotation) {
		setWorld(world);
		Pair<BlockPos, Collection<SimulateResult>> sim = simulate(world, pos, rotation, false);

		return sim.getSecond().stream().allMatch(r -> {
			BlockPos checkPos = r.getWorldPosition();
			TriPredicate<BlockGetter, BlockPos, BlockState> pred = r.getStateMatcher().getStatePredicate();
			BlockState state = world.getBlockState(checkPos).rotate(RotationUtil.fixHorizontal(rotation));

			return pred.test(world, checkPos, state);
		});
	}

	@Override
	public boolean isSymmetrical() {
		return symmetrical;
	}

	public void setWorld(Level world) {
		this.world = world;
	}

	@Override
	@Nullable
	public BlockEntity getBlockEntity(BlockPos pos) {
		BlockState state = getBlockState(pos);
		if (state.getBlock() instanceof EntityBlock) {
			return teCache.computeIfAbsent(pos.immutable(), p -> ((EntityBlock) state.getBlock()).newBlockEntity(pos, state));
		}
		return null;
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return Fluids.EMPTY.defaultFluidState();
	}

	@Override
	public abstract Vec3i getSize();

	@Override
	public float getShade(Direction direction, boolean shaded) {
		return 1.0F;
	}

	@Override
	public LevelLightEngine getLightEngine() {
		return null;
	}

	@Override
	public int getBlockTint(BlockPos pos, ColorResolver color) {
		var plains = world.registryAccess().registryOrThrow(Registries.BIOME)
				.getOrThrow(Biomes.PLAINS);
		return color.getColor(plains, pos.getX(), pos.getZ());
	}

	@Override
	public int getBrightness(LightLayer type, BlockPos pos) {
		return 15;
	}

	@Override
	public int getRawBrightness(BlockPos pos, int ambientDarkening) {
		return 15 - ambientDarkening;
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
