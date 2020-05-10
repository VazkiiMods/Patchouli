/*
package vazkii.patchouli.client.jei;


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
			if (!stack.hasTag() || !stack.getTag().contains(ItemModBook.TAG_BOOK))
				return "";
			return stack.getTag().getString(ItemModBook.TAG_BOOK);
		});
	}
}
*/
