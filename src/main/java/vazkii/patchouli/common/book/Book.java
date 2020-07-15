package vazkii.patchouli.common.book;

import com.google.gson.annotations.SerializedName;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.forgespi.language.IModInfo;

import vazkii.patchouli.api.BookContentsReloadEvent;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.book.BookContents;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.ExternalBookContents;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Book {

	private static final String[] ORDINAL_SUFFIXES = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
	public static final ResourceLocation DEFAULT_MODEL = new ResourceLocation(Patchouli.MOD_ID, "book_brown");
	private static final ResourceLocation UNICODE_FONT_ID = new ResourceLocation(Patchouli.MOD_ID, "unicode_font");

	private static final Map<String, String> DEFAULT_MACROS = Util.make(() -> {
		Map<String, String> ret = new HashMap<>();
		ret.put("$(list", "$(li"); //  The lack of ) is intended
		ret.put("/$", "$()");
		ret.put("<br>", "$(br)");

		ret.put("$(item)", "$(#b0b)");
		ret.put("$(thing)", "$(#490)");
		return ret;
	});

	public transient BookContents contents;

	private transient boolean wasUpdated = false;

	public transient IModInfo owner;
	public transient Class<?> ownerClass;
	public transient ResourceLocation id;
	private transient ItemStack bookItem;

	public transient int textColor, headerColor, nameplateColor, linkColor, linkHoverColor, progressBarColor, progressBarBackground;

	public transient boolean isExtension = false;
	public transient List<Book> extensions = new LinkedList<>();
	public transient Book extensionTarget;

	public transient boolean isExternal;

	// JSON Loaded properties

	public String name = "";
	@SerializedName("landing_text") public String landingText = "patchouli.gui.lexicon.landing_info";

	@SerializedName("book_texture") public ResourceLocation bookTexture = new ResourceLocation(Patchouli.MOD_ID, "textures/gui/book_brown.png");

	@SerializedName("filler_texture") public ResourceLocation fillerTexture = new ResourceLocation(Patchouli.MOD_ID, "textures/gui/page_filler.png");

	@SerializedName("crafting_texture") public ResourceLocation craftingTexture = new ResourceLocation(Patchouli.MOD_ID, "textures/gui/crafting.png");

	public ResourceLocation model = DEFAULT_MODEL;

	@SerializedName("text_color") public String textColorRaw = "000000";
	@SerializedName("header_color") public String headerColorRaw = "333333";
	@SerializedName("nameplate_color") public String nameplateColorRaw = "FFDD00";
	@SerializedName("link_color") public String linkColorRaw = "0000EE";
	@SerializedName("link_hover_color") public String linkHoverColorRaw = "8800EE";

	@SerializedName("use_blocky_font") public boolean useBlockyFont = false;

	@SerializedName("progress_bar_color") public String progressBarColorRaw = "FFFF55";
	@SerializedName("progress_bar_background") public String progressBarBackgroundRaw = "DDDDDD";

	@SerializedName("open_sound") public ResourceLocation openSound = new ResourceLocation(Patchouli.MOD_ID, "book_open");

	@SerializedName("flip_sound") public ResourceLocation flipSound = new ResourceLocation(Patchouli.MOD_ID, "book_flip");

	@SerializedName("show_progress") public boolean showProgress = true;

	@SerializedName("index_icon") public String indexIconRaw = "";

	public String version = "0";
	public String subtitle = "";

	@SerializedName("creative_tab") public String creativeTab = "misc";

	@SerializedName("advancements_tab") public ResourceLocation advancementsTab;

	@SerializedName("dont_generate_book") public boolean noBook = false;

	@SerializedName("custom_book_item") public String customBookItem = "";

	@SerializedName("show_toasts") public boolean showToasts = true;

	@SerializedName("extend") public ResourceLocation extend;

	@SerializedName("allow_extensions") public boolean allowExtensions = true;

	@SerializedName("pause_game") public boolean pauseGame = false;

	public boolean i18n = false;

	public Map<String, String> macros = new HashMap<>();

	public void build(IModInfo owner, Class<?> ownerClass, ResourceLocation resource, boolean external) {
		this.owner = owner;
		this.ownerClass = ownerClass;
		this.id = resource;
		this.isExternal = external;

		isExtension = extend != null;

		if (!isExtension) {
			textColor = 0xFF000000 | Integer.parseInt(textColorRaw, 16);
			headerColor = 0xFF000000 | Integer.parseInt(headerColorRaw, 16);
			nameplateColor = 0xFF000000 | Integer.parseInt(nameplateColorRaw, 16);
			linkColor = 0xFF000000 | Integer.parseInt(linkColorRaw, 16);
			linkHoverColor = 0xFF000000 | Integer.parseInt(linkHoverColorRaw, 16);
			progressBarColor = 0xFF000000 | Integer.parseInt(progressBarColorRaw, 16);
			progressBarBackground = 0xFF000000 | Integer.parseInt(progressBarBackgroundRaw, 16);

			for (String m : DEFAULT_MACROS.keySet()) {
				if (!macros.containsKey(m)) {
					macros.put(m, DEFAULT_MACROS.get(m));
				}
			}
		}
	}

	public String getModNamespace() {
		return id.getNamespace();
	}

	public ItemStack getBookItem() {
		if (bookItem == null) {
			if (noBook) {
				bookItem = ItemStackUtil.loadStackFromString(customBookItem);
			} else {
				bookItem = ItemModBook.forBook(this);
			}
		}

		return bookItem;
	}

	@OnlyIn(Dist.CLIENT)
	public void markUpdated() {
		wasUpdated = true;
	}

	public boolean popUpdated() {
		boolean updated = wasUpdated;
		wasUpdated = false;
		return updated;
	}

	@OnlyIn(Dist.CLIENT)
	public void reloadContentsAndExtensions() {
		reloadContents();

		for (Book b : extensions) {
			b.reloadExtensionContents();
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void reloadContents() {
		if (contents == null) {
			contents = isExternal ? new ExternalBookContents(this) : new BookContents(this);
		}

		if (!isExtension) {
			contents.reload(false);
			MinecraftForge.EVENT_BUS.post(new BookContentsReloadEvent(this.id));
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void reloadExtensionContents() {
		if (isExtension) {
			if (extensionTarget == null) {
				extensionTarget = BookRegistry.INSTANCE.books.get(extend);

				if (extensionTarget == null) {
					throw new IllegalArgumentException("Extension Book " + id + " has no valid target");
				} else if (!extensionTarget.allowExtensions) {
					throw new IllegalArgumentException("Book " + extensionTarget.id + " doesn't allow extensions, so " + id + " can't resolve");
				}

				extensionTarget.extensions.add(this);

				contents.categories = extensionTarget.contents.categories;
				contents.entries = extensionTarget.contents.entries;
				contents.templates = extensionTarget.contents.templates;
				contents.recipeMappings = extensionTarget.contents.recipeMappings;
			}

			contents.reload(true);
			MinecraftForge.EVENT_BUS.post(new BookContentsReloadEvent(id));
		}
	}

	public final boolean advancementsEnabled() {
		return !PatchouliConfig.disableAdvancementLocking.get() && !PatchouliConfig.noAdvancementBooks.get().contains(id.toString());
	}

	@OnlyIn(Dist.CLIENT)
	public void reloadLocks(boolean suppressToasts) {
		contents.entries.values().forEach(BookEntry::updateLockStatus);
		contents.categories.values().forEach(c -> c.updateLockStatus(true));

		boolean updated = popUpdated();
		if (updated && !suppressToasts && advancementsEnabled() && showToasts) {
			ClientAdvancements.sendBookToast(this);
		}
	}

	public String getOwnerName() {
		return owner.getDisplayName();
	}

	@OnlyIn(Dist.CLIENT)
	public Style getFontStyle() {
		if (useBlockyFont) {
			return Style.EMPTY;
		} else {
			return Style.EMPTY.setFontId(UNICODE_FONT_ID);
		}
	}

	public IFormattableTextComponent getSubtitle() {
		ITextComponent editionStr;

		try {
			int ver = Integer.parseInt(version);
			if (ver == 0) {
				return new TranslationTextComponent(subtitle);
			}

			editionStr = new StringTextComponent(numberToOrdinal(ver));
		} catch (NumberFormatException e) {
			editionStr = new TranslationTextComponent("patchouli.gui.lexicon.dev_edition");
		}

		return new TranslationTextComponent("patchouli.gui.lexicon.edition_str", editionStr);
	}

	private static String numberToOrdinal(int i) {
		return i % 100 == 11 || i % 100 == 12 || i % 100 == 13 ? i + "th" : i + ORDINAL_SUFFIXES[i % 10];
	}

}
