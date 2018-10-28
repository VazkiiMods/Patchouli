package vazkii.patchouli.client.book.template;

import java.util.Map;

public interface IComponentInflater {

	public void setup(Map<String, String> variables);
	
	public String getInflatedValue(String key);
	
	public default boolean allowRender(String group) {
		return true;
	}
	
}
