package vazkii.patchouli.common.item;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

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
		List<ModelResourceLocation> models = new LinkedList();
		BookRegistry.INSTANCE.books.values().forEach(b -> models.add(new ModelResourceLocation(b.model, "inventory")));
		ModelBakery.registerItemVariants(book, models.toArray(new ModelResourceLocation[models.size()]));
		
        ModelLoader.setCustomMeshDefinition(book, (stack) -> {
    		Book book = ItemModBook.getBook(stack);
    		if(book != null)
    			return book.modelResourceLoc;
    		
    		return Book.DEFAULT_MODEL_RES;
        });
	}
	
}
