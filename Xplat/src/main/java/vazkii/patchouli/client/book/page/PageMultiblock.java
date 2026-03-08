package vazkii.patchouli.client.book.page;

import com.google.gson.annotations.SerializedName;

import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import org.joml.Vector3f;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.base.PersistentData.Bookmark;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookEye;
import vazkii.patchouli.client.book.page.abstr.PageWithText;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.common.multiblock.AbstractMultiblock;
import vazkii.patchouli.common.multiblock.MultiblockRegistry;
import vazkii.patchouli.common.multiblock.SerializedMultiblock;
import vazkii.patchouli.xplat.IClientXplatAbstractions;

public class PageMultiblock extends PageWithText {

	String name = "";
	@SerializedName("multiblock_id") Identifier multiblockId;

	@SerializedName("multiblock") SerializedMultiblock serializedMultiblock;

	@SerializedName("enable_visualize") boolean showVisualizeButton = true;

	private transient AbstractMultiblock multiblockObj;
	private transient Button visualizeButton;

	@Override
	public void build(Level level, BookEntry entry, BookContentsBuilder builder, int pageNum) {
		super.build(level, entry, builder, pageNum);
		if (multiblockId != null) {
			IMultiblock mb = MultiblockRegistry.MULTIBLOCKS.get(multiblockId);

			if (mb instanceof AbstractMultiblock) {
				multiblockObj = (AbstractMultiblock) mb;
			}
		}

		if (multiblockObj == null && serializedMultiblock != null) {
			multiblockObj = serializedMultiblock.toMultiblock();
		}

		if (multiblockObj == null) {
			throw new IllegalArgumentException("No multiblock located for " + multiblockId);
		}
	}

	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);

		if (showVisualizeButton) {
			addButton(visualizeButton = new GuiButtonBookEye(parent, 12, 97, this::handleButtonVisualize));
		}
	}

	@Override
	public int getTextHeight() {
		return 115;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float pticks) {
		int x = GuiBook.PAGE_WIDTH / 2 - 53;
		int y = 7;
		GuiBook.drawFromTexture(graphics, book, x, y, 405, 149, 106, 106);

		parent.drawCenteredStringNoShadow(graphics, i18n(name), GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);

		if (multiblockObj != null) {

			float time = parent.ticksInBook * 0.5F;
			if (!mc.hasShiftDown()) {
				time += ClientTicker.partialTicks;
			}
			IClientXplatAbstractions.INSTANCE.submitMultiblockPiP(graphics, multiblockObj, 1, new Vector3f(), Axis.YP.rotationDegrees(time).mul(Axis.YP.rotationDegrees(45)), 0, 0,  106, 106);
		}

		super.render(graphics, mouseX, mouseY, pticks);
	}

	public void handleButtonVisualize(Button button) {
		var entryKey = parent.getEntry().getId();
		Bookmark bookmark = new Bookmark(entryKey, pageNum / 2);
		MultiblockVisualizationHandler.INSTANCE.setMultiblock(multiblockObj, i18nText(name), bookmark, true);
		parent.addBookmarkButtons();

		if (!PersistentData.data.clickedVisualize) {
			PersistentData.data.clickedVisualize = true;
			PersistentData.save();
		}
	}
}
