---
navigation:
    parent: multiblocks/multiblocks-index.md
    title: Stamping Factory
    icon: stamping_press
    position: 230
---

<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4" interactive={true}>
        <ImportStructure src="../assets/assemblies/stamping_factory.nbt" />
        <IsometricCamera yaw="195" pitch="5" />
    </GameScene>
</Column>

# Stamping Factory

## Applied Pressure at Department Scale

The standalone [Stamping Press](../blocks/stamping-press.md) successfully reshaped metal. GenTech leadership concluded
that surrounding it with two additional floors should also reshape the production schedule. The resulting **Stamping
Factory** is a compact 3×3×3 multiblock that accelerates every approved Stamping Press recipe.

The factory uses the Stamping Press as its controller and retains the same material, die, output, and energy storage. Its
uniform middle ring determines the processing-speed upgrade, allowing operators to choose between standard performance
and results that arrive before the associated paperwork.

## Construction Requirements

Before substituting [Hatches](../blocks/hatches.md), construct the factory shown above using:

* **1 Stamping Press** — controller, user interface, and officially designated source of downward pressure.
* **8 Machine Frames** — the lower structural ring.
* **8 matching middle-ring blocks** — use Machine Frames for 1× operation or one uniform Factory Matrix tier for an upgrade.
* **9 Machine Casings** — the upper structural layer.
* **1 block of air** — the center of the matrix ring must remain empty.

The empty center is a required structural component. Installing a block, storage solution, decorative fern, or employee in
that space prevents the factory from forming.

> **GENTECH OCCUPANCY NOTICE:** “Must remain empty” is a complete operating instruction, not an invitation to determine how
> briefly the space may be occupied.

## Hatch Integration

Eligible Machine Frame and Machine Casing positions may be replaced with up to one of each supported hatch:

* **Item Input Hatch** — inserts metal into the input slot and reusable tools into the die slot.
* **Item Output Hatch** — extracts the finished plate, nuggets, or gear.
* **Power Hatch** — supplies energy to the factory.
* **Network Hatch** — exposes assigned item services directly to the Machine Network.

When the Stamping Factory forms, direct connections to the controller are disabled and its side configuration is locked.
Install a Power Hatch for sustained operation, then use item hatches or a Network Hatch for the desired level of
automation. GenTech recommends completing this step before sealing the factory and asking why production has stopped.

## Matrix Performance

| Middle-Ring Material | Processing Speed |
| --- | ---: |
| Machine Frame | 1× |
| Iron Core Matrix | 1.25× |
| Steel Core Matrix | 2× |
| Titanium Core Matrix | 4× |
| Tungsten Core Matrix | 32× |
| Quantum Core Matrix | 64× |

All eight middle-ring blocks must match. Mixing Machine Frames or matrix tiers invalidates the factory rather than
averaging their performance, because the Stamping Factory applies pressure to metal—not organizational disagreements.

Higher tiers shorten recipe duration without reducing total energy use. The factory consumes the same energy over fewer
ticks, increasing the required delivery rate. Insufficient power pauses processing until the supply recovers or the Energy
Department produces a sufficiently technical explanation.

> **GENTECH STRUCTURAL NOTICE:** Breaking the factory returns the Stamping Press to standalone operation and resets active
> work. Any partially stamped component will be retained as evidence that structural maintenance was not coordinated with
> Production.

GenTech congratulates you on turning one press into three floors of measurable force.
