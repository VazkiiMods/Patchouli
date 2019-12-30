package vazkii.patchouli.client.mixin;

import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderLayer.class)
public interface MixinRenderLayer {
    @Accessor
    public boolean getTranslucent();
}
