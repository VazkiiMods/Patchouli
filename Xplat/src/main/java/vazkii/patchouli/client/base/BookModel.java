package vazkii.patchouli.client.base;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import org.joml.Matrix4fc;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BookModel implements ItemModel {
	private final ItemModel base;
	private final Map<Identifier, ItemModel> bookModels;

	public BookModel(ItemModel base, Map<Identifier, ItemModel> bookModels) {
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
			Map<Identifier, ItemModel.Unbaked> models = new HashMap<>();
			for (Book book : BookRegistry.INSTANCE.books.values()) {
				Identifier modelLoc = book.model;
				models.computeIfAbsent(modelLoc, loc -> new CuboidItemModelWrapper.Unbaked(loc, Optional.empty(), List.of()));
			}
			return new BookModel(base().bake(bakingContext, matrix4fc), Maps.transformValues(models, m -> m.bake(bakingContext, matrix4fc)));
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
