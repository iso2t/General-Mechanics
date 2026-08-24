---
navigation:
    parent: blocks/blocks-index.md
    title: Stamping Press
    icon: stamping_press
    position: 320
categories:
- machines
item_ids:
- generalmechanics:stamping_press
- generalmechanics:gear_die
- generalmechanics:nugget_die
---

<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4">
        <Block id="generalmechanics:stamping_press" />
    </GameScene>
</Column>

# Stamping Press

## Metallurgy Through Applied Downward Pressure

GenTech metallurgists spent years asking metal ingots to become flatter, smaller, or significantly more gear-shaped. When
verbal encouragement failed, the Industrial Persuasion Division developed the **Stamping Press**.

The Stamping Press uses electrical power to form Steel, Titanium, and Tungsten into plates, nuggets, and gears. Insert the
material into the primary input slot, install the required die when applicable, and leave the output slot available for
the machine's professionally reshaped response.

## Approved Forming Operations

| Operation | Material Input | Required Die |    Output |      Steel |   Titanium |    Tungsten |
|-----------|---------------:|--------------|----------:|-----------:|-----------:|------------:|
| Plate     |        1 Ingot | None         |   1 Plate | 10 seconds | 12 seconds |  15 seconds |
| Nuggets   |        1 Ingot | Nugget Die   | 9 Nuggets |  5 seconds |  6 seconds | 7.5 seconds |
| Gear      |       4 Ingots | Gear Die     |    1 Gear | 20 seconds | 24 seconds |  30 seconds |

Stamping Dies are reusable catalysts and are not consumed by recipes. GenTech recommends leaving the appropriate die in
its assigned slot instead of repeatedly verifying that it remains reusable.

> **GENTECH PRESS SAFETY NOTICE:** The Stamping Press applies enough force to reshape Tungsten. Your fingers are not a
> higher-tier material. Keep all personnel components outside the forming area.

## Power Requirements

In standalone operation, the Stamping Press consumes **40 energy per tick**. It stores up to **100,000 energy** and accepts
up to **10,000 energy per tick**. If energy runs out or the output becomes blocked, processing pauses without losing
progress. The unfinished material will wait indefinitely; Productivity Accounting will not.

## Configurable Sides

The Stamping Press begins with the following GenTech-approved side configuration:

| Machine Face | Default Function    |
|--------------|---------------------|
| Front        | Disabled and locked |
| Back         | Energy Input        |
| Left         | Item Input          |
| Right        | Item Output         |
| Top          | Machine Network     |
| Bottom       | Disabled            |

While operating as a standalone machine, every face except the front may be configured as disabled, item input, item
output, energy input, or Machine Network. Item Input can access both the material and die slots; Item Output extracts the
finished product. A side configured for Machine Network access can transfer items and receive energy through a connected
[Cable](cable.md).

## Factory Promotion

The Stamping Press can serve as the controller for a 3×3×3
[Stamping Factory](../multiblocks/stamping-factory.md). A uniform ring of [Factory Matrices](matrices.md) increases
processing speed; Machine Frames may be used instead for standard 1× operation.

Once the factory forms, direct side configuration is locked and external resources are routed through the factory's
Hatches. GenTech advises configuring the standalone machine before construction, then installing the appropriate Item
Input, Item Output, Power, and optional Network Hatches before announcing that production is ready.

GenTech congratulates you on replacing traditional metalworking with a machine that can flatten both ingots and quarterly
production estimates.
