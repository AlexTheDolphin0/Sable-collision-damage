package com.sable.collision_damage.mixin;

import dev.ryanhcode.sable.physics.callback.FragileBlockCallback;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FragileBlockCallback.class)
public abstract class FragileBlockCallbackMixin {
    @Redirect(
            method = "sable$onCollision",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"
            )
    )
    private Comparable<?> sablecollisiondamage$ignorePersistentLeavesGuard(final BlockState state, final Property<?> property) {
        if ("persistent".equals(property.getName())) {
            return Boolean.FALSE;
        }

        return state.getValue((Property) property);
    }
}
