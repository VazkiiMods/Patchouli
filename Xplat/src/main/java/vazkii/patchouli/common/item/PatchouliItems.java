package vazkii.patchouli.common.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import vazkii.patchouli.api.PatchouliAPI;

import java.util.function.BiConsumer;

public class PatchouliItems {

	public static final Identifier BOOK_ID = Identifier.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "guide_book");
	public static final Item BOOK = new ItemModBook(new Item.Properties().stacksTo(1).setId(ResourceKey.create(Registries.ITEM, BOOK_ID)));

	public static void submitItemRegistrations(BiConsumer<Identifier, Item> consumer) {
		consumer.accept(BOOK_ID, BOOK);
	}
}
