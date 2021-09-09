package vazkii.patchouli.common.recipe;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.PatchouliItems;

public abstract class BookRecipe<T extends CraftingRecipe> implements CraftingRecipe {
	protected final T compose;
	private final ResourceLocation outputBook;

	protected BookRecipe(T compose, ResourceLocation outputBook) {
		this.compose = compose;
		this.outputBook = outputBook;
	}

	@Override
	public boolean matches(CraftingContainer inventory, Level world) {
		return compose.matches(inventory, world);
	}

	@Override
	public ItemStack assemble(CraftingContainer inventory) {
		return getResultItem();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return compose.canCraftInDimensions(width, height);
	}

	@Override
	public ItemStack getResultItem() {
		return PatchouliAPI.get().getBookStack(outputBook);
	}

	@Override
	public ResourceLocation getId() {
		return compose.getId();
	}

	@Override
	public abstract RecipeSerializer<?> getSerializer();

	protected abstract static class WrapperSerializer<R extends CraftingRecipe, T extends BookRecipe<R>> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {
		protected abstract RecipeSerializer<R> getSerializer();

		protected abstract T getRecipe(R recipe, ResourceLocation outputBook);

		@Override
		public T fromJson(ResourceLocation id, JsonObject json) {
			if (!json.has("result")) {
				JsonObject object = new JsonObject();
				object.addProperty("item", PatchouliItems.BOOK.getId().toString());
				json.add("result", object);
			}
			R recipe = getSerializer().fromJson(id, json);

			ResourceLocation outputBook = new ResourceLocation(GsonHelper.getAsString(json, "book"));
			if (!BookRegistry.INSTANCE.books.containsKey(outputBook)) {
				Patchouli.LOGGER.warn("Book {} in recipe {} does not exist!", outputBook, id);
			}

			return getRecipe(recipe, outputBook);
		}

		@Override
		public T fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			R recipe = getSerializer().fromNetwork(id, buf);
			ResourceLocation outputBook = buf.readResourceLocation();

			return getRecipe(recipe, outputBook);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, T recipe) {
			getSerializer().toNetwork(buf, recipe.compose);
			buf.writeResourceLocation(recipe.getId());
		}
	}
}
