package vazkii.patchouli.common.book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.client.book.BookContents;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.handler.AdvancementSyncHandler;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.util.ItemStackUtil;

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
		
		put("$(nocolor)", "$(0)");
		put("$(item)", "$(#b0b)");
		put("$(thing)", "$(#490)");
	}};
	
	@SideOnly(Side.CLIENT)
	public transient BookContents contents;

	@SideOnly(Side.CLIENT)
	private transient boolean wasUpdated = false;
	
	public transient ModContainer owner;
	public transient ResourceLocation resourceLoc;
	public transient ModelResourceLocation modelResourceLoc;
	private transient ItemStack bookItem;
	
	public transient ResourceLocation bookResource, fillerResource, craftingResource;
	public transient int textColor, headerColor, nameplateColor, linkColor, linkHoverColor;
	
	public transient boolean isExtension = false;
	public transient List<Book> extensions = new LinkedList();
	public transient Book extensionTarget;
	
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
	
	@SerializedName("dont_generate_book")
	public boolean noBook = false;
	@SerializedName("custom_book_item")
	public String customBookItem = "";
	
	@SerializedName("show_toasts")
	public boolean showToasts = true;
	
	@SerializedName("extend")
	public String extend = "";
	@SerializedName("allow_extensions")
	public boolean allowExtensions = true;
	
	public Map<String, String> macros = new HashMap();
	
	public void build(ModContainer owner, ResourceLocation resource) {
		this.owner = owner;
		this.resourceLoc = resource;
		
		isExtension = !extend.isEmpty();
		
		if(!isExtension) {
			modelResourceLoc = new ModelResourceLocation(model, "inventory");
			
			// minecraft has an advancement for every recipe, so we don't allow
			// tracking it to keep packets at a reasonable size
			advancementNamespaces.remove("minecraft"); 
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
	}
	
	public boolean usesAdvancements() {
		return !advancementNamespaces.isEmpty();
	}
	
	public String getModNamespace() {
		return resourceLoc.getResourceDomain();
	}
	
	public ItemStack getBookItem() {
		if(bookItem == null) {
			if(noBook)
				bookItem = ItemStackUtil.loadStackFromString(customBookItem);
			else bookItem = ItemModBook.forBook(this);
		}
		
		return bookItem;
	}
	
	@SideOnly(Side.CLIENT)
	public void markUpdated() {
		wasUpdated = true;
	}
	
	public boolean popUpdated() {
		boolean updated = wasUpdated;
		wasUpdated = false;
		return updated;
	}
	
	@SideOnly(Side.CLIENT)
	public void reloadContentsAndExtensions() {
		reloadContents();

		for(Book b : extensions)
			b.reloadExtensionContents();
	}
	
	@SideOnly(Side.CLIENT)
	public void reloadContents() {
		if(contents == null)
			contents = new BookContents(this);
	
		if(!isExtension)
			contents.reload(false);
	}
	
	@SideOnly(Side.CLIENT)
	public void reloadExtensionContents() {
		if(isExtension) {
			if(extensionTarget == null) {
				extensionTarget = BookRegistry.INSTANCE.books.get(new ResourceLocation(extend));
				
				if(extensionTarget == null)
					throw new IllegalArgumentException("Extension Book " + resourceLoc + " has no valid target");
				else if(!extensionTarget.allowExtensions)
					throw new IllegalArgumentException("Book " + extensionTarget.resourceLoc + " doesn't allow extensions, so " + resourceLoc + " can't resolve");
				
				extensionTarget.extensions.add(this);
				
				contents.categories = extensionTarget.contents.categories;
				contents.entries = extensionTarget.contents.entries;
				contents.recipeMappings = extensionTarget.contents.recipeMappings;
			}
			
			contents.reload(true);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void reloadLocks(boolean reset) {
		contents.entries.values().forEach((e) -> e.updateLockStatus());
		contents.categories.values().forEach((c) -> c.updateLockStatus(true));
		
		if(reset)
			popUpdated();
	}
	
	public String getOwnerName() {
		return owner.getName();
	}
	
}
