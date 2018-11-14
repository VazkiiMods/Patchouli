package vazkii.patchouli.common.util;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

import java.lang.reflect.Field;

public class ReflectionUtil {
	public static Field accessField(Class<?> owner, String srgName, String descriptor) throws NoSuchFieldException {
		Field result = owner.getDeclaredField(FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(owner.getCanonicalName().replace('.', '/'), srgName, descriptor));
		result.setAccessible(true);
		return result;
	}
}
