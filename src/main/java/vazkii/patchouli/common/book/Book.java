package vazkii.patchouli.common.book;

import com.google.gson.annotations.SerializedName;
import net.minecraft.Util;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.book.BookContents;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookIcon;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.HashMap;
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

	private transient BookContents contents;

	private transient boolean wasUpdated = false;

	public transient ResourceLocation id;
	private transient ItemStack bookItem;

	public transient int textColor, headerColor, nameplateColor, linkColor, linkHoverColor, progressBarColor, progressBarBackground;

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

	@SerializedName("pause_game") public boolean pauseGame = false;

	public boolean i18n = false;

	public Map<String, String> macros = new HashMap<>();

	public void build(ResourceLocation resource) {
		this.id = resource;

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
	public void reloadContents(ResourceManager resourceManager) {
		BookContentsBuilder builder = new BookContentsBuilder();
		try {
			builder.loadFrom(this, resourceManager);
			contents = builder.build(this);
		} catch (Exception e) {
			Patchouli.LOGGER.error("Error compiling book {}, using empty contents", id, e);
			contents = BookContents.empty(this, e);
		}
	}

	public final boolean advancementsEnabled() {
		return !PatchouliConfig.disableAdvancementLocking.get() && !PatchouliConfig.noAdvancementBooks.get().contains(id.toString());
	}

	@OnlyIn(Dist.CLIENT)
	public void reloadLocks(boolean suppressToasts) {
		if (contents != null) {
			contents.entries.values().forEach(BookEntry::updateLockStatus);
			contents.categories.values().forEach(c -> c.updateLockStatus(true));
		}

		boolean updated = popUpdated();
		if (updated && !suppressToasts && advancementsEnabled() && showToasts) {
			ClientAdvancements.sendBookToast(this);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public Style getFontStyle() {
		if (useBlockyFont) {
			return Style.EMPTY;
		} else {
			return Style.EMPTY.withFont(UNICODE_FONT_ID);
		}
	}

	public MutableComponent getSubtitle() {
		Component editionStr;

		try {
			int ver = Integer.parseInt(version);
			if (ver == 0) {
				return new TranslatableComponent(subtitle);
			}

			editionStr = new TextComponent(numberToOrdinal(ver));
		} catch (NumberFormatException e) {
			editionStr = new TranslatableComponent("patchouli.gui.lexicon.dev_edition");
		}

		return new TranslatableComponent("patchouli.gui.lexicon.edition_str", editionStr);
	}

	@OnlyIn(Dist.CLIENT)
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

	@OnlyIn(Dist.CLIENT)
	public BookContents getContents() {
		return contents != null ? contents : BookContents.empty(this, null);
	}
}
