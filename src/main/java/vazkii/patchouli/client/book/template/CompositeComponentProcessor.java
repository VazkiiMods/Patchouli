package vazkii.patchouli.client.book.template;

import net.minecraft.client.gui.GuiScreen;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;

public class CompositeComponentProcessor implements IComponentProcessor {

	private final IComponentProcessor processor1, processor2;
	
	public CompositeComponentProcessor(IComponentProcessor processor1, IComponentProcessor processor2) {
		this.processor1 = processor1;
		this.processor2 = processor2;
	}
	
	@Override
	public void setup(IVariableProvider variables) {
		processor1.setup(variables);
		processor2.setup(variables);
	}

	@Override
	public String process(String key) {
		String res = processor1.process(key);
		if(res == null)
			res = processor2.process(key);
		
		return res;
	}
	
	@Override
	public void refresh(GuiScreen parent, int left, int top) {
		processor1.refresh(parent, left, top);
		processor2.refresh(parent, left, top);
	}
	
	@Override
	public boolean allowRender(String group) {
		return processor1.allowRender(group) && processor2.allowRender(group);
	}

}
