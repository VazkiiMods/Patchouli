package vazkii.patchouli.client.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BookModel implements BakedModel {
	private final BakedModel original;
	private final ItemOverrides itemHandler;

	public BookModel(BakedModel original, ModelBakery loader) {
		this.original = original;
		UnbakedModel missing = loader.getModel(ModelBakery.MISSING_MODEL_LOCATION);

		this.itemHandler = new ItemOverrides(loader, missing, id -> missing, loader.getSpriteMap()::getSprite, Collections.emptyList()) {
			@Override
			public BakedModel resolve(@Nonnull BakedModel original, @Nonnull ItemStack stack,
					@Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
				Book book = ItemModBook.getBook(stack);
				if (book != null) {
					ModelResourceLocation modelPath = new ModelResourceLocation(book.model, "inventory");
					return Minecraft.getInstance().getModelManager().getModel(modelPath);
				}
				return original;
			}
		};
	}

	@Nonnull
	@Override
	public ItemOverrides getOverrides() {
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
	public boolean isGui3d() {
		return original.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return original.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return original.isCustomRenderer();
	}

	@Nonnull
	@Override
	public TextureAtlasSprite getParticleIcon() {
		return original.getParticleIcon();
	}

	@Override
	public ItemTransforms getTransforms() {
		return original.getTransforms();
	}
}
