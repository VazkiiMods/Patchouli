package vazkii.patchouli.client.book;

import com.google.gson.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.client.model.ModelLoader;

import vazkii.patchouli.client.book.page.*;
import vazkii.patchouli.client.book.template.BookTemplate;
import vazkii.patchouli.client.book.template.TemplateComponent;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.util.SerializationUtil;

import javax.annotation.Nullable;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ClientBookRegistry {

	public final Map<ResourceLocation, Class<? extends BookPage>> pageTypes = new HashMap<>();

	public final Gson gson = new GsonBuilder()
			.registerTypeHierarchyAdapter(BookPage.class, new LexiconPageAdapter())
			.registerTypeHierarchyAdapter(TemplateComponent.class, new TemplateComponentAdapter())
			.create();
	public String currentLang;

	public static final ClientBookRegistry INSTANCE = new ClientBookRegistry();
	private boolean loaded = false;

	private ClientBookRegistry() {}

	public void init() {
		addPageTypes();
	}

	private void addPageTypes() {
		pageTypes.put(new ResourceLocation(Patchouli.MOD_ID, "text"), PageText.class);
		pageTypes.put(new ResourceLocation(Patchouli.MOD_ID, "crafting"), PageCrafting.class);
		pageTypes.put(new ResourceLocation(Patchouli.MOD_ID, "smelting"), PageSmelting.class);
		pageTypes.put(new ResourceLocation(Patchouli.MOD_ID, "blasting"), PageBlasting.class);
		pageTypes.put(new ResourceLocation(Patchouli.MOD_ID, "smoking"), PageSmoking.class);
		pageTypes.put(new ResourceLocation(Patchouli.MOD_ID, "campfire"), PageCampfireCooking.class);
		pageTypes.put(new ResourceLocation(Patchouli.MOD_ID, "smithing"), PageSmithing.class);
		pageTypes.put(new ResourceLocation(Patchouli.MOD_ID, "stonecutting"), PageStonecutting.class);
		pageTypes.put(new ResourceLocation(Patchouli.MOD_ID, "image"), PageImage.class);
		pageTypes.put(new ResourceLocation(Patchouli.MOD_ID, "spotlight"), PageSpotlight.class);
		pageTypes.put(new ResourceLocation(Patchouli.MOD_ID, "empty"), PageEmpty.class);
		pageTypes.put(new ResourceLocation(Patchouli.MOD_ID, "multiblock"), PageMultiblock.class);
		pageTypes.put(new ResourceLocation(Patchouli.MOD_ID, "link"), PageLink.class);
		pageTypes.put(new ResourceLocation(Patchouli.MOD_ID, "relations"), PageRelations.class);
		pageTypes.put(new ResourceLocation(Patchouli.MOD_ID, "entity"), PageEntity.class);
		pageTypes.put(new ResourceLocation(Patchouli.MOD_ID, "quest"), PageQuest.class);
	}

	public void reload() {
		var mc = Minecraft.getInstance();
		currentLang = mc.getLanguageManager().getSelected().getCode();
		reloadContents(mc.getResourceManager());
	}

	public void refreshModels() {
		BookRegistry.INSTANCE.getBooks().stream()
				.map(b -> new ModelResourceLocation(b.model, "inventory"))
				.forEach(ModelLoader::addSpecialModel);
	}

	public void reloadResources() {
		Minecraft.getInstance().reloadResourcePacks();
	}

	public void reloadLocks(boolean suppressToasts) {
		BookRegistry.INSTANCE.getBooks().forEach(b -> b.reloadLocks(suppressToasts));
	}

	public void reloadContents(ResourceManager resourceManager) {
		PatchouliConfig.reloadBuiltinFlags();
		for (Book book : BookRegistry.INSTANCE.getBooks()) {
			book.reloadContents(resourceManager);
		}
		ClientBookRegistry.INSTANCE.reloadLocks(false);
		loaded = true;
	}

	/**
	 * @param entryId Entry to force to the top of the stack
	 * @param page    Page in the entry to force. Ignored if {@code entryId} is null.
	 */
	public void displayBookGui(ResourceLocation bookStr, @Nullable ResourceLocation entryId, int page) {
		Minecraft mc = Minecraft.getInstance();
		currentLang = mc.getLanguageManager().getSelected().getCode();

		BookRegistry.INSTANCE.getBook(bookStr).ifPresent(book -> {
			var contents = book.getContents();
			contents.checkValidCurrentEntry();

			if (entryId != null) {
				contents.setTopEntry(entryId, page);
			}

			contents.openLexiconGui(contents.getCurrentGui(), false);

			if (mc.player != null) {
				SoundEvent sfx = PatchouliSounds.getSound(book.openSound, PatchouliSounds.BOOK_OPEN);
				mc.player.playSound(sfx, 1F, (float) (0.7 + Math.random() * 0.4));
			}
		});
	}

	public static class LexiconPageAdapter implements JsonDeserializer<BookPage> {

		@Override
		public BookPage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();
			JsonPrimitive prim = (JsonPrimitive) obj.get("type");
			String string = prim.getAsString();
			if (string.indexOf(':') < 0) {
				string = Patchouli.MOD_ID + ":" + string;
			}
			ResourceLocation type = new ResourceLocation(string);
			Class<? extends BookPage> clazz = ClientBookRegistry.INSTANCE.pageTypes.get(type);
			if (clazz == null) {
				clazz = PageTemplate.class;
			}

			BookPage page = SerializationUtil.RAW_GSON.fromJson(json, clazz);
			page.sourceObject = obj;

			return page;
		}

	}

	public static class TemplateComponentAdapter implements JsonDeserializer<TemplateComponent> {

		@Override
		public TemplateComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();
			JsonPrimitive prim = (JsonPrimitive) obj.get("type");
			ResourceLocation type = new ResourceLocation(prim.getAsString());
			Class<? extends TemplateComponent> clazz = BookTemplate.componentTypes.get(type);

			if (clazz == null) {
				return null;
			}

			TemplateComponent component = SerializationUtil.RAW_GSON.fromJson(json, clazz);
			component.sourceObject = obj;

			return component;
		}

	}
}
