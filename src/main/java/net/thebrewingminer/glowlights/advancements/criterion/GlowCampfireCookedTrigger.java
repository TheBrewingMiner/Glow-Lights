package net.thebrewingminer.glowlights.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;


@SuppressWarnings("NullableProblems")
public class GlowCampfireCookedTrigger extends SimpleCriterionTrigger<GlowCampfireCookedTrigger.TriggerInstance> {

    @Override
    protected TriggerInstance createInstance(JsonObject json, Optional<ContextAwarePredicate> contextAwarePredicate, DeserializationContext context) {
        Optional<LocationPredicate> location = LocationPredicate.fromJson(json.get("location"));
        return new TriggerInstance(contextAwarePredicate, location);
    }

    public void trigger(ServerPlayer player, BlockPos pos) {
        this.trigger(player, instance -> instance.matches(player.serverLevel(), pos));
    }

    // Inner TriggerInstance class defines the core behavior for each instance of the trigger.
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final Optional<LocationPredicate> locationPredicate;

        public TriggerInstance(Optional<ContextAwarePredicate> contextAwarePredicate, Optional<LocationPredicate> locationPredicate) {
            super(contextAwarePredicate);
            this.locationPredicate = locationPredicate;
        }

        public boolean matches(ServerLevel level, BlockPos pos) {
            return (this.locationPredicate.isEmpty() || this.locationPredicate.get().matches(level, pos.getX(), pos.getY(), pos.getZ()));
        }

        @Override
        public JsonObject serializeToJson() {
            JsonObject obj = super.serializeToJson();
            this.locationPredicate.ifPresent((p -> obj.add("location", p.serializeToJson())));
            return obj;
        }

    }
}