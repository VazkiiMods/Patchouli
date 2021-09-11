package vazkii.patchouli.common.base;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class PatchouliSounds {

	public static SoundEvent BOOK_OPEN;
	public static SoundEvent BOOK_FLIP;

	public static void init() {
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(SoundEvent.class, PatchouliSounds::registerSoundEvents);
	}

	public static void registerSoundEvents(RegistryEvent.Register<SoundEvent> evt) {
		BOOK_OPEN = register("book_open");
		BOOK_FLIP = register("book_flip");
		evt.getRegistry().register(BOOK_OPEN);
		evt.getRegistry().register(BOOK_FLIP);
	}

	public static SoundEvent register(String name) {
		ResourceLocation loc = new ResourceLocation(Patchouli.MOD_ID, name);
		SoundEvent e = new SoundEvent(loc);
		e.setRegistryName(loc);
		return e;
	}

	public static SoundEvent getSound(ResourceLocation key, SoundEvent fallback) {
		return Registry.SOUND_EVENT.getOptional(key).orElse(fallback);
	}
}
