package vazkii.patchouli.mixin.client;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TileEntity.class)
public interface AccessorTileEntity {
	@Accessor
	void setCachedBlockState(BlockState state);
}
