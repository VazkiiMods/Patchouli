package vazkii.patchouli.client.book.template;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.resources.ResourceLocation;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.template.component.*;
import vazkii.patchouli.common.book.Book;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class BookTemplate {

	public static final HashMap<ResourceLocation, Class<? extends TemplateComponent>> componentTypes = new HashMap<>();

	static {
		registerComponent(new ResourceLocation(PatchouliAPI.MOD_ID, "text"), ComponentText.class);
		registerComponent(new ResourceLocation(PatchouliAPI.MOD_ID, "item"), ComponentItemStack.class);
		registerComponent(new ResourceLocation(PatchouliAPI.MOD_ID, "image"), ComponentImage.class);
		registerComponent(new ResourceLocation(PatchouliAPI.MOD_ID, "header"), ComponentHeader.class);
		registerComponent(new ResourceLocation(PatchouliAPI.MOD_ID, "separator"), ComponentSeparator.class);
		registerComponent(new ResourceLocation(PatchouliAPI.MOD_ID, "frame"), ComponentFrame.class);
		registerComponent(new ResourceLocation(PatchouliAPI.MOD_ID, "entity"), ComponentEntity.class);
		registerComponent(new ResourceLocation(PatchouliAPI.MOD_ID, "tooltip"), ComponentTooltip.class);
		registerComponent(new ResourceLocation(PatchouliAPI.MOD_ID, "custom"), ComponentCustom.class);
	}

	@SerializedName("include") List<TemplateInclusion> inclusions = new ArrayList<>();
	List<TemplateComponent> components = new ArrayList<>();

	@SerializedName("processor") String processorClass;

	transient Book book;

	/**
	 * Information about how the parent template is including this one.
	 * Can be null when there is no parent, e.g. the top template at the page level
	 */
	@Nullable transient TemplateInclusion encapsulation;

	transient IComponentProcessor processor;
	transient boolean compiled = false;
	transient boolean attemptedCreatingProcessor = false;

	public static BookTemplate createTemplate(Book book, BookContentsBuilder builder, String type, @Nullable TemplateInclusion inclusion) {
		ResourceLocation key;
		if (type.contains(":")) {
			key = new ResourceLocation(type);
		} else {
			key = new ResourceLocation(book.getModNamespace(), type);
		}

		Supplier<BookTemplate> supplier = builder.getTemplate(key);
		if (supplier == null) {
			throw new IllegalArgumentException("Template " + key + " does not exist");
		}

		BookTemplate template = supplier.get();
		template.book = book;
		template.encapsulation = inclusion;

		return template;
	}

	public void compile(BookContentsBuilder builder, IVariableProvider variables) {
		if (compiled) {
			return;
		}

		createProcessor();
		components.removeIf(Objects::isNull);

		if (processor != null) {
			IVariableProvider processorVars = variables;
			if (encapsulation != null) {
				processorVars = encapsulation.wrapProvider(variables);
			}

			try {
				processor.setup(processorVars);
			} catch (Exception e) {
				throw new RuntimeException("Error setting up template processor", e);
			}
		}

		for (TemplateInclusion include : inclusions) {
			if (include.template == null || include.template.isEmpty() || include.as == null || include.as.isEmpty()) {
				throw new IllegalArgumentException("Template inclusion must define both \"template\" and \"as\" fields.");
			}

			include.upperMerge(encapsulation);
			include.process(processor);

			BookTemplate template = createTemplate(book, builder, include.template, include);
			template.compile(builder, variables);
			components.addAll(template.components);
		}

		for (TemplateComponent c : components) {
			c.compile(variables, processor, encapsulation);
		}

		compiled = true;
	}

	public void build(BookContentsBuilder builder, BookPage page, BookEntry entry, int pageNum) {
		if (compiled) {
			components.forEach(c -> c.build(builder, page, entry, pageNum));
		}
	}

	public void onDisplayed(BookPage page, GuiBookEntry parent, int left, int top) {
		if (compiled) {
			if (processor != null) {
				processor.refresh(parent, left, top);
			}

			components.forEach(c -> c.isVisible = c.getVisibleStatus(processor));
			components.forEach(c -> c.onDisplayed(page, parent, left, top));
		}
	}

	public void render(PoseStack ms, BookPage page, int mouseX, int mouseY, float pticks) {
		if (compiled) {
			components.forEach(c -> {
				if (c.isVisible) {
					c.render(ms, page, mouseX, mouseY, pticks);
				}
			});
		}
	}

	public boolean mouseClicked(BookPage page, double mouseX, double mouseY, int mouseButton) {
		if (compiled) {
			for (TemplateComponent c : components) {
				if (c.isVisible && c.mouseClicked(page, mouseX, mouseY, mouseButton)) {
					return true;
				}
			}
		}

		return false;
	}

	public static void registerComponent(ResourceLocation name, Class<? extends TemplateComponent> clazz) {
		componentTypes.put(name, clazz);
	}

	private void createProcessor() {
		if (!attemptedCreatingProcessor) {
			if (processorClass != null && !processorClass.isEmpty()) {
				try {
					Class<?> clazz = Class.forName(processorClass);
					processor = (IComponentProcessor) clazz.newInstance();
				} catch (Exception e) {
					throw new RuntimeException("Failed to create component processor " + processorClass, e);
				}
			}

			attemptedCreatingProcessor = true;
		}
	}
	
	public boolean overwriteKeyPressed(BookPage page, int keyCode, int scanCode, int modifiers) {
		if (compiled) {
			for (TemplateComponent c : components) {
				if (c.isVisible && c.overwriteKeyPressed(page, keyCode, scanCode, modifiers)) {
					return true;
				}
			}
		}

		return false;
	}
}
