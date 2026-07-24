# Radical Exp Strategy — 激进经验策略

![NeoForge](https://img.shields.io/badge/NeoForge-21.1.235-green?style=flat-square)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-blue?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)

一个可自定义的经验机制模组。  

---

## 功能特性

### 1. 经验吸收间隔（拾取冷却）
配置玩家拾取一个经验球后，需要等待多少个 tick 才能拾取下一个。

- **原版默认：** 2 ticks
- **可配置范围：** 0 ~ `Integer.MAX_VALUE`

### 2. 经验直连模式
**玩家击杀生物**所掉落的经验将**直接给予玩家**，而**不会生成经验球实体**。

- 无需再跑来跑去捡拾散落的经验球。
- 战斗后立即获得经验，干净利落。
- 如需原版经验球行为，关闭此选项即可。

### 3. 激进经验合并
启用后，**所有**经验球都可以相互合并，不受单个价值限制；合并后的经验球会累计总经验值。

- 原版只会合并 value 相近的经验球——此模式完全移除了该限制。
- 一个巨型经验球 = 一次拾取，无论其中积累了多少经验。
- 适用于刷怪塔、经验农场等大规模生物击杀场景。

### 4. 最大经验球经验值
控制单个经验碎片（经验球）可持有的最大经验值。

- **原版默认：** 2477
- **本模组默认：** 32767
- 作为激进合并的上限，避免可能出现的原版上限不足。
- 更大的值可减少实体数量、提升性能，但可能导致单个经验球极大。

---

## 配置说明

所有设置通过 **NeoForge ModConfig**（COMMON 配置）进行管理。

### 配置项一览

| 配置项              | 类型    | 默认值  | 描述 |
|---------------------|---------|---------|------|
| `experienceAbsorbDelay` | Integer | 0     | 拾取经验球后的冷却 tick 数。原版：2。 |
| `directXpAbsorb`       | Boolean | true  | 启用后，玩家击杀生物的经验直接给予玩家，不生成经验球。 |
| `aggressiveXpMerge`    | Boolean | false | 启用后，所有经验球均可无视 value 进行合并。 |
| `maxXpValue`           | Integer | 32767 | 单个合并经验球的最大经验值。原版：2477。 |

> 配置文件在首次运行带有本模组的游戏后自动生成。  
> 文件位置：  
> `./config/radical_exp_strategy-common.toml`

---

## 开源许可

本项目基于 [MIT 许可协议](LICENSE) 开源。