package com.hsmourait.radical_exp_strategy.mixin;

import com.hsmourait.radical_exp_strategy.Config;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修改经验球行为
 */
@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin {

    @Shadow
    public int value;

    // ===== 1. 拾取延迟（所有模式通用） =====

    @ModifyConstant(method = "playerTouch", constant = @Constant(intValue = 2))
    private int modifyXpDelay(int original) {
        return Config.EXPERIENCE_ABSORB_DELAY.get();
    }

    // ===== 3. 激进合并：移除 value 匹配和分组限制 =====

    @Inject(method = "canMerge(Lnet/minecraft/world/entity/ExperienceOrb;II)Z", at = @At("HEAD"), cancellable = true)
    private static void modifyCanMerge(ExperienceOrb orb, int amount, int other, CallbackInfoReturnable<Boolean> cir) {
        if (Config.AGGRESSIVE_XP_MERGE.getAsBoolean()) {
            cir.setReturnValue(!orb.isRemoved());
        }
    }

    // ===== 4. 激进合并：跳过 tryMergeToExisting =====

    @Inject(method = "tryMergeToExisting", at = @At("HEAD"), cancellable = true)
    private static void modifyTryMergeToExisting(ServerLevel level, Vec3 pos, int amount, CallbackInfoReturnable<Boolean> cir) {
        if (Config.AGGRESSIVE_XP_MERGE.getAsBoolean()) {
            cir.setReturnValue(false);
        }
    }

    // ===== 5. 激进合并：merge 时累计总价值 =====

    @Inject(method = "merge", at = @At("HEAD"))
    private void modifyMerge(ExperienceOrb orb, CallbackInfo ci) {
        if (Config.AGGRESSIVE_XP_MERGE.getAsBoolean()) {
            ExperienceOrb self = (ExperienceOrb) (Object) this;
            self.value += orb.value;
        }
    }

    // ===== 6. 激进合并：getExperienceValue =====

    @Inject(method = "getExperienceValue", at = @At("HEAD"), cancellable = true)
    private static void modifyGetExperienceValue(int expValue, CallbackInfoReturnable<Integer> cir) {
        if (Config.AGGRESSIVE_XP_MERGE.getAsBoolean()) {
            cir.setReturnValue(Math.min(expValue, Config.MAX_XP_VALUE.get()));
        }
    }

    // ===== 7. 激进合并：getIcon =====

    @Inject(method = "getIcon", at = @At("HEAD"), cancellable = true)
    private void modifyGetIcon(CallbackInfoReturnable<Integer> cir) {
        if (Config.AGGRESSIVE_XP_MERGE.getAsBoolean()) {
            int maxXp = Config.MAX_XP_VALUE.get();
            int icon = (int) ((double) this.value / maxXp * 10);
            cir.setReturnValue(Math.min(icon, 10));
        }
    }
}