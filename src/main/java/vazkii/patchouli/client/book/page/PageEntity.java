package vazkii.patchouli.client.book.page;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.page.abstr.PageWithText;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.util.EntityUtil;

import java.util.function.Function;

public class PageEntity extends PageWithText {

	@SerializedName("entity")
	public String entityId;

	float scale = 1F;
	@SerializedName("offset")
	float extraOffset = 0F;
	String name;

	boolean rotate = true;
	@SerializedName("default_rotation")
	float defaultRotation = -45f;

	transient boolean errored;
	transient Entity entity;
	transient Function<World, Entity> creator;
	transient float renderScale, offset;

	@Override
	public void build(BookEntry entry, int pageNum) {
		super.build(entry, pageNum);

		creator = EntityUtil.loadEntity(entityId);
	}

	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);

		loadEntity(parent.getMinecraft().world);
	}

	@Override
	public int getTextHeight() {
		return 115;
	}

	@Override
	public void render(int mouseX, int mouseY, float pticks) {
		int x = GuiBook.PAGE_WIDTH / 2 - 53;
		int y = 7;
		GlStateManager.enableBlend();
		GlStateManager.color3f(1F, 1F, 1F);
		GuiBook.drawFromTexture(book, x, y, 405, 149, 106, 106);

		parent.drawCenteredStringNoShadow(name, GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);

		if(errored)
			fontRenderer.drawStringWithShadow(I18n.format("patchouli.gui.lexicon.loading_error"), 58, 60, 0xFF0000);

		if(entity != null)
			renderEntity(parent.getMinecraft().world, rotate ? ClientTicker.total : defaultRotation);

		super.render(mouseX, mouseY, pticks);
	}

	private void renderEntity(World world, float rotation) {
		renderEntity(entity, world, 58, 60, rotation, renderScale, offset);
	}	

	public static void renderEntity(Entity entity, World world, float x, float y, float rotation, float renderScale, float offset) {
		entity.world = world;

		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		GlStateManager.color3f(1F, 1F, 1F);		
		GlStateManager.translatef(x, y, 50.0F);
		GlStateManager.scalef(-renderScale, renderScale, renderScale);
		GlStateManager.translatef(0F, offset, 0F);
		GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotatef(rotation, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		Minecraft.getInstance().getRenderManager().playerViewY = 180.0F;
		Minecraft.getInstance().getRenderManager().renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.activeTexture(GLX.GL_TEXTURE1);
		GlStateManager.disableTexture();
		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
	}

	private void loadEntity(World world) {
		if(!errored && (entity == null || !entity.isAlive())) {
			try {
				entity = creator.apply(world);

				float width = entity.getWidth();
				float height = entity.getHeight();
				
				float entitySize = width;
				if(width < height)
					entitySize = height;
				entitySize = Math.max(1F, entitySize);

				renderScale = 100F / entitySize * 0.8F * scale;
				offset = Math.max(height, entitySize) * 0.5F + extraOffset;

				if(name == null || name.isEmpty())
					name = entity.getName().getFormattedText();
			} catch(Exception e) {
				errored = true;
				Patchouli.LOGGER.error("Failed to load entity", e);
			}
		}
	}



}
