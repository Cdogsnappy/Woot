package ipsis.woot.fluilds.network;

import ipsis.woot.Woot;
import ipsis.woot.util.TankPacketHandler;
import net.minecraft.client.Minecraft;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Supplier;

/**
 * Server -> Client
 */
public record TankPacket(FluidStack fluidStack, int tankId) implements CustomPacketPayload {

    public static final Type<TankPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Woot.MODID, "tankpacket"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TankPacket> STREAM_CODEC = StreamCodec.of(
            (buf, pac) -> {
                if(pac.fluidStack.isEmpty()) {
                    buf.writeVarInt(0);
                }
                else{
                    buf.writeVarInt(1);
                    FluidStack.STREAM_CODEC.encode(buf, pac.fluidStack);
                }
                buf.writeVarInt(pac.tankId);

            },
            (buf)
                    ->  new TankPacket(buf.readVarInt() == 0 ? FluidStack.EMPTY : FluidStack.STREAM_CODEC.decode(buf), buf.readVarInt())


    );


    public static void handle(TankPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() ->  {
            final LocalPlayer player = Minecraft.getInstance().player;
            if (player.containerMenu instanceof TankPacketHandler)
                ((TankPacketHandler) player.containerMenu).handlePacket(packet);
        });
    }


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
