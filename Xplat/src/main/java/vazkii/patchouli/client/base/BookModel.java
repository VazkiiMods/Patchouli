package vazkii.patchouli.client.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class BookModel implements BakedModel {
	private final BakedModel original;
	private final ItemOverrides itemHandler;

	public BookModel(BakedModel original, ModelBakery loader) {
		this.original = original;
		BlockModel missing = (BlockModel) loader.getModel(ModelBakery.MISSING_MODEL_LOCATION);

		this.itemHandler = new ItemOverrides(new ModelBaker() {
			// soft implement IForgeModelBaker
			public Function<Material, TextureAtlasSprite> getModelTextureGetter() {
				return null;
			}

			// soft implement IForgeModelBaker
			public BakedModel bake(ResourceLocation location, ModelState state, Function<Material, TextureAtlasSprite> sprites) {
				return null;
			}

			@Override
			public UnbakedModel getModel(ResourceLocation resourceLocation) {
				return null;
			}

			@Nullable
			@Override
			public BakedModel bake(ResourceLocation resourceLocation, ModelState modelState) {
				return null;
			}
		}, missing, Collections.emptyList()) {
			@Override
			public BakedModel resolve(@NotNull BakedModel original, @NotNull ItemStack stack,
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

	@NotNull
	@Override
	public ItemOverrides getOverrides() {
		return itemHandler;
	}

	@NotNull
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand) {
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

	@NotNull
	@Override
	public TextureAtlasSprite getParticleIcon() {
		return original.getParticleIcon();
	}

	@Override
	public ItemTransforms getTransforms() {
		return original.getTransforms();
	}
}
