package vazkii.patchouli.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.recipe.ShapedBookRecipe;
import vazkii.patchouli.common.recipe.ShapelessBookRecipe;

public class PatchouliItems {

	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Patchouli.MOD_ID);
	private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Patchouli.MOD_ID);

	public static final RegistryObject<ItemModBook> BOOK = ITEMS.register("guide_book", ItemModBook::new);

	public static final RegistryObject<ShapedBookRecipe.Serializer> SHAPED_BOOK_RECIPE = RECIPE_SERIALIZERS.register("shaped_book_recipe", ShapedBookRecipe.Serializer::new);
	public static final RegistryObject<ShapelessBookRecipe.Serializer> SHAPELESS_BOOK_RECIPE = RECIPE_SERIALIZERS.register("shapeless_book_recipe", ShapelessBookRecipe.Serializer::new);

	public static void init() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		ITEMS.register(bus);
		RECIPE_SERIALIZERS.register(bus);
	}
}
