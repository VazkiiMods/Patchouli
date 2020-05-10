package vazkii.patchouli.common.base;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PatchouliSounds {

	public static SoundEvent book_open;
	public static SoundEvent book_flip;

	public static void preInit() {
		book_open = register("book_open");
		book_flip = register("book_flip");
	}

	public static SoundEvent register(String name) {
		Identifier loc = new Identifier(Patchouli.MOD_ID, name);
		SoundEvent e = new SoundEvent(loc);
		Registry.register(Registry.SOUND_EVENT, loc, e);
		return e;
	}

	public static SoundEvent getSound(Identifier key, SoundEvent fallback) {
		return Registry.SOUND_EVENT.getOrEmpty(key).orElse(fallback);
	}

}
