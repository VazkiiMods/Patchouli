package vazkii.patchouli.client.book.template.component;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.platform.GlStateManager;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.template.TemplateComponent;

public class ComponentImage extends TemplateComponent {

	@VariableHolder 
	public String image;

	public int u, v, width, height;

	@SerializedName("texture_width")
	public int textureWidth = 256;
	@SerializedName("texture_height")
	public int textureHeight = 256;

	public float scale = 1F;

	transient ResourceLocation resource;

	@Override
	public void build(BookPage page, BookEntry entry, int pageNum) {
		if(image.contains(":"))
			resource = new ResourceLocation(image);
		else resource = new ResourceLocation(page.book.getModNamespace(), image);
	}
	
	@Override
	public void render(BookPage page, int mouseX, int mouseY, float pticks) {
		if(scale == 0F)
			return;
		
		page.mc.textureManager.bindTexture(resource);
		RenderSystem.pushMatrix();
		RenderSystem.translatef(x, y, 0);
		RenderSystem.scalef(scale, scale, scale);
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		RenderSystem.enableBlend();
		AbstractGui.blit(0, 0, u, v, width, height, textureWidth, textureHeight);
		RenderSystem.popMatrix();
	}

}
