---
navigation:
    parent: blocks/blocks-index.md
    title: Hatches
    icon: item_input_hatch
    position: 10
categories:
- network
item_ids:
- generalmechanics:item_input_hatch
- generalmechanics:item_output_hatch
- generalmechanics:fluid_input_hatch
- generalmechanics:fluid_output_hatch
- generalmechanics:power_hatch
- generalmechanics:network_hatch
---

# Hatches

Hatches allow pipes, storage blocks, power systems, and machine networks to interact with a formed multiblock. A hatch
does not contain its own inventory or tank; it provides controlled access to resources stored by its controller. The
multiblock determines which hatch types it accepts, how many may be installed, and which slots, tanks, or services each
hatch can access.

Hatches only function while their multiblock is formed and operational. Each hatch belongs to exactly one controller, so
placing one in a [shared wall](../multiblocks/multiblocks-index.md#shared-walls) invalidates any other multiblock that would
also use it.

## Item Input Hatch

<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4">
        <Block id="generalmechanics:item_input_hatch" />
    </GameScene>
</Column>

The Item Input Hatch allows external item automation to insert items into the controller slots assigned to it by the
multiblock. It does not allow those items to be extracted through the same hatch.

## Item Output Hatch

<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4">
        <Block id="generalmechanics:item_output_hatch" />
    </GameScene>
</Column>

The Item Output Hatch allows external item automation to extract items from the controller slots assigned to it by the
multiblock. It does not accept items through the same hatch.

## Fluid Input Hatch

<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4">
        <Block id="generalmechanics:fluid_input_hatch" />
    </GameScene>
</Column>

The Fluid Input Hatch allows external fluid automation to fill the controller tanks assigned to it by the multiblock. It
does not allow fluid to be drained through the same hatch.

## Fluid Output Hatch

<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4">
        <Block id="generalmechanics:fluid_output_hatch" />
    </GameScene>
</Column>

The Fluid Output Hatch allows external fluid automation to drain fluid from the controller tanks assigned to it by the
multiblock. It does not accept fluid through the same hatch.

## Power Hatch

<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4">
        <Block id="generalmechanics:power_hatch" />
    </GameScene>
</Column>

The Power Hatch connects external energy systems to a multiblock's energy storage. Depending on the multiblock, a Power
Hatch may accept energy, provide energy, or support both directions.

## Network Hatch

<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4">
        <Block id="generalmechanics:network_hatch" />
    </GameScene>
</Column>

The Network Hatch connects a multiblock directly to the General Mechanics Machine Network. It can expose the item, fluid,
energy, or other network services assigned by the multiblock without requiring a separate Network Connector. Only the
services explicitly supported by that multiblock are available through the hatch.
