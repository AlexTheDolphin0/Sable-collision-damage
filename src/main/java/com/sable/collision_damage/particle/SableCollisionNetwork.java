package com.sable.collision_damage.particle;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.joml.Vector3d;

import java.lang.reflect.Method;

public final class SableCollisionNetwork {
    private static final double SEND_RADIUS = 64.0D;

    private SableCollisionNetwork() {
    }

    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .playToClient(BlockDebrisPayload.TYPE, BlockDebrisPayload.STREAM_CODEC, SableCollisionNetwork::handleBlockDebris);
    }

    public static void sendBlockDebrisBurst(final ServerLevel level, final double x, final double y, final double z,
                                            final BlockState state, final Vector3d normal, final double impactSpeed) {
        final BlockDebrisPayload payload = new BlockDebrisPayload(
                x, y, z,
                Block.getId(state),
                normal.x, normal.y, normal.z,
                impactSpeed
        );
        PacketDistributor.sendToPlayersNear(level, null, x, y, z, SEND_RADIUS, payload);
    }

    private static void handleBlockDebris(final BlockDebrisPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!FMLEnvironment.dist.isClient()) {
                return;
            }
            try {
                final Class<?> handlerClass = Class.forName("com.sable.collision_damage.client.SableCollisionClientParticles");
                final Method handler = handlerClass.getMethod("handle", BlockDebrisPayload.class);
                handler.invoke(null, payload);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to spawn Sable block debris particles on the client", e);
            }
        });
    }

    public record BlockDebrisPayload(
            double x,
            double y,
            double z,
            int stateId,
            double normalX,
            double normalY,
            double normalZ,
            double impactSpeed
    ) implements CustomPacketPayload {
        public static final Type<BlockDebrisPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("sablecollisiondamage", "block_debris"));
        public static final StreamCodec<FriendlyByteBuf, BlockDebrisPayload> STREAM_CODEC = StreamCodec.of(BlockDebrisPayload::encode, BlockDebrisPayload::decode);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        private static void encode(final FriendlyByteBuf buf, final BlockDebrisPayload payload) {
            buf.writeDouble(payload.x);
            buf.writeDouble(payload.y);
            buf.writeDouble(payload.z);
            buf.writeVarInt(payload.stateId);
            buf.writeDouble(payload.normalX);
            buf.writeDouble(payload.normalY);
            buf.writeDouble(payload.normalZ);
            buf.writeDouble(payload.impactSpeed);
        }

        private static BlockDebrisPayload decode(final FriendlyByteBuf buf) {
            return new BlockDebrisPayload(
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readVarInt(),
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble()
            );
        }
    }
}
