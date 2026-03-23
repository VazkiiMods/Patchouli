package vazkii.patchouli.client.multiblock;

import com.mojang.datafixers.util.Pair;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;

import org.jspecify.annotations.Nullable;

import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.common.multiblock.AbstractMultiblock;

import java.util.Collection;

public record MultiblockTintWrapper(AbstractMultiblock multiblock) implements IMultiblock, BlockAndTintGetter {
	@Override
	public @Nullable BlockEntity getBlockEntity(BlockPos blockPos) {
		return multiblock.getBlockEntity(blockPos);
	}

	@Override
	public BlockState getBlockState(BlockPos blockPos) {
		return multiblock.getBlockState(blockPos);
	}

	@Override
	public FluidState getFluidState(BlockPos blockPos) {
		return multiblock.getFluidState(blockPos);
	}

	@Override
	public Vec3i getSize() {
		return multiblock.getSize();
	}

	@Override
	public IMultiblock offset(int x, int y, int z) {
		return multiblock.offset(x, y, z);
	}

	@Override
	public IMultiblock offsetView(int x, int y, int z) {
		return multiblock.offsetView(x, y, z);
	}

	@Override
	public IMultiblock setSymmetrical(boolean symmetrical) {
		return multiblock.setSymmetrical(symmetrical);
	}

	@Override
	public IMultiblock setId(Identifier res) {
		return multiblock.setId(res);
	}

	@Override
	public boolean isSymmetrical() {
		return multiblock.isSymmetrical();
	}

	@Override
	public Identifier getID() {
		return multiblock.getID();
	}

	@Override
	public void place(Level world, BlockPos pos, Rotation rotation) {
		multiblock.place(world, pos, rotation);
	}

	@Override
	public Pair<BlockPos, Collection<SimulateResult>> simulate(Level world, BlockPos anchor, Rotation rotation, boolean forView) {
		return multiblock.simulate(world, anchor, rotation, forView);
	}

	@Override
	public @Nullable Rotation validate(Level world, BlockPos pos) {
		return multiblock.validate(world, pos);
	}

	@Override
	public boolean validate(Level world, BlockPos pos, Rotation rotation) {
		return multiblock.validate(world, pos, rotation);
	}

	@Override
	public boolean test(Level world, BlockPos start, int x, int y, int z, Rotation rotation) {
		return multiblock.test(world, start, x, y, z, rotation);
	}

	@Override
	public CardinalLighting cardinalLighting() {
		return CardinalLighting.DEFAULT;
	}

	@Override
	public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
		return 0xffffffff;
	}

	@Override
	public LevelLightEngine getLightEngine() {
		return multiblock.getLightEngine();
	}

	@Override
	public int getHeight() {
		return multiblock.getHeight();
	}

	@Override
	public int getMinY() {
		return multiblock.getMinY();
	}

	public void setWorld(@Nullable Level level) {
		multiblock.setWorld(level);
	}

	public AABB getBounds() {
		return multiblock.getBounds();
	}
}
