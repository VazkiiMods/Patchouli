package vazkii.patchouli.client.book.template.component;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.template.TemplateComponent;

import java.util.function.UnaryOperator;

public class ComponentImage extends TemplateComponent {

	public String image;

	public int u, v, width, height;

	@SerializedName("texture_width") public int textureWidth = 256;
	@SerializedName("texture_height") public int textureHeight = 256;

	public float scale = 1F;

	transient Identifier resource;

	@Override
	public void build(BookContentsBuilder builder, BookPage page, BookEntry entry, int pageNum) {
		resource = new Identifier(image);
	}

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
		super.onVariablesAvailable(lookup);
		image = lookup.apply(IVariable.wrap(image)).asString();
	}

	@Override
	public void render(MatrixStack ms, BookPage page, int mouseX, int mouseY, float pticks) {
		if (scale == 0F) {
			return;
		}

		RenderSystem.setShaderTexture(0, resource);
		ms.push();
		ms.translate(x, y, 0);
		ms.scale(scale, scale, scale);
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		RenderSystem.enableBlend();
		DrawableHelper.drawTexture(ms, 0, 0, u, v, width, height, textureWidth, textureHeight);
		ms.pop();
	}

}
