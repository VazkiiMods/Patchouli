package vazkii.patchouli.fabric.common;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.PatchouliItems;

import java.util.function.BiFunction;

public class FabricRecipeSerializerWrapper<T extends Recipe<?>, U extends T> implements RecipeSerializer<U> {
	private final RecipeSerializer<T> compose;
	private final BiFunction<T, ResourceLocation, U> converter;

	public FabricRecipeSerializerWrapper(RecipeSerializer<T> compose, BiFunction<T, ResourceLocation, U> converter) {
		this.compose = compose;
		this.converter = converter;
	}

	@Override
	public U fromJson(ResourceLocation id, JsonObject json) {
		if (!json.has("result")) {
			JsonObject object = new JsonObject();
			object.addProperty("item", PatchouliItems.BOOK_ID.toString());
			json.add("result", object);
		}
		T recipe = compose.fromJson(id, json);

		ResourceLocation outputBook = new ResourceLocation(GsonHelper.getAsString(json, "book"));
		if (!BookRegistry.INSTANCE.books.containsKey(outputBook)) {
			PatchouliAPI.LOGGER.warn("Book {} in recipe {} does not exist!", outputBook, id);
		}

		return converter.apply(recipe, outputBook);
	}

	@Override
	public U fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
		T recipe = compose.fromNetwork(id, buf);
		ResourceLocation outputBook = buf.readResourceLocation();

		return converter.apply(recipe, outputBook);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buf, U recipe) {
		compose.toNetwork(buf, recipe);
		buf.writeResourceLocation(recipe.getId());
	}
}
