package ipsis.woot.advancements;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ipsis.woot.Woot;
import ipsis.woot.modules.factory.Tier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;


public class TierValidateTrigger extends AbstractCriterionTrigger<TierValidateTrigger.Instance> {

    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Woot.MODID, "validatetier");

    @Override
    public ResourceLocation getId() { return ID; }

    @Override
    protected Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        JsonElement element = json.get("tier");
        Tier tier = Tier.TIER_1;
        if (element != null && !element.isJsonNull())
            tier = Tier.byIndex(GsonHelper.convertToInt(json, "tier"));

        return new Instance(entityPredicate, tier);
    }

    public void trigger(ServerPlayer playerEntity, Tier tier) {
        this.triggerListeners(playerEntity, instance -> instance.test(playerEntity, tier));
    }

    public static class Instance extends CriterionInstance {
        private final Tier tier;

        public Instance(EntityPredicate.AndPredicate player, Tier tier) {
            super(TierValidateTrigger.ID, player);
            this.tier = tier;
        }

        public static TierValidateTrigger.Instance forTier(Tier tier) {
            return new TierValidateTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, tier);
        }

        public boolean test(ServerPlayer playerEntity, Tier tier) { return this.tier == tier; }

        public JsonObject serialize(ConditionArraySerializer conditions) {
            JsonObject jsonObject = super.serialize(conditions);
            jsonObject.addProperty("tier", tier.ordinal());
            return jsonObject;
        }

    }
}
