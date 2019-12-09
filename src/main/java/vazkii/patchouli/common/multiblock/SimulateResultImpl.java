package vazkii.patchouli.common.multiblock;

import net.minecraft.block.BlockState;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.common.util.RotationUtil;

import javax.annotation.Nullable;

public class SimulateResultImpl implements IMultiblock.SimulateResult {
    private final BlockPos worldPosition;
    private final IStateMatcher stateMatcher;
    @Nullable
    private final Character character;

    public SimulateResultImpl(BlockPos worldPosition, IStateMatcher stateMatcher, @Nullable Character character) {
        this.worldPosition = worldPosition;
        this.stateMatcher = stateMatcher;
        this.character = character;
    }

    @Override
    public BlockPos getWorldPosition() {
        return worldPosition;
    }

    @Override
    public IStateMatcher getStateMatcher() {
        return stateMatcher;
    }

    @Nullable
    @Override
    public Character getCharacter() {
        return character;
    }

    @Override
    public boolean test(World world, Rotation rotation) {
        BlockState state = world.getBlockState(getWorldPosition()).rotate(RotationUtil.fixHorizontal(rotation));
        return getStateMatcher().getStatePredicate().test(world, getWorldPosition(), state);
    }
}
