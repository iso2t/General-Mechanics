---
navigation:
    title: Multiblocks
    position: 50
---

# Multiblocks

A list of all multiblocks added to the game.

## Shared Walls

Multiblocks may share ordinary casing blocks across one or more walls. Controller blocks cannot be shared because each
controller defines its own multiblock.

Hatches may be placed in a shared wall, but each hatch belongs to exactly one controller. An existing owner is preserved;
if an unbound hatch could belong to multiple multiblocks, one controller is selected as its owner. Any other multiblock
that includes the same hatch is invalid and cannot process recipes or open its interface.

To keep every adjacent multiblock valid, place each machine's hatches on casing blocks that are not shared with another
multiblock.
