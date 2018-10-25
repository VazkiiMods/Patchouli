package vazkii.patchouli.client.base;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.handler.AdvancementSyncHandler;

public class ClientAdvancements {

	static List<String> doneAdvancements;

	public static void setDoneAdvancements(String[] done, boolean showToast) {
		showToast &= !PatchouliConfig.disableAdvancementLocking;
		int doneCount = getCompleteAdvancements(showToast);

		doneAdvancements = Arrays.asList(done);
		updateLockStatus();

		int doneCount2 = getCompleteAdvancements(showToast);
		if(doneCount2 > doneCount)
			Minecraft.getMinecraft().getToastGui().add(new LexiconToast());
	}

	private static int getCompleteAdvancements(boolean toast) {
		if(!toast)
			return 0;
		
		int total = (int) BookRegistry.INSTANCE.books.values().stream()
				.filter(Book::usesAdvancements)
				.map(b ->
					b.contents.entries.values().stream()
					.filter((e) -> !e.isLocked())
					.count()
				)
				.collect(Collectors.summingInt((l) -> (int) (long) l));
		
		return total;
	}
	
	public static void updateLockStatus() {
		ClientBookRegistry.INSTANCE.reloadLocks();
	}

	public static void resetIfNeeded() {
		if(doneAdvancements != null && doneAdvancements.size() > 0)
			setDoneAdvancements(new String[0], false);
	}

	public static boolean hasDone(String advancement) {
		return doneAdvancements != null && doneAdvancements.contains(advancement);
	}

	@SubscribeEvent
	public static void onTick(ClientTickEvent event) {
		if(event.phase == Phase.END && Minecraft.getMinecraft().player == null)
			resetIfNeeded();
	}

	public static class LexiconToast implements IToast {

		@Override
		public Visibility draw(GuiToast toastGui, long delta) {
			Minecraft mc = Minecraft.getMinecraft();
			mc.getTextureManager().bindTexture(TEXTURE_TOASTS);
			GlStateManager.color(1.0F, 1.0F, 1.0F);
			toastGui.drawTexturedModalRect(0, 0, 0, 32, 160, 32);
			
			// TODO support multiple book types
            toastGui.getMinecraft().fontRenderer.drawString(I18n.format("patchouli.gui.lexicon.toast"), 30, 7, -11534256);
            toastGui.getMinecraft().fontRenderer.drawString(I18n.format("patchouli.gui.lexicon.toast.info"), 30, 17, -16777216);

            RenderHelper.enableGUIStandardItemLighting();
            toastGui.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(null, new ItemStack(Items.BOOK), 8, 8); 
			
			return delta >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
		}

	}

}
