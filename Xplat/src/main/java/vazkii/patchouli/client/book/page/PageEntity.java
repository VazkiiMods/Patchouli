package vazkii.patchouli.client.book.page;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;


import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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

@SuppressWarnings("unused")
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

        if (parent.getMinecraft() != null) {
            loadEntity(parent.getMinecraft().level);
        }
    }

	@Override
	public int getTextHeight() {
		return 115;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float pticks) {
		int x = GuiBook.PAGE_WIDTH / 2 - 53;
		int y = 7;

		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
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
			float rotation = rotate ? (ClientTicker.total + pticks) : defaultRotation;
			renderEntity(graphics, (LivingEntity) entity, 58, 60, rotation, renderScale, offset, pticks);
		}

		super.render(graphics, mouseX, mouseY, pticks);
	}
	@SuppressWarnings("unchecked")
	public static void renderEntity(GuiGraphics graphics, LivingEntity entity, float x, float y, float rotation, float scale, float offset, float pticks) {
		Minecraft mc = Minecraft.getInstance();
		EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();

		// Bind renderer with correct generics
		EntityRenderer<LivingEntity, EntityRenderState> renderer = (EntityRenderer<LivingEntity, EntityRenderState>) dispatcher.getRenderer(entity);
		EntityRenderState state = renderer.createRenderState();
		renderer.extractRenderState(entity, state, pticks);

		PoseStack ms = graphics.pose();
		ms.pushPose();
		ms.translate(x, y, 50);
		ms.scale(scale, scale, scale);
		ms.translate(0, offset, 0);
		ms.mulPose(Axis.ZP.rotationDegrees(180));
		ms.mulPose(Axis.YP.rotationDegrees(rotation));

		MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
		dispatcher.setRenderShadow(false);
		renderer.render(state, ms, buffers, 0x00F000F0);
		dispatcher.setRenderShadow(true);

		buffers.endBatch();
		ms.popPose();
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
