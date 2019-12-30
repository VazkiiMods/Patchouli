package vazkii.patchouli.client.book.page.abstr;

import net.minecraft.client.MinecraftClient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;
import vazkii.patchouli.client.book.BookEntry;

public abstract class PageDoubleRecipeRegistry<T extends Recipe<?>> extends PageDoubleRecipe<T> {

	private final Class<T> clazz;
	
	public PageDoubleRecipeRegistry(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	@Override
	protected T loadRecipe(BookEntry entry, String loc) {
		if(loc == null)
			return null;
		
		Identifier res = new Identifier(loc);
		RecipeManager manager = MinecraftClient.getInstance().world.getRecipeManager();
		Recipe<?> tempRecipe = manager.get(res).orElse(null);
		if(tempRecipe == null) // this is hacky but it works around Forge requiring custom recipes to have the prefix of the adding mod
			tempRecipe = manager.get(new Identifier("crafttweaker", res.getPath())).orElse(null);
		
		if(tempRecipe != null && clazz.isAssignableFrom(tempRecipe.getClass())) {
			entry.addRelevantStack(tempRecipe.getOutput(), pageNum);
			return (T) tempRecipe;
		}
		
		return null;
	}
	
}
