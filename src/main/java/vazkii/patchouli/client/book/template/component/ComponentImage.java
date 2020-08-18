package vazkii.patchouli.client.book.template.component;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.template.TemplateComponent;

import java.util.function.UnaryOperator;

public class ComponentImage extends TemplateComponent {

	public String image;

	@SerializedName("u") public IVariable u;
	@SerializedName("v") public IVariable v;
	@SerializedName("width") public IVariable width;
	@SerializedName("height") public IVariable height;

	@SerializedName("texture_width") public IVariable textureWidth;
	@SerializedName("texture_height") public IVariable textureHeight;

	public int uInt, vInt, widthInt, heightInt, textureWidthInt, textureHeightInt;

	public float scale = 1F;

	transient Identifier resource;

	@Override
	public void build(BookPage page, BookEntry entry, int pageNum) {
		if (image.contains(":")) {
			resource = new Identifier(image);
		} else {
			resource = new Identifier(page.book.getModNamespace(), image);
		}
	}

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
		super.onVariablesAvailable(lookup);
		image = lookup.apply(IVariable.wrap(image)).asString();
		u = lookup.apply(u);
		v = lookup.apply(u);
		width = lookup.apply(width);
		height = lookup.apply(height);
		textureWidth = lookup.apply(textureWidth);
		textureHeight = lookup.apply(textureHeight);
	}

	@Override
	public void render(MatrixStack ms, BookPage page, int mouseX, int mouseY, float pticks) {
		if (scale == 0F) {
			return;
		}

		checkValues();

		page.mc.getTextureManager().bindTexture(resource);
		ms.push();
		ms.translate(x, y, 0);
		ms.scale(scale, scale, scale);
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		RenderSystem.enableBlend();
		DrawableHelper.drawTexture(ms, 0, 0, uInt, vInt, widthInt, heightInt, textureWidthInt, textureHeightInt);
		ms.pop();
	}

	//prepares all the Ivariables and defaults them if they are of improper type
	private void checkValues() {
		try {
			textureWidthInt = textureWidth.asNumber().intValue();
		} catch (Exception e) {
			textureWidthInt = 256;
		}

		try {
			textureHeightInt = textureHeight.asNumber().intValue();
		} catch (Exception e) {
			textureHeightInt = 256;
		}

		try {
			uInt = u.asNumber().intValue();
		} catch (Exception e) {
			uInt = 0;
		}

		try {
			vInt = v.asNumber().intValue();
		} catch (Exception e) {
			vInt = 0;
		}

		try {
			widthInt = width.asNumber().intValue();
		} catch (Exception e) {
			widthInt = 0;
		}

		try {
			heightInt = height.asNumber().intValue();
		} catch (Exception e) {
			heightInt = 0;
		}
	}

}
