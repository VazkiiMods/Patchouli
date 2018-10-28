package vazkii.patchouli.client.book;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.client.book.page.PageCrafting;
import vazkii.patchouli.client.book.page.PageEmpty;
import vazkii.patchouli.client.book.page.PageImage;
import vazkii.patchouli.client.book.page.PageLink;
import vazkii.patchouli.client.book.page.PageMultiblock;
import vazkii.patchouli.client.book.page.PageRelations;
import vazkii.patchouli.client.book.page.PageSmelting;
import vazkii.patchouli.client.book.page.PageSpotlight;
import vazkii.patchouli.client.book.page.PageTemplate;
import vazkii.patchouli.client.book.page.PageText;
import vazkii.patchouli.client.book.template.BookTemplate;
import vazkii.patchouli.client.book.template.TemplateComponent;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.util.SerializationUtil;

public class ClientBookRegistry implements IResourceManagerReloadListener {

	public final Map<String, Class<? extends BookPage>> pageTypes = new HashMap();

	private boolean firstLoad = true;

	public Gson gson;
	public String currentLang;

	public static final ClientBookRegistry INSTANCE = new ClientBookRegistry();
	
	private ClientBookRegistry() { 
		gson = new GsonBuilder()
				.registerTypeHierarchyAdapter(BookPage.class, new LexiconPageAdapter())
				.registerTypeHierarchyAdapter(TemplateComponent.class, new TemplateComponentAdapter())
				.create();
	}
	
	public void init() {
		addPageTypes();

		IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
		if (manager instanceof IReloadableResourceManager)
			((IReloadableResourceManager) manager).registerReloadListener(this);
		else
			throw new RuntimeException("Minecraft's resource manager is not reloadable. Something went way wrong.");
	}

	private void addPageTypes() {
		pageTypes.put("text", PageText.class);
		pageTypes.put("crafting", PageCrafting.class);
		pageTypes.put("smelting", PageSmelting.class);
		pageTypes.put("image", PageImage.class);
		pageTypes.put("spotlight", PageSpotlight.class);
		pageTypes.put("empty", PageEmpty.class);
		pageTypes.put("multiblock", PageMultiblock.class); 
		pageTypes.put("link", PageLink.class);
		pageTypes.put("relations", PageRelations.class);
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		currentLang = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
		
		if(!firstLoad) {
			BookRegistry.INSTANCE.reload();
			firstLoad = false;
		}
	}
	
	public void reloadLocks(boolean reset) {
		BookRegistry.INSTANCE.books.values().forEach(b -> b.reloadLocks(reset));
	}
	
	public void displayBookGui(String bookStr) {
		ResourceLocation res = new ResourceLocation(bookStr);
		Book book = BookRegistry.INSTANCE.books.get(res);
		
		if(book != null)
			book.contents.openLexiconGui(book.contents.getCurrentGui(), false);
	}

	public static class LexiconPageAdapter implements JsonDeserializer<BookPage> {
		
		@Override
		public BookPage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
	        JsonObject obj = json.getAsJsonObject();
	        JsonPrimitive prim = (JsonPrimitive) obj.get("type");
	        String type = prim.getAsString();
	        Class<? extends BookPage> clazz = ClientBookRegistry.INSTANCE.pageTypes.get(type);
	        if(clazz == null)
	        	clazz = PageTemplate.class;
	        
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
	        String type = prim.getAsString();
	        Class<? extends TemplateComponent> clazz = BookTemplate.componentTypes.get(type);
	        
	        if(clazz == null)
	        	return null;
	        
	        return SerializationUtil.RAW_GSON.fromJson(json, clazz);
		}
		
	}	
}
