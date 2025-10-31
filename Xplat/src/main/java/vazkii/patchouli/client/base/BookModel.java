package vazkii.patchouli.client.base;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class BookModel implements ItemModel {
	private static final ItemTransforms ITEM_TRANSFORMS = createItemTransforms();

	private static ItemTransforms createItemTransforms() {
		// minimal transforms; copy or adjust as needed
		return new ItemTransforms(
				new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1)),
				new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1)),
				new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0.5f, 0.5f, 0.5f)),
				new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0.5f, 0.5f, 0.5f)),
				new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1f, 1f, 1f)),
				new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1f, 1f, 1f)),
				new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0.5f, 0.5f, 0.5f)),
				new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0.75f, 0.75f, 0.75f))
        );
	}

	private final SpecialRenderer specialRenderer = new SpecialRenderer();
	/* baseModel is the fallback model used when stack has no Book. */
	private final BlockStateModel baseModel;
	/* modelGetter resolves a ResourceLocation -> BlockStateModel for a given book model */
	private final Function<ResourceLocation, BlockStateModel> modelGetter;
	private final Supplier<Vector3f[]> extents;

	public BookModel(BlockStateModel baseModel, Function<ResourceLocation, BlockStateModel> modelGetter) {
		this.baseModel = baseModel;
		this.modelGetter = modelGetter;
		this.extents = Suppliers.memoize(() -> BlockModelWrapper.computeExtents(new ArrayList<>()));
	}

	@Override
	public void update(ItemStackRenderState stackRenderState, ItemStack stack, ItemModelResolver itemModelResolver,
					   ItemDisplayContext displayContext, @Nullable ClientLevel clientLevel,
					   @Nullable LivingEntity livingEntity, int seed) {
		// Choose model based on book stored in stack
		Book book = ItemModBook.getBook(stack);
		BlockStateModel model = book == null ? baseModel : modelGetter.apply(book.model);

		ItemStackRenderState.LayerRenderState renderLayer = stackRenderState.newLayer();
		if (stack.hasFoil()) renderLayer.setFoilType(ItemStackRenderState.FoilType.STANDARD);

		// No tint logic here in patchouli by default. Add if needed.
		int[] tintLayers = renderLayer.prepareTintLayers(0);

		// prepare base properties
		renderLayer.setExtents(extents);
		renderLayer.setUsesBlockLight(true);
		renderLayer.setParticleIcon(model.particleIcon());
		renderLayer.setTransform(ITEM_TRANSFORMS.getTransform(displayContext));
		renderLayer.prepareQuadList().addAll(new ArrayList<>());
		specialRenderer.setModelRenderParameters(tintLayers, new ArrayList<>());

		renderLayer.setupSpecialModel(specialRenderer, specialRenderer.extractArgument(stack));
	}

	public record Unbaked(ResourceLocation base) implements ItemModel.Unbaked {
		public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
				ResourceLocation.CODEC.fieldOf("base").forGetter(Unbaked::base)
		).apply(builder, Unbaked::new));

		@Override
		public MapCodec<? extends ItemModel.Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public ItemModel bake(BakingContext context) {
			ResolvedModel resolved = context.blockModelBaker().getModel(base);
			if (resolved.wrapped() instanceof BlockStateModel blockStateModel) {
				Function<ResourceLocation, BlockStateModel> getter = rl -> {
					ResolvedModel r = context.blockModelBaker().getModel(rl);
					if (r.wrapped() instanceof BlockStateModel bs) {
						// bake orientation neutral; callers may rotate if needed
						return bs;
					}
					throw new IllegalStateException("Expected a BlockStateModel for book model: " + rl);
				};
				return new BookModel(blockStateModel, getter);
			}
			throw new IllegalStateException("Expected a BlockStateModel as base book model for: " + base);
		}

		@Override
		public void resolveDependencies(ResolvableModel.Resolver resolver) {
			resolver.markDependency(base);
		}
	}

	public static class SpecialRenderer implements NoDataSpecialModelRenderer {
		private final Minecraft minecraft = Minecraft.getInstance();
		private int[] tintLayers;
		private List<BakedQuad> baseModel;

		@Override
		public void render(ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int packedOverlay, boolean hasFoil) {
			ItemRenderer.renderItem(
					displayContext,
					poseStack,
					buffer,
					combinedLight,
					packedOverlay,
					tintLayers,
					baseModel,
					Sheets.translucentItemSheet(),
					hasFoil ? ItemStackRenderState.FoilType.STANDARD : ItemStackRenderState.FoilType.NONE
			);
		}

		public void setModelRenderParameters(int[] tintLayers, List<BakedQuad> baseModel) {
			this.tintLayers = tintLayers;
			this.baseModel = baseModel;
		}
	}
}
