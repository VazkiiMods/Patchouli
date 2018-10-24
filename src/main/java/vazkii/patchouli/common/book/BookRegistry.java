package vazkii.patchouli.common.book;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.Patchouli;

public class BookRegistry {

	public static final BookRegistry INSTANCE = new BookRegistry();
	public static final String BOOKS_LOCATION = Patchouli.MOD_ID + "_books"; 
	
	public final Map<ResourceLocation, Book> books = new HashMap();
	
	private BookRegistry() { }
	
	public void init() {

//		mods.forEach((mod) -> {
//			String id = mod.getModId();
//			CraftingHelper.findFiles(mod, String.format("assets/%s/%s", id, loc), (path) -> {
//				if (Files.exists(path)) {
//					AdvancementSyncHandler.trackedNamespaces.add(id);
//					foundMods.add(mod);
//					return true;
//				}
//
//				return false;
//			}, null);
//		});
	}
	
	@SideOnly(Side.CLIENT)
	public void reload() {
		BookRegistry.INSTANCE.books.values().forEach(Book::reloadContents);
	}
	
}
