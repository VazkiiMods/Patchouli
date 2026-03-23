package vazkii.patchouli.common.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;

import vazkii.patchouli.xplat.IXplatAbstractions;

public class PatchouliItems {

	public static final Holder<Item> BOOK = IXplatAbstractions.INSTANCE.registerItem("guide_book", ItemModBook::new, p -> p.stacksTo(1));

	public static void init() {}
}
