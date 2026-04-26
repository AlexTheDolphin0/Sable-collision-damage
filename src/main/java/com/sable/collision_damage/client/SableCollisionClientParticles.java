package com.sable.collision_damage.client;

import com.sable.collision_damage.Config;
import com.sable.collision_damage.particle.SableCollisionNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;

public final class SableCollisionClientParticles {
    private static final double SPAWN_OFFSET = 0.72D;
    private static final double SPAWN_JITTER = 0.28D;
    private static final double SPREAD_XZ = 0.70D;
    private static final double SPREAD_Y = 0.60D;
    private static final double DIR_XZ = 0.85D;
    private static final double DIR_Y = 0.75D;
    private static final double UPWARD = 0.35D;

    private SableCollisionClientParticles() {
    }

    public static void handle(final SableCollisionNetwork.BlockDebrisPayload payload) {
        final Minecraft minecraft = Minecraft.getInstance();
        final ClientLevel level = minecraft.level;
        if (level == null) {
            return;
        }

        final BlockState state = Block.stateById(payload.stateId());
        if (state.isAir()) {
            return;
        }

        final Vector3d normal = normalizeOrUp(payload.normalX(), payload.normalY(), payload.normalZ());
        final double countBase = Config.BLOCK_BURST_COUNT_BASE.get();
        final double countPerSpeed = Config.BLOCK_BURST_COUNT_PER_SPEED.get();
        final int maxParticles = Math.max(1, (int) Math.floor(Config.MAX_BLOCK_PARTICLES_PER_COLLISION.get()));
        final int blockCount = (int) Math.floor(Math.max(0.0D, Math.min(maxParticles, countBase + payload.impactSpeed() * countPerSpeed)));
        final double speedBase = Config.BLOCK_BURST_SPEED_BASE.get();
        final double speedPerSpeed = Config.BLOCK_BURST_SPEED_PER_SPEED.get();
        final double speed = Math.max(0.0D, speedBase + payload.impactSpeed() * speedPerSpeed);

        for (int i = 0; i < blockCount; i++) {
            final double spreadX = (level.random.nextDouble() - 0.5D) * SPREAD_XZ;
            final double spreadY = (level.random.nextDouble() - 0.5D) * SPREAD_Y;
            final double spreadZ = (level.random.nextDouble() - 0.5D) * SPREAD_XZ;

            final double vx = (normal.x * DIR_XZ + spreadX) * speed;
            final double vy = (normal.y * DIR_Y + spreadY + UPWARD) * speed;
            final double vz = (normal.z * DIR_XZ + spreadZ) * speed;

            final double sx = payload.x() + normal.x * SPAWN_OFFSET + (level.random.nextDouble() - 0.5D) * SPAWN_JITTER;
            final double sy = payload.y() + normal.y * SPAWN_OFFSET + (level.random.nextDouble() - 0.5D) * SPAWN_JITTER;
            final double sz = payload.z() + normal.z * SPAWN_OFFSET + (level.random.nextDouble() - 0.5D) * SPAWN_JITTER;

            final ImpactTerrainParticle particle = new ImpactTerrainParticle(level, sx, sy, sz, state);
            particle.setParticleSpeed(vx, vy, vz);
            minecraft.particleEngine.add(particle);
        }
    }

    private static Vector3d normalizeOrUp(final double x, final double y, final double z) {
        final Vector3d normal = new Vector3d(x, y, z);
        if (normal.lengthSquared() < 1.0E-8D) {
            normal.set(0.0D, 1.0D, 0.0D);
        } else {
            normal.normalize();
        }
        return normal;
    }

    private static final class ImpactTerrainParticle extends TerrainParticle {
        private ImpactTerrainParticle(final ClientLevel level, final double x, final double y, final double z, final BlockState state) {
            super(level, x, y, z, 0.0D, 0.0D, 0.0D, state);
            this.hasPhysics = true;
            this.gravity = 0.68F;
            this.friction = 0.96F;
            this.speedUpWhenYMotionIsBlocked = true;
            this.setLifetime(42 + this.random.nextInt(30));
            this.scale(1.05F + this.random.nextFloat() * 0.35F);
        }
    }
}
