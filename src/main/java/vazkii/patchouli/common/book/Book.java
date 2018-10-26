package vazkii.patchouli.common.book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;
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
	@SerializedName("landing_text")
	public String landingText = "patchouli.gui.lexicon.landing_info";

	@SerializedName("advancement_namespaces")
	public List<String> advancementNamespaces = new ArrayList();

	@SerializedName("book_texture")
	public String bookTexture = Patchouli.PREFIX + "textures/gui/book_brown";
	@SerializedName("filler_texture")
	public String fillerTexture = Patchouli.PREFIX + "textures/gui/page_filler";
	@SerializedName("crafting_texture")
	public String craftingTexture = Patchouli.PREFIX + "textures/gui/crafting";

	public String model = DEFAULT_MODEL;

	@SerializedName("text_color")
	public String textColorRaw = "000000";
	@SerializedName("header_color")
	public String headerColorRaw = "333333";
	@SerializedName("nameplate_color")
	public String nameplateColorRaw = "FFDD00";
	@SerializedName("link_color")
	public String linkColorRaw = "0000EE";
	@SerializedName("link_hover_color")
	public String linkHoverColorRaw = "8800EE";

	public String version = "0";
	public String subtitle = "";

	@SerializedName("creative_tab")
	public String creativeTab = "misc";
	@SerializedName("advancements_tab")
	public String advancementsTab = "";
	
	@SerializedName("no_book_item")
	public boolean noBook = false;
	
	public Map<String, String> macros = new HashMap();
	
	public void build(ModContainer owner, ResourceLocation resource) {
		this.owner = owner;
		this.resourceLoc = resource;
		
		modelResourceLoc = new ModelResourceLocation(model, "inventory");
		
		AdvancementSyncHandler.trackedNamespaces.addAll(advancementNamespaces);
		
		bookResource = new ResourceLocation(bookTexture + ".png");
		fillerResource = new ResourceLocation(fillerTexture + ".png");
		craftingResource = new ResourceLocation(craftingTexture + ".png");
		
		textColor = Integer.parseInt(textColorRaw, 16);
		headerColor = Integer.parseInt(headerColorRaw, 16);
		nameplateColor = Integer.parseInt(nameplateColorRaw, 16);
		linkColor = Integer.parseInt(linkColorRaw, 16);
		linkHoverColor = Integer.parseInt(linkHoverColorRaw, 16);

		for(String m : DEFAULT_MACROS.keySet())
			if(!macros.containsKey(m))
				macros.put(m, DEFAULT_MACROS.get(m));
	}
	
	public boolean usesAdvancements() {
		return !advancementNamespaces.isEmpty();
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
