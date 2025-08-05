package ipsis.woot.modules.factory.network;

import io.netty.buffer.ByteBuf;
import ipsis.woot.modules.factory.FormedSetup;
import ipsis.woot.modules.factory.perks.Perk;
import ipsis.woot.modules.factory.blocks.HeartMenu;
import ipsis.woot.modules.factory.blocks.HeartRecipe;
import ipsis.woot.modules.factory.client.ClientFactorySetup;
import ipsis.woot.modules.factory.perks.Helper;
import ipsis.woot.simulator.MobSimulator;
import ipsis.woot.simulator.SimulatedMobDropSummary;
import ipsis.woot.util.FakeMob;
import ipsis.woot.util.NetworkHelper;
import ipsis.woot.util.oss.NetworkTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.network.handling.IPayloadContext;


import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public record HeartStaticDataReply(FormedSetup formedSetup, HeartRecipe recipe, ClientFactorySetup clientFactorySetup) {






    public HeartStaticDataReply(FormedSetup formedSetup, HeartRecipe recipe) {
        this(formedSetup, recipe, null);
    }

    public static HeartStaticDataReply fromBytes(ByteBuf buf) {
        HeartStaticDataReply pkt = new HeartStaticDataReply();
        pkt.clientFactorySetup = ClientFactorySetup.STREAM_CODEC.decode(buf);
        return pkt;
    }

    public void toBytes(RegistryFriendlyByteBuf buf) {

        buf.writeInt(formedSetup.getTier().ordinal());
        buf.writeInt(formedSetup.getCellCapacity());
        buf.writeInt(formedSetup.getCellFluidAmount());
        buf.writeInt(formedSetup.getLootingLevel());
        buf.writeInt(formedSetup.getExotic().ordinal());

        buf.writeInt(recipe.getNumTicks());
        buf.writeInt(recipe.getNumUnits());

        if (formedSetup.getAllPerks().containsKey(Perk.Group.TIER_SHARD)) {
            buf.writeInt(formedSetup.getPerkTierShardValue());
            buf.writeDouble(formedSetup.getShardDropChance());
            double full = formedSetup.getBasicShardWeight() + formedSetup.getAdvancedShardWeight() + formedSetup.getEliteShardWeight();
            buf.writeDouble((100.0F / full) * formedSetup.getBasicShardWeight());
            buf.writeDouble((100.0F / full) * formedSetup.getAdvancedShardWeight());
            buf.writeDouble((100.0F / full) * formedSetup.getEliteShardWeight());
        } else {
            buf.writeInt(0);
            buf.writeDouble(0.0F);
            buf.writeDouble(0.0F);
            buf.writeDouble(0.0F);
            buf.writeDouble(0.0F);
        }

        buf.writeInt(formedSetup.getAllMobs().size());
        for (FakeMob fakeMob : formedSetup.getAllMobs()) {
            NetworkTools.writeString(buf, fakeMob.toString());

            // Params
            buf.writeInt(formedSetup.getAllMobParams().get(fakeMob).baseSpawnTicks);
            buf.writeInt(formedSetup.getAllMobParams().get(fakeMob).baseMassCount);
            buf.writeInt(formedSetup.getAllMobParams().get(fakeMob).baseFluidCost);
            buf.writeInt(formedSetup.getAllMobParams().get(fakeMob).getPerkRateValue());
            buf.writeInt(formedSetup.getAllMobParams().get(fakeMob).getPerkEfficiencyValue());
            buf.writeInt(formedSetup.getAllMobParams().get(fakeMob).getMobCount(formedSetup.getAllPerks().containsKey(Perk.Group.MASS), formedSetup.hasMassExotic()));
            buf.writeInt(formedSetup.getAllMobParams().get(fakeMob).getPerkXpValue());
            buf.writeInt(formedSetup.getAllMobParams().get(fakeMob).getPerkHeadlessValue());

            List<SimulatedMobDropSummary> drops = MobSimulator.getInstance().getDropSummary(fakeMob);
            buf.writeInt(drops.size());
            for (SimulatedMobDropSummary drop : drops) {
                ItemStack itemStack = drop.stack().copy();
                itemStack.setCount((int)(drop.chanceToDrop().get(formedSetup.getLootingLevel()) * 1000.0F));
                NetworkTools.writeItemStack(buf, itemStack);
                buf.writeFloat(drop.chanceToDrop().get(formedSetup.getLootingLevel()));
            }
        }

        buf.writeBoolean(formedSetup.isPerkCapped());
        buf.writeInt(formedSetup.getAllPerks().size());
        for (Map.Entry<Perk.Group, Integer> e : formedSetup.getAllPerks().entrySet()) {
            Perk perk = Helper.getPerk(e.getKey(), e.getValue());
            buf.writeInt(perk.ordinal());
        }

        // Ingredients are the sum from all the mobs
        buf.writeInt(recipe.recipeItems.size());
        recipe.recipeItems.forEach(i -> NetworkTools.writeItemStack(buf, i));
        buf.writeInt(recipe.recipeFluids.size());
        recipe.recipeFluids.forEach(i -> NetworkHelper.writeFluidStack(buf, i));
    }

    public void handle(HeartStaticDataReply reply, IPayloadContext ctx) {
            final LocalPlayer player = Minecraft.getInstance().player;
            if (player.containerMenu instanceof HeartMenu)
                ((HeartMenu) player.containerMenu).handleStaticDataReply(clientFactorySetup);
    }
}
