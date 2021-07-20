package vazkii.patchouli.common.recipe;

import com.google.gson.JsonObject;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.PatchouliItems;

public abstract class BookRecipe<T extends CraftingRecipe> implements CraftingRecipe {
	protected final T compose;
	private final Identifier outputBook;

	protected BookRecipe(T compose, Identifier outputBook) {
		this.compose = compose;
		this.outputBook = outputBook;
	}

	@Override
	public boolean matches(CraftingInventory inventory, World world) {
		return compose.matches(inventory, world);
	}

	@Override
	public ItemStack craft(CraftingInventory inventory) {
		return getOutput();
	}

	@Override
	public boolean fits(int width, int height) {
		return compose.fits(width, height);
	}

	@Override
	public ItemStack getOutput() {
		return PatchouliAPI.get().getBookStack(outputBook);
	}

	@Override
	public Identifier getId() {
		return compose.getId();
	}

	@Override
	public abstract RecipeSerializer<?> getSerializer();

	protected abstract static class WrapperSerializer<R extends CraftingRecipe, T extends BookRecipe<R>> implements RecipeSerializer<T> {
		protected abstract RecipeSerializer<R> getSerializer();

		protected abstract T getRecipe(R recipe, Identifier outputBook);

		@Override
		public T read(Identifier id, JsonObject json) {
			if (!json.has("result")) {
				JsonObject object = new JsonObject();
				object.addProperty("item", PatchouliItems.BOOK_ID.toString());
				json.add("result", object);
			}
			R recipe = getSerializer().read(id, json);

			Identifier outputBook = new Identifier(JsonHelper.getString(json, "book"));
			if (!BookRegistry.INSTANCE.books.containsKey(outputBook)) {
				Patchouli.LOGGER.warn("Book {} in recipe {} does not exist!", outputBook, id);
			}

			return getRecipe(recipe, outputBook);
		}

		@Override
		public T read(Identifier id, PacketByteBuf buf) {
			R recipe = getSerializer().read(id, buf);
			Identifier outputBook = buf.readIdentifier();

			return getRecipe(recipe, outputBook);
		}

		@Override
		public void write(PacketByteBuf buf, T recipe) {
			getSerializer().write(buf, recipe.compose);
			buf.writeIdentifier(recipe.getId());
		}
	}
}
