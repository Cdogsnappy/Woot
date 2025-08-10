package ipsis.woot.setup;

import ipsis.woot.Woot;
import ipsis.woot.fluilds.network.FluidStackPacket;
import ipsis.woot.fluilds.network.TankPacket;
import ipsis.woot.modules.factory.network.HeartStaticDataReply;
import ipsis.woot.modules.oracle.network.SimulatedMobDropsSummaryReply;
import ipsis.woot.modules.oracle.network.SimulatedMobsReply;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.registration.PayloadRegistration;

import java.util.Objects;

@Mod(Woot.MODID)
@EventBusSubscriber(modid=Woot.MODID)
public class NetworkChannel {

    private static ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(Woot.MODID, "net");
    static PayloadRegistrar payloadRegistrar;

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        payloadRegistrar = event.registrar(Woot.MODID);

        payloadRegistrar
                .playToClient(TankPacket.TYPE, TankPacket.STREAM_CODEC, TankPacket::handle)
                .playToClient(SimulatedMobDropsSummaryReply.TYPE, SimulatedMobDropsSummaryReply.STREAM_CODEC, SimulatedMobDropsSummaryReply::handle)
                .playToClient(SimulatedMobsReply.TYPE, SimulatedMobsReply.STREAM_CODEC, SimulatedMobsReply::handle)
                .playToServer(ServerDataRequest.TYPE, ServerDataRequest.STREAM_CODEC, ServerDataRequest::handle)
                .playToClient(HeartStaticDataReply.TYPE, HeartStaticDataReply.STREAM_CODEC, HeartStaticDataReply::handle);

    }
}
