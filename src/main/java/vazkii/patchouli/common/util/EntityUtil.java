package vazkii.patchouli.common.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;

import vazkii.patchouli.common.base.Patchouli;

import java.util.Optional;
import java.util.function.Function;

public final class EntityUtil {

	private EntityUtil() {}

	public static String getEntityName(String entityId) {
		Pair<String, String> nameAndNbt = splitNameAndNBT(entityId);
		EntityType<?> type = Registry.ENTITY_TYPE.get(new Identifier(nameAndNbt.getLeft()));

		return type.getTranslationKey();
	}

	public static Function<World, Entity> loadEntity(String entityId) {
		Pair<String, String> nameAndNbt = splitNameAndNBT(entityId);
		entityId = nameAndNbt.getLeft();
		String nbtStr = nameAndNbt.getRight();
		CompoundTag nbt = null;

		if (!nbtStr.isEmpty()) {
			try {
				nbt = StringNbtReader.parse(nbtStr);
			} catch (CommandSyntaxException e) {
				Patchouli.LOGGER.error("Failed to load entity data", e);
			}
		}

		Identifier key = new Identifier(entityId);
		Optional<EntityType<?>> maybeType = Registry.ENTITY_TYPE.getOrEmpty(key);
		if (!maybeType.isPresent()) {
			throw new RuntimeException("Unknown entity id: " + entityId);
		}
		EntityType<?> type = maybeType.get();
		final CompoundTag useNbt = nbt;
		final String useId = entityId;
		return (world) -> {
			Entity entity;
			try {
				entity = type.create(world);
				if (useNbt != null) {
					entity.fromTag(useNbt);
				}

				return entity;
			} catch (Exception e) {
				throw new IllegalArgumentException("Can't load entity " + useId, e);
			}
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
