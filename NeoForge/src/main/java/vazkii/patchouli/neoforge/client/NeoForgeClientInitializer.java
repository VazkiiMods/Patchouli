package vazkii.patchouli.neoforge.client;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.client.event.RegisterItemModelsEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
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
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.PatchouliItems;

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
                    .map(b -> b.model)
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
        event.register(
                ResourceLocation.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "item/guide_book"),
                BookModel.Unbaked.MAP_CODEC
        );
    }



    @SubscribeEvent
    public static void registerItemDecorations(RegisterItemDecorationsEvent event) {
        // resolve the real Item whether BOOK is DeferredItem or already Item
        Item item;
        try {
            // if BOOK is DeferredItem, this returns the Item; if not, this will throw or be invalid
            // so we use reflection-safe detection
            Object bookObj = PatchouliItems.BOOK;
            if (bookObj instanceof net.neoforged.neoforge.registries.DeferredItem<?> deferred) {
                item = (Item) deferred.get();
            } else if (bookObj instanceof Item it) {
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
    public static void onBakingCompleted(ModelEvent.BakingCompleted event) {
        var modelManager = event.getModelManager();
        for (ResourceLocation bookModel : getBookModels()) {
            StandaloneModelKey<?> key = new StandaloneModelKey<>(bookModel);
            modelManager.getStandaloneModel(key); // ensures model is loaded
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
