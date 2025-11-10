package vazkii.patchouli.client.handler;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData.Bookmark;
import vazkii.patchouli.common.multiblock.StateMatcher;
import vazkii.patchouli.common.util.RotationUtil;
import vazkii.patchouli.mixin.client.AccessorMultiBufferSource;

import java.awt.*;
import com.mojang.blaze3d.systems.RenderSystem;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;


@SuppressWarnings("unused")

public class MultiblockVisualizationHandler {

	public static boolean hasMultiblock;
	public static Bookmark bookmark;

	private static IMultiblock multiblock;
	private static Component name;
	private static BlockPos pos;
	private static boolean isAnchored;
	private static Rotation facingRotation;
	private static Function<BlockPos, BlockPos> offsetApplier;
	private static int blocks, blocksDone, airFilled;
	private static int timeComplete;
	private static BlockState lookingState;
	private static MultiBufferSource.BufferSource buffers = null;
	public static ItemStack lookingStack = ItemStack.EMPTY;

	public static void setMultiblock(IMultiblock multiblock, Component name, Bookmark bookmark, boolean flip) {
		setMultiblock(multiblock, name, bookmark, flip, pos -> pos);
	}

	public static void setMultiblock(IMultiblock multiblock, Component name, Bookmark bookmark, boolean flip, Function<BlockPos, BlockPos> offsetApplier) {
		if (flip && hasMultiblock) {
			hasMultiblock = false;
		} else {
			MultiblockVisualizationHandler.multiblock = multiblock;
			MultiblockVisualizationHandler.name = name;
			MultiblockVisualizationHandler.bookmark = bookmark;
			MultiblockVisualizationHandler.offsetApplier = offsetApplier;
			pos = null;
			hasMultiblock = multiblock != null;
			isAnchored = false;
		}
	}

	public static void onRenderHUD(GuiGraphics graphics, DeltaTracker deltaTracker) {
		if (hasMultiblock) {
			int waitTime = 40;
			int fadeOutSpeed = 4;
			int fullAnimTime = waitTime + 10;
			float animTime = timeComplete + (timeComplete == 0 ? 0 : deltaTracker.getGameTimeDeltaPartialTick(false));

			if (animTime > fullAnimTime) {
				hasMultiblock = false;
				return;
			}

			graphics.pose().pushPose();
			graphics.pose().translate(0, -Math.max(0, animTime - waitTime) * fadeOutSpeed, 0);

			Minecraft mc = Minecraft.getInstance();
			int x = mc.getWindow().getGuiScaledWidth() / 2;
			int y = 12;

			graphics.drawCenteredString(mc.font, name, x, y, 0xFFFFFF);

			int width = 180;
			int height = 9;
			int left = x - width / 2;
			int top = y + 10;

			if (timeComplete > 0) {
				graphics.pose().pushPose();
				graphics.pose().translate(0, Math.min(height + 5, animTime), 0);
				graphics.drawCenteredString(mc.font, Component.translatable("patchouli.gui.lexicon.structure_complete"), x, top + height - 10, 0x00FF00);
				graphics.pose().popPose();
			}

			// Background
			graphics.fill(left - 1, top - 1, left + width + 1, top + height + 1, 0xFF000000);
			drawGradientRect(graphics, left, top, left + width, top + height);

			// Progress
			float fract = (float) blocksDone / Math.max(1, blocks);
			int progressWidth = (int) ((float) width * fract);
			int color = Mth.hsvToRgb(fract / 3.0F, 1.0F, 1.0F) | 0xFF000000;
			int color2 = new Color(color).darker().getRGB();
			graphics.fillGradient(left, top, left + progressWidth, top + height, color, color2);

			if (!isAnchored) {
				graphics.drawCenteredString(mc.font, Component.translatable("patchouli.gui.lexicon.not_anchored"), x, top + height + 8, 0xFFFFFF);
			} else {
				if (lookingState != null) {
					try {
						ItemStack stack = new ItemStack(lookingState.getBlock().asItem());

						if (!lookingStack.isEmpty()) {
							graphics.drawString(mc.font, lookingStack.getHoverName(), left + 20, top + height + 8, 0xFFFFFF, true);
							graphics.renderItem(lookingStack, left, top + height + 2);
						}
					} catch (Exception ignored) {}
				}

				if (timeComplete == 0) {
					color = 0xFFFFFF;
					int posx = left + width;
					int posy = top + height + 2;
					int mult = 1;
					String progress = blocksDone + "/" + blocks;

					if (blocksDone == blocks && airFilled > 0) {
						progress = I18n.get("patchouli.gui.lexicon.needs_air");
						color = 0xDA4E3F;
						mult *= 2;
						posx -= width / 2;
						posy += 2;
					}

					graphics.drawString(mc.font, progress, posx - mc.font.width(progress) / mult, posy, color, false);
				}
			}

			graphics.pose().popPose();
		}
	}

