package ipsis.woot.util;

import io.netty.buffer.ByteBuf;
import ipsis.woot.modules.factory.client.ClientFactorySetup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.checkerframework.checker.units.qual.K;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


public class ExtraWootCodecs {


    public static final StreamCodec<RegistryFriendlyByteBuf, List<ItemStack>> ITEM_LIST_CODEC = ByteBufCodecs.collection(
            ArrayList::new, ItemStack.STREAM_CODEC);


    public static final StreamCodec<RegistryFriendlyByteBuf, List<FluidStack>> FLUID_LIST_CODEC = ByteBufCodecs.collection(
            ArrayList::new, FluidStack.STREAM_CODEC);


    public static <T> StreamCodec<RegistryFriendlyByteBuf, List<T>> listStreamCodec(StreamCodec<? super RegistryFriendlyByteBuf, T> datCodec){
        return ByteBufCodecs.collection(ArrayList::new, datCodec);
    }


    public static <T, S, M extends Map<T,S>> StreamCodec<RegistryFriendlyByteBuf, M> mapStreamCodec(StreamCodec<? super RegistryFriendlyByteBuf, T> keyCodec,
                                                                                       StreamCodec<? super RegistryFriendlyByteBuf, S> valueCodec, Supplier<M> mapSupplier){
        return StreamCodec.of(
                (buf, map) -> {
                    buf.writeVarInt(map.size());
                    map.forEach((key, value) -> {
                        keyCodec.encode(buf, key);
                        valueCodec.encode(buf, value);
                    });
                },
                (buf) -> {
                    int size = buf.readVarInt();
                    M map = mapSupplier.get();
                    for (int i = 0; i < size; i++) {
                        T key = keyCodec.decode(buf);
                        S value = valueCodec.decode(buf);
                        map.put(key, value);
                    }
                    return map;
                }
        );

    }

    public static <E extends Enum<E>> StreamCodec<RegistryFriendlyByteBuf, E> enumStreamCodec(Class<E> enumClass){
        return StreamCodec.of((buf, enumValue) -> buf.writeVarInt(enumValue.ordinal()),
                (buf) -> enumClass.getEnumConstants()[buf.readVarInt()]
        );
    }

}
