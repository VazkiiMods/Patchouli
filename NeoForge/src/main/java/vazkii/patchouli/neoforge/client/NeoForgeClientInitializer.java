package vazkii.patchouli.neoforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.base.*;
import vazkii.patchouli.client.book.BookContentResourceListenerLoader;
import vazkii.patchouli.client.book.BookReloadHook;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.multiblock.MultiblockPiPRenderState;
import vazkii.patchouli.client.multiblock.MultiblockPiPRenderer;
import vazkii.patchouli.client.handler.BookRightClickHandler;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.client.handler.TooltipHandler;
import vazkii.patchouli.client.hud.BookOverlayHud;
import vazkii.patchouli.client.hud.MultiblockProgressHud;

import org.jetbrains.annotations.NotNull;

@Mod(value = PatchouliAPI.MOD_ID, dist = Dist.CLIENT)
public class NeoForgeClientInitializer {
	public NeoForgeClientInitializer(IEventBus modBus) {
		NeoForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> {
			BookRightClickHandler.onRightClick(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec());
			InteractionResult result = MultiblockVisualizationHandler.INSTANCE.onPlayerInteract(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec());
			if (result.consumesAction()) {
				e.setCanceled(true);
				e.setCancellationResult(result);
			}
		});
		NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post e) -> {
			ClientTicker.endClientTick(Minecraft.getInstance());
			MultiblockVisualizationHandler.INSTANCE.onClientTick(Minecraft.getInstance());
		});
		NeoForge.EVENT_BUS.addListener((RenderFrameEvent.Pre e) -> {
			ClientTicker.renderTickStart(e.getPartialTick().getGameTimeDeltaPartialTick(false));
		});
		NeoForge.EVENT_BUS.addListener((RenderFrameEvent.Post e) -> {
			ClientTicker.renderTickEnd();
		});
		NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut e) -> {
			ClientAdvancements.playerLogout();
		});
		NeoForge.EVENT_BUS.addListener((RenderTooltipEvent.Pre e) -> {
			TooltipHandler.onTooltip(e.getGraphics(), e.getItemStack(), e.getX(), e.getY());
		});
		NeoForge.EVENT_BUS.addListener((RecipesReceivedEvent e) -> {
			ClientRecipes.INSTANCE.receivedRecipes(e.getRecipeMap().values());
		});
		modBus.addListener((FMLClientSetupEvent e) -> {
			ClientBookRegistry.INSTANCE.init();
			PersistentData.setup();
		});
		modBus.addListener((AddClientReloadListenersEvent e) -> {
			e.addListener(BookContentResourceListenerLoader.ID, BookContentResourceListenerLoader.INSTANCE);
			e.addListener(BookReloadHook.ID, BookReloadHook.INSTANCE);
		});
		modBus.addListener((RegisterRangeSelectItemModelPropertyEvent e) -> {
			e.register(BookCompletionModelProperty.ID, BookCompletionModelProperty.MAP_CODEC);
		});
		modBus.addListener((RegisterItemModelsEvent e) -> {
			e.register(BookModel.Unbaked.ID, BookModel.Unbaked.MAP_CODEC);
		});
		modBus.addListener((RegisterGuiLayersEvent e) -> {
			e.registerAbove(VanillaGuiLayers.CROSSHAIR, BookOverlayHud.ID, BookOverlayHud::render);
			e.registerBelow(VanillaGuiLayers.BOSS_OVERLAY, MultiblockProgressHud.ID, MultiblockProgressHud::render);
		});
		modBus.addListener((RegisterPictureInPictureRenderersEvent e) -> {
			e.register(MultiblockPiPRenderState.class, bufferSource -> new MultiblockPiPRenderer(bufferSource, Minecraft.getInstance(), Minecraft.getInstance().gameRenderer.getSubmitNodeStorage()));
		});
	}

	private static @NotNull Identifier modLoc(String name) {
		return Identifier.fromNamespaceAndPath(PatchouliAPI.MOD_ID, name);
	}
}
