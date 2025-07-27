package ipsis.woot.modules.oracle.network;

import io.netty.buffer.ByteBuf;
import ipsis.woot.Woot;
import ipsis.woot.fluilds.network.TankPacket;
import ipsis.woot.modules.oracle.blocks.OracleContainer;
import ipsis.woot.simulator.MobSimulator;
import ipsis.woot.simulator.SimulatedMobDropSummary;
import ipsis.woot.util.FakeMob;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record SimulatedMobDropsSummaryReply(List<SimulatedMobDropSummary> drops) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SimulatedMobDropsSummaryReply> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Woot.MODID, "simmobdropsummaryreply"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SimulatedMobDropsSummaryReply> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, SimulatedMobDropSummary.STREAM_CODEC), SimulatedMobDropsSummaryReply::drops,
            SimulatedMobDropsSummaryReply::new
            );



    public static SimulatedMobDropsSummaryReply fromMob(String entityKey) {
        SimulatedMobDropsSummaryReply reply = new SimulatedMobDropsSummaryReply(new ArrayList<>());
        FakeMob fakeMob = new FakeMob(entityKey);
        if (fakeMob.isValid())
            reply.drops.addAll(MobSimulator.getInstance().getDropSummary(fakeMob));
        return reply;
    }

    public static void handle(SimulatedMobDropsSummaryReply packet, IPayloadContext ctx) {

            final LocalPlayer player = Minecraft.getInstance().player;
            if (player.containerMenu instanceof OracleContainer)
                ((OracleContainer) player.containerMenu).handleSimulatedMobDropsSummaryReply(packet);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
