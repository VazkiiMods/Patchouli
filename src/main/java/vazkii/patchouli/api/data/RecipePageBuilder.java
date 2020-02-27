package vazkii.patchouli.api.data;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

/**
 * @author Minecraftschurli
 * @version 2020-02-27
 */
public abstract class RecipePageBuilder extends PageBuilder{
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
        if (recipe2 != null)
            json.addProperty("recipe2", recipe2);
        if (title != null)
            json.addProperty("title", title);
        if (text != null)
            json.addProperty("text", text);
    }

    public void setRecipe2(String recipe2) {
        this.recipe2 = recipe2;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

}
