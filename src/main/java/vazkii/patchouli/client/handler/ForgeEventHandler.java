package vazkii.patchouli.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import vazkii.patchouli.client.base.BookModel;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.item.PatchouliItems;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ForgeEventHandler {
	@SubscribeEvent
	public static void onRenderTick(TickEvent.RenderTickEvent evt) {
		switch (evt.phase) {
		case START -> ClientTicker.renderTickStart(evt.renderTickTime);
		case END -> ClientTicker.renderTickEnd();
		}
	}

	@SubscribeEvent
	public static void onPlayerLogout(ClientPlayerNetworkEvent.LoggedOutEvent evt) {
		ClientAdvancements.playerLogout();
	}

	@SubscribeEvent
	public static void onModelBake(ModelBakeEvent evt) {
		ModelResourceLocation key = new ModelResourceLocation(PatchouliItems.BOOK.getId(), "inventory");
		BakedModel oldModel = evt.getModelRegistry().get(key);
		if (oldModel != null) {
			evt.getModelRegistry().put(key, new BookModel(oldModel, evt.getModelLoader()));
		}
	}

	@SubscribeEvent
	public static void onWorldRenderLast(RenderWorldLastEvent evt) {
		if (MultiblockVisualizationHandler.hasMultiblock && MultiblockVisualizationHandler.getMultiblock() != null) {
			MultiblockVisualizationHandler.renderMultiblock(Minecraft.getInstance().level, evt.getMatrixStack());
		}
	}

	@SubscribeEvent
	public static void onRenderTooltip(RenderTooltipEvent.Pre evt) {
		TooltipHandler.onTooltip(evt.getMatrixStack(), evt.getStack(), evt.getX(), evt.getY());
	}

	@SubscribeEvent
	public static void onBlockRightClicked(PlayerInteractEvent.RightClickBlock evt) {
		BookRightClickHandler.onRightClick(evt.getWorld(), evt.getPlayer());
		InteractionResult result = MultiblockVisualizationHandler.onPlayerInteract(evt.getPlayer(), evt.getWorld(), evt.getHand(), evt.getHitVec());
		if (result.consumesAction()) {
			evt.setCanceled(true);
			evt.setCancellationResult(result);
		}
	}

	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent e) {
		BookRegistry.INSTANCE.books.values().stream()
				.map(b -> new ModelResourceLocation(b.model, "inventory"))
				.forEach(ModelLoader::addSpecialModel);

		ItemPropertyFunction prop = (stack, world, entity, seed) -> ItemModBook.getCompletion(stack);
		ItemProperties.register(PatchouliItems.BOOK.get(), new ResourceLocation(Patchouli.MOD_ID, "completion"), prop);
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent e) {
		if (e.phase != TickEvent.Phase.END) {
			return;
		}
		ClientTicker.tick();
		MultiblockVisualizationHandler.onClientTick(Minecraft.getInstance());
	}
}
