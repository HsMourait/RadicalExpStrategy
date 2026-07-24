package com.hsmourait.radical_exp_strategy;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;

/**
 * 处理经验拦截：当实体因玩家行为掉落经验时，不生成经验球，直接给玩家加经验
 */
public class PlayerXpRedirectHandler {

    @SubscribeEvent
    public void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        // 检查是否启用直连模式
        if (!Config.DIRECT_XP_ABSORB.getAsBoolean()) {
            return;
        }

        // 获取攻击玩家
        var attackingPlayer = event.getAttackingPlayer();
        if (!(attackingPlayer instanceof ServerPlayer serverPlayer)) {
            return;
        }

        // 获取原始经验值
        int originalXp = event.getOriginalExperience();
        if (originalXp <= 0) {
            return;
        }

        // 让玩家直接获取经验
        serverPlayer.giveExperiencePoints(originalXp);

        // 阻止经验球掉落
        event.setDroppedExperience(0);
    }

    /**
     * 注册事件监听器
     */
    public static void register() {
        NeoForge.EVENT_BUS.register(new PlayerXpRedirectHandler());
    }
}