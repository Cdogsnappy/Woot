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
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;


import java.util.List;
import java.util.Map;

public record HeartStaticDataReply(FormedSetup formedSetup, HeartRecipe recipe, ClientFactorySetup clientFactorySetup) implements CustomPacketPayload {



    public static final Type<HeartStaticDataReply> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Woot.MODID, "heartstaticreply"));


    public HeartStaticDataReply(FormedSetup formedSetup, HeartRecipe recipe) {
        this(formedSetup, recipe, null);
    }


    public static final StreamCodec<RegistryFriendlyByteBuf, HeartStaticDataReply> STREAM_CODEC = StreamCodec.composite(
            FormedSetup.STREAM_CODEC, HeartStaticDataReply::formedSetup,
            HeartRecipe.STREAM_CODEC, HeartStaticDataReply::recipe,
            ClientFactorySetup.STREAM_CODEC, HeartStaticDataReply::clientFactorySetup,
            HeartStaticDataReply::new
    );



    public static void handle(HeartStaticDataReply reply, IPayloadContext ctx) {
            final LocalPlayer player = Minecraft.getInstance().player;
            if (player.containerMenu instanceof HeartMenu)
                ((HeartMenu) player.containerMenu).handleStaticDataReply(reply.clientFactorySetup);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
