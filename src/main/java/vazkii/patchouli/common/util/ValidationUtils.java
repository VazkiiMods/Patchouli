package vazkii.patchouli.common.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.Set;

public class ValidationUtils {

    public static AdvancementList ADVANCEMENT_LIST = ReflectionHelper.getPrivateValue(AdvancementManager.class, null, 2);

    public static boolean isValidAdvancement(String advancement) {
        return ADVANCEMENT_LIST.getAdvancement(new ResourceLocation(advancement)) != null;
    }



    public static void validateAdvancement(String advancement) {
        if(advancement != null && !advancement.isEmpty()) {
            Set<Advancement> advancements = Sets.newHashSet(ADVANCEMENT_LIST.getAdvancements());
            if(!advancements.isEmpty())
                Preconditions.checkState(isValidAdvancement(advancement), "Invalid advancement:" + advancement);
        }
    }

}
