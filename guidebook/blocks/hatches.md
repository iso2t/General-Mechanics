---
navigation:
    parent: blocks/blocks-index.md
    title: Hatches
    icon: item_input_hatch
    position: 10
categories:
- devices
item_ids:
- generalmechanics:item_input_hatch
- generalmechanics:item_output_hatch
- generalmechanics:fluid_input_hatch
- generalmechanics:fluid_output_hatch
- generalmechanics:power_hatch
- generalmechanics:network_hatch
---

# Hatches

## Approved Holes in Expensive Machinery

During the first GenTech multiblock trials, researchers discovered that surrounding a machine with solid casing made it
exceptionally secure and almost entirely unusable. The Infrastructure Access Division responded with **hatches**:
controlled interfaces that allow pipes, storage blocks, power systems, and Machine Networks to interact with a formed
multiblock.

A hatch does not contain its own inventory or tank. It provides access to resources stored by its controller. Each
multiblock decides which hatch types it accepts, how many may be installed, and which slots, tanks, or services they may
access. GenTech calls this “delegated responsibility.” The controller calls it Tuesday.

Hatches function only while their multiblock is formed and operational. Each hatch belongs to exactly one controller, so
do not install one in a [shared wall](../multiblocks/multiblocks-index.md#shared-walls). Any other multiblock attempting to
use the same hatch will become invalid, initiating the traditional engineering process of removing blocks until everything
works again.

> **GENTECH ACCESS NOTICE:** One hatch. One controller. Multiple controllers may submit a formal ownership dispute by
> refusing to operate.

## Item Input Hatch

<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4">
        <Block id="generalmechanics:item_input_hatch" />
    </GameScene>
</Column>

The Item Input Hatch is the multiblock's designated receiving department. External item automation may insert items into
the controller slots assigned to this hatch. Extraction is not supported; once an item enters Receiving, it is no longer
Receiving's problem.

## Item Output Hatch

<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4">
        <Block id="generalmechanics:item_output_hatch" />
    </GameScene>
</Column>

The Item Output Hatch allows external item automation to extract items from the controller slots assigned to it. It does
not accept incoming items. Please direct all unsolicited materials, packages, and personal effects to Item Input.

## Fluid Input Hatch

<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4">
        <Block id="generalmechanics:fluid_input_hatch" />
    </GameScene>
</Column>

The Fluid Input Hatch allows external fluid automation to fill the controller tanks assigned to it by the multiblock. It
does not permit fluid extraction, ensuring all approved liquids continue moving in the approved direction.

## Fluid Output Hatch

<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4">
        <Block id="generalmechanics:fluid_output_hatch" />
    </GameScene>
</Column>

The Fluid Output Hatch allows external fluid automation to drain the controller tanks assigned to it. It does not accept
incoming fluids, even if the fluid has an appointment.

## Power Hatch

<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4">
        <Block id="generalmechanics:power_hatch" />
    </GameScene>
</Column>

The Power Hatch connects external energy systems to a multiblock's energy storage. Depending on the multiblock, it may
accept energy, provide energy, or support both directions. Consult the machine's approved operating procedure before
assuming electricity will travel in whichever direction is currently most convenient.

## Network Hatch

<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4">
        <Block id="generalmechanics:network_hatch" />
    </GameScene>
</Column>

The Network Hatch connects a multiblock directly to the General Mechanics Machine Network without requiring a separate
Network Connector. It may expose item, fluid, energy, or other services assigned by the multiblock. Installing a Network
Hatch does not grant the machine new capabilities; only services explicitly supported by that multiblock are available.

This limitation is intentional and must not be described as a missed opportunity in quarterly infrastructure reviews.
