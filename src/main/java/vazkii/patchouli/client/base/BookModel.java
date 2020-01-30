package vazkii.patchouli.client.base;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BookModel implements IBakedModel {
    private final IBakedModel original;

    public BookModel(IBakedModel original) {
        this.original = original;
    }

    private final ItemOverrideList itemHandler = new ItemOverrideList() {
        @Override
        public IBakedModel getModelWithOverrides(@Nonnull IBakedModel original, @Nonnull ItemStack stack,
                                                 @Nullable World world, @Nullable LivingEntity entity) {
            Book book = ItemModBook.getBook(stack);
            if (book != null) {
                ModelResourceLocation modelPath = new ModelResourceLocation(book.modelResourceLoc, "inventory");
                return Minecraft.getInstance().getModelManager().getModel(modelPath);
            }
            return original;
        }
    };

    @Nonnull
    @Override
    public ItemOverrideList getOverrides() {
        return itemHandler;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
        return original.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return original.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return original.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return original.isBuiltInRenderer();
    }

    @Nonnull
    @Override
    public TextureAtlasSprite getParticleTexture() {
        return original.getParticleTexture();
    }
}
