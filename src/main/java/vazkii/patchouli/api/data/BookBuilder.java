package vazkii.patchouli.api.data;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import vazkii.patchouli.api.PatchouliAPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class BookBuilder {

	private final ResourceLocation id;
	private final String displayName;
	private final String landingText;
	private final List<CategoryBuilder> categories = new ArrayList<>();
	private String bookTexture;
	private String fillerTexture;
	private String craftingTexture;
	private String model;
	private String textColor;
	private String headerColor;
	private String nameplateColor;
	private String linkColor;
	private String linkHoverColor;
	private String progressBarColor;
	private String progressBarBackground;
	private String openSound;
	private String flipSound;
	private String indexIcon;
	private Boolean showProgress;
	private String version;
	private String subtitle;
	private String creativeTab;
	private String advancementsTab;
	private Boolean dontGenerateBook;
	private String customBookItem;
	private Boolean showToasts;
	private Boolean useBlockyFont;
	private Boolean i18n;

	protected BookBuilder(String modid, String id, String displayName, String landingText) {
		this(new ResourceLocation(modid, id), displayName, landingText);
	}

	protected BookBuilder(ResourceLocation id, String displayName, String landingText) {
		this.id = id;
		this.displayName = displayName;
		this.landingText = landingText;
	}

	JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("name", displayName);
		json.addProperty("landing_text", landingText);
		if (bookTexture != null) {
			json.addProperty("book_texture", bookTexture);
		}
		if (fillerTexture != null) {
			json.addProperty("filler_texture", fillerTexture);
		}
		if (craftingTexture != null) {
			json.addProperty("crafting_texture", craftingTexture);
		}
		if (model != null) {
			json.addProperty("model", model);
		}
		if (textColor != null) {
			json.addProperty("text_color", textColor);
		}
		if (headerColor != null) {
			json.addProperty("header_color", headerColor);
		}
		if (nameplateColor != null) {
			json.addProperty("nameplate_color", nameplateColor);
		}
		if (linkColor != null) {
			json.addProperty("link_color", linkColor);
		}
		if (linkHoverColor != null) {
			json.addProperty("link_hover_color", linkHoverColor);
		}
		if (progressBarColor != null) {
			json.addProperty("progress_bar_color", progressBarColor);
		}
		if (progressBarBackground != null) {
			json.addProperty("progress_bar_background", progressBarBackground);
		}
		if (openSound != null) {
			json.addProperty("open_sound", openSound);
		}
		if (flipSound != null) {
			json.addProperty("flip_sound", flipSound);
		}
		if (indexIcon != null) {
			json.addProperty("index_icon", indexIcon);
		}
		if (showProgress != null) {
			json.addProperty("show_progress", showProgress);
		}
		if (version != null) {
			json.addProperty("version", version);
		}
		if (subtitle != null) {
			json.addProperty("subtitle", subtitle);
		}
		if (creativeTab != null) {
			json.addProperty("creative_tab", creativeTab);
		}
		if (advancementsTab != null) {
			json.addProperty("advancements_tab", advancementsTab);
		}
		if (dontGenerateBook != null) {
			json.addProperty("dont_generate_book", dontGenerateBook);
		}
		if (customBookItem != null) {
			json.addProperty("custom_book_item", customBookItem);
		}
		if (showToasts != null) {
			json.addProperty("show_toasts", showToasts);
		}
		if (useBlockyFont != null) {
			json.addProperty("use_blocky_font", useBlockyFont);
		}
		if (i18n != null) {
			json.addProperty("i18n", i18n);
		}
		this.serialize(json);
		return json;
	}

	protected void serialize(JsonObject json) {}

	protected List<CategoryBuilder> getCategories() {
		return Collections.unmodifiableList(categories);
	}

	public void build(Consumer<BookBuilder> consumer) {
		consumer.accept(this);
	}

	public CategoryBuilder addCategory(String id, String name, String description, ItemStack icon) {
		return this.addCategory(new CategoryBuilder(id, name, description, icon, this));
	}

	public CategoryBuilder addCategory(String id, String name, String description, String icon) {
		return this.addCategory(new CategoryBuilder(id, name, description, icon, this));
	}

	protected <T extends CategoryBuilder> T addCategory(T builder) {
		this.categories.add(builder);
		return builder;
	}

	public BookBuilder setBookTexture(String bookTexture) {
		this.bookTexture = bookTexture;
		return this;
	}

	public BookBuilder setFillerTexture(String fillerTexture) {
		this.fillerTexture = fillerTexture;
		return this;
	}

	public BookBuilder setCraftingTexture(String craftingTexture) {
		this.craftingTexture = craftingTexture;
		return this;
	}

	public BookBuilder setModel(ResourceLocation model) {
		return this.setModel(model.toString());
	}

	public BookBuilder setModel(String model) {
		this.model = model;
		return this;
	}

	public BookBuilder setTextColor(String textColor) {
		this.textColor = textColor;
		return this;
	}

	public BookBuilder setHeaderColor(String headerColor) {
		this.headerColor = headerColor;
		return this;
	}

	public BookBuilder setNameplateColor(String nameplateColor) {
		this.nameplateColor = nameplateColor;
		return this;
	}

	public BookBuilder setLinkColor(String linkColor) {
		this.linkColor = linkColor;
		return this;
	}

	public BookBuilder setLinkHoverColor(String linkHoverColor) {
		this.linkHoverColor = linkHoverColor;
		return this;
	}

	public BookBuilder setProgressBarColor(String progressBarColor) {
		this.progressBarColor = progressBarColor;
		return this;
	}

	public BookBuilder setProgressBarBackground(String progressBarBackground) {
		this.progressBarBackground = progressBarBackground;
		return this;
	}

	public BookBuilder setOpenSound(String openSound) {
		this.openSound = openSound;
		return this;
	}

	public BookBuilder setFlipSound(String flipSound) {
		this.flipSound = flipSound;
		return this;
	}

	public BookBuilder setIndexIcon(String indexIcon) {
		this.indexIcon = indexIcon;
		return this;
	}

	public BookBuilder setIndexIcon(ItemStack indexIcon) {
		this.indexIcon = PatchouliAPI.instance.serializeItemStack(indexIcon);
		return this;
	}

	public BookBuilder setVersion(String version) {
		this.version = version;
		return this;
	}

	public BookBuilder setSubtitle(String subtitle) {
		this.subtitle = subtitle;
		return this;
	}

	public BookBuilder setCreativeTab(String creativeTab) {
		this.creativeTab = creativeTab;
		return this;
	}

	public BookBuilder setAdvancementsTab(String advancementsTab) {
		this.advancementsTab = advancementsTab;
		return this;
	}

	public BookBuilder setCustomBookItem(ItemStack customBookItem) {
		this.customBookItem = PatchouliAPI.instance.serializeItemStack(customBookItem);
		return this;
	}

	public BookBuilder setShowProgress(boolean showProgress) {
		this.showProgress = showProgress;
		return this;
	}

	public BookBuilder setDontGenerateBook(boolean dontGenerateBook) {
		this.dontGenerateBook = dontGenerateBook;
		return this;
	}

	public BookBuilder setShowToasts(boolean showToasts) {
		this.showToasts = showToasts;
		return this;
	}

	public BookBuilder setUseBlockyFont(boolean useBlockyFont) {
		this.useBlockyFont = useBlockyFont;
		return this;
	}

	public BookBuilder setI18n(boolean i18n) {
		this.i18n = i18n;
		return this;
	}

	protected ResourceLocation getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof BookBuilder && Objects.equals(((BookBuilder) obj).getId(), this.getId());
	}
}
