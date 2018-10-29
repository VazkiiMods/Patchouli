package vazkii.patchouli.client.book.template;

import java.util.Map;

public interface IComponentProcessor {

	public void setup(IVariableProvider variables);
	
	public String process(String key);
	
	public default boolean allowRender(String group) {
		return true;
	}
	
}
