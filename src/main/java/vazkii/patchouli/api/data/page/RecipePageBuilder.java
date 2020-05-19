package vazkii.patchouli.api.data.page;

import com.google.gson.JsonObject;

import net.minecraft.util.ResourceLocation;

import vazkii.patchouli.api.data.AbstractPageBuilder;
import vazkii.patchouli.api.data.EntryBuilder;

@SuppressWarnings("unchecked")
public abstract class RecipePageBuilder<T extends RecipePageBuilder<T>> extends AbstractPageBuilder<T> {
	private final String recipe;
	private String recipe2;
	private String title;
	private String text;

	public RecipePageBuilder(String type, ResourceLocation recipe, EntryBuilder parent) {
		super(type, parent);
		this.recipe = recipe.toString();
	}

	@Override
	protected void serialize(JsonObject json) {
		json.addProperty("recipe", recipe);
		if (recipe2 != null) {
			json.addProperty("recipe2", recipe2);
		}
		if (title != null) {
			json.addProperty("title", title);
		}
		if (text != null) {
			json.addProperty("text", text);
		}
	}

	public T setRecipe2(ResourceLocation recipe2) {
		this.recipe2 = recipe2.toString();
		return (T) this;
	}

	public T setTitle(String title) {
		this.title = title;
		return (T) this;
	}

	public T setText(String text) {
		this.text = text;
		return (T) this;
	}

}
