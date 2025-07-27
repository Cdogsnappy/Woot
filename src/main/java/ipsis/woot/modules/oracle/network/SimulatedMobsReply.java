package ipsis.woot.modules.oracle.network;

import io.netty.buffer.ByteBuf;
import ipsis.woot.Woot;
import ipsis.woot.modules.oracle.blocks.OracleContainer;
import ipsis.woot.simulator.MobSimulator;
import ipsis.woot.util.FakeMob;
import ipsis.woot.util.oss.NetworkTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public record SimulatedMobsReply(List<FakeMob> simulatedMobs) implements CustomPacketPayload {

    public static StreamCodec<RegistryFriendlyByteBuf, SimulatedMobsReply> STREAM_CODEC = StreamCodec
            .composite(ByteBufCodecs.collection(ArrayList::new, FakeMob.STREAM_CODEC),
                    SimulatedMobsReply::simulatedMobs, SimulatedMobsReply::new);

    public static Type<SimulatedMobsReply> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Woot.MODID, "simmobsreply"));

    public static void handle(SimulatedMobsReply reply, IPayloadContext ctx) {
            final LocalPlayer player = Minecraft.getInstance().player;
            if (player.containerMenu instanceof OracleContainer)
                ((OracleContainer) player.containerMenu).handleSimulatedMobsReply(reply);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
