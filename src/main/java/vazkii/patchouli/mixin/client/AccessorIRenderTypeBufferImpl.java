package vazkii.patchouli.mixin.client;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(IRenderTypeBuffer.Impl.class)
public interface AccessorIRenderTypeBufferImpl {
	@Accessor
	BufferBuilder getBuffer();

	@Accessor
	Map<RenderType, BufferBuilder> getFixedBuffers();

}
