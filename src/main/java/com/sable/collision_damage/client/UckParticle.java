package com.sable.collision_damage.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public final class UckParticle extends TextureSheetParticle {
    private UckParticle(final ClientLevel level, final double x, final double y, final double z,
                        final double vx, final double vy, final double vz, final SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz);

        this.hasPhysics = false;
        this.friction = 0.985F;
        this.gravity = 0.68F;
        this.quadSize *= 0.9F + this.random.nextFloat() * 0.35F;
        this.lifetime = 24 + this.random.nextInt(20);
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.removed) {
            this.setAlpha(1.0F - (this.age / (float) this.lifetime));
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(final SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(final SimpleParticleType type, final ClientLevel level, final double x, final double y, final double z,
                                       final double vx, final double vy, final double vz) {
            return new UckParticle(level, x, y, z, vx, vy, vz, this.sprites);
        }
    }
}
