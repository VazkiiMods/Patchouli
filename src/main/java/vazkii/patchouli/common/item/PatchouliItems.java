package vazkii.patchouli.common.item;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import vazkii.patchouli.common.base.Patchouli;

public class PatchouliItems {

	public static final Identifier BOOK_ID = new Identifier(Patchouli.MOD_ID, "guide_book");
	public static Item book;

	public static void init() {
		book = new ItemModBook();
		Registry.register(Registry.ITEM, BOOK_ID, book);

	}
}
