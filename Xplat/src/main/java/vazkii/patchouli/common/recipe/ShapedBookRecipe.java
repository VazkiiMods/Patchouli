package vazkii.patchouli.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

import vazkii.patchouli.api.PatchouliAPI;

import org.jetbrains.annotations.Nullable;

/**
 * Recipe type for shaped book recipes.
 * The format is the same as vanilla shaped recipes, but the
 * "result" object is replaced by a "book" string for the book ID.
 */
public class ShapedBookRecipe extends ShapedRecipe {
	public static final RecipeSerializer<ShapedBookRecipe> SERIALIZER = new Serializer();

	final ShapedRecipePattern pattern;
	final ItemStack result;
	final String group;
	final @Nullable ResourceLocation outputBook;

	public ShapedBookRecipe(String group, ShapedRecipePattern recipePattern, ItemStack result, @Nullable ResourceLocation outputBook) {
		super(group, CraftingBookCategory.MISC, recipePattern, getOutputBook(result, outputBook));
		this.pattern = recipePattern;
		this.result = result;
		this.group = group;
		this.outputBook = outputBook;
	}

	private static ItemStack getOutputBook(ItemStack result, @Nullable ResourceLocation outputBook) {
		if (outputBook != null) {
			return PatchouliAPI.get().getBookStack(outputBook);
		}
		return result;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	public static class Serializer implements RecipeSerializer<ShapedBookRecipe> {
		public static final Codec<ShapedBookRecipe> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
								ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(bookRecipe -> bookRecipe.group),
								ShapedRecipePattern.MAP_CODEC.forGetter(bookRecipe -> bookRecipe.pattern),
								ExtraCodecs.strictOptionalField(ItemStack.ITEM_WITH_COUNT_CODEC, "result", ItemStack.EMPTY).forGetter(bookRecipe -> bookRecipe.result),
								ExtraCodecs.strictOptionalField(ResourceLocation.CODEC, "book", null).forGetter(bookRecipe -> bookRecipe.outputBook)
						)
						.apply(instance, ShapedBookRecipe::new)
		);

		@Override
		public Codec<ShapedBookRecipe> codec() {
			return CODEC;
		}

		@Override
		public ShapedBookRecipe fromNetwork(FriendlyByteBuf buf) {
			String group = buf.readUtf();
			ShapedRecipePattern recipePattern = ShapedRecipePattern.fromNetwork(buf);
			ItemStack result = buf.readItem();
			ResourceLocation outputBook = buf.readBoolean() ? buf.readResourceLocation() : null;
			return new ShapedBookRecipe(group, recipePattern, result, outputBook);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, ShapedBookRecipe bookRecipe) {
			buf.writeUtf(bookRecipe.group);
			bookRecipe.pattern.toNetwork(buf);
			buf.writeItem(bookRecipe.result);
			buf.writeBoolean(bookRecipe.outputBook != null);
			if (bookRecipe.outputBook != null) {
				buf.writeResourceLocation(bookRecipe.outputBook);
			}
		}
	}
}
