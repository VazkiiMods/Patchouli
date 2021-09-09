package vazkii.patchouli.common.base;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class PatchouliSounds {

	static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Patchouli.MOD_ID);
	public static final RegistryObject<SoundEvent> BOOK_OPEN = register("book_open");
	public static final RegistryObject<SoundEvent> BOOK_FLIP = register("book_flip");

	public static RegistryObject<SoundEvent> register(String name) {
		return REGISTER.register(name, () -> new SoundEvent(new ResourceLocation(Patchouli.MOD_ID, name)));
	}

	public static SoundEvent getSound(ResourceLocation key, Supplier<SoundEvent> fallback) {
		if (!ForgeRegistries.SOUND_EVENTS.containsKey(key)) {
			return fallback.get();
		}
		return ForgeRegistries.SOUND_EVENTS.getValue(key);
	}

	public static SoundEvent getSound(ResourceLocation key, SoundEvent fallback) {
		return getSound(key, () -> fallback);
	}
}
