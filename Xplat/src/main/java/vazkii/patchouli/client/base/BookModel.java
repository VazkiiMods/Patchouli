package vazkii.patchouli.client.base;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public record BookModel(List<BakedQuad> quads, ModelRenderProperties properties, RenderType type) implements ItemModel {


	@Override
	public void update(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
		// Create a new layer to render the model in
		ItemStackRenderState.LayerRenderState layerState = state.newLayer();
		if (stack.hasFoil()) {
			layerState.setFoilType(ItemStackRenderState.FoilType.STANDARD);
		}
		layerState.setExtents(
				()-> BlockModelWrapper.computeExtents(this.quads));
		layerState.setRenderType(this.type);
		this.properties.applyToLayer(layerState, displayContext);
		layerState.prepareQuadList().addAll(this.quads);
	}

	public record Unbaked(ResourceLocation model, RenderType types) implements ItemModel.Unbaked {
		// Create a render type map for the codec
		private static final BiMap<String, RenderType> RENDER_TYPES = Util.make(HashBiMap.create(), map -> {
			map.put("translucent_item", Sheets.translucentItemSheet());
			map.put("cutout_block", Sheets.cutoutBlockSheet());
		});
		private static final Codec<RenderType> RENDER_TYPE_CODEC = ExtraCodecs.idResolverCodec(Codec.STRING, RENDER_TYPES::get, RENDER_TYPES.inverse()::get);

		// The map codec to register
		public static final MapCodec<BookModel.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
				instance.group(
								ResourceLocation.CODEC.fieldOf("model").forGetter(BookModel.Unbaked::model),
								RENDER_TYPE_CODEC.fieldOf("render_type").forGetter(BookModel.Unbaked::types)
						)
						.apply(instance, BookModel.Unbaked::new)
		);

		@Override
		public void resolveDependencies(ResolvableModel.Resolver resolver) {
			// Mark all dependencies for this item model
			resolver.markDependency(this.model);
		}

		@Override
		public ItemModel bake(ItemModel.BakingContext context) {
			// Get the baked quads and return
			ModelBaker baker = context.blockModelBaker();
			ResolvedModel resolvedModel = baker.getModel(this.model);
			TextureSlots slots = resolvedModel.getTopTextureSlots();

			return new BookModel(
					resolvedModel.bakeTopGeometry(slots, baker, BlockModelRotation.X0_Y0).getAll(),
					ModelRenderProperties.fromResolvedModel(baker, resolvedModel, slots),
					this.types
			);
		}

		@Override
		public MapCodec<BookModel.Unbaked> type() {
			return MAP_CODEC;
		}
	}
}