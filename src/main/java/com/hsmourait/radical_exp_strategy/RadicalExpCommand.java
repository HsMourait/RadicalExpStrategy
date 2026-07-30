package com.hsmourait.radical_exp_strategy;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * 游戏内指令：/radicalexp
 * 允许 OP 玩家在运行时修改模组配置，无需编辑 toml 文件。
 * 配置修改立即生效并保存到文件。
 */
public class RadicalExpCommand {

    private static final String PREFIX = "§a[RadicalExp]§r ";

    /**
     * 注册指令事件
     */
    public static void register() {
        NeoForge.EVENT_BUS.register(new RadicalExpCommand());
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("radicalexp")
                        .requires(source -> source.hasPermission(2)) // 仅 OP
                        .executes(ctx -> {
                            // 无参数：显示当前配置
                            showCurrentConfig(ctx.getSource());
                            return Command.SINGLE_SUCCESS;
                        })
                        // 子命令：directXpAbsorb
                        .then(boolSubCommand("directXpAbsorb",
                                "Direct XP Absorption",
                                Config.DIRECT_XP_ABSORB))
                        // 子命令：aggressiveXpMerge
                        .then(boolSubCommand("aggressiveXpMerge",
                                "Aggressive XP Merge",
                                Config.AGGRESSIVE_XP_MERGE))
                        // 子命令：experienceAbsorbDelay
                        .then(Commands.literal("experienceAbsorbDelay")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                        .executes(ctx -> {
                                            int value = IntegerArgumentType.getInteger(ctx, "value");
                                            Config.EXPERIENCE_ABSORB_DELAY.set(value);
                                            Config.EXPERIENCE_ABSORB_DELAY.save();
                                            ctx.getSource().sendSuccess(
                                                    () -> Component.literal(PREFIX
                                                            + "§7experienceAbsorbDelay§r 已设为 §e" + value + "§r ticks"),
                                                    true);
                                            return Command.SINGLE_SUCCESS;
                                        }))
                                .executes(ctx -> {
                                    ctx.getSource().sendSuccess(
                                            () -> Component.literal(PREFIX
                                                    + "§7experienceAbsorbDelay§r = §e"
                                                    + Config.EXPERIENCE_ABSORB_DELAY.get() + "§r ticks"),
                                            false);
                                    return Command.SINGLE_SUCCESS;
                                }))
                        // 子命令：maxXpValue
                        .then(Commands.literal("maxXpValue")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("value", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                                        .executes(ctx -> {
                                            int value = IntegerArgumentType.getInteger(ctx, "value");
                                            Config.MAX_XP_VALUE.set(value);
                                            Config.MAX_XP_VALUE.save();
                                            ctx.getSource().sendSuccess(
                                                    () -> Component.literal(PREFIX
                                                            + "§7maxXpValue§r 已设为 §e" + value),
                                                    true);
                                            return Command.SINGLE_SUCCESS;
                                        }))
                                .executes(ctx -> {
                                    ctx.getSource().sendSuccess(
                                            () -> Component.literal(PREFIX
                                                    + "§7maxXpValue§r = §e"
                                                    + Config.MAX_XP_VALUE.get()),
                                            false);
                                    return Command.SINGLE_SUCCESS;
                                }))
        );
    }

    /**
     * 构造一个布尔类型的子命令
     */
    private static LiteralArgumentBuilder<CommandSourceStack> boolSubCommand(
            String name, String displayName, net.neoforged.neoforge.common.ModConfigSpec.BooleanValue config) {
        return Commands.literal(name)
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("true")
                        .executes(ctx -> {
                            config.set(true);
                            config.save();
                            ctx.getSource().sendSuccess(
                                    () -> Component.literal(PREFIX
                                            + "§7" + displayName + "§r 已启用"),
                                    true);
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("false")
                        .executes(ctx -> {
                            config.set(false);
                            config.save();
                            ctx.getSource().sendSuccess(
                                    () -> Component.literal(PREFIX
                                            + "§7" + displayName + "§r 已禁用"),
                                    true);
                            return Command.SINGLE_SUCCESS;
                        }))
                .executes(ctx -> {
                    ctx.getSource().sendSuccess(
                            () -> Component.literal(PREFIX
                                    + "§7" + displayName + "§r = §e"
                                    + (config.get() ? "true (已启用)" : "false (已禁用)")),
                            false);
                    return Command.SINGLE_SUCCESS;
                });
    }

    /**
     * 显示所有当前配置值
     */
    private static void showCurrentConfig(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("§6=== RadicalExpStrategy 当前配置 ==="), false);
        source.sendSuccess(() -> Component.literal(
                "§7experienceAbsorbDelay§r = §e" + Config.EXPERIENCE_ABSORB_DELAY.get() + "§r ticks"), false);
        source.sendSuccess(() -> Component.literal(
                "§7directXpAbsorb§r = §e" + Config.DIRECT_XP_ABSORB.get()), false);
        source.sendSuccess(() -> Component.literal(
                "§7aggressiveXpMerge§r = §e" + Config.AGGRESSIVE_XP_MERGE.get()), false);
        source.sendSuccess(() -> Component.literal(
                "§7maxXpValue§r = §e" + Config.MAX_XP_VALUE.get()), false);
        source.sendSuccess(() -> Component.literal(
                "§7用法§r: §o/radicalexp <选项名> <值>"), false);
    }
}