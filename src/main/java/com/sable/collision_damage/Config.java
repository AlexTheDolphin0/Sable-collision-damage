package com.sable.collision_damage;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue MIN_EXPLOSION_SPEED = BUILDER
            .defineInRange("minExplosionSpeed", 15.0D, 0.0D, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue EXPLOSION_COEFFICIENT = BUILDER
            .defineInRange("explosionCoefficient", 0.05D, 0.0D, Double.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue INFLAME_FLAMMABLE = BUILDER
            .define("inflameFlammable", true);

    public static final ModConfigSpec.BooleanValue OVERRIDE_SABLE_FRAGILE_BLOCKS = BUILDER
            .comment("Если выключенно, хрупкие блоки sable обрабатываются в этом аддоне ")
            .define("overrideSableFragileBlocks", false);

    public static final ModConfigSpec.DoubleValue MIN_BREAK_SPEED = BUILDER
            .comment("Минимальная скорость")
            .defineInRange("minBreakSpeed", 15.0D, 0.0D, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue STATIC_SLOWDOWN_PER_BLOCK = BUILDER
            .comment("\n")
            .comment("статическое замедление от разрушения блоков")
            .defineInRange("staticSlowdownPerBlock", 3.0D, 0.0D, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue BLOCK_BURST_COUNT_BASE = BUILDER
            .comment("\n")
            .comment("блок партиклов: базовое количество")
            .defineInRange("blockBurstCountBase", 4.0D, 0.0D, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue BLOCK_BURST_COUNT_PER_SPEED = BUILDER
            .comment("блок партиклов: добавка количества на 1 м/с")
            .defineInRange("blockBurstCountPerSpeed", 0.1D, 0.0D, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue BLOCK_BURST_SPEED_BASE = BUILDER
            .comment("блок партиклов: базовая скорость разлета")
            .defineInRange("blockBurstSpeedBase", 0.4D, 0.0D, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue BLOCK_BURST_SPEED_PER_SPEED = BUILDER
            .comment("блок партиклов: добавка скорости на 1 м/с")
            .defineInRange("blockBurstSpeedPerSpeed", 0.01D, 0.0D, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue MAX_BLOCK_PARTICLES_PER_COLLISION = BUILDER
            .comment("Макс. блок партиклов за одно столкновение")
            .defineInRange("maxBlockParticlesPerCollision", 200.0D, 1.0D, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue UCK_COUNT_BASE = BUILDER
            .comment("\n")
            .comment("искры: базовое количество")
            .defineInRange("uckCountBase", 1.0D, 0.0D, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue UCK_COUNT_PER_SPEED = BUILDER
            .comment("искры: добавка количества на 1 м/с")
            .defineInRange("uckCountPerSpeed", 0.04D, 0.0D, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue UCK_SPEED = BUILDER
            .comment("искры: статическая скорость разлета")
            .defineInRange("uckSpeed", 0.23D, 0.0D, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue MAX_UCK_PARTICLES_PER_COLLISION = BUILDER
            .comment("Макс. искр за один удар")
            .defineInRange("maxUckParticlesPerCollision", 200.0D, 1.0D, Double.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }
}
