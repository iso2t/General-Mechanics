---
navigation:
    parent: blocks/blocks-index.md
    title: Electric Furnace
    icon: electric_furnace
    position: 310
categories:
- machines
item_ids:
- generalmechanics:electric_furnace
---

<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4">
        <Block id="generalmechanics:electric_furnace" />
    </GameScene>
</Column>

# Electric Furnace

## Combustion Is No Longer in the Budget

After reviewing the recurring cost of coal, charcoal, and employees assigned to refill furnaces, GenTech engineers removed
the fuel slot and replaced it with a power bill. The **Electric Furnace** processes standard smelting recipes using energy
instead of fuel, delivering familiar results through a system Accounting can monitor in real time.

In standalone operation, the Electric Furnace processes recipes at their normal speed and consumes **20 energy per tick**.
It stores up to **100,000 energy** and can accept up to **10,000 energy per tick**. If power becomes unavailable, processing
pauses without losing progress, preserving both the unfinished product and the operator's unfinished explanation.

> **GENTECH POWER NOTICE:** “Electric” does not mean “self-powering.” Connecting the machine to optimism will produce only
> optimism.

## Material Handling

The Electric Furnace contains one primary input slot, one optional catalyst slot, and four output slots. Standard smelting
recipes use the primary input and first available output. Specialized Electric Furnace recipes may use the catalyst and
additional outputs when required.

Blocked outputs pause processing until sufficient room becomes available. GenTech classifies this as intelligent inventory
management rather than a machine refusing to work in a cluttered environment.

## Configurable Sides

Each side may be assigned an approved function from the machine interface. Sides are defined relative to the front of the
furnace and begin with the following configuration:

| Machine Face | Default Function    |
|--------------|---------------------|
| Front        | Disabled and locked |
| Back         | Energy Input        |
| Left         | Item Input          |
| Right        | Item Output         |
| Top          | Machine Network     |
| Bottom       | Disabled            |

Every face except the front may be configured as disabled, item input, item output, energy input, or Machine Network. Item
Input can access the primary input and catalyst slots; Item Output can extract from all four output slots.

Connect a [Cable](cable.md) to a side configured for Machine Network access to transfer items and supply energy through the
network. The furnace will not accept a cable through a side currently designated for something else, even if the cable was
placed with confidence.

## Factory Expansion

The Electric Furnace also serves as the controller for an
[Electric Furnace Factory](../multiblocks/electric-furnace-factory.md). Constructing the factory around it applies a
[Factory Matrix](matrices.md) speed multiplier while keeping the same total energy cost per operation.

GenTech recommends standalone operation until demand exceeds supply, patience, or the available space surrounding the
machine.
