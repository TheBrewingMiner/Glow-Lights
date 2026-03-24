package net.thebrewingminer.glowlights.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;


@SuppressWarnings("NullableProblems")
public class GlowCampfireCookedTrigger extends SimpleCriterionTrigger<GlowCampfireCookedTrigger.TriggerInstance> {

    public Codec<GlowCampfireCookedTrigger.TriggerInstance> codec() {
        return GlowCampfireCookedTrigger.TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, BlockPos pos) {
        this.trigger(player, instance -> instance.matches(player.serverLevel(), pos));
    }

    // Inner TriggerInstance class defines the core behavior for each instance of the trigger.
    @SuppressWarnings("CodeBlock2Expr")
    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<LocationPredicate> location) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<GlowCampfireCookedTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(TriggerInstance::player),
                    ExtraCodecs.strictOptionalField(LocationPredicate.CODEC, "location").forGetter(GlowCampfireCookedTrigger.TriggerInstance::location)).apply(instance, GlowCampfireCookedTrigger.TriggerInstance::new);
            }
        );

        public boolean matches(ServerLevel level, BlockPos pos) {
            return (this.location.isEmpty() || this.location.get().matches(level, pos.getX(), pos.getY(), pos.getZ()));
        }

        @Override
        public Optional<ContextAwarePredicate> player() {
            return this.player;
        }
    }
}