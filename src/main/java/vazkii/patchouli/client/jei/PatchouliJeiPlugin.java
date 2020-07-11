package vazkii.patchouli.client.jei;


import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.item.PatchouliItems;

import javax.annotation.Nonnull;

@JeiPlugin
public class PatchouliJeiPlugin implements IModPlugin {
	private static final ResourceLocation UID = new ResourceLocation(Patchouli.MOD_ID, Patchouli.MOD_ID);

	@Nonnull
	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		registration.registerSubtypeInterpreter(PatchouliItems.book, stack -> {
			if (!stack.hasTag() || !stack.getTag().contains(ItemModBook.TAG_BOOK)) {
				return "";
			}
			return stack.getTag().getString(ItemModBook.TAG_BOOK);
		});
	}
}
