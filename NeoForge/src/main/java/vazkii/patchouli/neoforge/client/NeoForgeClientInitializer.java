package vazkii.patchouli.neoforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionResult;
import net.neoforged.api.distmarker.Dist;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterItemModelsEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.handler.BookRightClickHandler;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.client.handler.TooltipHandler;
import vazkii.patchouli.common.book.BookRegistry;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@EventBusSubscriber(modid = PatchouliAPI.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClientInitializer {
	private static final Lock BOOK_LOAD_LOCK = new ReentrantLock();
	private static final Condition BOOK_LOAD_CONDITION = BOOK_LOAD_LOCK.newCondition();
	private static boolean booksLoaded = false;

	public static void signalBooksLoaded() {
		BOOK_LOAD_LOCK.lock();
		try {
			booksLoaded = true;
			BOOK_LOAD_CONDITION.signalAll();
		} finally {
			BOOK_LOAD_LOCK.unlock();
		}
	}

	@SuppressWarnings("unused")
	private static List<ResourceLocation> getBookModels() {
		BOOK_LOAD_LOCK.lock();
		try {
			while (!booksLoaded) {
				BOOK_LOAD_CONDITION.awaitUninterruptibly();
			}
			return BookRegistry.INSTANCE.books.values().stream().map(b -> b.model).toList();
		} finally {
			BOOK_LOAD_LOCK.unlock();
		}
	}



	@SubscribeEvent
	public static void registerOverlays(RegisterGuiLayersEvent evt) {
		evt.registerAbove(VanillaGuiLayers.CROSSHAIR, ResourceLocation.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "book_overlay"),
				BookRightClickHandler::onRenderHUD
		);
		evt.registerBelow(VanillaGuiLayers.BOSS_OVERLAY, ResourceLocation.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "multiblock_progress"),
				MultiblockVisualizationHandler::onRenderHUD
		);
	}

	@SubscribeEvent
	public static void onInitializeClient(FMLClientSetupEvent evt) {
		ClientBookRegistry.INSTANCE.init();
		PersistentData.setup();

		NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post e) -> ClientTicker.endClientTick(Minecraft.getInstance()));
		NeoForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> BookRightClickHandler.onRightClick(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec()));
		NeoForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> {
			InteractionResult result = MultiblockVisualizationHandler.onPlayerInteract(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec());
			if (result.consumesAction()) {
				e.setCanceled(true);
				e.setCancellationResult(result);
			}
		});
		NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post e) -> MultiblockVisualizationHandler.onClientTick(Minecraft.getInstance()));

		NeoForge.EVENT_BUS.addListener((RenderFrameEvent.Pre e) -> ClientTicker.renderTickStart(e.getPartialTick().getGameTimeDeltaPartialTick(false)));
		NeoForge.EVENT_BUS.addListener((RenderFrameEvent.Post e) -> ClientTicker.renderTickEnd());

		NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut e) -> ClientAdvancements.playerLogout());

		NeoForge.EVENT_BUS.addListener((RenderTooltipEvent.Pre e) -> TooltipHandler.onTooltip(e.getGraphics(), e.getItemStack(), e.getX(), e.getY()));
	}

}

