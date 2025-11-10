package vazkii.patchouli.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.common.multiblock.StateMatcher;

import java.util.Optional;

/**
 * Centralizes ghost block targeting logic for the multiblock visualizer.
 */
public final class MultiblockGhostHandler {

    private static BlockHitResult ghostHit;
    private static BlockState previewState;
    private static ItemStack previewStack = ItemStack.EMPTY;

    private MultiblockGhostHandler() {
    }

    public static void reset() {
        ghostHit = null;
        previewState = null;
        previewStack = ItemStack.EMPTY;
    }

    public static void updateHit(BlockHitResult hit) {
        ghostHit = hit;
    }

    public static BlockHitResult getHit() {
        return ghostHit;
    }

    public static void clearPreview() {
        previewState = null;
        previewStack = ItemStack.EMPTY;
    }

    public static void updatePreview(BlockState renderState, BlockState displayState, boolean air) {
        previewState = renderState;
        previewStack = air ? ItemStack.EMPTY : new ItemStack(displayState.getBlock().asItem());
    }

    public static BlockState getPreviewState() {
        return previewState;
    }

    public static ItemStack getPreviewStack() {
        return previewStack;
    }

    public static boolean canPick() {
        return ghostHit != null && previewStack != null && !previewStack.isEmpty();
    }

    public static boolean handleMiddleClick(Player player) {
        if (!MultiblockVisualizationHandler.hasMultiblock || !canPick()) {
            return false;
        }

        Minecraft mc = Minecraft.getInstance();
        MultiPlayerGameMode gameMode = mc.gameMode;
        Inventory inv = player.getInventory();
        ItemStack target = previewStack;

        if (player.isCreative()) {
            ItemStack[] before = snapshotInventory(inv);
            int previousSelected = inv.getSelectedSlot();
            int matchSlot = inv.findSlotMatchingItem(target);
            boolean selectionChanged = false;

            if (matchSlot != -1) {
                if (Inventory.isHotbarSlot(matchSlot)) {
                    selectionChanged = previousSelected != matchSlot;
                    inv.setSelectedSlot(matchSlot);
                } else {
                    int hotbarSlot = inv.getSuitableHotbarSlot();
                    inv.setSelectedSlot(hotbarSlot);
                    ItemStack hotbarStack = inv.getItem(hotbarSlot);
                    ItemStack foundStack = inv.getItem(matchSlot);
                    inv.setItem(hotbarSlot, foundStack);
                    inv.setItem(matchSlot, hotbarStack);
                    selectionChanged = true;
                }
            } else {
                inv.addAndPickItem(target.copy());
                selectionChanged = true;
            }

            inv.setChanged();
            syncCreativeInventory(mc, gameMode, inv, before);

            if (selectionChanged) {
                sendSelectedSlotPacket(mc, inv.getSelectedSlot());
            }

            return true;
        }

        int found = inv.findSlotMatchingItem(target);
        if (found == -1) {
            return true;
        }

        if (gameMode == null) {
            return false;
        }

        if (Inventory.isHotbarSlot(found)) {
            if (inv.getSelectedSlot() != found) {
                inv.setSelectedSlot(found);
                sendSelectedSlotPacket(mc, found);
            }
            return true;
        }

        int hotbarSlot = inv.getSuitableHotbarSlot();
        int containerId = player.containerMenu.containerId;

        gameMode.handleInventoryMouseClick(containerId, found, 0, ClickType.PICKUP, player);
        gameMode.handleInventoryMouseClick(containerId, hotbarSlot, 0, ClickType.PICKUP, player);
        gameMode.handleInventoryMouseClick(containerId, found, 0, ClickType.PICKUP, player);

        inv.setSelectedSlot(hotbarSlot);
        sendSelectedSlotPacket(mc, hotbarSlot);
        return true;
    }

