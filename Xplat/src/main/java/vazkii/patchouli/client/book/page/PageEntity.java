package vazkii.patchouli.client.book.page;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
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
	public void build(BookEntry entry, BookContentsBuilder builder, int pageNum) {
		super.build(entry, builder, pageNum);

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
	public void render(PoseStack ms, int mouseX, int mouseY, float pticks) {
		int x = GuiBook.PAGE_WIDTH / 2 - 53;
		int y = 7;
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		GuiBook.drawFromTexture(ms, book, x, y, 405, 149, 106, 106);

		if (name == null || name.isEmpty()) {
			if (entity != null) {
				parent.drawCenteredStringNoShadow(ms, entity.getName().getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
			}
		} else {
			parent.drawCenteredStringNoShadow(ms, name, GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
		}

		if (errored) {
			fontRenderer.drawShadow(ms, I18n.get("patchouli.gui.lexicon.loading_error"), 58, 60, 0xFF0000);
		}

		if (entity != null) {
			float rotation = rotate ? ClientTicker.total : defaultRotation;
			renderEntity(ms, entity, parent.getMinecraft().level, 58, 60, rotation, renderScale, offset);
		}

		super.render(ms, mouseX, mouseY, pticks);
	}

	public static void renderEntity(PoseStack ms, Entity entity, Level world, float x, float y, float rotation, float renderScale, float offset) {
		entity.level = world;

		ms.pushPose();
		ms.translate(x, y, 50);
		ms.scale(renderScale, renderScale, renderScale);
		ms.translate(0, offset, 0);
		ms.mulPose(Axis.ZP.rotationDegrees(180));
		ms.mulPose(Axis.YP.rotationDegrees(rotation));
		EntityRenderDispatcher erd = Minecraft.getInstance().getEntityRenderDispatcher();
		MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
		erd.setRenderShadow(false);
		erd.render(entity, 0, 0, 0, 0, 1, ms, immediate, 0xF000F0);
		erd.setRenderShadow(true);
		immediate.endBatch();
		ms.popPose();
	}

	private void loadEntity(Level world) {
		if (!errored && (entity == null || !entity.isAlive())) {
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
