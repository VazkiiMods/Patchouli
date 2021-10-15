package vazkii.patchouli.common.base;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class PatchouliSounds {

	public static final SoundEvent BOOK_OPEN = new SoundEvent(new ResourceLocation(Patchouli.MOD_ID, "book_open"));
	public static final SoundEvent BOOK_FLIP = new SoundEvent(new ResourceLocation(Patchouli.MOD_ID, "book_flip"));

	public static void init() {
		registerSounds();
	}

	private static void registerSounds() {
		Registry.register(Registry.SOUND_EVENT, BOOK_OPEN.getLocation(), BOOK_OPEN);
		Registry.register(Registry.SOUND_EVENT, BOOK_FLIP.getLocation(), BOOK_FLIP);
	}

	public static SoundEvent getSound(ResourceLocation key, SoundEvent fallback) {
		return Registry.SOUND_EVENT.getOptional(key).orElse(fallback);
	}

}
