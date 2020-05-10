package vazkii.patchouli.client.book.template;

import com.google.gson.annotations.SerializedName;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TemplateInclusion {
	/**
	 * The template to include.
	 */
	public String template;

	/**
	 * The scope under which the included template's variables are exposed to the including template.
	 * Modified on load to become absolute, i.e. the full path from the top level page to the
	 * included template's variables.
	 */
	public String as;

	/**
	 * Bindings to perform on the included template.
	 * Right hand side can reference variables in the including template.
	 */
	@SerializedName("using") public Map<String, String> localBindings = new HashMap<>();

	public int x, y;

	transient List<String> visitedTemplates = new ArrayList<>();

	public void upperMerge(@Nullable TemplateInclusion parent) {
		if (parent == null)
			return;

		if (parent.visitedTemplates.contains(template))
			throw new IllegalArgumentException("Breaking when include template " + template + ", circular dependencies aren't allowed (stack = " + parent.visitedTemplates + ")");

		visitedTemplates = new ArrayList<>(parent.visitedTemplates);
		visitedTemplates.add(template);
		as = parent.realName(as);
		x += parent.x;
		y += parent.y;

		Set<String> keys = localBindings.keySet();
		for (String key : keys) {
			String val = localBindings.get(key);
			if (val.startsWith("#")) {
				String realVal = val.substring(1);
				if (parent.localBindings.containsKey(realVal))
					localBindings.put(key, parent.localBindings.get(realVal));
			}
		}
	}

	public void process(IComponentProcessor processor) {
		if (processor == null)
			return;

		Set<String> keys = localBindings.keySet();
		for (String key : keys) {
			String val = localBindings.get(key);
			if (val.startsWith("#")) {
				String realVal = val.substring(1);
				String res = processor.process(realVal);
				if (res != null)
					localBindings.put(key, res);
			}
		}
	}

	private String realName(String name) {
		if (name.isEmpty())
			return as;
		return as + "." + name;
	}

	public String transform(String var, boolean prefixedOnly) {
		boolean isPrefixed = var.startsWith("#");
		if (!prefixedOnly || isPrefixed) {
			String key = isPrefixed ? var.substring(1) : var;
			if (localBindings.containsKey(key))
				return localBindings.get(key);

			return (isPrefixed ? "#" : "") + realName(key);
		}

		return var;
	}

	public IVariableProvider<String> wrapProvider(IVariableProvider<String> provider) {
		return new IVariableProvider<String>() {

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
