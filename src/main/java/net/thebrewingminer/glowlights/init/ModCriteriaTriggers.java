package net.thebrewingminer.glowlights.init;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.thebrewingminer.glowlights.advancements.criterion.GlowCampfireCookedTrigger;

import java.util.function.Supplier;

public class ModCriteriaTriggers {

    public static <T extends CriterionTrigger<?>> T register(Supplier<T> criteria){
        return CriteriaTriggers.register(criteria.get());
    }

    public static final GlowCampfireCookedTrigger GLOW_CAMPFIRE_COOKED_TRIGGER = register(GlowCampfireCookedTrigger::new);

    public static void init() {
        // Static method call causes class to load, and thus the field(s).
    }
}