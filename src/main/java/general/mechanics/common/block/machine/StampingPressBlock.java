package general.mechanics.common.block.machine;

import general.api.crafting.RecipeDataProvider;
import general.api.crafting.RecipeGenerationContext;
import general.api.machine.MachineBlock;
import general.api.model.IConfigurableMachineModel;
import general.api.resources.Resource;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.SoundType;

public class StampingPressBlock extends MachineBlock<StampingPressBlockEntity> implements IConfigurableMachineModel, RecipeDataProvider {

	public StampingPressBlock (Properties properties) {
		super(properties.requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.IRON), StampingPressBlockEntity.class, StampingPressBlockEntity.MACHINE);
	}

	@Override
	public Identifier getFrontTexture () {
		return Resource.getMainMod("block/machine/stamping_press/stamping_press");
	}

	@Override
	public Identifier getLitTexture () {
		return Resource.getMainMod("block/machine/stamping_press/stamping_press_lit");
	}

	@Override
	public void generateRecipes (RecipeGenerationContext context) {

	}

	@Override
	public ItemLike getRecipeUnlockItem () {
		return null;
	}
}
