package com.sable.collision_damage.particle;

import com.sable.collision_damage.Config;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;

public final class SableImpactParticles {
    private static final double SPAWN_OFFSET = 0.72D;
    private static final double SPAWN_JITTER = 0.28D;
    private static final double SPREAD_XZ = 0.70D;
    private static final double SPREAD_Y = 0.60D;
    private static final double DIR_XZ = 0.85D;
    private static final double DIR_Y = 0.75D;
    private static final double UPWARD = 0.35D;

    private SableImpactParticles() {
    }

    public static void emitImpact(final ServerLevel level, final BlockState state, final Vector3d hitPos, final double impactSpeed) {
        if (state.isAir()) {
            return;
        }

        final Vector3d normal = randomNormal(level);
        emitBlockBurst(level, state, hitPos, normal, impactSpeed);
        emitUckBurst(level, hitPos, normal, impactSpeed);
    }

    private static void emitBlockBurst(final ServerLevel level, final BlockState state, final Vector3d pos, final Vector3d normal,
                                       final double impactSpeed) {
        final double countBase = Config.BLOCK_BURST_COUNT_BASE.get();
        final double countPerSpeed = Config.BLOCK_BURST_COUNT_PER_SPEED.get();
        final int maxParticles = Math.max(1, (int) Math.floor(Config.MAX_BLOCK_PARTICLES_PER_COLLISION.get()));
        final int count = (int) Math.floor(Math.max(0.0D, Math.min(maxParticles, countBase + impactSpeed * countPerSpeed)));
        if (count <= 0) {
            return;
        }
        SableCollisionNetwork.sendBlockDebrisBurst(level, pos.x, pos.y, pos.z, state, normal, impactSpeed);
    }

    private static void emitUckBurst(final ServerLevel level, final Vector3d pos, final Vector3d normal, final double impactSpeed) {
        final int count = computeUckCount(impactSpeed);
        if (count <= 0) {
            return;
        }

        final double speed = Config.UCK_SPEED.get();
        if (level.random.nextDouble() <= 0.90D) {
            emitDirected(level, SableCollisionParticles.UCK_1.get(), pos, normal, speed, count);
        }
        if (level.random.nextDouble() <= 0.85D) {
            emitDirected(level, SableCollisionParticles.UCK_2.get(), pos, normal, speed, count);
        }
        if (level.random.nextDouble() <= 0.80D) {
            emitDirected(level, SableCollisionParticles.UCK_3.get(), pos, normal, speed, count);
        }
        if (level.random.nextDouble() <= 0.65D) {
            emitDirected(level, SableCollisionParticles.UCK_4.get(), pos, normal, speed, count);
        }
    }

    private static int computeUckCount(final double impactSpeed) {
        final double base = Config.UCK_COUNT_BASE.get();
        final double perSpeed = Config.UCK_COUNT_PER_SPEED.get();
        final int maxParticles = Math.max(1, (int) Math.floor(Config.MAX_UCK_PARTICLES_PER_COLLISION.get()));
        final int raw = Math.max(0, (int) Math.floor(base + impactSpeed * perSpeed));
        return Math.min(raw, maxParticles);
    }

    private static void emitDirected(final ServerLevel level, final ParticleOptions type, final Vector3d pos, final Vector3d normal,
                                     final double speed, final int count) {
        for (int i = 0; i < count; i++) {
            final double spreadX = (level.random.nextDouble() - 0.5D) * 0.22D;
            final double spreadY = (level.random.nextDouble() - 0.5D) * 0.22D;
            final double spreadZ = (level.random.nextDouble() - 0.5D) * 0.22D;
            final double vx = normal.x * 0.16D + spreadX;
            final double vy = normal.y * 0.16D + spreadY + 0.16D;
            final double vz = normal.z * 0.16D + spreadZ;

            final double sx = pos.x + normal.x * 0.95D + (level.random.nextDouble() - 0.5D) * 0.26D;
            final double sy = pos.y + normal.y * 0.95D + (level.random.nextDouble() - 0.5D) * 0.26D;
            final double sz = pos.z + normal.z * 0.95D + (level.random.nextDouble() - 0.5D) * 0.26D;

            sendDirected(level, type, sx, sy, sz, vx * speed, vy * speed, vz * speed);
        }
    }

    private static Vector3d randomNormal(final ServerLevel level) {
        final Vector3d normal = new Vector3d(
                level.random.nextDouble() - 0.5D,
                level.random.nextDouble() * 0.6D + 0.2D,
                level.random.nextDouble() - 0.5D
        );
        if (normal.lengthSquared() < 1.0E-8D) {
            normal.set(0.0D, 1.0D, 0.0D);
        } else {
            normal.normalize();
        }
        return normal;
    }

    private static void sendDirected(final ServerLevel level, final ParticleOptions particle, final double x, final double y, final double z,
                                     final double vx, final double vy, final double vz) {
        level.sendParticles(particle, x, y, z, 0, vx, vy, vz, 1.0D);
    }
}
