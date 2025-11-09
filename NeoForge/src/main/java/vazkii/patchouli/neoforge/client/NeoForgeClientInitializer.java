package vazkii.patchouli.neoforge.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.base.BookModel;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.handler.BookRightClickHandler;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.client.handler.TooltipHandler;

import vazkii.patchouli.common.item.PatchouliItems;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterItemModelsEvent;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelBaker;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.client.base.ModelLog;

@EventBusSubscriber(modid = PatchouliAPI.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClientInitializer {

    private static boolean booksLoaded = false;
    private static final Lock BOOK_LOAD_LOCK = new ReentrantLock();
    private static final Condition BOOK_LOAD_CONDITION = BOOK_LOAD_LOCK.newCondition();

    public static void signalBooksLoaded() {
        BOOK_LOAD_LOCK.lock();
        booksLoaded = true;
        BOOK_LOAD_CONDITION.signalAll();
        BOOK_LOAD_LOCK.unlock();
    }

    private static List<ResourceLocation> getBookModels() {
        BOOK_LOAD_LOCK.lock();
        try {
            while (!booksLoaded) {
                BOOK_LOAD_CONDITION.awaitUninterruptibly();
            }
            return BookRegistry.INSTANCE.books.values().stream()
                    .map(b -> {
                        if (b.model.getPath().startsWith("item/")) {
                            return b.model;
                        } else {
                            return ResourceLocation.fromNamespaceAndPath(b.model.getNamespace(), "item/" + b.model.getPath());
                        }
                    })
                    .toList();
        } finally {
            BOOK_LOAD_LOCK.unlock();
        }
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiLayersEvent evt) {
        evt.registerAbove(VanillaGuiLayers.CROSSHAIR,
                ResourceLocation.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "book_overlay"),
                BookRightClickHandler::onRenderHUD
        );
        evt.registerBelow(VanillaGuiLayers.BOSS_OVERLAY,
                ResourceLocation.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "multiblock_progress"),
                MultiblockVisualizationHandler::onRenderHUD
        );
    }

    @SubscribeEvent
    public static void registerModels(RegisterItemModelsEvent event) {
        ResourceLocation loaderId = ResourceLocation.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "book");
        String msg = ">>> PATCHOULI_MODEL >>> Registering book item model codec for loader " + loaderId;
        System.out.println(msg);
        ModelLog.log(msg);
        event.register(loaderId, BookModel.Unbaked.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerAdditional(ModelEvent.RegisterStandalone event) {
        for (final ResourceLocation bookModel : getBookModels()) {
            PatchouliAPI.LOGGER.info("Registering book model: " + bookModel);
            String msg = ">>> PATCHOULI_MODEL >>> Registering book model: " + bookModel;
            System.out.println(msg);
            ModelLog.log(msg);
            event.register(new StandaloneModelKey<>(bookModel), StandaloneModelBaker.quadCollection());
        }
    }


    @SubscribeEvent
    public static void registerItemDecorations(RegisterItemDecorationsEvent event) {
         Item item;
        try {
            Object bookObj = PatchouliItems.BOOK;
            if (bookObj instanceof Item it) {
                item = it;
            } else {
                throw new IllegalStateException("Unsupported PatchouliItems.BOOK type: " + bookObj.getClass());
            }

            event.register(item, new BookCompletionDecoration());
        } catch (Throwable t) {
            PatchouliAPI.LOGGER.error("Failed to register book completion property", t);
        }
    }

    
    

    @SubscribeEvent
    public static void onInitializeClient(FMLClientSetupEvent evt) {
        evt.enqueueWork(() -> {
        ClientBookRegistry.INSTANCE.init();
        PersistentData.setup();
    });


        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post e) ->
                ClientTicker.endClientTick(Minecraft.getInstance())
        );

        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Pre e) -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.screen != null || mc.isPaused()) {
                return;
            }
            if (!MultiblockVisualizationHandler.canPickGhost()) {
                return;
            }

            KeyMapping pickKey = mc.options.keyPickItem;
            if (pickKey == null) {
                return;
            }

            while (pickKey.consumeClick()) {
                MultiblockVisualizationHandler.handleMiddleClick(mc.player);
            }
        });

        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock e) ->
                BookRightClickHandler.onRightClick(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec())
        );

        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock e) -> {
            InteractionResult result = MultiblockVisualizationHandler.onPlayerInteract(
                    e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec()
            );
            if (result.consumesAction()) {
                e.setCanceled(true);
                e.setCancellationResult(result);
            }
        });

        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post e) ->
                MultiblockVisualizationHandler.onClientTick(Minecraft.getInstance())
        );

        NeoForge.EVENT_BUS.addListener((RenderFrameEvent.Pre e) ->
                ClientTicker.renderTickStart(e.getPartialTick().getGameTimeDeltaPartialTick(false))
        );

        NeoForge.EVENT_BUS.addListener((RenderFrameEvent.Post e) ->
                ClientTicker.renderTickEnd()
        );

        NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut e) ->
                ClientAdvancements.playerLogout()
        );

        NeoForge.EVENT_BUS.addListener((RenderTooltipEvent.Pre e) ->
                TooltipHandler.onTooltip(e.getGraphics(), e.getItemStack(), e.getX(), e.getY())
        );
    }
}
