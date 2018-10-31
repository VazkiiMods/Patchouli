package vazkii.patchouli.client.book.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.annotations.SerializedName;

import vazkii.patchouli.api.IVariableProvider;

public class TemplateInclusion {

	public String template;
	public String as;
	@SerializedName("using")
	public Map<String, String> map = new HashMap();
	public int x, y;
	
	transient List<String> visitedTemplates = new ArrayList();
	
	public void upperMerge(TemplateInclusion upper) {
		if(upper == null)
			return;
		
		if(upper.visitedTemplates.contains(template))
			throw new IllegalArgumentException("Breaking when include template " + template + ", circular dependencies aren't allowed.");
		
		visitedTemplates = upper.visitedTemplates;
		visitedTemplates.add(template);
		as = upper.realName(as);
		x += upper.x;
		y += upper.y;

		Set<String> keys = map.keySet();
		for(String key : keys) {
			String val = map.get(key);
			if(upper.map.containsKey(val))
				map.put(key, upper.map.get(val));
		}
	}
	
	private String realName(String name) {
		if(name.isEmpty())
			return as;
		return as + "." + name;
	}
	
	public String transform(String var, boolean prefixedOnly) {
		boolean isPrefixed = var.startsWith("#");
		if(!prefixedOnly || isPrefixed) {
			if(map.containsKey(var))
				return map.get(var);
			
			String key = isPrefixed ? var.substring(1) : var;
			return (isPrefixed ? "#" : "") + realName(key);
		}
		
		return var;
	}
	
	public IVariableProvider wrapProvider(IVariableProvider provider) {
		return new IVariableProvider() {
			
			@Override
			public boolean has(String key) {
				return provider.has(transform(key, false));
			}
			
			@Override
			public String get(String key) {
				return provider.get(transform(key, false));
			}
		};
	}
	
}
