package vazkii.patchouli.client.book.model;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemDisplayContext;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.ItemModel;
import net.neoforged.neoforge.client.model.ItemModel.BakingContext;
import net.neoforged.neoforge.client.model.ItemStackRenderState;
import net.neoforged.neoforge.client.model.ResolvableModel;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.item.ItemModBook;

public class BookModel implements ItemModel {

    public static final BookModel INSTANCE = new BookModel();
    private static final ModelResourceLocation DEFAULT_MODEL = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "item/book_base"));

    @Override
    public void update(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        var book = ItemModBook.getBook(stack);
        ModelResourceLocation model =
                book != null
                        ? ModelResourceLocation.standalone(book.model)
                        : DEFAULT_MODEL;
        resolver.getModel(model).update(state, stack, resolver, displayContext, level, entity, seed);
    }

    public static class Unbaked implements ItemModel.Unbaked {
        public static final Unbaked INSTANCE = new Unbaked();
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(INSTANCE);

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
            // This is a dynamic model, dependencies are resolved at render time.
        }

        @Override
        public ItemModel bake(BakingContext context) {
            return BookModel.INSTANCE;
        }

        @Override
        public MapCodec<? extends ItemModel.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
