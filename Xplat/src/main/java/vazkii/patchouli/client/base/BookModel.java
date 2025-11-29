package vazkii.patchouli.client.base;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import org.jetbrains.annotations.Nullable;

public class BookModel implements ItemModel {
	private final ItemModel original;

	public BookModel(ItemModel original) {
		this.original = original;
	}

	@Override
	public void update(ItemStackRenderState renderState, ItemStack stack, ItemModelResolver itemModelResolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
		renderState.appendModelIdentityElement(this);
		Book book = ItemModBook.getBook(stack);
		ItemModel model = null;
		if (book != null) {
			model = Minecraft.getInstance().getModelManager().getItemModel(book.model);
		}
		if (model == null) {
			model = original;
		}
		model.update(renderState, stack, itemModelResolver, displayContext, level, owner, seed);
	}

	public record Unbaked(BlockModelWrapper.Unbaked base) implements ItemModel.Unbaked {
		public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
				BlockModelWrapper.Unbaked.MAP_CODEC.forGetter(Unbaked::base)
		).apply(inst, Unbaked::new));

		@Override
		public MapCodec<? extends ItemModel.Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public ItemModel bake(BakingContext context) {
			return new BookModel(base().bake(context));
		}

		@Override
		public void resolveDependencies(Resolver resolver) {
			base().resolveDependencies(resolver);

		}
	}
}
