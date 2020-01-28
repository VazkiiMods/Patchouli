package vazkii.patchouli.client.handler;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.gui.fonts.providers.UnicodeTextureGlyphProvider;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.common.base.Patchouli;

public final class UnicodeFontHandler {

	private static LazyValue<FontRenderer> unicodeFont = new LazyValue<>(() -> {
		Minecraft mc = Minecraft.getInstance();
		FontRenderer ret = new FontRenderer(mc.textureManager, new Font(mc.textureManager, new ResourceLocation(Patchouli.MOD_ID, "unicode")));

		IGlyphProvider provider = new UnicodeTextureGlyphProvider.Factory(new ResourceLocation("font/glyph_sizes.bin"), "minecraft:font/unicode_page_%s.png").create(mc.getResourceManager());
		ret.setGlyphProviders(Lists.newArrayList(provider));
		return ret;
	});
	
	public static FontRenderer getUnicodeFont() {
		return unicodeFont.getValue();
	}
	
}
