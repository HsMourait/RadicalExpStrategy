# Radical Exp Strategy

![NeoForge](https://img.shields.io/badge/NeoForge-21.1.235-green?style=flat-square)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-blue?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)

A customizable experience mechanism mod.

---

## Features

### 1. Experience Absorb Delay
Configure how many ticks the player must wait before picking up another experience orb after collecting one.

- **Vanilla default:** 2 ticks
- **Configurable range:** 0 ~ `Integer.MAX_VALUE`

### 2. Direct XP Absorption
Experience from **mobs killed by the player** is granted directly to the player's XP bar, **without spawning an experience orb entity**.

- No more running around to collect scattered orbs.
- Clean, instant XP rewards for combat.
- Disable this if you prefer the vanilla orb-dropping behavior.

### 3. Aggressive XP Merge
When enabled, **all** experience orbs can merge with each other regardless of their individual value, and merged orbs accumulate their total XP into a single entity.

- Vanilla behavior only merges orbs with similar values — this mode removes that restriction entirely.
- One big orb = one pickup, no matter how much XP it contains.
- Ideal for farms, grinders, and large-scale mob slaughtering.

### 4. Max XP Value Per Orb
Controls the maximum amount of XP a single orb fragment can hold.

- **Vanilla default:** 2477
- **Default (this mod):** 32767
- Acts as a cap for the aggressive merge system, avoiding potential vanilla cap inadequacy.
- Higher values reduce entity count and improve performance, but may result in extremely large single orbs.

---

## Configuration

All settings are configured via a **NeoForge ModConfig** file (COMMON config).

### Config Options

| Option              | Type    | Default | Description |
|---------------------|---------|---------|-------------|
| `experienceAbsorbDelay` | Integer | 0     | Delay in ticks before picking up another orb. Vanilla: 2. |
| `directXpAbsorb`       | Boolean | true  | When enabled, player-kill XP goes directly to the player without spawning orbs. |
| `aggressiveXpMerge`    | Boolean | false | When enabled, all orbs can merge regardless of value. |
| `maxXpValue`           | Integer | 32767 | Maximum XP a single merged orb can hold. Vanilla: 2477. |

> The config file is generated automatically after running the game once with the mod installed.  
> You can find it at:  
> `./config/radical_exp_strategy-common.toml`

---

## License

This project is licensed under the [MIT License](LICENSE).