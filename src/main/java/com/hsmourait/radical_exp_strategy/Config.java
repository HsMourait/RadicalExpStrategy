package com.hsmourait.radical_exp_strategy;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 模组配置类
 */
@EventBusSubscriber(modid = RadicalExpStrategy.MODID)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    /**
     * 经验吸收延迟（tick数）
     * 玩家拾取经验球后，下一次可拾取前需要等待的 tick 数
     * 原版为 2
     */
    public static final ModConfigSpec.IntValue EXPERIENCE_ABSORB_DELAY = BUILDER
            .comment(
                    "Experience absorption delay in ticks.",
                    "How many ticks the player must wait before picking up another experience orb after picking one up.",
                    "Default: 2 (vanilla), Set to 0 for instant absorption."
            )
            .defineInRange("experienceAbsorbDelay", 0, 0, Integer.MAX_VALUE);

    /**
     * 经验直连模式
     * 启用后，当实体因玩家行为掉落经验时，不生成经验球，直接给玩家加经验
     * 适用场景：玩家杀怪、玩家破坏经验相关的方块等
     */
    public static final ModConfigSpec.BooleanValue DIRECT_XP_ABSORB = BUILDER
            .comment(
                    "Direct experience absorption mode.",
                    "When enabled, experience orbs from player kills (mobs) are directly added to the player's XP bar,",
                    "without spawning an experience orb entity.",
                    "Applies to: mob kills by player."
            )
            .define("directXpAbsorb", true);

    /**
     * 激进经验合并
     * 启用后，修改经验球的合并行为：
     * - 所有经验球（不论value）都可以合并
     * - 合并后累计总经验值
     * - 单次拾取吸取全部经验
     */
    public static final ModConfigSpec.BooleanValue AGGRESSIVE_XP_MERGE = BUILDER
            .comment(
                    "Aggressive experience orb merging.",
                    "When enabled, all experience orbs can merge regardless of their value.",
                    "Merged orbs accumulate total XP, and the player gets all XP in one pickup."
            )
            .define("aggressiveXpMerge", false);

    /**
     * 最大经验碎片值
     * 控制单个经验碎片的最大经验值。
     * 原版最大为 2477。
     * 仅在激进合并启用时生效。
     */
    public static final ModConfigSpec.IntValue MAX_XP_VALUE = BUILDER
            .comment(
                    "Maximum experience value for a single orb fragment.",
                    "Controls the maximum XP a single orb fragment can hold.",
                    "Vanilla: 2477. Higher values reduce entity count."
            )
            .defineInRange("maxXpValue", 32767, 1, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();

    /**
     * 当配置文件被加载或更改时调用
     */
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        // 配置已自动重新加载，无需额外操作
        RadicalExpStrategy.LOGGER.debug("Configuration loaded: experienceAbsorbDelay = {}", EXPERIENCE_ABSORB_DELAY.get());
        RadicalExpStrategy.LOGGER.debug("Configuration loaded: aggressiveXpMerge = {}, maxXpValue = {}",
                AGGRESSIVE_XP_MERGE.get(), MAX_XP_VALUE.get());
    }
}