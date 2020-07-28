package vazkii.patchouli.client.base;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import vazkii.patchouli.client.RenderHelper;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.mixin.client.AccessorClientAdvancementManager;

import javax.annotation.Nonnull;

import java.util.Map;

@EventBusSubscriber(Dist.CLIENT)
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
		ResourceLocation id = ResourceLocation.tryCreate(advancement);
		if (id != null) {
			ClientPlayNetHandler conn = Minecraft.getInstance().getConnection();
			if (conn != null) {
				ClientAdvancementManager cm = conn.getAdvancementManager();
				Advancement adv = cm.getAdvancementList().getAdvancement(id);
				if (adv != null) {
					Map<Advancement, AdvancementProgress> progressMap = ((AccessorClientAdvancementManager) cm).getAdvancementToProgress();
					AdvancementProgress progress = progressMap.get(adv);
					return progress != null && progress.isDone();
				}
			}
		}
		return false;
	}

	@SubscribeEvent
	public static void playerLogout(ClientPlayerNetworkEvent.LoggedOutEvent evt) {
		gotFirstAdvPacket = false;
	}

	public static void sendBookToast(Book book) {
		ToastGui gui = Minecraft.getInstance().getToastGui();
		if (gui.getToast(LexiconToast.class, book) == null) {
			gui.add(new LexiconToast(book));
		}
	}

	public static class LexiconToast implements IToast {
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
		public Visibility func_230444_a_(MatrixStack ms, ToastGui toastGui, long delta) {
			Minecraft mc = Minecraft.getInstance();
			mc.getTextureManager().bindTexture(TEXTURE_TOASTS);
			RenderSystem.color3f(1.0F, 1.0F, 1.0F);
			toastGui.blit(ms, 0, 0, 0, 32, 160, 32);

			toastGui.getMinecraft().fontRenderer.drawString(ms, I18n.format(book.name), 30, 7, -11534256);
			toastGui.getMinecraft().fontRenderer.drawString(ms, I18n.format("patchouli.gui.lexicon.toast.info"), 30, 17, -16777216);

			RenderHelper.renderItemStackInGui(ms, book.getBookItem(), 8, 8);

			return delta >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
		}

	}

}
