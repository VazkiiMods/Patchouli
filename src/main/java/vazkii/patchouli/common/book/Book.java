package vazkii.patchouli.common.book;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.client.book.BookContents;
import vazkii.patchouli.common.handler.AdvancementSyncHandler;
import vazkii.patchouli.common.item.ItemModBook;

public class Book {

	@SideOnly(Side.CLIENT)
	public transient BookContents contents;
	
	public transient ModContainer owner;
	public transient ResourceLocation resourceLoc;
	private transient ItemStack bookItem;
	
	public String name = "";
	public String landing_text = "patchouli.gui.lexicon.landing_info";
	public List<String> advancement_namespaces = new ArrayList();
	
	public void build(ModContainer owner, ResourceLocation resource) {
		this.owner = owner;
		this.resourceLoc = resource;
		
		AdvancementSyncHandler.trackedNamespaces.addAll(advancement_namespaces);
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
