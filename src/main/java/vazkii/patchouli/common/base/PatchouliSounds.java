package vazkii.patchouli.common.base;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class PatchouliSounds {

	public static SoundEvent book_open;
	public static SoundEvent book_flip;

	public static void preInit() {
		book_open = register("book_open");
		book_flip = register("book_flip");
	}

	public static SoundEvent register(String name) {
		ResourceLocation loc = new ResourceLocation(Patchouli.MOD_ID, name);
		SoundEvent e = new SoundEvent(loc);
		Registry.register(Registry.SOUND_EVENT, loc, e);
		return e;
	}

	public static SoundEvent getSound(ResourceLocation key, SoundEvent fallback) {
		return Registry.SOUND_EVENT.getOptional(key).orElse(fallback);
	}

}
