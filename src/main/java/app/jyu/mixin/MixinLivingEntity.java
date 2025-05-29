package app.jyu.mixin;

import app.jyu.QuizCraft;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
    // @Redirect(
    //   method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z",
    //   at = @At(
    //     value  = "INVOKE",
    //     target = "Lnet/minecraft/entity/LivingEntity;blockedByShield(Lnet/minecraft/entity/damage/DamageSource;)Z"
    //   )
    // )
    // public boolean redirectBlockedByShield(LivingEntity self, DamageSource source) {
    //     return QuizCraft.redirectBlockedByShield(self, source);
    // }

    @Inject(
      method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z",
      at = @At(
        value  = "INVOKE",
        target = "Lnet/minecraft/entity/LivingEntity;blockedByShield(Lnet/minecraft/entity/damage/DamageSource;)Z",
        shift = At.Shift.AFTER
      ),
      cancellable = true
    )
    public void onInvokingBlockedByShieldInDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity)(Object)this;
        QuizCraft.onDamageStart(self, source, amount, cir);
        // monkey patch: cancel the subsequent damage() procedure if blocked by shield
        if (QuizCraft.redirectBlockedByShield(self, source) && source.getSource() instanceof ServerPlayerEntity serverPlayer) {
            // simulate hit on shield: play sound shield_block1-5; knockback
            serverPlayer.playSound(SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.BLOCKS, 0.75f, 1f);
            // TODO show energy shield ring visual effect
            // START --- Visual Effect --- START
            if (self.getWorld() instanceof ServerWorld serverWorld) {
                double radius = 0.75; // Radius of the sphere
                int particleCount = 40; // Number of particles to spawn
                for (int i = 0; i < particleCount; i++) {
                    // Generate random points on the surface of a sphere
                    double u = Math.random();
                    double v = Math.random();
                    double theta = 2 * Math.PI * u;
                    double phi = Math.acos(2 * v - 1);
                    double xOffset = radius * Math.sin(phi) * Math.cos(theta);
                    double yOffset = radius * Math.sin(phi) * Math.sin(theta);
                    double zOffset = radius * Math.cos(phi);
                    // Particle position
                    double particleX = self.getX() + xOffset;
                    double particleY = self.getBodyY(0.5) + yOffset; // Centered on the entity's body
                    double particleZ = self.getZ() + zOffset;
                    // Spawn the particle
                    serverWorld.spawnParticles(ParticleTypes.CRIT, // Changed to FLASH for a quick effect
                                               particleX, particleY, particleZ,
                                               1, // count
                                               0.0, 0.0, 0.0, // dx, dy, dz (velocity/spread)
                                               0.0); // speed
                }
            }
            // END --- Visual Effect --- END
            // TODO: hack here: mannully knockback due to MC-267775
            self.takeKnockback(0.25f, serverPlayer.getX() - self.getX(), serverPlayer.getZ() - self.getZ());
            cir.setReturnValue(true);
        }
    }
} 