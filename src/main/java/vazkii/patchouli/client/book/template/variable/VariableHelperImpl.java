package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonElement;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableSerializer;
import vazkii.patchouli.api.VariableHelper;

import java.util.HashMap;
import java.util.Map;

public class VariableHelperImpl implements VariableHelper {

	public VariableHelperImpl() {
		registerSerializer(new ItemStackVariableSerializer(), ItemStack.class);
		registerSerializer(new IngredientVariableSerializer(), Ingredient.class);
		registerSerializer(new TextComponentVariableSerializer(), Text.class);
	}

	public Map<Class<?>, IVariableSerializer<?>> serializers = new HashMap<>();

	@Override
	@SuppressWarnings("unchecked")
	public <T> IVariableSerializer<T> serializerForClass(Class<?> clazz) {
		return (IVariableSerializer<T>) serializers.get(clazz);
	}

	@Override
	public <T> IVariable createFromObject(T object) {
		Class<?> clazz = object.getClass();
		for (Class<?> c : serializers.keySet()) {
			if (c.isAssignableFrom(clazz)) {
				return create(serializerForClass(c).toJson(object), clazz);
			}
		}

		throw new IllegalArgumentException(String.format("Can't serialize object %s of type %s to IVariable", object, clazz));
	}

	@Override
	public IVariable createFromJson(JsonElement elem) {
		return create(elem, null);
	}

	private IVariable create(JsonElement elem, Class<?> originator) {
		return new Variable(elem, originator);
	}

	@Override
	public <T> VariableHelper registerSerializer(IVariableSerializer<T> serializer, Class<T> clazz) {
		serializers.put(clazz, serializer);
		return this;
	}

}
