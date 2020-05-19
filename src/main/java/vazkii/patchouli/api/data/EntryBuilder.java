package vazkii.patchouli.api.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.api.data.page.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntryBuilder {

	private final CategoryBuilder parent;
	private final ResourceLocation id;
	private final String name;
	private final String category;
	private final String icon;
	private final List<AbstractPageBuilder<?>> pages = new ArrayList<>();
	private String advancement;
	private String flag;
	private Boolean priority;
	private Boolean secret;
	private Boolean readByDefault;
	private Integer sortnum;
	private String turnin;
	private Map<ItemStack, Integer> extraRecipeMappings;

	protected EntryBuilder(String id, String name, String icon, CategoryBuilder parent) {
		this.id = new ResourceLocation(parent.getId().getNamespace(), id);
		this.name = name;
		this.category = parent.getId().getPath();
		this.icon = icon;
		this.parent = parent;
	}

	protected EntryBuilder(String id, String name, ItemStack icon, CategoryBuilder parent) {
		this(id, name, PatchouliAPI.instance.serializeItemStack(icon), parent);
	}

	JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("name", name);
		json.addProperty("category", category);
		json.addProperty("icon", icon);
		JsonArray pages = new JsonArray();
		for (AbstractPageBuilder<?> page : this.pages) {
			pages.add(page.toJson());
		}
		json.add("pages", pages);
		if (advancement != null) {
			json.addProperty("advancement", advancement);
		}
		if (flag != null) {
			json.addProperty("flag", flag);
		}
		if (priority != null) {
			json.addProperty("priority", priority);
		}
		if (secret != null) {
			json.addProperty("secret", secret);
		}
		if (readByDefault != null) {
			json.addProperty("read_by_default", readByDefault);
		}
		if (sortnum != null) {
			json.addProperty("sortnum", sortnum);
		}
		if (turnin != null) {
			json.addProperty("turnin", turnin);
		}
		if (extraRecipeMappings != null) {
			JsonObject mappings = new JsonObject();
			for (Map.Entry<ItemStack, Integer> entry : extraRecipeMappings.entrySet()) {
				mappings.addProperty(PatchouliAPI.instance.serializeItemStack(entry.getKey()), entry.getValue());
			}
			json.add("extra_recipe_mappings", mappings);
		}
		this.serialize(json);
		return json;
	}

	protected void serialize(JsonObject json) {}

	public CategoryBuilder build() {
		return parent;
	}

	public EntryBuilder addSimpleTextPage(String text) {
		return addPage(new TextPageBuilder(text, this)).build();
	}

	public EntryBuilder addSimpleTextPage(String text, String title) {
		return addPage(new TextPageBuilder(text, title, this)).build();
	}

	public TextPageBuilder addTextPage(String text) {
		return addPage(new TextPageBuilder(text, this));
	}

	public TextPageBuilder addTextPage(String text, String title) {
		return addPage(new TextPageBuilder(text, title, this));
	}

	public ImagePageBuilder addImagePage(ResourceLocation image) {
		return addPage(new ImagePageBuilder(image, this));
	}

	public CraftingPageBuilder addCraftingPage(ResourceLocation recipe) {
		return addPage(new CraftingPageBuilder(recipe, this));
	}

	public SmeltingPageBuilder addSmeltingPage(ResourceLocation recipe) {
		return addPage(new SmeltingPageBuilder(recipe, this));
	}

	public EntityPageBuilder addEntityPage(String entity) {
		return addPage(new EntityPageBuilder(entity, this));
	}

	public EntityPageBuilder addEntityPage(ResourceLocation entity) {
		return addEntityPage(entity.toString());
	}

	public SpotlightPageBuilder addSpotlightPage(ItemStack stack) {
		return addPage(new SpotlightPageBuilder(stack, this));
	}

	public LinkPageBuilder addLinkPage(String url, String linkText) {
		return addPage(new LinkPageBuilder(url, linkText, this));
	}

	public EmptyPageBuilder addEmptyPage() {
		return addPage(new EmptyPageBuilder(true, this));
	}

	public EmptyPageBuilder addEmptyPage(boolean drawFiller) {
		return addPage(new EmptyPageBuilder(drawFiller, this));
	}

	public <T extends AbstractPageBuilder<T>> T addPage(T builder) {
		pages.add(builder);
		return builder;
	}

	public EntryBuilder setAdvancement(String advancement) {
		this.advancement = advancement;
		return this;
	}

	public EntryBuilder setFlag(String flag) {
		this.flag = flag;
		return this;
	}

	public EntryBuilder setPriority(boolean priority) {
		this.priority = priority;
		return this;
	}

	public EntryBuilder setSecret(boolean secret) {
		this.secret = secret;
		return this;
	}

	public EntryBuilder setReadByDefault(boolean readByDefault) {
		this.readByDefault = readByDefault;
		return this;
	}

	public EntryBuilder setSortnum(int sortnum) {
		this.sortnum = sortnum;
		return this;
	}

	public EntryBuilder setTurnin(String turnin) {
		this.turnin = turnin;
		return this;
	}

	public EntryBuilder addExtraRecipeMapping(ItemStack stack, int index) {
		if (this.extraRecipeMappings == null) {
			this.extraRecipeMappings = new HashMap<>();
		}
		this.extraRecipeMappings.put(stack, index);
		return this;
	}

	protected ResourceLocation getId() {
		return id;
	}
}
