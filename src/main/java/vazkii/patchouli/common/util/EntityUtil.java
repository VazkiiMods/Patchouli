package vazkii.patchouli.common.util;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.patchouli.common.base.Patchouli;

import java.util.function.Function;

public class EntityUtil {

	public static String getEntityName(String entityId) {
		Pair<String, String> nameAndNbt = splitNameAndNBT(entityId);
		EntityType<?> type = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(nameAndNbt.getLeft()));

		return type.getTranslationKey();
	}
	
	public static Function<World, Entity> loadEntity(String entityId) {
		Pair<String, String> nameAndNbt = splitNameAndNBT(entityId);
		entityId = nameAndNbt.getLeft();
		String nbtStr = nameAndNbt.getRight();
		CompoundNBT nbt = null;
		
		if(!nbtStr.isEmpty()) {
			try {
				nbt = JsonToNBT.getTagFromJson(nbtStr);
			} catch(CommandSyntaxException e) {
				Patchouli.LOGGER.error("Failed to load entity data", e);
			}
		}

		ResourceLocation key = new ResourceLocation(entityId);
		if (!ForgeRegistries.ENTITIES.containsKey(key)) {
			throw new RuntimeException("Unknown entity id: " + entityId);
		}
		EntityType<?> type = ForgeRegistries.ENTITIES.getValue(key);
		final CompoundNBT useNbt = nbt;
		final String useId = entityId;
		return (world) -> {
			Entity entity;
			try {
				entity = type.create(world);
				if(useNbt != null)
					entity.read(useNbt);

				return entity;
			} catch (Exception e) {
				throw new IllegalArgumentException("Can't load entity " + useId, e);
			}
		};
	}
	
	private static Pair<String, String> splitNameAndNBT(String entityId) {
		int nbtStart = entityId.indexOf("{");
		String nbtStr = "";
		if(nbtStart > 0) {
			nbtStr = entityId.substring(nbtStart).replaceAll("([^\\\\])'", "$1\"").replaceAll("\\\\'", "'");
			entityId = entityId.substring(0, nbtStart);
		}
		
		return Pair.of(entityId, nbtStr);
	}
	

}
