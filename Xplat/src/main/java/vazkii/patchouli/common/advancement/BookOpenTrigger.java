package vazkii.patchouli.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import vazkii.patchouli.api.PatchouliAPI;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * An advancement trigger for opening Patchouli books.
 */
public class BookOpenTrigger extends SimpleCriterionTrigger<BookOpenTrigger.TriggerInstance> {
	public static final Identifier ID = Identifier.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "open_book");
	public static final BookOpenTrigger INSTANCE = new BookOpenTrigger();

	@NotNull
	@Override
	public Codec<TriggerInstance> codec() {
		return BookOpenTrigger.TriggerInstance.CODEC;
	}

	public void trigger(@NotNull ServerPlayer player, @NotNull Identifier book) {
		trigger(player, instance -> instance.matches(book, null, 0));
	}

	public void trigger(@NotNull ServerPlayer player, @NotNull Identifier book, @Nullable Identifier entry, int page) {
		trigger(player, instance -> instance.matches(book, entry, page));
	}

	public record TriggerInstance(Optional<ContextAwarePredicate> player, Identifier book, Optional<Identifier> entry, MinMaxBounds.Ints page) implements SimpleInstance {

		public static Codec<BookOpenTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
				Identifier.CODEC.fieldOf("book").forGetter(TriggerInstance::book),
				Identifier.CODEC.optionalFieldOf("entry").forGetter(TriggerInstance::entry),
				MinMaxBounds.Ints.CODEC.optionalFieldOf("page", MinMaxBounds.Ints.ANY).forGetter(TriggerInstance::page)
		).apply(instance, TriggerInstance::new));

		public boolean matches(@NotNull Identifier book, @Nullable Identifier entry, int page) {
			return this.book.equals(book) && (this.entry.isEmpty() || this.entry.get().equals(entry)) && this.page.matches(page);
		}
	}
}
