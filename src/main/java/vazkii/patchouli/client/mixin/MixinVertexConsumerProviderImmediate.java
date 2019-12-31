package vazkii.patchouli.client.mixin;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import vazkii.patchouli.client.base.CustomVertexConsumer;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Mixin(VertexConsumerProvider.Immediate.class)
public abstract class MixinVertexConsumerProviderImmediate implements VertexConsumerProvider, CustomVertexConsumer {
    @Shadow protected Optional<RenderLayer> currentLayer;
    @Shadow @Final protected BufferBuilder fallbackBuffer;
    @Shadow @Final protected Map<RenderLayer, BufferBuilder> layerBuffers;
    @Shadow @Final protected Set<BufferBuilder> activeConsumers;

    @Shadow
    abstract BufferBuilder getBufferInternal(RenderLayer layer);

    // Exactly same as draw() and draw(RenderLayer), except allows camera coords to be passed
    // to the underlying bufferbuilder for transparency sorting
    @Override
    public void patchouli_drawWithCamera(float x, float y, float z) {
        this.currentLayer.ifPresent((renderLayerx) -> {
            VertexConsumer vertexConsumer = this.getBuffer(renderLayerx);
            if (vertexConsumer == this.fallbackBuffer) {
                this.patchouli_drawWithCamera(renderLayerx, x, y, z);
            }

        });
        Iterator var1 = this.layerBuffers.keySet().iterator();

        while(var1.hasNext()) {
            RenderLayer renderLayer = (RenderLayer)var1.next();
            this.patchouli_drawWithCamera(renderLayer, x, y, z);
        }
    }

    private void patchouli_drawWithCamera(RenderLayer layer, float x, float y, float z) {
        BufferBuilder bufferBuilder = this.getBufferInternal(layer);
        boolean bl = Objects.equals(this.currentLayer, Optional.of(layer));
        if (bl || bufferBuilder != this.fallbackBuffer) {
            if (this.activeConsumers.remove(bufferBuilder)) {
                // Inline layer.draw because it only takes ints and we want to pass floats through to sortQuads
                if (bufferBuilder.isBuilding()) {
                    if (((MixinRenderLayer) layer).getTranslucent()) {
                        bufferBuilder.sortQuads(x, y, z);
                    }

                    bufferBuilder.end();
                    layer.startDrawing();
                    BufferRenderer.draw(bufferBuilder);
                    layer.endDrawing();
                }
                // end inline
                if (bl) {
                    this.currentLayer = Optional.empty();
                }

            }
        }
    }

    // Same as draw() and draw(RenderLayer), except takes additional callback to alter the GL state before rendering
    @Override
    public void patchouli_drawWithCustomState(Runnable custom) {
        this.currentLayer.ifPresent((renderLayerx) -> {
            VertexConsumer vertexConsumer = this.getBuffer(renderLayerx);
            if (vertexConsumer == this.fallbackBuffer) {
                this.patchouli_drawWithCustomState(renderLayerx, custom);
            }

        });
        Iterator var1 = this.layerBuffers.keySet().iterator();

        while(var1.hasNext()) {
            RenderLayer renderLayer = (RenderLayer)var1.next();
            this.patchouli_drawWithCustomState(renderLayer, custom);
        }
    }

    private void patchouli_drawWithCustomState(RenderLayer layer, Runnable custom) {
        BufferBuilder bufferBuilder = this.getBufferInternal(layer);
        boolean bl = Objects.equals(this.currentLayer, Optional.of(layer));
        if (bl || bufferBuilder != this.fallbackBuffer) {
            if (this.activeConsumers.remove(bufferBuilder)) {
                // Inline layer.draw so we can call custom
                if (bufferBuilder.isBuilding()) {
                    if (((MixinRenderLayer) layer).getTranslucent()) {
                        bufferBuilder.sortQuads(0, 0, 0);
                    }

                    bufferBuilder.end();
                    layer.startDrawing();
                    custom.run();
                    BufferRenderer.draw(bufferBuilder);
                    layer.endDrawing();
                }
                // end inline

                if (bl) {
                    this.currentLayer = Optional.empty();
                }

            }
        }
    }
}
