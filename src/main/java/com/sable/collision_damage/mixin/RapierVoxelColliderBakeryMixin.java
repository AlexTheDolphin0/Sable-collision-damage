package com.sable.collision_damage.mixin;

import com.sable.collision_damage.Config;
import com.sable.collision_damage.SablePreSolverDamage;
import dev.ryanhcode.sable.api.block.BlockWithSubLevelCollisionCallback;
import dev.ryanhcode.sable.api.physics.callback.BlockSubLevelCollisionCallback;
import dev.ryanhcode.sable.physics.callback.FragileBlockCallback;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "dev.ryanhcode.sable.physics.impl.rapier.collider.RapierVoxelColliderBakery")
public abstract class RapierVoxelColliderBakeryMixin {
    @Redirect(
            method = "buildPhysicsDataForBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/ryanhcode/sable/api/block/BlockWithSubLevelCollisionCallback;sable$getCallback(Lnet/minecraft/world/level/block/state/BlockState;)Ldev/ryanhcode/sable/api/physics/callback/BlockSubLevelCollisionCallback;"
            ),
            remap = false
    )
    private BlockSubLevelCollisionCallback sablecollisiondamage$wrapCollisionCallback(final BlockState state) {
        final BlockSubLevelCollisionCallback originalCallback = BlockWithSubLevelCollisionCallback.sable$getCallback(state);
        if (state.getBlock() instanceof TntBlock) {
            return originalCallback;
        }

        if (originalCallback != null && originalCallback != FragileBlockCallback.INSTANCE) {
            return originalCallback;
        }

        if (!Config.OVERRIDE_SABLE_FRAGILE_BLOCKS.get() && originalCallback == FragileBlockCallback.INSTANCE) {
            return originalCallback;
        }

        return SablePreSolverDamage.getCallbackFor(state);
    }
}
