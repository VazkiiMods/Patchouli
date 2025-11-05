package vazkii.patchouli.client.handler;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
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
	private static BlockPos lookingPos;
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

		blocks = blocksDone = airFilled = 0;
		lookingState = null;
		lookingPos = checkPos;

		Pair<BlockPos, Collection<IMultiblock.SimulateResult>> sim = multiblock.simulate(world, getStartPos(), getFacingRotation(), true);
		for (IMultiblock.SimulateResult r : sim.getSecond()) {
			float alpha = 0.3F;
			if (r.getWorldPosition().equals(checkPos)) {
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

		if (!isAnchored) {
			blocks = blocksDone = 0;
		}
		for (IMultiblock.SimulateResult r : sim.getSecond()) {
			if (r.getWorldPosition().equals(checkPos)) {
				lookingState = r.getStateMatcher().getDisplayedState(ClientTicker.ticksInGame);
				lookingStack = new ItemStack(lookingState.getBlock().asItem());
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

				state = Blocks.RED_CONCRETE.defaultBlockState();
			}

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
		ByteBufferBuilder fallback = ((AccessorMultiBufferSource) original).getFallbackBuffer();
		SequencedMap<RenderType, ByteBufferBuilder> layerBuffers = ((AccessorMultiBufferSource) original).getFixedBuffers();
		SequencedMap<RenderType, ByteBufferBuilder> remapped = new Object2ObjectLinkedOpenHashMap<>();
		for (Map.Entry<RenderType, ByteBufferBuilder> e : layerBuffers.entrySet()) {
			remapped.put(GhostRenderLayer.remap(e.getKey()), e.getValue());
		}
		return new GhostBuffers(fallback, remapped);
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

	private static class GhostRenderLayer extends RenderType {
		private static final Map<RenderType, RenderType> remappedTypes = new IdentityHashMap<>();
		private final RenderType original;

		private GhostRenderLayer(RenderType original) {
			super(
					String.format("%s_%s_ghost", original.toString(), PatchouliAPI.MOD_ID),

					original.bufferSize(),
					original.affectsCrumbling(),
					true, // sortOnUpload
					() -> {
						original.setupRenderState();

						RenderSystem.setShaderColor(1, 1, 1, 0.4F);
					},
					() -> {
						RenderSystem.setShaderColor(1, 1, 1, 1);

						original.clearRenderState();
					}
			);
			this.original = original;
		}

		public static RenderType remap(RenderType in) {
			if (in instanceof GhostRenderLayer) {
				return in;
			} else {
				return remappedTypes.computeIfAbsent(in, GhostRenderLayer::new);
			}
		}

		@Override
		public void draw(@NotNull MeshData meshData) {
			original.draw(meshData);
		}

		@Override
		public @NotNull RenderTarget getRenderTarget() {
			return original.getRenderTarget();
		}

		@Override
		public @NotNull RenderPipeline getRenderPipeline() {
			return original.getRenderPipeline();
		}

		@Override
		public @NotNull VertexFormat format() {
			return original.format();
		}

		@Override
		public @NotNull Mode mode() {
			return original.mode();
		}
	}
	public static void handleMiddleClick(Player player) {
		if (!hasMultiblock || lookingStack == null || lookingStack.isEmpty()) return;

		Inventory inv = player.getInventory();

		// CREATIVE: clone the item into hotbar like vanilla
		if (player.isCreative()) {
			int slot = inv.findSlotMatchingItem(lookingStack);
			if (slot == -1) {
				// try to put in empty hotbar
				for (int i = 0; i < 9; i++) {
					if (inv.getItem(i).isEmpty()) {
						slot = i;
						break;
					}
				}
			}
			if (slot == -1) slot = inv.getSelectedSlot();

			ItemStack copy = lookingStack.copy();
			inv.setItem(slot, copy);

			if (Minecraft.getInstance().getConnection() != null) {
				Minecraft.getInstance().getConnection().send(
						new ServerboundSetCreativeModeSlotPacket(36 + slot, copy)
				);
			}
			return;
		}

		// SURVIVAL:
		// Only allow item pick if player already has at least one
		int found = inv.findSlotMatchingItem(lookingStack);
		if (found == -1) {
			// player doesn't own this item -> do nothing
			return;
		}

		int selected = inv.getSelectedSlot();

		// Item already in hotbar -> just select that slot
		if (Inventory.isHotbarSlot(found)) {
			inv.setSelectedSlot(found);

			if (Minecraft.getInstance().getConnection() != null) {
				Minecraft.getInstance().getConnection().send(
						new ServerboundSetCarriedItemPacket(found)
				);
			}
			return;
		}

		// Item in main inventory -> swap it into the selected slot
		ItemStack selectedStack = inv.getItem(selected);
		ItemStack foundStack = inv.getItem(found);

		inv.setItem(selected, foundStack); // move found to hand
		inv.setItem(found, selectedStack); // move hand item to old slot

		// Send slot change to server
		if (Minecraft.getInstance().getConnection() != null) {
			Minecraft.getInstance().getConnection().send(
					new ServerboundSetCarriedItemPacket(selected)
			);
		}
	}
	public static BlockHitResult getAdjustedHitResult(Player player, double unusedReach) {
		Minecraft mc = Minecraft.getInstance();
		if (!hasMultiblock || lookingState == null || lookingPos == null) {
			return mc.hitResult instanceof BlockHitResult bhr ? bhr : null;
		}

		// reach distance: prefer client game mode pick range if present
        double reach = 0;
        if (mc.player != null) {
            reach = mc.gameMode != null ? mc.player.blockInteractionRange() : 6.0D;
        }

        Vec3 eye = player.getEyePosition(1f);
		Vec3 look = player.getViewVector(1f);
		Vec3 end = eye.add(look.scale(reach));

		// ghost block AABB in world space
        AABB box = null;
        if (mc.level != null) {
            box = lookingState.getShape(mc.level, lookingPos).bounds().move(lookingPos.getX(), lookingPos.getY(), lookingPos.getZ());
        }

        Optional<Vec3> opt = Objects.requireNonNull(box).clip(eye, end);
		if (opt.isEmpty()) {
			return mc.hitResult instanceof BlockHitResult bhr ? bhr : null;
		}

		Vec3 hit = opt.get();
		double hitDist = hit.distanceTo(eye);

		// if there is an existing real hit, keep whichever is closer
		double currentDist = Double.POSITIVE_INFINITY;
		if (mc.hitResult instanceof BlockHitResult realBhr) {
			currentDist = realBhr.getLocation().distanceTo(eye);
		}

		if (hitDist > currentDist) {
			return mc.hitResult instanceof BlockHitResult bhr ? bhr : null;
		}

		// determine face by comparing hit relative to block center
		Vec3 center = new Vec3(lookingPos.getX() + 0.5D, lookingPos.getY() + 0.5D, lookingPos.getZ() + 0.5D);
		Vec3 rel = hit.subtract(center);
		double ax = Math.abs(rel.x);
		double ay = Math.abs(rel.y);
		double az = Math.abs(rel.z);
		Direction face;
		if (ax >= ay && ax >= az) {
			face = rel.x > 0 ? Direction.EAST : Direction.WEST;
		} else if (ay >= ax && ay >= az) {
			face = rel.y > 0 ? Direction.UP : Direction.DOWN;
		} else {
			face = rel.z > 0 ? Direction.SOUTH : Direction.NORTH;
		}

		return new BlockHitResult(hit, face, lookingPos, false);
	}

	public static InteractionResult onPlayerInteract(Player player, InteractionHand hand) {
		BlockHitResult bhr = getAdjustedHitResult(player,  Objects.requireNonNull(player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE)).getValue());
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
