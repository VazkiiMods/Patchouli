package vazkii.patchouli.client.base;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.mixin.client.AccessorClientAdvancements;

import org.jetbrains.annotations.NotNull;

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
			ClientPacketListener conn = Minecraft.getInstance().getConnection();
			if (conn != null) {
				net.minecraft.client.multiplayer.ClientAdvancements cm = conn.getAdvancements();
				AdvancementHolder adv = cm.get(id);
				if (adv != null) {
					Map<AdvancementHolder, AdvancementProgress> progressMap = ((AccessorClientAdvancements) cm).getProgress();
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
		ToastManager gui = Minecraft.getInstance().getToastManager();
		if (gui.getToast(LexiconToast.class, book) == null) {
			gui.addToast(new LexiconToast(book));
		}
	}

	public static class LexiconToast implements Toast {
		private static final Identifier BACKGROUND_SPRITE = Identifier.withDefaultNamespace("toast/advancement");
		private final Book book;
		private Visibility wantedVisibility;

		public LexiconToast(Book book) {
			this.book = book;
		}

		@NotNull
		@Override
		public Book getToken() {
			return book;
		}

		@Override
		public void render(GuiGraphics graphics, Font font, long visibilityTime) {
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, 0, 0, width(), height());

			graphics.drawString(font, Component.translatable(book.name), 30, 7, 0xfff000f0, false);
			graphics.drawString(font, Component.translatable("patchouli.gui.lexicon.toast.info"), 30, 17, 0xffffffff, false);

			graphics.renderItem(book.getBookItem(), 8, 8);
			graphics.renderItemDecorations(font, book.getBookItem(), 8, 8);
		}

		@Override
		public Visibility getWantedVisibility() {
			return wantedVisibility;
		}

		@Override
		public void update(ToastManager toastManager, long visibilityTime) {
			this.wantedVisibility = visibilityTime >= 5000.0 * toastManager.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
		}
	}

}
