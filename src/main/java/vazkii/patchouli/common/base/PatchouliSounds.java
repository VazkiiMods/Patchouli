package vazkii.patchouli.common.base;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class PatchouliSounds {

	public static final SoundEvent BOOK_OPEN = new SoundEvent(new ResourceLocation(Patchouli.MOD_ID, "book_open"));
	public static final SoundEvent BOOK_FLIP = new SoundEvent(new ResourceLocation(Patchouli.MOD_ID, "book_flip"));

	public static void init() {
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(SoundEvent.class, PatchouliSounds::registerSounds);
	}

	private static void registerSounds(RegistryEvent.Register<SoundEvent> evt) {
		var r = evt.getRegistry();
		r.register(BOOK_OPEN.setRegistryName(BOOK_OPEN.getLocation()));
		r.register(BOOK_FLIP.setRegistryName(BOOK_FLIP.getLocation()));
	}

	public static SoundEvent getSound(ResourceLocation key, SoundEvent fallback) {
		return Registry.SOUND_EVENT.getOptional(key).orElse(fallback);
	}

}
