package vazkii.patchouli.client.base;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.platform.GlStateManager;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

@EventBusSubscriber(Dist.CLIENT)
public class ClientAdvancements {

	static List<String> doneAdvancements;

	public static void setDoneAdvancements(String[] done, boolean showToast, boolean reset) {
		Preconditions.checkState(BookRegistry.INSTANCE.isLoaded(), "Advancement packet when books aren't loaded");
		showToast &= !PatchouliConfig.disableAdvancementLocking.get();

		doneAdvancements = Arrays.asList(done);
		ClientBookRegistry.INSTANCE.reloadLocks(reset);

		if(showToast)
			BookRegistry.INSTANCE.books.values().forEach(b -> {
				if(b.popUpdated() && b.showToasts) {
					Minecraft.getInstance().getToastGui().add(new LexiconToast(b));
				}
			});
	}

	public static void resetIfNeeded() {
		if(doneAdvancements != null && doneAdvancements.size() > 0)
			setDoneAdvancements(new String[0], false, true);
	}

	public static boolean hasDone(String advancement) {
		return doneAdvancements != null && doneAdvancements.contains(advancement);
	}

	@SubscribeEvent
	public static void onTick(TickEvent.ClientTickEvent event) {
		if(event.phase == TickEvent.Phase.END && Minecraft.getInstance().player == null)
			resetIfNeeded();
	}

	public static class LexiconToast implements IToast {

		final Book book;

		public LexiconToast(Book book) {
			this.book = book;
		}

		@Override
		public Visibility draw(ToastGui toastGui, long delta) {
			Minecraft mc = Minecraft.getInstance();
			mc.getTextureManager().bindTexture(TEXTURE_TOASTS);
			RenderSystem.color3f(1.0F, 1.0F, 1.0F);
			toastGui.blit(0, 0, 0, 32, 160, 32);

			toastGui.getMinecraft().fontRenderer.drawString(I18n.format(book.name), 30, 7, -11534256);
			toastGui.getMinecraft().fontRenderer.drawString(I18n.format("patchouli.gui.lexicon.toast.info"), 30, 17, -16777216);

			toastGui.getMinecraft().getItemRenderer().renderItemAndEffectIntoGUI(null, book.getBookItem(), 8, 8);

			return delta >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
		}

	}

}
