package vazkii.patchouli.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import vazkii.patchouli.api.PatchouliAPI;

import org.jetbrains.annotations.Nullable;

/**
 * Recipe type for shapeless book recipes.
 * The format is the same as vanilla shapeless recipes, but the
 * "result" object is replaced by a "book" string for the book ID.
 */
public class ShapelessBookRecipe extends ShapelessRecipe {
	public static final RecipeSerializer<ShapelessBookRecipe> SERIALIZER = new Serializer();

	final String group;
	final ItemStack result;
	final NonNullList<Ingredient> ingredients;
	final @Nullable ResourceLocation outputBook;

	public ShapelessBookRecipe(String group, ItemStack result, NonNullList<Ingredient> ingredients, @Nullable ResourceLocation outputBook) {
		super(group, CraftingBookCategory.MISC, getOutputBook(result, outputBook), ingredients);
		this.group = group;
		this.result = result;
		this.ingredients = ingredients;
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

	public static class Serializer implements RecipeSerializer<ShapelessBookRecipe> {
		static int maxWidth = 3;
		static int maxHeight = 3;
		private static final Codec<ShapelessBookRecipe> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(bookRecipe -> bookRecipe.group),
						ExtraCodecs.strictOptionalField(ItemStack.ITEM_WITH_COUNT_CODEC, "result", ItemStack.EMPTY).forGetter(bookRecipe -> bookRecipe.result),
						Ingredient.CODEC_NONEMPTY
								.listOf()
								.fieldOf("ingredients")
								.flatXmap(
										ingredientList -> {
											Ingredient[] aingredient = ingredientList
													.toArray(Ingredient[]::new);
											if (aingredient.length == 0) {
												return DataResult.error(() -> "No ingredients for shapeless book recipe");
											} else {
												return aingredient.length > maxHeight * maxWidth
														? DataResult.error(() -> "Too many ingredients for shapeless book recipe. The maximum is: %s".formatted(maxHeight * maxWidth))
														: DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
											}
										},
										DataResult::success
								)
								.forGetter(bookRecipe -> bookRecipe.ingredients),
						ExtraCodecs.strictOptionalField(ResourceLocation.CODEC, "book", null).forGetter(bookRecipe -> bookRecipe.outputBook)
				)
						.apply(instance, ShapelessBookRecipe::new)
		);

		@Override
		public Codec<ShapelessBookRecipe> codec() {
			return CODEC;
		}

		@Override
		public ShapelessBookRecipe fromNetwork(FriendlyByteBuf buf) {
			String group = buf.readUtf();
			int i = buf.readVarInt();
			NonNullList<Ingredient> ingredients = NonNullList.withSize(i, Ingredient.EMPTY);

			for (int j = 0; j < ingredients.size(); ++j) {
				ingredients.set(j, Ingredient.fromNetwork(buf));
			}

			ItemStack result = buf.readItem();
			ResourceLocation outputBook = buf.readBoolean() ? buf.readResourceLocation() : null;
			return new ShapelessBookRecipe(group, result, ingredients, outputBook);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, ShapelessBookRecipe bookRecipe) {
			buf.writeUtf(bookRecipe.group);
			buf.writeVarInt(bookRecipe.ingredients.size());

			for (Ingredient ingredient : bookRecipe.ingredients) {
				ingredient.toNetwork(buf);
			}

			buf.writeItem(bookRecipe.result);
			buf.writeBoolean(bookRecipe.outputBook != null);
			if (bookRecipe.outputBook != null) {
				buf.writeResourceLocation(bookRecipe.outputBook);
			}
		}
	}
}
