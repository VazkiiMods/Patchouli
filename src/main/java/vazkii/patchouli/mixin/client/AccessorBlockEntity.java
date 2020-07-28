package vazkii.patchouli.mixin.client;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockEntity.class)
public interface AccessorBlockEntity {
	@Accessor
	void setCachedState(BlockState state);
}
