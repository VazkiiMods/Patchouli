package vazkii.patchouli.common.recipe;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.PatchouliItems;

import javax.annotation.Nonnull;

import java.util.function.BiFunction;

public interface BookRecipeSerializer<T extends Recipe<?>, U extends T> extends RecipeSerializer<U> {
	RecipeSerializer<T> getCompose();

	BiFunction<T, ResourceLocation, U> getConverter();

	@Override
	@Nonnull
	default U fromJson(@Nonnull ResourceLocation id, @Nonnull JsonObject json) {
		if (!json.has("result")) {
			JsonObject object = new JsonObject();
			object.addProperty("item", PatchouliItems.BOOK_ID.toString());
			json.add("result", object);
		}
		T recipe = getCompose().fromJson(id, json);

		ResourceLocation outputBook = new ResourceLocation(GsonHelper.getAsString(json, "book"));
		if (!BookRegistry.INSTANCE.books.containsKey(outputBook)) {
			PatchouliAPI.LOGGER.warn("Book {} in recipe {} does not exist!", outputBook, id);
		}

		return getConverter().apply(recipe, outputBook);
	}

	@Override
	@Nonnull
	default U fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buf) {
		T recipe = getCompose().fromNetwork(id, buf);
		return getConverter().apply(recipe, null);
	}

	@Override
	default void toNetwork(@Nonnull FriendlyByteBuf buf, @Nonnull U recipe) {
		getCompose().toNetwork(buf, recipe);
	}
}
