package vazkii.patchouli.common.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;


/**
 * Switched over to {@link PatchouliBookItem}
 */
@Deprecated
public class ItemModBook extends PatchouliBookItem {

	// Having the properties created within super call made this horribly unusable.
	public ItemModBook() {
		super(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC));
	}
}
