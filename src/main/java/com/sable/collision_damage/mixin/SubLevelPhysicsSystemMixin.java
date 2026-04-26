package com.sable.collision_damage.mixin;

import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(SubLevelPhysicsSystem.class)
public abstract class SubLevelPhysicsSystemMixin {
    @Shadow @Final private PhysicsPipeline pipeline;

    @Unique
    private final Set<ServerSubLevel> sablecollisiondamage$pendingPipelineRemovals = new ObjectOpenHashSet<>();

    @Unique
    private boolean sablecollisiondamage$physicsTickActive;

    @Inject(method = "tickPipelinePhysics", at = @At("HEAD"))
    private void sablecollisiondamage$beginDeferredRemovalWindow(final CallbackInfo ci) {
        this.sablecollisiondamage$physicsTickActive = true;
    }

    @Inject(method = "tickPipelinePhysics", at = @At("TAIL"))
    private void sablecollisiondamage$flushDeferredRemovals(final CallbackInfo ci) {
        this.sablecollisiondamage$physicsTickActive = false;

        if (this.sablecollisiondamage$pendingPipelineRemovals.isEmpty()) {
            return;
        }

        for (final ServerSubLevel subLevel : this.sablecollisiondamage$pendingPipelineRemovals) {
            this.pipeline.remove(subLevel);
        }

        this.sablecollisiondamage$pendingPipelineRemovals.clear();
    }

    @Inject(method = "onSubLevelRemoved", at = @At("HEAD"), cancellable = true)
    private void sablecollisiondamage$deferRemovalDuringPhysicsTick(final SubLevel subLevel, final SubLevelRemovalReason reason, final CallbackInfo ci) {
        if (!this.sablecollisiondamage$physicsTickActive || !(subLevel instanceof final ServerSubLevel serverSubLevel)) {
            return;
        }

        this.sablecollisiondamage$pendingPipelineRemovals.add(serverSubLevel);
        ci.cancel();
    }
}
