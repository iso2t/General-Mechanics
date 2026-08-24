---
navigation:
    parent: blocks/blocks-index.md
    title: Macerator
    icon: macerator
    position: 330
categories:
- machines
item_ids:
- generalmechanics:macerator
---

<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4">
        <Block id="generalmechanics:macerator" />
    </GameScene>
</Column>

# Macerator

## Industrial Progress, Now Available as Dust

GenTech material scientists discovered that metal ingots were inconveniently solid, offensively singular, and largely
uncooperative when asked to become powder. The **Macerator** resolves all three concerns by applying powered mechanical
violence until the material becomes easier to pour, process, inhale accidentally, and assign to a different department.

Insert one approved ingot into the input slot. The Macerator produces **one guaranteed dust** in its primary output and
has an independent **10% chance** to produce one additional dust in the bonus-output slot. GenTech cannot guarantee the
bonus, but Probability has assured management that this arrangement becomes profitable if observed for long enough.

## Approved Pulverization Schedule

| Material Input | Processing Time | Guaranteed Output | Bonus Output |
| --- | ---: | --- | --- |
| Copper Ingot | 8 seconds | 1 Copper Dust | 10% chance of 1 additional dust |
| Iron Ingot | 10 seconds | 1 Iron Dust | 10% chance of 1 additional dust |
| Gold Ingot | 7 seconds | 1 Gold Dust | 10% chance of 1 additional dust |
| Netherite Ingot | 20 seconds | 1 Netherite Dust | 10% chance of 1 additional dust |
| Steel Ingot | 10 seconds | 1 Steel Dust | 10% chance of 1 additional dust |
| Titanium Ingot | 12 seconds | 1 Titanium Dust | 10% chance of 1 additional dust |
| Tungsten Ingot | 15 seconds | 1 Tungsten Dust | 10% chance of 1 additional dust |

The primary and bonus outputs use separate slots. Keep both available unless your production plan includes learning how a
machine expresses disappointment through stopped progress.

> **GENTECH PARTICULATE NOTICE:** Metal dust is a production intermediate, not a seasoning, atmosphere, or substitute for
> approved workplace ventilation.

## Power Requirements

The Macerator consumes **30 energy per tick**, stores up to **100,000 energy**, and accepts up to **10,000 energy per tick**.
If energy becomes unavailable or an output is blocked, processing pauses without losing progress. The machine will resume
when conditions improve, a benefit not currently included in the employee wellness package.

## Configurable Sides

The Macerator begins with the following GenTech-approved side configuration:

| Machine Face | Default Function |
| --- | --- |
| Front | Disabled and locked |
| Back | Energy Input |
| Left | Item Input |
| Right | Item Output |
| Top | Machine Network |
| Bottom | Disabled |

Every face except the front may be configured as disabled, item input, item output, energy input, or Machine Network. Item
Input accesses the material slot; Item Output extracts from both dust slots. A Machine Network side connected by
[Cable](cable.md) can transfer items and receive energy without requiring anyone to carry powdered Netherite through the
hallway in an open container.

## Factory Expansion

The Macerator also serves as the controller for a 3×3×3
[Macerator Factory](../multiblocks/macerator-factory.md). Forming the factory doubles the guaranteed primary output from one
dust to two while retaining the independent 10% chance for a third. This is GenTech's first approved step toward ore
duplication and its first official finding that breaking something can increase its value.

GenTech congratulates you on converting one organized piece of metal into several thousand less organized pieces of metal.
