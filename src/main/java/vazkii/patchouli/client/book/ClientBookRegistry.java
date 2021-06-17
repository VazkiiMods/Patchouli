package vazkii.patchouli.client.book;

import com.google.gson.*;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import vazkii.patchouli.client.book.page.*;
import vazkii.patchouli.client.book.template.BookTemplate;
import vazkii.patchouli.client.book.template.TemplateComponent;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.util.SerializationUtil;

import javax.annotation.Nullable;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ClientBookRegistry {

	public final Map<Identifier, Class<? extends BookPage>> pageTypes = new HashMap<>();

	public final Gson gson = new GsonBuilder()
			.registerTypeHierarchyAdapter(BookPage.class, new LexiconPageAdapter())
			.registerTypeHierarchyAdapter(TemplateComponent.class, new TemplateComponentAdapter())
			.create();
	public String currentLang;

	public static final ClientBookRegistry INSTANCE = new ClientBookRegistry();

	private ClientBookRegistry() {}

	public void init() {
		addPageTypes();
	}

	private void addPageTypes() {
		pageTypes.put(new Identifier(Patchouli.MOD_ID, "text"), PageText.class);
		pageTypes.put(new Identifier(Patchouli.MOD_ID, "crafting"), PageCrafting.class);
		pageTypes.put(new Identifier(Patchouli.MOD_ID, "smelting"), PageSmelting.class);
		pageTypes.put(new Identifier(Patchouli.MOD_ID, "blasting"), PageBlasting.class);
		pageTypes.put(new Identifier(Patchouli.MOD_ID, "smoking"), PageSmoking.class);
		pageTypes.put(new Identifier(Patchouli.MOD_ID, "campfire"), PageCampfireCooking.class);
		pageTypes.put(new Identifier(Patchouli.MOD_ID, "smithing"), PageSmithing.class);
		pageTypes.put(new Identifier(Patchouli.MOD_ID, "stonecutting"), PageStonecutting.class);
		pageTypes.put(new Identifier(Patchouli.MOD_ID, "image"), PageImage.class);
		pageTypes.put(new Identifier(Patchouli.MOD_ID, "spotlight"), PageSpotlight.class);
		pageTypes.put(new Identifier(Patchouli.MOD_ID, "empty"), PageEmpty.class);
		pageTypes.put(new Identifier(Patchouli.MOD_ID, "multiblock"), PageMultiblock.class);
		pageTypes.put(new Identifier(Patchouli.MOD_ID, "link"), PageLink.class);
		pageTypes.put(new Identifier(Patchouli.MOD_ID, "relations"), PageRelations.class);
		pageTypes.put(new Identifier(Patchouli.MOD_ID, "entity"), PageEntity.class);
		pageTypes.put(new Identifier(Patchouli.MOD_ID, "quest"), PageQuest.class);
	}

	public void reload() {
		currentLang = MinecraftClient.getInstance().getLanguageManager().getLanguage().getCode();
		BookRegistry.INSTANCE.reloadContents();
	}

	public void reloadLocks(boolean suppressToasts) {
		BookRegistry.INSTANCE.books.values().forEach(b -> b.reloadLocks(suppressToasts));
	}

	/**
	 * @param entryId Entry to force to the top of the stack
	 * @param page    Page in the entry to force. Ignored if {@code entryId} is null.
	 */
	public void displayBookGui(Identifier bookStr, @Nullable Identifier entryId, int page) {
		MinecraftClient mc = MinecraftClient.getInstance();
		currentLang = mc.getLanguageManager().getLanguage().getCode();

		Book book = BookRegistry.INSTANCE.books.get(bookStr);

		if (book != null && !book.isExtension) {
			book.getContents().checkValidCurrentEntry();

			if (entryId != null) {
				book.getContents().setTopEntry(entryId, page);
			}

			book.getContents().openLexiconGui(book.getContents().getCurrentGui(), false);

			if (mc.player != null) {
				SoundEvent sfx = PatchouliSounds.getSound(book.openSound, PatchouliSounds.book_open);
				mc.player.playSound(sfx, 1F, (float) (0.7 + Math.random() * 0.4));
			}
		}
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
			Identifier type = new Identifier(string);
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
			Identifier type = new Identifier(prim.getAsString());
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
