package vazkii.patchouli.client.book.page;

import com.google.gson.annotations.SerializedName;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.page.abstr.PageWithText;
import vazkii.patchouli.common.util.EntityUtil;

import java.util.function.Function;

public class PageEntity extends PageWithText {

	@SerializedName("entity") public String entityId;

	float scale = 1F;
	@SerializedName("offset") float extraOffset = 0F;
	String name;

	boolean rotate = true;
	@SerializedName("default_rotation") float defaultRotation = -45f;

	transient boolean errored;
	transient Entity entity;
	transient Function<Level, Entity> creator;
	transient float renderScale, offset;

	@Override
	public void build(Level level, BookEntry entry, BookContentsBuilder builder, int pageNum) {
		super.build(level, entry, builder, pageNum);

		creator = EntityUtil.loadEntity(entityId);
	}

	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);

		loadEntity(parent.getMinecraft().level);
	}

	@Override
	public int getTextHeight() {
		return 115;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float pticks) {
		int x = GuiBook.PAGE_WIDTH / 2 - 53;
		int y = 7;
		GuiBook.drawFromTexture(graphics, book, x, y, 405, 149, 106, 106);

		if (name == null || name.isEmpty()) {
			if (entity != null) {
				parent.drawCenteredStringNoShadow(graphics, entity.getName().getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
			}
		} else {
			parent.drawCenteredStringNoShadow(graphics, name, GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
		}

		if (errored) {
			graphics.drawString(fontRenderer, I18n.get("patchouli.gui.lexicon.loading_error"), 58, 60, 0xFF0000, true);
		}

		if (entity != null) {
			float rotation = rotate ? ClientTicker.total : defaultRotation;
			graphics.pose().pushMatrix();
			graphics.pose().translate(x, y);
			renderEntity(graphics, entity, 58, 60, 106, 106, rotation, renderScale * parent.getScaleFactor(), offset, pticks);
			graphics.pose().popMatrix();
		}

		super.render(graphics, mouseX, mouseY, pticks);
	}

	public static void renderEntity(GuiGraphics graphics, Entity entity, int x, int y, int width, int height, float rotation, float renderScale, float offset, float pticks) {
		Vector2f position = graphics.pose().transformPosition(x, y, new Vector2f());

		int posX = Math.round(position.x);
		int posY = Math.round(position.y);

		EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		EntityRenderer<? super Entity, ?> entityrenderer = entityrenderdispatcher.getRenderer(entity);
		EntityRenderState entityrenderstate = entityrenderer.createRenderState(entity, pticks);
		entityrenderstate.lightCoords = 0xf000f0;
		entityrenderstate.shadowPieces.clear();
		entityrenderstate.outlineColor = 0;

		Quaternionf rot = Axis.ZP.rotationDegrees(180).mul(Axis.YP.rotationDegrees(rotation));
		int startX = posX - width;
		int startY = posY - height;
		int endX = posX + width;
		int endY = posY + height;
		graphics.enableScissor(3, 2, width - 3, height - 3);
		graphics.submitEntityRenderState(entityrenderstate, renderScale, new Vector3f(-0.1f, offset, 0f), rot, new Quaternionf(), startX, startY, endX, endY);
		graphics.disableScissor();
	}

	private void loadEntity(Level world) {
		if (!errored && (entity == null || !entity.isAlive() || entity.level() != world)) {
			try {
				entity = creator.apply(world);

				float width = entity.getBbWidth();
				float height = entity.getBbHeight();

				float entitySize = Math.max(1F, Math.max(width, height));

				renderScale = 100F / entitySize * 0.8F * scale;
				offset = Math.max(height, entitySize) * 0.5F + extraOffset;
			} catch (Exception e) {
				errored = true;
				PatchouliAPI.LOGGER.error("Failed to load entity", e);
			}
		}
	}

}
