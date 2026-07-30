package com.hsmourait.radical_exp_strategy.mixin;

import com.hsmourait.radical_exp_strategy.Config;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * 修改经验球行为
 */
@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin {

    @Shadow
    public int value;
    @Shadow
    public int age;
    @Shadow
    public int count;

    // ===== 1. 拾取延迟（所有模式通用） =====

    @ModifyConstant(method = "playerTouch", constant = @Constant(intValue = 2))
    private int modifyXpDelay(int original) {
        return Config.EXPERIENCE_ABSORB_DELAY.get();
    }

    // ===== 2. 激进合并：canMerge 永远为 true =====

    @Inject(method = "canMerge(Lnet/minecraft/world/entity/ExperienceOrb;II)Z", at = @At("HEAD"), cancellable = true)
    private static void modifyCanMerge(ExperienceOrb orb, int amount, int other, CallbackInfoReturnable<Boolean> cir) {
        if (Config.AGGRESSIVE_XP_MERGE.getAsBoolean()) {
            cir.setReturnValue(!orb.isRemoved());
        }
    }

    // ===== 3. 激进合并：tryMergeToExisting 改用 value 累加 =====

    @Inject(method = "tryMergeToExisting", at = @At("HEAD"), cancellable = true)
    private static void modifyTryMergeToExisting(ServerLevel level, Vec3 pos, int amount, CallbackInfoReturnable<Boolean> cir) {
        if (Config.AGGRESSIVE_XP_MERGE.getAsBoolean()) {
            AABB aabb = AABB.ofSize(pos, 1.0, 1.0, 1.0);
            List<ExperienceOrb> list = level.getEntities(
                EntityTypeTest.forClass(ExperienceOrb.class), aabb, orb -> !orb.isRemoved());
            if (!list.isEmpty()) {
                ExperienceOrb orb = list.get(0);
                ((ExperienceOrbMixin) (Object) orb).value += amount;
                ((ExperienceOrbMixin) (Object) orb).count = 1;
                ((ExperienceOrbMixin) (Object) orb).age = 0;
                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(false);
            }
        }
    }

    // ===== 4. 激进合并：merge 改用 value 累加 =====

    @Inject(method = "merge", at = @At("HEAD"), cancellable = true)
    private void modifyMerge(ExperienceOrb other, CallbackInfo ci) {
        if (Config.AGGRESSIVE_XP_MERGE.getAsBoolean()) {
            ExperienceOrbMixin otherAccess = (ExperienceOrbMixin) (Object) other;
            this.value = this.value + otherAccess.value;
            this.count = 1;
            this.age = Math.min(this.age, otherAccess.age);
            other.discard();
            ci.cancel();
        }
    }

    // ===== 5. 激进合并：getExperienceValue =====

    @Inject(method = "getExperienceValue", at = @At("HEAD"), cancellable = true)
    private static void modifyGetExperienceValue(int expValue, CallbackInfoReturnable<Integer> cir) {
        if (Config.AGGRESSIVE_XP_MERGE.getAsBoolean()) {
            cir.setReturnValue(Math.min(expValue, Config.MAX_XP_VALUE.get()));
        }
    }

    // ===== 6. 激进合并：getIcon 适用于更大 value 范围 =====

    @Inject(method = "getIcon", at = @At("HEAD"), cancellable = true)
    private void modifyGetIcon(CallbackInfoReturnable<Integer> cir) {
        if (Config.AGGRESSIVE_XP_MERGE.getAsBoolean()) {
            int maxXp = Config.MAX_XP_VALUE.get();
            int icon;
            if (this.value <= 0) {
                icon = 0;
            } else {
                icon = Math.max(1, (int) ((double) this.value / maxXp * 10));
            }
            cir.setReturnValue(Math.min(icon, 10));
        }
    }
}
