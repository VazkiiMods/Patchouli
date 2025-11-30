package vazkii.patchouli.client.base;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookModel implements ItemModel {
	private final ItemModel base;
	private final Map<ResourceLocation, ItemModel> bookModels;

	public BookModel(ItemModel base, Map<ResourceLocation, ItemModel> bookModels) {
		this.base = base;
		this.bookModels = bookModels;
	}

	@Override
	public void update(ItemStackRenderState renderState, ItemStack stack, ItemModelResolver itemModelResolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
		renderState.appendModelIdentityElement(this);
		Book book = ItemModBook.getBook(stack);
		ItemModel model;
		if (book == null) {
			model = base;
		} else {
			model = bookModels.getOrDefault(book.model, base);
		}
		model.update(renderState, stack, itemModelResolver, displayContext, level, owner, seed);
	}

	public record Unbaked(BlockModelWrapper.Unbaked base) implements ItemModel.Unbaked {
		public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "book");
		public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
				BlockModelWrapper.Unbaked.MAP_CODEC.forGetter(Unbaked::base)
		).apply(inst, Unbaked::new));

		@Override
		public MapCodec<? extends ItemModel.Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public ItemModel bake(BakingContext context) {
			Map<ResourceLocation, BlockModelWrapper.Unbaked> models = new HashMap<>();
			for (Book book : BookRegistry.INSTANCE.books.values()) {
				ResourceLocation modelLoc = book.model;
				models.computeIfAbsent(modelLoc, loc -> new BlockModelWrapper.Unbaked(loc, List.of()));
			}
			return new BookModel(base().bake(context), Maps.transformValues(models, m -> m.bake(context)));
		}

		@Override
		public void resolveDependencies(Resolver resolver) {
			base().resolveDependencies(resolver);
			for (Book book : BookRegistry.INSTANCE.books.values()) {
				PatchouliAPI.LOGGER.info("Adding model {}", book.model);
				resolver.markDependency(book.model);
			}
		}
	}
}
