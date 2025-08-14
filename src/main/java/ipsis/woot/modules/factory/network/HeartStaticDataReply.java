package ipsis.woot.modules.factory.network;

import io.netty.buffer.ByteBuf;
import ipsis.woot.Woot;
import ipsis.woot.fluilds.network.TankPacket;
import ipsis.woot.modules.factory.FormedSetup;
import ipsis.woot.modules.factory.perks.Perk;
import ipsis.woot.modules.factory.blocks.HeartMenu;
import ipsis.woot.modules.factory.blocks.HeartRecipe;
import ipsis.woot.modules.factory.client.ClientFactorySetup;
import ipsis.woot.modules.factory.perks.Helper;
import ipsis.woot.simulator.MobSimulator;
import ipsis.woot.simulator.SimulatedMobDropSummary;
import ipsis.woot.util.FakeMob;
import ipsis.woot.util.ExtraWootCodecs;
import ipsis.woot.util.oss.NetworkTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record HeartStaticDataReply(FormedSetup formedSetup, HeartRecipe recipe, ClientFactorySetup clientSetup) implements CustomPacketPayload {



    public static final Type<HeartStaticDataReply> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Woot.MODID, "heartstaticreply"));


    public HeartStaticDataReply(FormedSetup formedSetup, HeartRecipe recipe) {
        this(formedSetup, recipe, new ClientFactorySetup());
        populate(clientSetup, formedSetup, recipe);
    }

    public void populate(ClientFactorySetup setup, FormedSetup formedSetup, HeartRecipe recipe){
        setup.tier = formedSetup.getTier();
        setup.controllerMobs = formedSetup.getAllMobs();
        formedSetup.getAllPerks().keySet().forEach(group ->
                setup.perks.add(Helper.getPerk(group, formedSetup().getAllPerks().get(group)))
        );
        setup.mobParams.putAll(formedSetup.getAllMobParams());
        setup.exotic = formedSetup.getExotic();
        setup.cellCapacity = formedSetup.getCellCapacity();
        setup.looting = formedSetup.getLootingLevel();
        setup.recipeTicks = recipe.numTicks();
        setup.recipeFluid = recipe.numUnits();
        setup.perkCapped = formedSetup.isPerkCapped();
        setup.shardDropChance = formedSetup().getShardDropChance();
        setup.shardDrops = new double[]{formedSetup.getBasicShardWeight(), formedSetup.getAdvancedShardWeight(), formedSetup.getEliteShardWeight()};

        //Drops population
        setup.controllerMobs.forEach((mob) -> {
            setup.mobInfo.put(mob, new ClientFactorySetup.Mob(MobSimulator.getInstance().getDropSummary(mob)));

        });

    }



    public static final StreamCodec<RegistryFriendlyByteBuf, HeartStaticDataReply> STREAM_CODEC = StreamCodec.of(
            (buf, reply) -> {
                ClientFactorySetup.STREAM_CODEC.encode(buf, reply.clientSetup);
            },
            (buf) -> new HeartStaticDataReply(null, null, ClientFactorySetup.STREAM_CODEC.decode(buf))

    );



    public static void handle(HeartStaticDataReply reply, IPayloadContext ctx) {
            final LocalPlayer player = Minecraft.getInstance().player;
            if (player.containerMenu instanceof HeartMenu)
                ((HeartMenu) player.containerMenu).handleStaticDataReply(reply.clientSetup);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
