package vazkii.patchouli.client.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.BakedModelWrapper;

import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BookModel extends BakedModelWrapper<IBakedModel> {
	public BookModel(IBakedModel original) {
		super(original);
	}

	private final ItemOverrideList itemHandler = new ItemOverrideList() {
		@Override
		public IBakedModel getOverrideModel(@Nonnull IBakedModel original, @Nonnull ItemStack stack,
				@Nullable ClientWorld world, @Nullable LivingEntity entity) {
			Book book = ItemModBook.getBook(stack);
			if (book != null) {
				ModelResourceLocation modelPath = new ModelResourceLocation(book.model, "inventory");
				return Minecraft.getInstance().getModelManager().getModel(modelPath);
			}
			return original;
		}
	};

	@Nonnull
	@Override
	public ItemOverrideList getOverrides() {
		return itemHandler;
	}
}
