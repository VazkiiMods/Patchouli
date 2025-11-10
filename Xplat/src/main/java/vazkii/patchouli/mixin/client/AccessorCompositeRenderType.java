package vazkii.patchouli.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.renderer.RenderType$CompositeRenderType")
public interface AccessorCompositeRenderType {
	@Accessor("state")
	Object patchouli$getState();
}
