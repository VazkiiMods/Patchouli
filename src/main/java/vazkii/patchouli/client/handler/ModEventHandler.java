package vazkii.patchouli.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.base.Patchouli;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Patchouli.MOD_ID, value = Dist.CLIENT)
public class ModEventHandler {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent evt) {
        ClientBookRegistry.INSTANCE.init();
        PersistentData.setup();
        BookRightClickHandler.init();
        MultiblockVisualizationHandler.init();

        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener((ResourceManagerReloadListener)(manager) -> {
            if (Minecraft.getInstance().level != null) {
                Patchouli.LOGGER.info("Reloading resource pack-based books, world is nonnull");
                ClientBookRegistry.INSTANCE.reload(true);
            } else {
                Patchouli.LOGGER.info("Not reloading resource pack-based books as client world is missing");
            }
        });
        ClientBookRegistry.INSTANCE.reload(false);
    }
}
