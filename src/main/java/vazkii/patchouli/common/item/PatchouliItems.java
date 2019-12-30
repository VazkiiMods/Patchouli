package vazkii.patchouli.common.item;

import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import vazkii.patchouli.client.base.BookModel;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.BookRegistry;

public class PatchouliItems {

	public static Item book;

	public static void init() {
		book = new ItemModBook();
		Registry.register(Registry.ITEM, new Identifier(Patchouli.MOD_ID, "guide_book"), book);

	}
}
