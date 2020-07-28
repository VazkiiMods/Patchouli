package vazkii.patchouli.mixin.client;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(VertexConsumerProvider.Immediate.class)
public interface AccessorVertexConsumerProviderImmediate {
	@Accessor
	BufferBuilder getFallbackBuffer();

	@Accessor
	Map<RenderLayer, BufferBuilder> getLayerBuffers();
}
