package vazkii.patchouli.client.multiblock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.List;

public record GhostBlockGeometry(BlockPos position, BlockState state, float alpha, float scale) implements SubmitNodeCollector.CustomGeometryRenderer {

	@Override
	public void render(PoseStack.Pose pose, VertexConsumer builder) {
		final var mc = Minecraft.getInstance();
		final BlockColors colors = mc.getBlockColors();

		var model = mc.getModelManager().getBlockStateModelSet().get(state);

		float offset = (1 - scale) / 2f;

		final var realPosition = Vec3.atLowerCornerWithOffset(position, offset, offset, offset).toVector3f();
		pose.translate(realPosition.x, realPosition.y, realPosition.z);
		pose.scale(scale, scale, scale);

		List<BlockStateModelPart> parts = new ArrayList<>();
		model.collectParts(RandomSource.create(), parts);

		parts.forEach(part -> {
			for (var dir : Direction.values()) {
				for (var quad : part.getQuads(dir)) {
					addQuad(state, pose, colors, builder, quad, alpha);
				}
			}

			for (var quad : part.getQuads(null)) {
				addQuad(state, pose, colors, builder, quad, alpha);
			}
		});
	}

	private static void addQuad(BlockState state, PoseStack.Pose pose, BlockColors colors, VertexConsumer builder, BakedQuad quad, float alpha) {
		final var material = quad.materialInfo();
		int color = CommonColors.WHITE;
		if (material.isTinted()) {
			var tintSource = colors.getTintSource(state, material.tintIndex());
			if (tintSource != null)
				color = tintSource.color(state);
		}

		final float red = ARGB.redFloat(color);
		final float green = ARGB.greenFloat(color);
		final float blue = ARGB.blueFloat(color);

		final float trueAlpha = Mth.clamp(alpha, 0.1f, 1f);

		Matrix4f matrix = pose.pose();
		Vector3f faceNormal = pose.transformNormal(quad.direction().getUnitVec3f(), new Vector3f());
		int lightEmission = quad.materialInfo().lightEmission();

		for (int vertex = 0; vertex < 4; vertex++) {
			long packedUv = quad.packedUV(vertex);
			int c = ARGB.colorFromFloat(trueAlpha, red, green, blue);
			int light = LightCoordsUtil.lightCoordsWithEmission(LightCoordsUtil.FULL_SKY, lightEmission);
			Vector3f pos = matrix.transformPosition(quad.position(vertex).x(), quad.position(vertex).y(), quad.position(vertex).z(), new Vector3f());
			float u = UVPair.unpackU(packedUv);
			float v = UVPair.unpackV(packedUv);
			Vector3fc normal;
			normal = faceNormal;
			builder.addVertex(pos.x(), pos.y(), pos.z(), c, u, v, OverlayTexture.NO_OVERLAY, light, normal.x(), normal.y(), normal.z());
		}
	}
}
