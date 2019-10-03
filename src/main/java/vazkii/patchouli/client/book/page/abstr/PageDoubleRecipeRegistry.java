package vazkii.patchouli.client.book.page.abstr;

import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.client.book.BookEntry;

public abstract class PageDoubleRecipeRegistry<T extends IRecipe<?>> extends PageDoubleRecipe<T> {

	private final Class<T> clazz;
	
	public PageDoubleRecipeRegistry(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	@Override
	protected T loadRecipe(BookEntry entry, String loc) {
		if(loc == null)
			return null;
		
		ResourceLocation res = new ResourceLocation(loc);
		RecipeManager manager = Minecraft.getInstance().world.getRecipeManager();
		IRecipe<?> tempRecipe = manager.getRecipe(res).orElse(null);
		if(tempRecipe == null) // this is hacky but it works around Forge requiring custom recipes to have the prefix of the adding mod
			tempRecipe = manager.getRecipe(new ResourceLocation("crafttweaker", res.getPath())).orElse(null);
		
		if(tempRecipe != null && clazz.isAssignableFrom(tempRecipe.getClass())) {
			entry.addRelevantStack(tempRecipe.getRecipeOutput(), pageNum);
			return (T) tempRecipe;
		}
		
		return null;
	}
	
}
