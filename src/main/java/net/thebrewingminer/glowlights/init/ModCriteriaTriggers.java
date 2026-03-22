package net.thebrewingminer.glowlights.init;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.thebrewingminer.glowlights.advancements.criterion.GlowCampfireCookedTrigger;

import java.util.function.Supplier;

public class ModCriteriaTriggers {

    public static <T extends CriterionTrigger<?>> T register(String name, Supplier<T> criteria){
        return CriteriaTriggers.register(name, criteria.get());
    }

    public static final GlowCampfireCookedTrigger GLOW_CAMPFIRE_COOKED_TRIGGER = register("glowlights:glow_campfire_cooked", GlowCampfireCookedTrigger::new);

    public static void init() {
        // Static method call causes class to load, and thus the field(s).
    }
}