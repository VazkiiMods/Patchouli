package vazkii.patchouli.common.network;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;

@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public class MessageSerializer {

	private static final HashMap<Class<?>, Pair<Reader, Writer>> handlers = new HashMap<>();
	private static final HashMap<Class<?>, Field[]> fieldCache = new HashMap<>();

	static {
		MessageSerializer.<Byte> mapHandler(byte.class, PacketBuffer::readByte, PacketBuffer::writeByte);
		MessageSerializer.<Short> mapHandler(short.class, PacketBuffer::readShort, PacketBuffer::writeShort);
		MessageSerializer.<Integer> mapHandler(int.class, PacketBuffer::readInt, PacketBuffer::writeInt);
		MessageSerializer.<Long> mapHandler(long.class, PacketBuffer::readLong, PacketBuffer::writeLong);
		MessageSerializer.<Float> mapHandler(float.class, PacketBuffer::readFloat, PacketBuffer::writeFloat);
		MessageSerializer.<Double> mapHandler(double.class, PacketBuffer::readDouble, PacketBuffer::writeDouble);
		MessageSerializer.<Boolean> mapHandler(boolean.class, PacketBuffer::readBoolean, PacketBuffer::writeBoolean);
		MessageSerializer.<Character> mapHandler(char.class, PacketBuffer::readChar, PacketBuffer::writeChar);

		mapHandler(BlockPos.class, PacketBuffer::readBlockPos, PacketBuffer::writeBlockPos);
		mapHandler(ITextComponent.class, PacketBuffer::readTextComponent, PacketBuffer::writeTextComponent);
		mapHandler(Enum.class, MessageSerializer::readEnumValue, PacketBuffer::writeEnumValue);
		mapHandler(UUID.class, PacketBuffer::readUniqueId, PacketBuffer::writeUniqueId);
		mapHandler(CompoundNBT.class, PacketBuffer::readCompoundTag, PacketBuffer::writeCompoundTag);
		mapHandler(ItemStack.class, PacketBuffer::readItemStack, MessageSerializer::writeItemStack);
		mapHandler(String.class, MessageSerializer::readString, MessageSerializer::writeString);
		mapHandler(ResourceLocation.class, PacketBuffer::readResourceLocation, PacketBuffer::writeResourceLocation);
		mapHandler(Date.class, PacketBuffer::readTime, PacketBuffer::writeTime);
		mapHandler(BlockRayTraceResult.class, PacketBuffer::readBlockRay, PacketBuffer::writeBlockRay);
	}
	
	public static void readObject(Object obj, PacketBuffer buf) {
		try {
			Class<?> clazz = obj.getClass();
			Field[] clFields = getClassFields(clazz);
			for(Field f : clFields) {
				Class<?> type = f.getType();
				if(acceptField(f, type))
					readField(obj, f, type, buf);
			}
		} catch(Exception e) {
			throw new RuntimeException("Error at reading message " + obj, e);
		}
	}
	
	public static void writeObject(Object obj, PacketBuffer buf) {
		try {
			Class<?> clazz = obj.getClass();
			Field[] clFields = getClassFields(clazz);
			for(Field f : clFields) {
				Class<?> type = f.getType();
				if(acceptField(f, type))
					writeField(obj, f, type, buf);
			}
		} catch(Exception e) {
			throw new RuntimeException("Error at writing message " + obj, e);
		}
	}

	private static Field[] getClassFields(Class<?> clazz) {
		if(fieldCache.containsKey(clazz))
			return fieldCache.get(clazz);
		else {
			Field[] fields = clazz.getFields();
			Arrays.sort(fields, Comparator.comparing(Field::getName));
			fieldCache.put(clazz, fields);
			return fields;
		}
	}

	private static void writeField(Object obj, Field f, Class<?> clazz, PacketBuffer buf) throws IllegalArgumentException, IllegalAccessException {
		Pair<Reader, Writer> handler = getHandler(clazz);
		handler.getRight().write(buf, f, f.get(obj));
	}

	private static void readField(Object obj, Field f, Class<?> clazz, PacketBuffer buf) throws IllegalArgumentException, IllegalAccessException {
		Pair<Reader, Writer> handler = getHandler(clazz);
		f.set(obj, handler.getLeft().read(buf, f));
	}

	private static Pair<Reader, Writer> getHandler(Class<?> clazz) {
		Pair<Reader, Writer> pair = handlers.get(clazz);
		if(pair == null)
			throw new RuntimeException("No R/W handler for  " + clazz);
		return pair;
	}

	private static boolean acceptField(Field f, Class<?> type) {
		int mods = f.getModifiers();
		if(Modifier.isFinal(mods) || Modifier.isStatic(mods) || Modifier.isTransient(mods))
			return false;

		return  handlers.containsKey(type);
	}

	private static <T> void mapHandler(Class<T> type, Function<PacketBuffer, T> readerLower, BiConsumer<PacketBuffer, T> writerLower) {
		Reader<T> reader = (buf, field) -> readerLower.apply(buf);
		Writer<T> writer = (buf, field, t) -> writerLower.accept(buf, t);
		mapHandler(type, reader, writer);
	}

	private static <T> void mapHandler(Class<T> type, Reader<T> reader, BiConsumer<PacketBuffer, T> writerLower) {
		Writer<T> writer = (buf, field, t) -> writerLower.accept(buf, t);
		mapHandler(type, reader, writer);	
	}

	private static <T> void mapHandler(Class<T> type, Function<PacketBuffer, T> readerLower, Writer<T> writer) {
		Reader<T> reader = (buf, field) -> readerLower.apply(buf);
		mapHandler(type, reader, writer);
	}

	public static <T> void mapHandler(Class<T> type, Reader<T> reader, Writer<T> writer) {
		Class<T[]> arrayType = (Class<T[]>) Array.newInstance(type, 0).getClass();

		Reader<T[]> arrayReader = (buf, field) -> {
			int count = buf.readInt();
			T[] arr = (T[]) Array.newInstance(type, count);

			for(int i = 0; i < count; i++)
				arr[i] = reader.read(buf, field);

			return arr;
		};
		
		Writer<T[]> arrayWriter = (buf, field, t) -> {
			int count = t.length;
			buf.writeInt(count);
			
			for(int i = 0; i < count; i++)
				writer.write(buf, field, t[i]);
		};
		
		handlers.put(type, Pair.of(reader, writer));
		handlers.put(arrayType, Pair.of(arrayReader, arrayWriter));
	}

	// ================================================================
	// Auxiliary I/O
	// ================================================================

	// Needed because we need the class type

	private static <T extends Enum<T>> T readEnumValue(PacketBuffer buf, Field f) {
		Class<?> clazz = f.getType();
		if(clazz.isArray())
			clazz = clazz.getComponentType();

		return buf.readEnumValue((Class<T>) clazz);
	}

	// Needed because the methods are overloaded

	private static void writeItemStack(PacketBuffer buf, ItemStack stack) {
		buf.writeItemStack(stack);
	}

	private static String readString(PacketBuffer buf) {
		return buf.readString(32767);
	}

	private static void writeString(PacketBuffer buf, String string) {
		buf.writeString(string);
	}

	// ================================================================
	// Functional interfaces
	// ================================================================

	public static interface Reader<T> {
		public T read(PacketBuffer buf, Field field);
	}

	public static interface Writer<T> {
		public void write(PacketBuffer buf, Field field, T t);
	}

}
