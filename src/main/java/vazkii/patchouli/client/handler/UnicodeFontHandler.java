package vazkii.patchouli.client.handler;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.gui.fonts.providers.UnicodeTextureGlyphProvider;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.common.base.Patchouli;

public final class UnicodeFontHandler {

	private static FontRenderer unicodeFont;
	
	private static void makeUnicodeFont() {
		Minecraft mc = Minecraft.getInstance();
		unicodeFont = new FontRenderer(mc.textureManager, new Font(mc.textureManager, new ResourceLocation(Patchouli.MOD_ID, "unicode")));
			
		IGlyphProvider provider = new UnicodeTextureGlyphProvider.Factory(new ResourceLocation("font/glyph_sizes.bin"), "minecraft:font/unicode_page_%s.png").create(mc.getResourceManager());
		unicodeFont.setGlyphProviders(Lists.newArrayList(provider));
	}
	
	public static FontRenderer getUnicodeFont() {
		if(unicodeFont == null)
			makeUnicodeFont();
		
		return unicodeFont;
	}
	
}
