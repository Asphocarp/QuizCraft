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
            // show energy shield ring visual effect
            // 主能量盾效果 - 从小快速扩大然后缓慢消失
            AreaEffectCloudEntity shield = new AreaEffectCloudEntity(
                serverPlayer.getWorld(), self.getX(), self.getY() + 1.0, self.getZ());
            shield.setRadius(0.5f); // 起始小半径
            shield.setDuration(8); // 短持续时间
            shield.setParticleType(ParticleTypes.ELECTRIC_SPARK);
            shield.setRadiusGrowth(0.5f); // 快速扩张
            shield.setWaitTime(0); // 立即开始扩张
            shield.setColor(0x00FFFF); // 青色
            serverPlayer.getWorld().spawnEntity(shield);
            
            // 冲击波效果
            AreaEffectCloudEntity shockwave = new AreaEffectCloudEntity(
                serverPlayer.getWorld(), self.getX(), self.getY() + 1.0, self.getZ());
            shockwave.setRadius(0.1f);
            shockwave.setDuration(8);
            shockwave.setParticleType(ParticleTypes.SONIC_BOOM);
            shockwave.setRadiusGrowth(0.8f); // 非常快速扩张
            shockwave.setColor(0x3399FF); // 蓝色
            serverPlayer.getWorld().spawnEntity(shockwave);
            
            // 爆发粒子效果
            for (int i = 0; i < 40; i++) {
                double speed = 0.5 + QuizCraft.rg.nextFloat() * 0.5; // 0.5-1.0的随机速度
                double angle1 = QuizCraft.rg.nextDouble() * Math.PI * 2; // 水平角度
                double angle2 = QuizCraft.rg.nextDouble() * Math.PI; // 垂直角度
                
                // 球形分布的方向向量
                double dirX = Math.sin(angle2) * Math.cos(angle1);
                double dirY = Math.cos(angle2);
                double dirZ = Math.sin(angle2) * Math.sin(angle1);
                
                // 应用速度
                dirX *= speed;
                dirY *= speed;
                dirZ *= speed;
                
                // 交替使用不同粒子类型
                serverPlayer.getWorld().addParticle(
                    i % 2 == 0 ? ParticleTypes.END_ROD : ParticleTypes.FLASH,
                    self.getX(), self.getY() + 1.0, self.getZ(),
                    dirX, dirY, dirZ
                );
            }
            
            // 冲击波环形效果
            for (int i = 0; i < 24; i++) {
                double angle = i * (Math.PI * 2 / 24); // 均匀分布在圆周上
                double offsetX = Math.cos(angle) * 0.3;
                double offsetZ = Math.sin(angle) * 0.3;
                
                serverPlayer.getWorld().addParticle(
                    ParticleTypes.EXPLOSION,
                    self.getX() + offsetX, self.getY() + 1.0, self.getZ() + offsetZ,
                    offsetX * 1.5, 0.1, offsetZ * 1.5
                );
            }
            // TODO: hack: mannully knockback due to MC-267775
            self.takeKnockback(0.25f, serverPlayer.getX() - self.getX(), serverPlayer.getZ() - self.getZ());
            cir.setReturnValue(true);
        }
    }
} 