package vazkii.patchouli.client.base;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BookModel implements BakedModel {
    private final BakedModel original;

    public BookModel(BakedModel original) {
        this.original = original;
    }

    private final ModelItemPropertyOverrideList itemHandler = new ModelItemPropertyOverrideList(null, null, null, Collections.emptyList()) {
        @Override
        public BakedModel apply(@Nonnull BakedModel original, @Nonnull ItemStack stack,
                                                 @Nullable World world, @Nullable LivingEntity entity) {
            Book book = ItemModBook.getBook(stack);
            if (book != null) {
                ModelIdentifier path = new ModelIdentifier(book.model, "inventory");
                return MinecraftClient.getInstance().getBakedModelManager().getModel(path);
            }
            return original;
        }
    };

    @Nonnull
    @Override
    public ModelItemPropertyOverrideList getItemPropertyOverrides() {
        return itemHandler;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
        return original.getQuads(state, side, rand);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return original.useAmbientOcclusion();
    }

    @Override
    public boolean hasDepthInGui() {
        return original.hasDepthInGui();
    }

    @Override
    public boolean isBuiltin() {
        return original.isBuiltin();
    }

    @Nonnull
    @Override
    public Sprite getSprite() {
        return original.getSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return original.getTransformation();
    }
}
