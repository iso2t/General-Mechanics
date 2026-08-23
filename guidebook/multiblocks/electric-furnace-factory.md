---
navigation:
    parent: multiblocks/multiblocks-index.md
    title: Electric Furnace Factory
    icon: electric_furnace
    position: 220
item_ids:
- generalmechanics:machine_frame
- generalmechanics:machine_casing
---

<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4" interactive={true}>
        <ImportStructure src="../assets/assemblies/electric_furnace_factory.nbt" />
        <IsometricCamera yaw="105" pitch="5" />
    </GameScene>
</Column>

# Electric Furnace Factory

## A Furnace With an Organizational Structure

The standalone [Electric Furnace](../blocks/electric-furnace.md) successfully replaced fuel with electricity. GenTech
leadership reviewed this achievement and asked the natural follow-up question: “Can we surround it with an entire building
until it becomes faster?”

The **Electric Furnace Factory** is a 3×3×5 multiblock built around an Electric Furnace controller. It processes the same
recipes as the standalone machine, but its uniform [Factory Matrix](../blocks/matrices.md) core increases processing speed
from a responsible 1.25× to a management-approved 64×.

## Construction Requirements

Before substituting [Hatches](../blocks/hatches.md), the complete factory requires:

* **1 Electric Furnace** — controller, operator interface, and designated recipient of all blame.
* **8 Machine Frames** — the lower structural layer.
* **33 Machine Casings, 9 if upgrading** — the upper structural layer.
* **24 matching Factory Matrices if upgrading** — all one tier; collective bargaining between tiers is not supported.
* **3 Heating Elements** — the central thermal column.

The Heating Elements are structural factory components and do not require separate redstone signals for factory
processing. Supplying them with redstone will still make them hot, because GenTech has not yet developed ceremonial heat.

<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4" interactive={true}>
        <ImportStructure src="../assets/assemblies/electric_furnace_factory_iron.nbt" />
        <IsometricCamera yaw="105" pitch="5" />
    </GameScene>
</Column>

## Hatch Integration

Eligible Machine Frame and Machine Casing positions may be replaced with up to one of each supported hatch:

* **Item Input Hatch** — inserts recipe inputs and catalysts.
* **Item Output Hatch** — extracts products from all four output slots.
* **Power Hatch** — supplies energy to the factory.
* **Network Hatch** — exposes assigned item services directly to the Machine Network.

Hatches are optional, but GenTech strongly recommends installing whichever interfaces are necessary to power and operate
the factory from outside its newly constructed walls.

## Matrix Performance

| Matrix Tier | Processing Speed |
| --- | ---: |
| Iron | 1.25× |
| Steel | 2× |
| Titanium | 4× |
| Tungsten | 32× |
| Quantum | 64× |

All 24 matrix positions must use the same tier. Mixing tiers invalidates the factory instead of averaging their speeds,
because compromise is an administrative tool rather than a structural material.

Higher tiers shorten processing time without reducing the total energy required. The same energy is consumed over fewer
ticks, so faster factories require faster power delivery. If the supply cannot keep pace, processing pauses until adequate
energy becomes available and the Power Department finishes blaming the Production Department.

> **GENTECH EXPANSION NOTICE:** Breaking the structure returns the controller to standalone speed and resets active work.
> The surrounding blocks retain their full functionality as an expensive monument to interrupted productivity.

GenTech congratulates you on successfully promoting one furnace into middle management.
