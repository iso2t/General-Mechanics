package general.mechanics.registries;

import general.api.mod.GenAPI;
import general.mechanics.common.menus.CokeOvenMenu;
import general.mechanics.common.menus.ElectricFurnaceMenu;
import general.mechanics.common.menus.FluidInfuserMenu;
import general.mechanics.common.menus.MaceratorMenu;
import general.mechanics.common.menus.StampingPressMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class GenMenus {

	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, GenAPI.getModId());

	public static final DeferredHolder<MenuType<?>, MenuType<CokeOvenMenu>>        COKE_OVEN        = REGISTRY.register("coke_oven", () -> IMenuTypeExtension.create(CokeOvenMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ElectricFurnaceMenu>> ELECTRIC_FURNACE = REGISTRY.register("electric_furnace", () -> IMenuTypeExtension.create(ElectricFurnaceMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<StampingPressMenu>>   STAMPING_PRESS   = REGISTRY.register("stamping_press", () -> IMenuTypeExtension.create(StampingPressMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<MaceratorMenu>>       MACERATOR        = REGISTRY.register("macerator", () -> IMenuTypeExtension.create(MaceratorMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<FluidInfuserMenu>>    FLUID_INFUSER    = REGISTRY.register("fluid_infuser", () -> IMenuTypeExtension.create(FluidInfuserMenu::new));

	private GenMenus () {
	}
}
