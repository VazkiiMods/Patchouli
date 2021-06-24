package vazkii.patchouli.common.book;

import com.google.gson.annotations.SerializedName;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.book.*;
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
	public static final Identifier DEFAULT_MODEL = new Identifier(Patchouli.MOD_ID, "book_brown");
	private static final Identifier UNICODE_FONT_ID = new Identifier(Patchouli.MOD_ID, "unicode_font");

	private static final Map<String, String> DEFAULT_MACROS = Util.make(() -> {
		Map<String, String> ret = new HashMap<>();
		ret.put("$(list", "$(li"); //  The lack of ) is intended
		ret.put("/$", "$()");
		ret.put("<br>", "$(br)");

		ret.put("$(item)", "$(#b0b)");
		ret.put("$(thing)", "$(#490)");
		return ret;
	});

	private transient BookContents contents;

	private transient boolean wasUpdated = false;

	public transient ModContainer owner;
	public transient Identifier id;
	private transient ItemStack bookItem;

	public transient int textColor, headerColor, nameplateColor, linkColor, linkHoverColor, progressBarColor, progressBarBackground;

	public transient boolean isExtension = false;
	public transient List<Book> extensions = new LinkedList<>();
	public transient Book extensionTarget;

	public transient boolean isExternal;

	// JSON Loaded properties

	public String name = "";
	@SerializedName("landing_text") public String landingText = "patchouli.gui.lexicon.landing_info";

	@SerializedName("book_texture") public Identifier bookTexture = new Identifier(Patchouli.MOD_ID, "textures/gui/book_brown.png");

	@SerializedName("filler_texture") public Identifier fillerTexture = new Identifier(Patchouli.MOD_ID, "textures/gui/page_filler.png");

	@SerializedName("crafting_texture") public Identifier craftingTexture = new Identifier(Patchouli.MOD_ID, "textures/gui/crafting.png");

	public Identifier model = DEFAULT_MODEL;

	@SerializedName("text_color") public String textColorRaw = "000000";
	@SerializedName("header_color") public String headerColorRaw = "333333";
	@SerializedName("nameplate_color") public String nameplateColorRaw = "FFDD00";
	@SerializedName("link_color") public String linkColorRaw = "0000EE";
	@SerializedName("link_hover_color") public String linkHoverColorRaw = "8800EE";

	@SerializedName("use_blocky_font") public boolean useBlockyFont = false;

	@SerializedName("progress_bar_color") public String progressBarColorRaw = "FFFF55";
	@SerializedName("progress_bar_background") public String progressBarBackgroundRaw = "DDDDDD";

	@SerializedName("open_sound") public Identifier openSound = new Identifier(Patchouli.MOD_ID, "book_open");

	@SerializedName("flip_sound") public Identifier flipSound = new Identifier(Patchouli.MOD_ID, "book_flip");

	@SerializedName("show_progress") public boolean showProgress = true;

	@SerializedName("index_icon") public String indexIconRaw = "";

	public String version = "0";
	public String subtitle = "";

	@SerializedName("creative_tab") public String creativeTab = "misc";

	@SerializedName("advancements_tab") public Identifier advancementsTab;

	@SerializedName("dont_generate_book") public boolean noBook = false;

	@SerializedName("custom_book_item") public String customBookItem = "";

	@SerializedName("show_toasts") public boolean showToasts = true;

	@SerializedName("extend") public Identifier extend;

	@SerializedName("allow_extensions") public boolean allowExtensions = true;

	@SerializedName("pause_game") public boolean pauseGame = false;

	@SerializedName("use_resource_pack")
	public boolean useResourcePack = false;

	public boolean i18n = false;

	public Map<String, String> macros = new HashMap<>();

	public void build(ModContainer owner, Identifier resource, boolean external) {
		this.owner = owner;
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

	@Environment(EnvType.CLIENT)
	public void markUpdated() {
		wasUpdated = true;
	}

	public boolean popUpdated() {
		boolean updated = wasUpdated;
		wasUpdated = false;
		return updated;
	}

	@Environment(EnvType.CLIENT)
	public void reloadContents() {
		if (!isExtension) {
			BookContentsBuilder builder = new BookContentsBuilder();
			try {
				builder.loadFrom(this);
			} catch (Exception e) {
				Patchouli.LOGGER.error("Error loading book {}, using empty contents and ignoring extensions", id, e);
				contents = BookContents.empty(this, e);
			}

			for (Book extension : extensions) {
				try {
					builder.loadFrom(extension);
				} catch (Exception e) {
					Patchouli.LOGGER.error("Error loading extending book {} with addon book {}, skipping", id, extension.id, e);
				}
			}

			try {
				contents = builder.build(this);
			} catch (Exception e) {
				Patchouli.LOGGER.error("Error compiling book {}, using empty contents", id, e);
				contents = BookContents.empty(this, e);
			}
		}
	}

	public final boolean advancementsEnabled() {
		return !PatchouliConfig.disableAdvancementLocking.getValue() && !PatchouliConfig.noAdvancementBooks.getValue().contains(id.toString());
	}

	@Environment(EnvType.CLIENT)
	public void reloadLocks(boolean suppressToasts) {
		contents.entries.values().forEach(BookEntry::updateLockStatus);
		contents.categories.values().forEach(c -> c.updateLockStatus(true));

		boolean updated = popUpdated();
		if (updated && !suppressToasts && advancementsEnabled() && showToasts) {
			ClientAdvancements.sendBookToast(this);
		}
	}

	public String getOwnerName() {
		return owner.getMetadata().getName();
	}

	@Environment(EnvType.CLIENT)
	public Style getFontStyle() {
		if (useBlockyFont) {
			return Style.EMPTY;
		} else {
			return Style.EMPTY.withFont(UNICODE_FONT_ID);
		}
	}

	public MutableText getSubtitle() {
		Text editionStr;

		try {
			int ver = Integer.parseInt(version);
			if (ver == 0) {
				return new TranslatableText(subtitle);
			}

			editionStr = new LiteralText(numberToOrdinal(ver));
		} catch (NumberFormatException e) {
			editionStr = new TranslatableText("patchouli.gui.lexicon.dev_edition");
		}

		return new TranslatableText("patchouli.gui.lexicon.edition_str", editionStr);
	}

	@Environment(EnvType.CLIENT)
	public BookIcon getIcon() {
		if (indexIconRaw == null || indexIconRaw.isEmpty()) {
			return new BookIcon(getBookItem());
		} else {
			return BookIcon.from(indexIconRaw);
		}
	}

	private static String numberToOrdinal(int i) {
		return i % 100 == 11 || i % 100 == 12 || i % 100 == 13 ? i + "th" : i + ORDINAL_SUFFIXES[i % 10];
	}

	@Environment(EnvType.CLIENT)
	public BookContents getContents() {
		if (isExtension) {
			return extensionTarget.getContents();
		} else {
			return contents != null ? contents : BookContents.empty(this, null);
		}
	}
}
