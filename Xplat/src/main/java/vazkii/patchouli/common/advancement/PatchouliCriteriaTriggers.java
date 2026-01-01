package vazkii.patchouli.common.advancement;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.resources.Identifier;

import java.util.function.BiConsumer;

public class PatchouliCriteriaTriggers {
	public static final BookOpenTrigger BOOK_OPEN = new BookOpenTrigger();

	public static void submitTriggerRegistrations(BiConsumer<Identifier, CriterionTrigger<?>> consumer) {
		consumer.accept(BookOpenTrigger.ID, BOOK_OPEN);
	}
}
