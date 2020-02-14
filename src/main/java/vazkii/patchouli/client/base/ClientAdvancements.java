package vazkii.patchouli.client.base;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

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
					MinecraftClient.getInstance().getToastManager().add(new LexiconToast(b));
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

	public static void init() {
		ClientTickCallback.EVENT.register(mc -> {
			if(mc.player == null)
				resetIfNeeded();
		});
	}

	public static class LexiconToast implements Toast {

		final Book book;

		public LexiconToast(Book book) {
			this.book = book;
		}

		@Override
		public Visibility draw(ToastManager toastGui, long delta) {
			toastGui.getGame().getTextureManager().bindTexture(TOASTS_TEX);
			RenderSystem.color3f(1.0F, 1.0F, 1.0F);
			toastGui.blit(0, 0, 0, 32, 160, 32);

			toastGui.getGame().textRenderer.draw(I18n.translate(book.name), 30, 7, -11534256);
			toastGui.getGame().textRenderer.draw(I18n.translate("patchouli.gui.lexicon.toast.info"), 30, 17, -16777216);

			toastGui.getGame().getItemRenderer().renderGuiItem(null, book.getBookItem(), 8, 8);

			return delta >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
		}

	}

}
