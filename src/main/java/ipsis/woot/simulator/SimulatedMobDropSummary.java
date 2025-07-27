package ipsis.woot.simulator;

import io.netty.buffer.ByteBuf;
import ipsis.woot.fluilds.network.TankPacket;
import ipsis.woot.util.oss.NetworkTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record SimulatedMobDropSummary(ItemStack stack, List<Float> chanceToDrop) {

    public static final StreamCodec<RegistryFriendlyByteBuf, SimulatedMobDropSummary> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, SimulatedMobDropSummary::stack,
            ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.FLOAT), SimulatedMobDropSummary::chanceToDrop,
            SimulatedMobDropSummary::new
    );


    @Override
    public String toString() {
        return "SimulatedMobDropSummary{" +
                "itemStack=" + stack +
                ", chanceToDrop=" + (chanceToDrop) +
                '}';
    }
}
