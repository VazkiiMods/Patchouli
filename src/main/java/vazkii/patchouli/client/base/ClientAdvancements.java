package vazkii.patchouli.client.base;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.util.Identifier;

import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.mixin.MixinClientAdvancementManagerAccessor;
import vazkii.patchouli.common.book.Book;

import javax.annotation.Nonnull;

import java.util.Map;

public class ClientAdvancements {
	private static boolean gotFirstAdvPacket = false;

	/* Hooked at the end of ClientAdvancementManager.read, when the advancement packet arrives clientside
	The initial book load is done here when the first advancement packet arrives.
	Doing it anytime before that leads to excessive toast spam because the book believes everything to be locked,
	and then the first advancement packet unlocks everything.
	*/
	public static void onClientPacket() {
		if (!gotFirstAdvPacket) {
			ClientBookRegistry.INSTANCE.reload();
			gotFirstAdvPacket = true;
		} else {
			ClientBookRegistry.INSTANCE.reloadLocks(false);
		}
	}

	public static boolean hasDone(String advancement) {
		Identifier id = Identifier.tryParse(advancement);
		if (id != null) {
			ClientPlayNetworkHandler conn = MinecraftClient.getInstance().getNetworkHandler();
			if (conn != null) {
				ClientAdvancementManager cm = conn.getAdvancementHandler();
				Advancement adv = cm.getManager().get(id);
				if (adv != null) {
					Map<Advancement, AdvancementProgress> progressMap = ((MixinClientAdvancementManagerAccessor) cm).getAdvancementProgresses();
					AdvancementProgress progress = progressMap.get(adv);
					return progress != null && progress.isDone();
				}
			}
		}
		return false;
	}

	public static void playerLogout() {
		gotFirstAdvPacket = false;
	}

	public static void sendBookToast(Book book) {
		ToastManager gui = MinecraftClient.getInstance().getToastManager();
		if (gui.getToast(LexiconToast.class, book) == null) {
			gui.add(new LexiconToast(book));
		}
	}

	public static class LexiconToast implements Toast {
		private final Book book;

		public LexiconToast(Book book) {
			this.book = book;
		}

		@Nonnull
		@Override
		public Book getType() {
			return book;
		}

		@Nonnull
		@Override
		public Visibility draw(ToastManager toastGui, long delta) {
			MinecraftClient mc = MinecraftClient.getInstance();
			mc.getTextureManager().bindTexture(TOASTS_TEX);
			RenderSystem.color3f(1.0F, 1.0F, 1.0F);
			toastGui.blit(0, 0, 0, 32, 160, 32);

			toastGui.getGame().textRenderer.draw(I18n.translate(book.name), 30, 7, -11534256);
			toastGui.getGame().textRenderer.draw(I18n.translate("patchouli.gui.lexicon.toast.info"), 30, 17, -16777216);

			toastGui.getGame().getItemRenderer().renderGuiItem(null, book.getBookItem(), 8, 8);

			return delta >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
		}

	}

}