	public static void onWorldRenderLast(PoseStack ms) {
		if (hasMultiblock && multiblock != null) {
			renderMultiblock(Minecraft.getInstance().level, ms);
		}
	}

	public static void anchorTo(BlockPos target, Rotation rot) {
		pos = target;
		facingRotation = rot;
		isAnchored = true;
	}

	public static InteractionResult onPlayerInteract(Player player, Level world, InteractionHand hand, BlockHitResult hit) {
		if (hasMultiblock && !isAnchored && player == Minecraft.getInstance().player) {
			anchorTo(hit.getBlockPos(), getRotation(player));
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	public static void onClientTick(Minecraft mc) {
		if (Minecraft.getInstance().level == null) {
			hasMultiblock = false;
		} else if (isAnchored && blocks == blocksDone && airFilled == 0) {
			timeComplete++;
			if (timeComplete == 14) {
				Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F));
			}
		} else {
			timeComplete = 0;
		}
	}

	
	public static void renderMultiblock(Level world, PoseStack ms) {
		Minecraft mc = Minecraft.getInstance();
		BlockHitResult ghostHit = MultiblockGhostHandler.getHit();
		BlockPos ghostPos = ghostHit != null ? ghostHit.getBlockPos() : null;
		MultiblockGhostHandler.clearPreview();

		if (!isAnchored) {
			if (mc.player != null) {
				facingRotation = getRotation(mc.player);
			}
			if (mc.hitResult instanceof BlockHitResult) {
				pos = ((BlockHitResult) mc.hitResult).getBlockPos();
			}
		} else if (mc.player != null && pos.distToCenterSqr(mc.player.position()) > 64 * 64) {
			return;
		}

		if (pos == null) {
			return;
		}
		if (multiblock.isSymmetrical()) {
			facingRotation = Rotation.NONE;
		}

		if (buffers == null) {
			buffers = initBuffers(mc.renderBuffers().bufferSource());
		}

		BlockPos checkPos = null;
		if (mc.hitResult instanceof BlockHitResult blockRes) {
			checkPos = blockRes.getBlockPos().relative(blockRes.getDirection());
		}
		BlockPos targetPos = ghostPos != null ? ghostPos : checkPos;

		blocks = blocksDone = airFilled = 0;
		lookingState = null;
		lookingStack = ItemStack.EMPTY;

		// Save shader color before rendering
		float[] prevColor = RenderSystem.getShaderColor();
		float prevR = prevColor[0];
		float prevG = prevColor[1];
		float prevB = prevColor[2];
		float prevA = prevColor[3];

		Pair<BlockPos, Collection<IMultiblock.SimulateResult>> sim = multiblock.simulate(world, getStartPos(), getFacingRotation(), true);
		for (IMultiblock.SimulateResult r : sim.getSecond()) {
			float alpha = 0.3F;
			if (targetPos != null && r.getWorldPosition().equals(targetPos)) {
				lookingState = r.getStateMatcher().getDisplayedState(ClientTicker.ticksInGame);
				alpha = 0.6F + (float) (Math.sin(ClientTicker.total * 0.3F) + 1F) * 0.1F;
			}

			if (r.getStateMatcher() != StateMatcher.ANY) {
				boolean air = r.getStateMatcher() == StateMatcher.AIR;
				if (!air) {
					blocks++;
				}

				if (!r.test(world, facingRotation)) {
					BlockState renderState = r.getStateMatcher().getDisplayedState(ClientTicker.ticksInGame).rotate(facingRotation);
					renderBlock(world, renderState, r.getWorldPosition(), alpha, ms);

					if (air) {
						airFilled++;
					}
				} else if (!air) {
					blocksDone++;
				}
			}
		}

		buffers.endBatch();
		
		// Restore shader color after batch is drawn
		RenderSystem.setShaderColor(prevR, prevG, prevB, prevA);

		if (ghostPos != null) {
			for (IMultiblock.SimulateResult r : sim.getSecond()) {
				if (r.getWorldPosition().equals(ghostPos) && r.getStateMatcher() != StateMatcher.ANY) {
					BlockState displayState = r.getStateMatcher().getDisplayedState(ClientTicker.ticksInGame);
					BlockState renderState = displayState.rotate(facingRotation);
					MultiblockGhostHandler.updatePreview(renderState, displayState, r.getStateMatcher() == StateMatcher.AIR);
					break;
				}
			}
		}

		if (!isAnchored) {
			blocks = blocksDone = 0;
		}
		for (IMultiblock.SimulateResult r : sim.getSecond()) {
			if (targetPos != null && r.getWorldPosition().equals(targetPos)) {
				lookingState = r.getStateMatcher().getDisplayedState(ClientTicker.ticksInGame);
				lookingStack = new ItemStack(lookingState.getBlock().asItem());
				break;
			}
		}
	}

