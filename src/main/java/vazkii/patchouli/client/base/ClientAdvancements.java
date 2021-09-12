package vazkii.patchouli.client.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import vazkii.patchouli.client.RenderHelper;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.mixin.client.AccessorClientAdvancements;

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
			ClientBookRegistry.INSTANCE.reload(false);
			gotFirstAdvPacket = true;
		} else {
			ClientBookRegistry.INSTANCE.reloadLocks(false);
		}
	}

	public static boolean hasDone(String advancement) {
		ResourceLocation id = ResourceLocation.tryParse(advancement);
		if (id != null) {
			ClientPacketListener conn = Minecraft.getInstance().getConnection();
			if (conn != null) {
				net.minecraft.client.multiplayer.ClientAdvancements cm = conn.getAdvancements();
				Advancement adv = cm.getAdvancements().get(id);
				if (adv != null) {
					Map<Advancement, AdvancementProgress> progressMap = ((AccessorClientAdvancements) cm).getProgress();
					AdvancementProgress progress = progressMap.get(adv);
					return progress != null && progress.isDone();
				}
			}
		}
		return false;
	}

	public static void init() {
		MinecraftForge.EVENT_BUS.addListener(ClientAdvancements::onPlayerLogout);
	}

	private static void onPlayerLogout(ClientPlayerNetworkEvent.LoggedOutEvent evt) {
		gotFirstAdvPacket = false;
	}

	public static void sendBookToast(Book book) {
		ToastComponent gui = Minecraft.getInstance().getToasts();
		if (gui.getToast(LexiconToast.class, book) == null) {
			gui.addToast(new LexiconToast(book));
		}
	}

	public static class LexiconToast implements Toast {
		private final Book book;

		public LexiconToast(Book book) {
			this.book = book;
		}

		@Nonnull
		@Override
		public Book getToken() {
			return book;
		}

		@Nonnull
		@Override
		public Visibility render(PoseStack ms, ToastComponent toastGui, long delta) {
			RenderSystem.setShaderTexture(0, TEXTURE);

			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			toastGui.blit(ms, 0, 0, 0, 32, 160, 32);

			toastGui.getMinecraft().font.draw(ms, I18n.get(book.name), 30, 7, -11534256);
			toastGui.getMinecraft().font.draw(ms, I18n.get("patchouli.gui.lexicon.toast.info"), 30, 17, -16777216);

			RenderHelper.renderItemStackInGui(ms, book.getBookItem(), 8, 8);

			return delta >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
		}

	}

}
