package vazkii.patchouli.client.base;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import org.joml.Matrix4fc;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import org.jetbrains.annotations.Nullable;

public class BookModel implements ItemModel {
	private final ItemModel base;

	public BookModel(ItemModel base) {
		this.base = base;
	}

	@Override
	public void update(ItemStackRenderState renderState, ItemStack stack, ItemModelResolver itemModelResolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
		renderState.appendModelIdentityElement(this);
		Book book = ItemModBook.getBook(stack);
		ItemModel model;
		if (book == null) {
			model = base;
		} else {
			model = Minecraft.getInstance().getModelManager().getItemModel(book.model);
		}
		model.update(renderState, stack, itemModelResolver, displayContext, level, owner, seed);
	}

	public record Unbaked(ItemModel.Unbaked base) implements ItemModel.Unbaked {
		public static final Identifier ID = Identifier.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "book");
		public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
				ItemModels.CODEC.fieldOf("base").forGetter(Unbaked::base)
		).apply(inst, Unbaked::new));

		@Override
		public MapCodec<? extends ItemModel.Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public ItemModel bake(BakingContext bakingContext, Matrix4fc matrix4fc) {
			return new BookModel(base().bake(bakingContext, matrix4fc));
		}

		@Override
		public void resolveDependencies(Resolver resolver) {
			base().resolveDependencies(resolver);
		}
	}
}
