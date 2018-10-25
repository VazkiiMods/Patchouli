package vazkii.patchouli.common.item;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.Patchouli;
import vazkii.patchouli.common.book.Book;

public class PatchouliItems {

	public static Item book;
	
	public static void preInit() {
		book = new ItemModBook();
		
		MinecraftForge.EVENT_BUS.register(PatchouliItems.class);
	}

	@SideOnly(Side.CLIENT)
	public static void setModels() {
		bindBookModel();
	}
	
	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(book);
	}
	
	private static void bindBookModel() {
		ModelBakery.registerItemVariants(book, 
				new ModelResourceLocation(Patchouli.PREFIX + "book_blue", "inventory"),
				new ModelResourceLocation(Patchouli.PREFIX + "book_brown", "inventory"),
				new ModelResourceLocation(Patchouli.PREFIX + "book_cyan", "inventory"),
				new ModelResourceLocation(Patchouli.PREFIX + "book_gray", "inventory"),
				new ModelResourceLocation(Patchouli.PREFIX + "book_green", "inventory"),
				new ModelResourceLocation(Patchouli.PREFIX + "book_purple", "inventory"),
				new ModelResourceLocation(Patchouli.PREFIX + "book_red", "inventory"));
		
        ModelLoader.setCustomMeshDefinition(book, (stack) -> {
    		Book book = ItemModBook.getBook(stack);
    		if(book != null)
    			return book.modelResourceLoc;
    		
    		return Book.DEFAULT_MODEL_RES;
        });
	}
	
}
