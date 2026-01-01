package vazkii.patchouli.common.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;

import org.apache.commons.lang3.tuple.Pair;

import vazkii.patchouli.api.PatchouliAPI;

import java.util.Optional;
import java.util.function.Function;

public final class EntityUtil {

	private EntityUtil() {}

	public static String getEntityName(String entityId) {
		Pair<String, String> nameAndNbt = splitNameAndNBT(entityId);
		EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.tryParse(nameAndNbt.getLeft()));

		return type.getDescriptionId();
	}

	public static Function<Level, Entity> loadEntity(String entityId) {
		Pair<String, String> nameAndNbt = splitNameAndNBT(entityId);
		entityId = nameAndNbt.getLeft();
		String nbtStr = nameAndNbt.getRight();
		CompoundTag nbt = null;

		if (!nbtStr.isEmpty()) {
			try {
				nbt = TagParser.parseCompoundFully(nbtStr);
			} catch (CommandSyntaxException e) {
				PatchouliAPI.LOGGER.error("Failed to load entity data", e);
			}
		}

		Identifier key = Identifier.tryParse(entityId);
		Optional<EntityType<?>> maybeType = BuiltInRegistries.ENTITY_TYPE.getOptional(key);
		if (maybeType.isEmpty()) {
			throw new RuntimeException("Unknown entity id: " + entityId);
		}
		EntityType<?> type = maybeType.get();
		final CompoundTag useNbt = nbt;
		final String useId = entityId;
		return (world) -> {
			Entity entity;
			ProblemReporter.Collector problemReporter = new ProblemReporter.Collector();
			try {
				entity = type.create(world, EntitySpawnReason.LOAD);
			} catch (Exception e) {
				throw new IllegalArgumentException("Can't load entity " + useId, e);
			}

			if (entity == null) {
				throw new IllegalArgumentException("Can't create entity " + useId);
			}

			if (useNbt != null) {
				entity.load(TagValueInput.create(problemReporter, world.registryAccess(), useNbt));
			}

			String report = problemReporter.getTreeReport();
			if (!report.isEmpty()) {
				throw new IllegalArgumentException("Invalid entity data for " + useId + ": " + report);
			}

			return entity;
		};
	}

	private static Pair<String, String> splitNameAndNBT(String entityId) {
		int nbtStart = entityId.indexOf("{");
		String nbtStr = "";
		if (nbtStart > 0) {
			nbtStr = entityId.substring(nbtStart).replaceAll("([^\\\\])'", "$1\"").replaceAll("\\\\'", "'");
			entityId = entityId.substring(0, nbtStart);
		}

		return Pair.of(entityId, nbtStr);
	}
}
