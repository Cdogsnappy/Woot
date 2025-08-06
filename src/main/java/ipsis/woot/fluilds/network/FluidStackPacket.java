package ipsis.woot.fluilds.network;

import ipsis.woot.modules.oracle.blocks.OracleContainer;
import ipsis.woot.util.FluidStackPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Server -> Client
 */
public record FluidStackPacket(List<FluidStack> fluidStackList) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidStackPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, FluidStack.STREAM_CODEC), FluidStackPacket::fluidStackList,
            FluidStackPacket::new);



    public static void handle(FluidStackPacket packet, IPayloadContext ctx) {
            final LocalPlayer player = Minecraft.getInstance().player;
            if (player.containerMenu instanceof FluidStackPacketHandler)
                ((FluidStackPacketHandler) player.containerMenu).handlePacket(packet);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return null;
    }
}
