package vazkii.patchouli.common.book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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
	
	public static final String DEFAULT_MODEL = Patchouli.PREFIX + "book_brown";
	public static final ModelResourceLocation DEFAULT_MODEL_RES = new ModelResourceLocation(DEFAULT_MODEL, "inventory");
	
	private static final Map<String, String> DEFAULT_MACROS = new HashMap() {{
		put("$(obf)", "$(k)");
		put("$(bold)", "$(l)");
		put("$(strike)", "$(m)");
		put("$(italic)", "$(o)");
		put("$(italics)", "$(o)");
		put("$(list", "$(li"); //  The lack of ) is intended
		put("$(reset)", "$()");
		put("$(clear)", "$()");
		put("$(2br)", "$(br2)");
		put("$(p)", "$(br2)");
		
		put("/$", "$()");
		put("<br>", "$(br)");
		
		put("$(item)", "$(#b0b)");
		put("$(thing)", "$(#490)");
	}};
	
	@SideOnly(Side.CLIENT)
	public transient BookContents contents;
	
	public transient ModContainer owner;
	public transient ResourceLocation resourceLoc;
	public transient ModelResourceLocation modelResourceLoc;
	private transient ItemStack bookItem;
	
	public transient ResourceLocation bookResource, fillerResource, craftingResource;
	public transient int textColor, headerColor, nameplateColor, linkColor, linkHoverColor;
	
	// JSON Loaded properties
	public String name = "";
	public String landing_text = "patchouli.gui.lexicon.landing_info";
	
	public List<String> advancement_namespaces = new ArrayList();
	
	public String book_texture = Patchouli.PREFIX + "textures/gui/book_brown";
	public String filler_texture = Patchouli.PREFIX + "textures/gui/page_filler";
	public String crafting_texture = Patchouli.PREFIX + "textures/gui/crafting";

	public String model = DEFAULT_MODEL;
	
	public String text_color = "000000";
	public String header_color = "333333";
	public String nameplate_color = "FFDD00";
	public String link_color = "0000EE";
	public String link_hover_color = "8800EE";

	public String version = "0";
	public String subtitle = "";
	
	public String creative_tab = "misc";
	
	public Map<String, String> macros = new HashMap();
	
	public void build(ModContainer owner, ResourceLocation resource) {
		this.owner = owner;
		this.resourceLoc = resource;
		
		modelResourceLoc = new ModelResourceLocation(model, "inventory");
		
		AdvancementSyncHandler.trackedNamespaces.addAll(advancement_namespaces);
		
		bookResource = new ResourceLocation(book_texture + ".png");
		fillerResource = new ResourceLocation(filler_texture + ".png");
		craftingResource = new ResourceLocation(crafting_texture + ".png");
		
		textColor = Integer.parseInt(text_color, 16);
		headerColor = Integer.parseInt(header_color, 16);
		nameplateColor = Integer.parseInt(nameplate_color, 16);
		linkColor = Integer.parseInt(link_color, 16);
		linkHoverColor = Integer.parseInt(link_hover_color, 16);

		for(String m : DEFAULT_MACROS.keySet())
			if(!macros.containsKey(m))
				macros.put(m, DEFAULT_MACROS.get(m));
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
