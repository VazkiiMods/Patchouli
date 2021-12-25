package vazkii.patchouli.forge.client.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;

import net.minecraft.resources.ResourceLocation;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.item.PatchouliItems;

import javax.annotation.Nonnull;

@JeiPlugin
public class PatchouliJeiPlugin implements IModPlugin {
	private static final ResourceLocation UID = new ResourceLocation(PatchouliAPI.MOD_ID, PatchouliAPI.MOD_ID);

	@Nonnull
	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		registration.registerSubtypeInterpreter(PatchouliItems.BOOK, (stack, context) -> {
			if (!stack.hasTag() || !stack.getTag().contains(ItemModBook.TAG_BOOK)) {
				return "";
			}
			return stack.getTag().getString(ItemModBook.TAG_BOOK);
		});
	}
}
