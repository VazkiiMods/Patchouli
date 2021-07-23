package vazkii.patchouli.mixin.client;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockEntity.class)
public interface AccessorBlockEntity {
	@Accessor("blockState")
	void setBlockState(BlockState state);
}
