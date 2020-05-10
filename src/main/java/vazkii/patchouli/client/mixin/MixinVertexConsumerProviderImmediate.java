package vazkii.patchouli.client.mixin;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(VertexConsumerProvider.Immediate.class)
public interface MixinVertexConsumerProviderImmediate {
	@Accessor
	BufferBuilder getFallbackBuffer();

	@Accessor
	Map<RenderLayer, BufferBuilder> getLayerBuffers();
}
