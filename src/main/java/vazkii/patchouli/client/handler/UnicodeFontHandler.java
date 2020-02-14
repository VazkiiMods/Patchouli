package vazkii.patchouli.client.handler;

import com.google.common.collect.Lists;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.UnicodeTextureFont;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import vazkii.patchouli.common.base.Patchouli;

public final class UnicodeFontHandler {

	private static Lazy<TextRenderer> unicodeFont = new Lazy<>(() -> {
		MinecraftClient mc = MinecraftClient.getInstance();
		TextRenderer ret = new TextRenderer(mc.getTextureManager(), new FontStorage(mc.getTextureManager(), new Identifier(Patchouli.MOD_ID, "unicode")));

		Font provider = new UnicodeTextureFont.Loader(new Identifier("font/glyph_sizes.bin"), "minecraft:font/unicode_page_%s.png").load(mc.getResourceManager());
		ret.setFonts(Lists.newArrayList(provider));
		return ret;
	});

	public static TextRenderer getUnicodeFont() {
		return unicodeFont.get();
	}
	
}
