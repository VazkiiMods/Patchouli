package net.minecraft.client.renderer.block.model;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface BlockModelPart extends net.neoforged.neoforge.client.extensions.BlockModelPartExtension {
    List<BakedQuad> getQuads(@Nullable Direction direction);

    /** @deprecated Neo: Use {@link #ambientOcclusion()} instead. */
    @Deprecated
    boolean useAmbientOcclusion();

    TextureAtlasSprite particleIcon();

    @OnlyIn(Dist.CLIENT)
    public interface Unbaked extends ResolvableModel {
        BlockModelPart bake(ModelBaker modelBaker);
    }
}
