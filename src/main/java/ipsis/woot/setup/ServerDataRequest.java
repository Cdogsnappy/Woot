package ipsis.woot.setup;

import io.netty.buffer.ByteBuf;
import ipsis.woot.Woot;
import ipsis.woot.modules.factory.blocks.HeartBlockEntity;
import ipsis.woot.modules.oracle.blocks.OracleTileEntity;
import ipsis.woot.modules.oracle.network.SimulatedMobDropsSummaryReply;
import ipsis.woot.modules.oracle.network.SimulatedMobsReply;
import ipsis.woot.util.oss.NetworkTools;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * Client request for information
 */
public record ServerDataRequest(String s, BlockPos pos, int requestType) implements CustomPacketPayload{


    public static final StreamCodec<RegistryFriendlyByteBuf, ServerDataRequest> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ServerDataRequest::s,
            BlockPos.STREAM_CODEC, ServerDataRequest::pos,
            ByteBufCodecs.VAR_INT, ServerDataRequest::requestType,
            ServerDataRequest::new
    );

    public static final CustomPacketPayload.Type<ServerDataRequest> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Woot.MODID, "datarequest"));

    public static void handle(ServerDataRequest request, IPayloadContext ctx) {
        final Player serverPlayerEntity = ctx.player();
        ctx.enqueueWork(() -> {
            BlockEntity te = serverPlayerEntity.level().getBlockEntity(request.pos);
            switch(Type.fromIndex(request.requestType)){
                case DROP_REGISTRY_STATUS:
                    if (te instanceof OracleTileEntity) {
                        PacketDistributor.sendToPlayer((ServerPlayer)serverPlayerEntity, new SimulatedMobsReply(new ArrayList<>()));
                        break;
                    }
                case SIMULATED_MOB_DROPS:
                    if (te instanceof OracleTileEntity) {
                        PacketDistributor.sendToPlayer((ServerPlayer)serverPlayerEntity,  SimulatedMobDropsSummaryReply.fromMob(request.s));
                        break;
                    }
                case HEART_STATIC_DATA:
                    if (te instanceof HeartBlockEntity) {
                        PacketDistributor.sendToPlayer((ServerPlayer)serverPlayerEntity, (((HeartBlockEntity) te).createStaticDataReply2()));
                        break;
                    }
            }

        });
    }

    @Override
    public String toString() {
        return requestType + " " + pos.toString() + "(" + s + ")";
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Type {
        HEART_STATIC_DATA,
        DROP_REGISTRY_STATUS,
        SIMULATED_MOB_DROPS;

        static Type[] VALUES = values();
        public static Type fromIndex(int index) {
            index = Math.clamp(index, 0, VALUES.length -1);
            return VALUES[index];
        }
    }
}
