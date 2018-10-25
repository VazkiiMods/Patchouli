package vazkii.patchouli.common.book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.Patchouli;
import vazkii.patchouli.client.book.BookContents;
import vazkii.patchouli.common.handler.AdvancementSyncHandler;
import vazkii.patchouli.common.item.ItemModBook;

public class Book {

	@SideOnly(Side.CLIENT)
	public transient BookContents contents;
	
	public transient ModContainer owner;
	public transient ResourceLocation resourceLoc;
	private transient ItemStack bookItem;
	
	public transient ResourceLocation bookResource, fillerResource, craftingResource;
	public transient int textColor, headerColor, frameColor;
	
	// JSON Loaded properties
	public String name = "";
	public String landing_text = "patchouli.gui.lexicon.landing_info";
	public List<String> advancement_namespaces = new ArrayList();
	
	public String book_texture = Patchouli.PREFIX + "textures/gui/book_brown";
	public String filler_texture = Patchouli.PREFIX + "textures/gui/page_filler";
	public String crafting_texture = Patchouli.PREFIX + "textures/gui/crafting";
	
	public String text_color = "000000";
	public String header_color = "333333";
	public String frame_color = "FFDD00";
	
	public String version = "0";
	public String subtitle = "";
	
	public Map<String, String> macros = new HashMap();
	
	public void build(ModContainer owner, ResourceLocation resource) {
		this.owner = owner;
		this.resourceLoc = resource;
		
		AdvancementSyncHandler.trackedNamespaces.addAll(advancement_namespaces);
		
		bookResource = new ResourceLocation(book_texture + ".png");
		fillerResource = new ResourceLocation(filler_texture + ".png");
		craftingResource = new ResourceLocation(crafting_texture + ".png");
		
		textColor = Integer.parseInt(text_color, 16);
		headerColor = Integer.parseInt(header_color, 16);
		frameColor = Integer.parseInt(frame_color, 16);
	}
	
	public boolean usesAdvancements() {
		return !advancement_namespaces.isEmpty();
	}
	
	public String getModNamespace() {
		return resourceLoc.getResourceDomain();
	}
	
	public ItemStack getBookItem() {
		if(bookItem == null)
			bookItem = ItemModBook.forBook(this);
		
		return bookItem;
	}
	
	@SideOnly(Side.CLIENT)
	public void reloadContents() {
		if(contents == null)
			contents = new BookContents(this);
		
		contents.reload();
	}
	
}
