package vazkii.patchouli.client.book.page;

import com.google.gson.annotations.SerializedName;
import com.mojang.math.Axis;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.base.PersistentData.Bookmark;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookEye;
import vazkii.patchouli.client.book.page.abstr.PageWithText;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.client.multiblock.MultiblockPiPRenderState;
import vazkii.patchouli.common.multiblock.MultiblockRegistry;
import vazkii.patchouli.common.multiblock.SerializedMultiblock;
import vazkii.patchouli.xplat.IClientXplatAbstractions;

import java.util.ArrayList;
import java.util.List;

public class PageMultiblock extends PageWithText {
	private static final int WIDTH = 99;
	private static final int HEIGHT = 99;
	private static boolean debug = false;

	String name = "";
	@SerializedName("multiblock_id") Identifier multiblockId;

	@SerializedName("multiblock") SerializedMultiblock serializedMultiblock;

	@SerializedName("enable_visualize") boolean showVisualizeButton = true;

	private transient final Camera camera = new Camera();
	private transient IMultiblock multiblockObj;
	private transient Button visualizeButton;

	@Override
	public void build(Level level, BookEntry entry, BookContentsBuilder builder, int pageNum) {
		super.build(level, entry, builder, pageNum);
		if (multiblockId != null) {
			multiblockObj = MultiblockRegistry.MULTIBLOCKS.get(multiblockId);
		}

		if (multiblockObj == null && serializedMultiblock != null) {
			multiblockObj = serializedMultiblock.toMultiblock();
		}

		if (multiblockObj == null) {
			throw new IllegalArgumentException("No multiblock located for " + multiblockId);
		}
	}

	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);

		if (showVisualizeButton) {
			addButton(visualizeButton = new GuiButtonBookEye(parent, 12, 97, this::handleButtonVisualize));
		}
	}

	@Override
	public int getTextHeight() {
		return 115;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float pticks) {
		graphics.pose().pushMatrix();
		graphics.pose().translate(GuiBook.PAGE_WIDTH / 2f, 0);
		GuiBook.drawFromTexture(graphics, book, -53, 7, 405, 149, WIDTH + 7, HEIGHT + 7);

		parent.drawCenteredStringNoShadow(graphics, i18n(name), 0, 0, book.headerColor);

		if (multiblockObj != null) {

			float time = parent.ticksInBook * 0.5F;
			if (!mc.hasShiftDown()) {
				time += ClientTicker.partialTicks;
			}

			var simulated = multiblockObj.simulate(mc.level, BlockPos.ZERO, Rotation.NONE, true);
			//camera.setSceneOrigin(simulated.getFirst().getCenter().toVector3f());
			camera.setYaw(time % 360);
			camera.setPitch(30);

			List<MultiblockPiPRenderState.BlockRenderState> blocks = new ArrayList<>();
			BlockModelResolver blockModelResolver = new BlockModelResolver(mc.getModelManager());
			BlockDisplayContext context = BlockDisplayContext.create();
			for (IMultiblock.SimulateResult simulateResult : simulated.getSecond()) {
				BlockPos pos = simulateResult.getWorldPosition();
				BlockState state = simulateResult.getStateMatcher().getDisplayedState(Math.round(time));

				if (state.isAir())
					continue;

				BlockModelRenderState modelRenderState = new BlockModelRenderState();
				blockModelResolver.update(modelRenderState, state, context);
				blocks.add(new MultiblockPiPRenderState.BlockRenderState(pos.immutable(), modelRenderState));
			}

			if (!blocks.isEmpty()) {
				graphics.pose().pushMatrix();
				graphics.pose().translate(-49, 11);
				graphics.enableScissor(0, 0, WIDTH, HEIGHT);

				if (debug) {
					graphics.fill(RenderPipelines.GUI, -10, -10, WIDTH + 10, HEIGHT + 10, 0xFF000000);
				}

				graphics.pose().pushMatrix();
				final float offsetX = (WIDTH / 2f);
				final float offsetY = (HEIGHT / 2f);
				graphics.pose().translate(offsetX, offsetY);
				renderMultiblock(graphics, -offsetX, -offsetY, WIDTH, HEIGHT, camera.viewMatrix(), blocks);
				graphics.pose().popMatrix();

				if (debug) {
					graphics.fill(RenderPipelines.GUI, Math.round(offsetX - 1), Math.round(offsetY - 1), Math.round(offsetX + 1), Math.round(offsetY + 1), 0xFFFF0000);
				}

				graphics.disableScissor();
				graphics.pose().popMatrix();
			}
		}
		graphics.pose().popMatrix();

		super.extractRenderState(graphics, mouseX, mouseY, pticks);
	}

	private static void renderMultiblock(GuiGraphicsExtractor graphics, float x, float y, int width, int height, Matrix4f viewMatrix, List<MultiblockPiPRenderState.BlockRenderState> blocks) {
		Vector2f start = graphics.pose().transformPosition(new Vector2f(x, y));
		Vector2f end = graphics.pose().transformPosition(new Vector2f(x + width, y + height));

		int startX = Math.round(start.x);
		int startY = Math.round(start.y);
		int endX = Math.round(end.x);
		int endY = Math.round(end.y);
		IClientXplatAbstractions.INSTANCE.submitPiPRenderState(graphics, scissor -> new MultiblockPiPRenderState(startX, startY, endX, endY, 1f, scissor, viewMatrix, blocks));
	}

	public void handleButtonVisualize(Button button) {
		var entryKey = parent.getEntry().getId();
		Bookmark bookmark = new Bookmark(entryKey, pageNum / 2);
		MultiblockVisualizationHandler.INSTANCE.setMultiblock(multiblockObj, i18nText(name), bookmark, true);
		parent.addBookmarkButtons();

		if (!PersistentData.data.clickedVisualize) {
			PersistentData.data.clickedVisualize = true;
			PersistentData.save();
		}
	}

	// Code donated by EnderIO
	public static final class Camera {
		private static final Quaternionf ROT_180_Z = Axis.ZP.rotation((float) Math.PI);

		private Vector3f sceneOrigin;

		private float scale;
		private float pitch;
		private float yaw;

		private Quaternionf blockTransform;
		private Matrix4f viewMatrix;
		private boolean isDirty = true;

		public Camera(Vector3f sceneOrigin, float scale, float pitch, float yaw) {
			this.sceneOrigin = new Vector3f(sceneOrigin.x, sceneOrigin.y, sceneOrigin.z);
			this.scale = scale;
			this.pitch = pitch;
			this.yaw = yaw;
		}

		public Camera() {
			this(new Vector3f(), 20, 0, 0);
		}

		public Vector3f sceneOrigin() {
			return sceneOrigin;
		}

		public float scale() {
			return scale;
		}

		public float pitch() {
			return pitch;
		}

		public float yaw() {
			return yaw;
		}

		public void setSceneOrigin(Vector3f sceneOrigin) {
			if (this.sceneOrigin.equals(sceneOrigin)) {
				return;
			}

			this.sceneOrigin = sceneOrigin;
			isDirty = true;
		}

		public void setScale(float scale) {
			if (this.scale == scale) {
				return;
			}

			this.scale = scale;
			isDirty = true;
		}

		public void setPitch(float pitch) {
			if (this.pitch == pitch) {
				return;
			}

			this.pitch = pitch;
			isDirty = true;
		}

		public void setYaw(float yaw) {
			if (this.yaw == yaw) {
				return;
			}

			this.yaw = yaw;
			isDirty = true;
		}

		public Quaternionf blockTransform() {
			if (isDirty) {
				recompute();
			}

			return blockTransform;
		}

		public Matrix4f viewMatrix() {
			if (isDirty) {
				recompute();
			}

			return viewMatrix;
		}

		private void recompute() {
			// Compute rotation
			Quaternionf rotPitch = Axis.XN.rotationDegrees(pitch);
			Quaternionf rotYaw = Axis.YP.rotationDegrees(yaw);

			// Build block transformation matrix
			// Rotate 180 around Z, otherwise the block is upside down
			blockTransform = new Quaternionf(ROT_180_Z);
			// Rotate around X (pitch) in negative direction
			blockTransform.mul(rotPitch);
			// Rotate around Y (yaw)
			blockTransform.mul(rotYaw);

			// Create view matrix
			viewMatrix = new Matrix4f();
			viewMatrix.scale(scale, scale, scale);
			viewMatrix.translate(-sceneOrigin.x, -sceneOrigin.y, -sceneOrigin.z);
			viewMatrix.rotate(blockTransform);

			isDirty = false;
		}
	}
}
