package vazkii.patchouli.client.book.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.annotations.SerializedName;

import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;

public class TemplateInclusion {

	public String template;
	public String as;
	@SerializedName("with")
	public Map<String, String> map = new HashMap();
	public int x, y;

	public transient IComponentProcessor processor;
	transient List<String> visitedTemplates = new ArrayList();
	
	public void upperMerge(TemplateInclusion upper) {
		if(upper == null)
			return;
		
		if(upper.visitedTemplates.contains(template))
			throw new IllegalArgumentException("Breaking when include template " + template + ", circular dependencies aren't allowed.");
		
		visitedTemplates = new ArrayList(upper.visitedTemplates);
		visitedTemplates.add(template);
		as = upper.realName(as);
		x += upper.x;
		y += upper.y;
		
		Set<String> keys = map.keySet();
		for(String key : keys) {
			String val = map.get(key);
			if(val.startsWith("#")) {
				String realVal = val.substring(1);
				if(upper.map.containsKey(realVal))	
					map.put(key, upper.map.get(realVal));
			}
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
				String transformed = transform(key, false);
				return !transformed.startsWith("#") || provider.has(transformed.substring(1));
			}
			
			@Override
			public String get(String key) {
				String transformed = transform(key, false);
				return transformed.startsWith("#") ? provider.get(transformed.substring(1)) : transformed;
			}
		};
	}
	
}