	public static void renderBlock(Level world, BlockState state, BlockPos pos, float alpha, PoseStack ms) {
		if (pos != null) {
			ms.pushPose();
			ms.translate(pos.getX(), pos.getY(), pos.getZ());

			if (state.getBlock() == Blocks.AIR) {
				float scale = 0.3F;
				float off = (1F - scale) / 2;
				ms.translate(off, off, -off);
				ms.scale(scale, scale, scale);

				state = Blocks.BARRIER.defaultBlockState();
			}

			RenderSystem.setShaderColor(1F, 1F, 1F, alpha);
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, ms, buffers, 0xF000F0, OverlayTexture.NO_OVERLAY);

			ms.popPose();
		}
	}

	public static IMultiblock getMultiblock() {
		return multiblock;
	}

	public static boolean isAnchored() {
		return isAnchored;
	}

	public static Rotation getFacingRotation() {
		return multiblock.isSymmetrical() ? Rotation.NONE : facingRotation;
	}

	public static boolean canPickGhost() {
		return MultiblockGhostHandler.canPick();
	}

	public static BlockPos getStartPos() {
		return offsetApplier.apply(pos);
	}

	// Updated drawGradientRect
	private static void drawGradientRect(GuiGraphics graphics, int left, int top, int right, int bottom) {
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		Matrix4f mat = graphics.pose().last().pose();

		bufferbuilder.addVertex(mat, right, top, 0).setColor(-10066330);
		bufferbuilder.addVertex(mat, left, top, 0).setColor(-10066330);
		bufferbuilder.addVertex(mat, left, bottom, 0).setColor(-11184811);
		bufferbuilder.addVertex(mat, right, bottom, 0).setColor(-11184811);

		var built = bufferbuilder.build();
		if (built != null) {
			RenderType.gui().draw(built);
		}
	}

	/**
	 * Returns the Rotation of a multiblock structure based on the given entity's facing direction.
	 */
	private static Rotation getRotation(Entity entity) {
		return RotationUtil.rotationFromFacing(entity.getDirection());
	}

	private static MultiBufferSource.BufferSource initBuffers(MultiBufferSource.BufferSource original) {
		SequencedMap<RenderType, ByteBufferBuilder> remapped = new Object2ObjectLinkedOpenHashMap<>();
		for (RenderType type : ((AccessorMultiBufferSource) original).getFixedBuffers().keySet()) {
			RenderType ghostType = GhostRenderLayer.remap(type);
			remapped.put(ghostType, new ByteBufferBuilder(type.bufferSize()));
		}
		return new GhostBuffers(new ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE), remapped);
	}

	private static class GhostBuffers extends MultiBufferSource.BufferSource {
		protected GhostBuffers(ByteBufferBuilder fallback, SequencedMap<RenderType, ByteBufferBuilder> layerBuffers) {
			super(fallback, layerBuffers);
		}

		@Override
		public @NotNull VertexConsumer getBuffer(@NotNull RenderType type) {
			return super.getBuffer(GhostRenderLayer.remap(type));
		}
	}

	private static class GhostRenderLayer {
		private static final RenderType TRANSLUCENT = RenderType.translucent();
		private static final Map<RenderType, RenderType> CACHE = new IdentityHashMap<>();
		private static final Class<?> COMPOSITE_RENDER_TYPE_CLASS;
		private static final Class<?> COMPOSITE_STATE_CLASS;
		private static final Method CREATE_METHOD;
		private static final Method PIPELINE_CLONE_METHOD;
		private static final Field COMPOSITE_STATE_FIELD;

		static {
			try {
				COMPOSITE_RENDER_TYPE_CLASS = Class.forName("net.minecraft.client.renderer.RenderType$CompositeRenderType");
				COMPOSITE_STATE_CLASS = Class.forName("net.minecraft.client.renderer.RenderType$CompositeState");
				CREATE_METHOD = RenderType.class.getDeclaredMethod("create",
						String.class,
						int.class,
						boolean.class,
						boolean.class,
						RenderPipeline.class,
						COMPOSITE_STATE_CLASS);
				CREATE_METHOD.setAccessible(true);

				Method clone = null;
				for (Method method : RenderPipeline.class.getDeclaredMethods()) {
					if (method.getParameterCount() == 0
							&& method.getReturnType().getName().equals("com.mojang.blaze3d.pipeline.RenderPipeline$Builder")) {
						clone = method;
						clone.setAccessible(true);
						break;
					}
				}

				if (clone == null) {
					throw new IllegalStateException("Unable to locate RenderPipeline builder accessor");
				}
				PIPELINE_CLONE_METHOD = clone;

				Field stateField;
				try {
					stateField = COMPOSITE_RENDER_TYPE_CLASS.getDeclaredField("state");
				} catch (NoSuchFieldException ignored) {
					stateField = null;
					for (Field field : COMPOSITE_RENDER_TYPE_CLASS.getDeclaredFields()) {
						if (field.getType() == COMPOSITE_STATE_CLASS) {
							stateField = field;
							break;
						}
					}
					if (stateField == null) {
						throw new IllegalStateException("Failed to locate CompositeRenderType state field");
					}
				}
				stateField.setAccessible(true);
				COMPOSITE_STATE_FIELD = stateField;
			} catch (ReflectiveOperationException e) {
				throw new IllegalStateException("Failed to resolve RenderType#create for ghost rendering", e);
			}
		}

		private GhostRenderLayer() {
		}

		public static RenderType remap(RenderType type) {
			if (type == TRANSLUCENT || type.toString().startsWith("patchouli_ghost/")) {
				return type;
			}

			if (!COMPOSITE_RENDER_TYPE_CLASS.isInstance(type)) {
				return type;
			}

			return CACHE.computeIfAbsent(type, GhostRenderLayer::createGhostType);
		}

		private static RenderType createGhostType(RenderType original) {
			Object state = getCompositeState(original);
			if (!COMPOSITE_STATE_CLASS.isInstance(state)) {
				throw new IllegalStateException("Unexpected composite state type for render layer " + original);
			}

			RenderPipeline.Builder builder = clonePipeline(original.getRenderPipeline())
					.withCull(false)
					.withDepthWrite(false)
					.withBlend(BlendFunction.TRANSLUCENT)
					.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST);
			RenderPipeline pipeline = builder.build();
			String name = "patchouli_ghost/" + original;

			try {
				return (RenderType) CREATE_METHOD.invoke(null,
						name,
						original.bufferSize(),
						original.affectsCrumbling(),
						true,
						pipeline,
						state);
			} catch (ReflectiveOperationException e) {
				throw new IllegalStateException("Failed to create ghost render type for " + original, e);
			}
		}

		private static RenderPipeline.Builder clonePipeline(RenderPipeline source) {
			try {
				return (RenderPipeline.Builder) PIPELINE_CLONE_METHOD.invoke(source);
			} catch (ReflectiveOperationException e) {
				throw new IllegalStateException("Failed to clone render pipeline", e);
			}
		}

		private static Object getCompositeState(RenderType original) {
			try {
				return COMPOSITE_STATE_FIELD.get(original);
			} catch (IllegalAccessException e) {
				throw new IllegalStateException("Failed to read composite state for " + original, e);
			}
		}
	}
	public static boolean handleMiddleClick(Player player) {
		return MultiblockGhostHandler.handleMiddleClick(player);
	}
	public static InteractionResult onPlayerInteract(Player player, InteractionHand hand) {
		Minecraft mc = Minecraft.getInstance();
		BlockHitResult realHit = mc.hitResult instanceof BlockHitResult bhr ? bhr : null;
		BlockHitResult bhr = MultiblockGhostHandler.getAdjustedHitResult(player, realHit);
		if (bhr != null && hasMultiblock && !isAnchored) {
			anchorTo(bhr.getBlockPos(), getRotation(player));
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	public static BlockHitResult raycastGhost(Player player, float partialTicks) {
		if (!hasMultiblock || multiblock == null || pos == null) return null;

		Level level = player.level();
		Rotation rot = getFacingRotation();
		BlockPos start = getStartPos();

		// Player view ray
		Vec3 eye = player.getEyePosition(partialTicks);
		Vec3 look = player.getViewVector(partialTicks);
		double reach = player.blockInteractionRange();
		Vec3 end = eye.add(look.scale(reach));

		// Iterate ghost structure blocks
		var sim = multiblock.simulate(level, start, rot, true).getSecond();

		BlockHitResult closest = null;
		double closestDist = Double.MAX_VALUE;

		for (IMultiblock.SimulateResult r : sim) {
			if (r.getStateMatcher() == StateMatcher.ANY) continue; // ignore don't-care blocks

			BlockPos blockPos = r.getWorldPosition();
			BlockState display = r.getStateMatcher().getDisplayedState(ClientTicker.ticksInGame).rotate(rot);

			// Get block shape AABB
			VoxelShape shape = display.getShape(level, blockPos);
			if (shape.isEmpty()) continue;

			for (AABB bb : shape.toAabbs()) {
				AABB shifted = bb.move(blockPos);

				Optional<Vec3> hit = shifted.clip(eye, end);
				if (hit.isEmpty()) continue;

				double dist = eye.distanceTo(hit.get());
				if (dist < closestDist) {
					closestDist = dist;
					closest = new BlockHitResult(hit.get(), Direction.getApproximateNearest(look.x, look.y, look.z), blockPos, false);
				}
			}
		}

		return closest;
	}



}
