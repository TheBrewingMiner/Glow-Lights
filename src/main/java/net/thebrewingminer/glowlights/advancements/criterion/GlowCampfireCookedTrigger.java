package net.thebrewingminer.glowlights.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.thebrewingminer.glowlights.GlowLights;


@SuppressWarnings("NullableProblems")
public class GlowCampfireCookedTrigger extends SimpleCriterionTrigger<GlowCampfireCookedTrigger.TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation(GlowLights.MOD_ID, "glow_campfire_cooked");

    @Override
    public ResourceLocation getId() { return ID; }

    @Override
    protected TriggerInstance createInstance(JsonObject json, ContextAwarePredicate contextAwarePredicate, DeserializationContext context) {
        LocationPredicate location = LocationPredicate.fromJson(json.get("location"));
        return new TriggerInstance(contextAwarePredicate, location);
    }

    public void trigger(ServerPlayer player, BlockPos pos) {
        this.trigger(player, instance -> instance.matches(player.serverLevel(), pos));
    }

    // Inner TriggerInstance class defines the core behavior for each instance of the trigger.
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final LocationPredicate locationPredicate;

        public TriggerInstance(ContextAwarePredicate contextAwarePredicate, LocationPredicate locationPredicate) {
            super(GlowCampfireCookedTrigger.ID, contextAwarePredicate);
            this.locationPredicate = locationPredicate;
        }

        public boolean matches(ServerLevel level, BlockPos pos) {
            return this.locationPredicate.matches(level, pos.getX(), pos.getY(), pos.getZ());
        }

        @Override
        public JsonObject serializeToJson(SerializationContext conditions) {
            JsonObject obj = super.serializeToJson(conditions);
            obj.add("location", this.locationPredicate.serializeToJson());

            return obj;
        }

    }
}