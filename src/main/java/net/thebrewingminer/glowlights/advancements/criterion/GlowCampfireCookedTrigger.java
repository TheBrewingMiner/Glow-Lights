package net.thebrewingminer.glowlights.advancements.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.registries.ForgeRegistries;
import net.thebrewingminer.glowlights.GlowLights;
import net.thebrewingminer.glowlights.block.utils.GlowUtils;
import org.jetbrains.annotations.NotNull;


@SuppressWarnings("NullableProblems")
public class GlowCampfireCookedTrigger extends SimpleCriterionTrigger<GlowCampfireCookedTrigger.TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation(GlowLights.MOD_ID, "glow_campfire_cooked");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject json, EntityPredicate.Composite player, DeserializationContext context) {
        Block block = deserializeBlock(json);
        boolean waterlogged = json.has("waterlogged") ? json.get("waterlogged").getAsBoolean() : false;

        return new TriggerInstance(player, block, waterlogged);
    }

    private static @NotNull Block deserializeBlock(JsonObject json) {
        if (!json.has("block")) throw new JsonSyntaxException("Missing required 'block' field for glow_campfire_cooked trigger");

        ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(json, "block"));
        Block block = ForgeRegistries.BLOCKS.getValue(id);

        if (block == null) {
            throw new JsonSyntaxException("Unknown block: '" + id + "'");
        }

        return block;
    }

    public void trigger(ServerPlayer player, BlockState state) {
        this.trigger(
                        player,
            instance -> instance.matches(state)
        );
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        private final Block block;
        private final boolean waterlogged;

        public TriggerInstance(EntityPredicate.Composite player, Block block, boolean waterlogged) {
            super(GlowCampfireCookedTrigger.ID, player);
            this.block = block;
            this.waterlogged = waterlogged;
        }

        public boolean matches(BlockState state) {
            if (!state.is(block)) return false;
            if (!state.hasProperty(BlockStateProperties.WATERLOGGED)) return false;

            return GlowUtils.isWaterlogged(state) == waterlogged;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext conditions) {
            JsonObject json = super.serializeToJson(conditions);

            json.addProperty("block", ForgeRegistries.BLOCKS.getKey(block).toString());
            json.addProperty("waterlogged", waterlogged);

            return json;
        }

    }
}