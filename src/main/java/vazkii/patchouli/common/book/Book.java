package vazkii.patchouli.common.book;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.client.book.BookContents;

public class Book {

	@SideOnly(Side.CLIENT)
	public transient BookContents contents;
	
	public final transient ModContainer owner;
	public final transient ResourceLocation resource; 
	
	public String name;
	public List<String> advancement_namespaces = new ArrayList();
	
	public Book(ModContainer owner, ResourceLocation resource) {
		this.owner = owner;
		this.resource = resource;
	}
	
	public boolean usesAdvancements() {
		return !advancement_namespaces.isEmpty();
	}
	
	@SideOnly(Side.CLIENT)
	public void reloadContents() {
		if(contents == null)
			contents = new BookContents(this);
		
		contents.reload();
	}
	
}
