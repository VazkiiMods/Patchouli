package vazkii.patchouli.client.base;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BookModel implements BakedModel {
	private final BakedModel original;
	private final ModelOverrideList itemHandler;

	public BookModel(BakedModel original, ModelLoader loader) {
		this.original = original;
		JsonUnbakedModel missing = (JsonUnbakedModel) loader.getOrLoadModel(ModelLoader.MISSING_ID);

		this.itemHandler = new ModelOverrideList(loader, missing, id -> missing, Collections.emptyList()) {
			@Override
			public BakedModel apply(@Nonnull BakedModel original, @Nonnull ItemStack stack,
					@Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
				Book book = ItemModBook.getBook(stack);
				if (book != null) {
					ModelIdentifier modelPath = new ModelIdentifier(book.model, "inventory");
					return MinecraftClient.getInstance().getBakedModelManager().getModel(modelPath);
				}
				return original;
			}
		};
	}

	@Nonnull
	@Override
	public ModelOverrideList getOverrides() {
		return itemHandler;
	}

	@Nonnull
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
		return original.getQuads(state, side, rand);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return original.useAmbientOcclusion();
	}

	@Override
	public boolean hasDepth() {
		return original.hasDepth();
	}

	@Override
	public boolean isSideLit() {
		return original.isSideLit();
	}

	@Override
	public boolean isBuiltin() {
		return original.isBuiltin();
	}

	@Nonnull
	@Override
	public Sprite getSprite() {
		return original.getSprite();
	}

	@Override
	public ModelTransformation getTransformation() {
		return original.getTransformation();
	}
}
