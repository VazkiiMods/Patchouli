package vazkii.patchouli.common.item;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.recipe.ShapedBookRecipe;
import vazkii.patchouli.common.recipe.ShapelessBookRecipe;

public class PatchouliItems {

	public static final ResourceLocation BOOK_ID = new ResourceLocation(Patchouli.MOD_ID, "guide_book");
	public static Item book;

	public static void init() {
		book = new ItemModBook();
		Registry.register(Registry.ITEM, BOOK_ID, book);

		Registry.register(Registry.RECIPE_SERIALIZER, new ResourceLocation(Patchouli.MOD_ID, "shaped_book_recipe"), ShapedBookRecipe.SERIALIZER);
		Registry.register(Registry.RECIPE_SERIALIZER, new ResourceLocation(Patchouli.MOD_ID, "shapeless_book_recipe"), ShapelessBookRecipe.SERIALIZER);
	}
}
