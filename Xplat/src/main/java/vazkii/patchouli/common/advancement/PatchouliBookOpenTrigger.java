package vazkii.patchouli.common.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import vazkii.patchouli.api.PatchouliAPI;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An advancement trigger for opening Patchouli books.
 */
public class PatchouliBookOpenTrigger extends SimpleCriterionTrigger<PatchouliBookOpenTrigger.Instance> {
	public static final ResourceLocation ID = new ResourceLocation(PatchouliAPI.MOD_ID, "open_book");
	public static final PatchouliBookOpenTrigger INSTANCE = new PatchouliBookOpenTrigger();
	private static final String ELEMENT_BOOK = "book";
	private static final String ELEMENT_ENTRY = "entry";
	private static final String ELEMENT_PAGE = "page";

	private PatchouliBookOpenTrigger() {}

	@NotNull
	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@NotNull
	@Override
	protected PatchouliBookOpenTrigger.Instance createInstance(@NotNull JsonObject json,
			@NotNull ContextAwarePredicate playerPred, @NotNull DeserializationContext conditions) {
		JsonElement bookElement = json.get(ELEMENT_BOOK);
		ResourceLocation book = bookElement instanceof JsonPrimitive bookPrimitive && bookPrimitive.isString()
				? new ResourceLocation(bookElement.getAsString()) : null;
		JsonElement entryElement = json.get(ELEMENT_ENTRY);
		ResourceLocation entry = entryElement instanceof JsonPrimitive entryPrimitive && entryPrimitive.isString()
				? new ResourceLocation(entryElement.getAsString()) : null;
		JsonElement pageElement = json.get(ELEMENT_PAGE);
		int page = pageElement instanceof JsonPrimitive pagePrimitive && pagePrimitive.isNumber() ? pageElement.getAsInt() : 0;
		return new PatchouliBookOpenTrigger.Instance(playerPred, book, entry, page);
	}

	public void trigger(@NotNull ServerPlayer player, @NotNull ResourceLocation book) {
		trigger(player, instance -> instance.test(book, null, 0));
	}

	public void trigger(@NotNull ServerPlayer player, @NotNull ResourceLocation book, @Nullable ResourceLocation entry, int page) {
		trigger(player, instance -> instance.test(book, entry, page));
	}

	public static class Instance extends AbstractCriterionTriggerInstance {
		private final ResourceLocation book;
		private final ResourceLocation entry;
		private final int page;

		/**
		 * Creates a trigger instance that tests against a book ID, and optional entry ID and an optional page number.
		 * Note that the entry and page are only tested when the server opens the book for a player, not when the player
		 * navigates to that entry and page while the book is open.
		 * 
		 * @param playerPred The player predicate.
		 * @param book       The book's {@link ResourceLocation}.
		 * @param entry      The entry's {@link ResourceLocation}. May be {@code null} to skip the check.
		 * @param page       The page number. Specify zero to skip the check.
		 */
		public Instance(@NotNull ContextAwarePredicate playerPred, @Nullable ResourceLocation book, @Nullable ResourceLocation entry, int page) {
			super(ID, playerPred);
			if (book == null) {
				PatchouliAPI.LOGGER.error("Missing book ID in advancement criterion {} - condition can never be satisfied!", getCriterion());
			}
			this.book = book;
			this.entry = entry;
			this.page = page;
		}

		/**
		 * Creates a trigger instance that tests only against a book ID.
		 * This type of instance will match regardless whether the book is opened on a particular entry or page.
		 * 
		 * @param playerPred The player predicate.
		 * @param book       The book's {@link ResourceLocation}.
		 */
		@SuppressWarnings("unused")
		public Instance(@NotNull ContextAwarePredicate playerPred, @NotNull ResourceLocation book) {
			this(playerPred, book, null, 0);
		}

		@NotNull
		@Override
		public ResourceLocation getCriterion() {
			return ID;
		}

		@Nullable
		public ResourceLocation getBook() {
			return this.book;
		}

		@Nullable
		public ResourceLocation getEntry() {
			return this.entry;
		}

		public int getPage() {
			return this.page;
		}

		boolean test(ResourceLocation book, ResourceLocation entry, int page) {
			return this.book != null && this.book.equals(book)
					&& (this.entry == null || this.entry.equals(entry))
					&& (this.page <= 0 || this.page == page);
		}

		@NotNull
		@Override
		public JsonObject serializeToJson(@NotNull SerializationContext serializationContext) {
			JsonObject json = super.serializeToJson(serializationContext);
			if (book != null) {
				json.addProperty(ELEMENT_BOOK, book.toString());
			}
			if (entry != null) {
				json.addProperty(ELEMENT_ENTRY, entry.toString());
			}
			if (page > 0) {
				json.addProperty(ELEMENT_PAGE, page);
			}
			return json;
		}
	}
}