    private static ItemStack[] snapshotInventory(Inventory inv) {
        int size = inv.getContainerSize();
        ItemStack[] before = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            before[i] = inv.getItem(i).copy();
        }
        return before;
    }

    private static void syncCreativeInventory(Minecraft mc, MultiPlayerGameMode gameMode, Inventory inv, ItemStack[] before) {
        if (before == null) {
            return;
        }

        int size = Math.min(before.length, inv.getContainerSize());
        for (int i = 0; i < size; i++) {
            ItemStack beforeStack = before[i];
            ItemStack afterStack = inv.getItem(i);
            if (!ItemStack.matches(beforeStack, afterStack)) {
                sendCreativeSlotUpdate(mc, gameMode, i, afterStack);
            }
        }
    }

    private static void sendCreativeSlotUpdate(Minecraft mc, MultiPlayerGameMode gameMode, int slot, ItemStack stack) {
        int creativeSlot = Inventory.isHotbarSlot(slot) ? 36 + slot : slot;
        if (gameMode != null) {
            gameMode.handleCreativeModeItemAdd(stack, creativeSlot);
        } else if (mc.getConnection() != null) {
            mc.getConnection().send(new ServerboundSetCreativeModeSlotPacket(creativeSlot, stack));
        }
    }

    private static void sendSelectedSlotPacket(Minecraft mc, int slot) {
        if (mc.getConnection() != null) {
            mc.getConnection().send(new ServerboundSetCarriedItemPacket(slot));
        }
    }

    public static BlockHitResult getAdjustedHitResult(Player player, BlockHitResult realHit) {
        if (!MultiblockVisualizationHandler.hasMultiblock) {
            return realHit;
        }

        BlockHitResult ghost = ghostHit;
        if (ghost == null) {
            return realHit;
        }

        Vec3 eye = player.getEyePosition(1f);
        double ghostDist = ghost.getLocation().distanceTo(eye);
        if (realHit != null) {
            double realDist = realHit.getLocation().distanceTo(eye);
            if (realDist <= ghostDist) {
                return realHit;
            }
        }
        return ghost;
    }

    public static BlockHitResult raycastGhost(Player player, float partialTicks) {
        if (!MultiblockVisualizationHandler.hasMultiblock) {
            return null;
        }
        IMultiblock multiblock = MultiblockVisualizationHandler.getMultiblock();
        if (multiblock == null) {
            return null;
        }
        BlockPos start = MultiblockVisualizationHandler.getStartPos();
        if (start == null) {
            return null;
        }

        Level level = player.level();
        Rotation rot = MultiblockVisualizationHandler.getFacingRotation();

        Vec3 eye = player.getEyePosition(partialTicks);
        Vec3 look = player.getViewVector(partialTicks);
        double reach = player.blockInteractionRange();
        Vec3 end = eye.add(look.scale(reach));

        var sim = multiblock.simulate(level, start, rot, true).getSecond();
        BlockHitResult closest = null;
        double closestDist = Double.MAX_VALUE;

        for (IMultiblock.SimulateResult r : sim) {
            if (r.getStateMatcher() == StateMatcher.ANY) {
                continue;
            }

            BlockPos blockPos = r.getWorldPosition();
            BlockState display = r.getStateMatcher().getDisplayedState(ClientTicker.ticksInGame).rotate(rot);
            VoxelShape shape = display.getShape(level, blockPos);
            if (shape.isEmpty()) {
                continue;
            }

            for (AABB bb : shape.toAabbs()) {
                AABB shifted = bb.move(blockPos);
                Optional<Vec3> hit = shifted.clip(eye, end);
                if (hit.isEmpty()) {
                    continue;
                }

                Vec3 hitVec = hit.get();
                double dist = eye.distanceTo(hitVec);
                if (dist < closestDist) {
                    closestDist = dist;
                    Vec3 center = new Vec3(blockPos.getX() + 0.5D, blockPos.getY() + 0.5D, blockPos.getZ() + 0.5D);
                    Direction face = faceFromRelative(hitVec.subtract(center));
                    closest = new BlockHitResult(hitVec, face, blockPos, false);
                }
            }
        }

        return closest;
    }

    private static Direction faceFromRelative(Vec3 rel) {
        double ax = Math.abs(rel.x);
        double ay = Math.abs(rel.y);
        double az = Math.abs(rel.z);
        if (ax >= ay && ax >= az) {
            return rel.x > 0 ? Direction.EAST : Direction.WEST;
        }
        if (ay >= ax && ay >= az) {
            return rel.y > 0 ? Direction.UP : Direction.DOWN;
        }
        return rel.z > 0 ? Direction.SOUTH : Direction.NORTH;
    }
}
