package vazkii.patchouli.common.util;

import java.lang.reflect.Constructor;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.command.EntityNotFoundException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class EntityUtil {

	public static String getEntityName(String entityId) {
		Pair<String, String> nameAndNbt = splitNameAndNBT(entityId);
		return EntityList.getTranslationName(new ResourceLocation(nameAndNbt.getLeft()));
	}
	
	public static EntityCreator loadEntity(String entityId) {
		Pair<String, String> nameAndNbt = splitNameAndNBT(entityId);
		entityId = nameAndNbt.getLeft();
		String nbtStr = nameAndNbt.getRight();
		NBTTagCompound nbt = null;
		
		if(!nbtStr.isEmpty()) {
			try {
				nbt = JsonToNBT.getTagFromJson(nbtStr);
			} catch(NBTException e) {
				e.printStackTrace();
			}
		}
		
		final Class<? extends Entity> clazz = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityId)).getEntityClass();
		final NBTTagCompound useNbt = nbt;
		final String useId = entityId;
		try {
			final Constructor<? extends Entity> constructor = clazz.getConstructor(World.class);
			
			return (world) -> {
				Entity entity;
				try {
					entity = constructor.newInstance(world);
					if(useNbt != null)
						entity.readFromNBT(useNbt);
					
					return entity;
				} catch (Exception e) {
					throw new EntityNotFoundException("Can't load entity " + useId);
				}
			};
		} catch(Exception e) {
			throw new RuntimeException("Could not find constructor for entity type " + entityId, e);
		}
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
	
	public static interface EntityCreator {
		
		Entity create(World world) throws EntityNotFoundException;
		
	}
	
}
