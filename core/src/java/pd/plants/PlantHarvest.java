/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.plants;

import pd.items.Heap;
import pd.items.Item;
import pd.items.consum.food.Blandfruit;
import pd.items.consum.food.fruit.Durian;
import pd.items.consum.food.processed.*;
import pd.items.consum.food.vegetable.*;
import pd.items.equipment.weapon.missiles.arrows.*;
import pd.levels.Level;
import pd.mechanics.pathfind.PathFinder;
import render.utils.math.Random;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * SPSEXPD: 种植三分支的公共实现。
 *
 * <ul>
 *   <li>野生：原生植物被踩踏时，触发原生效果之外再散落 1 枚投掷果实；</li>
 *   <li>人工种植：果丛（入口房/帐篷房/浇水的花盆）被踩踏时，掉 1 个蔬菜 + 2~3 枚果实；</li>
 *   <li>花盆精心种植：手动把种子种进花盆，掉 1 个蔬菜 + 周围 1~2 个蔬菜 + 3 枚果实，每枚 30% 为大型果实。</li>
 * </ul>
 *
 * 种子→蔬菜/果实/大型果实的对照集中在这里，避免在每个植物类里重复配置。
 */
public final class PlantHarvest {

	private PlantHarvest() { }

	/** 一种植物的精心种植产出。 */
	public static final class Species {
		public final Class<? extends Item> vegetable;
		public final Class<? extends Item> fruit;
		public final Class<? extends Item> largeFruit;

		Species(Class<? extends Item> vegetable, Class<? extends Item> fruit, Class<? extends Item> largeFruit) {
			this.vegetable = vegetable;
			this.fruit = fruit;
			this.largeFruit = largeFruit;
		}
	}

	private static final HashMap<Class<?>, Species> BY_CLASS = new HashMap<>();

	private static void register(Class<?> plant, Class<?> bush, Class<? extends Item> vegetable,
			Class<? extends Item> fruit, Class<? extends Item> largeFruit) {
		Species species = new Species(vegetable, fruit, largeFruit);
		BY_CLASS.put(plant, species);
		if (bush != null) BY_CLASS.put(bush, species);
	}

	static {
		register(Freshberry.class,   Freshberry.ExFreshberry.class,     Durian.class,          FreshFruit.class,      LargeFreshFruit.class);
		register(Rotberry.class,     Rotberry.ExRotberry.class,         Durian.class,          RotFruit.class,        LargeRotFruit.class);
		register(Firebloom.class,    Firebloom.ExFirebloom.class,       Chili.class,           FireFruit.class,       LargeFireFruit.class);
		register(Blindweed.class,    Blindweed.ExBlindweed.class,       Marigold.class,        BlindFruit.class,      LargeBlindFruit.class);
		register(Sungrass.class,     Sungrass.ExSungrass.class,         HealGrass.class,       HealFruit.class,       LargeHealFruit.class);
		register(Icecap.class,       Icecap.ExIcecap.class,             IceMint.class,         IceFruit.class,        LargeIceFruit.class);
		register(Stormvine.class,    Stormvine.ExStormvine.class,       Tulip.class,           ShockFruit.class,      LargeShockFruit.class);
		register(Sorrowmoss.class,   Sorrowmoss.ExSorrowmoss.class,     ToxicEggplant.class,   ToxicFruit.class,      LargeToxicFruit.class);
		register(Dreamfoil.class,    Dreamfoil.ExDreamfoil.class,       DreamLeaf.class,       CharmFruit.class,      LargeCharmFruit.class);
		register(Earthroot.class,    Earthroot.ExEarthroot.class,       Radish.class,          RootFruit.class,       LargeRootFruit.class);
		register(Fadeleaf.class,     Fadeleaf.ExFadeleaf.class,         Sunflower.class,       SmokeFruit.class,      LargeSmokeFruit.class);
		register(BlandfruitBush.class, BlandfruitBush.ExBlandfruitBush.class, Blandfruit.class, FlavorlessFruit.class, LargeFlavorlessFruit.class);
		register(Starflower.class,   Starflower.ExStarflower.class,     BattleFlower.class,    StarFruit.class,       LargeStarFruit.class);
		register(NutPlant.class,     NutPlant.ExNutPlant.class,         NutVegetable.class,    NutFruit.class,        LargeNutFruit.class);
		register(StarEater.class,    StarEater.ExStarEater.class,       StarEaterFlower.class, StarEaterFruit.class,  LargeStarEaterFruit.class);
		register(ReNepenth.class,    ReNepenth.ExReNepenth.class,       TransmuteCage.class,   TransmuteFruit.class,  LargeTransmuteFruit.class);
		register(SiOtwoFlower.class, SiOtwoFlower.ExSiOtwoFlower.class, QuartzFlower.class,    GlassFruit.class,      LargeGlassFruit.class);
		register(Dewcatcher.class,   Dewcatcher.ExDewcatcher.class,     DewSpore.class,        DewFruit.class,        LargeDewFruit.class);
		register(Seedpod.class,      Seedpod.ExSeedpod.class,           RainbowPansy.class,    SeedFruit.class,       LargeSeedFruit.class);
		register(Swiftthistle.class, Swiftthistle.ExSwiftthistle.class, Sorrel.class,          SwiftFruit.class,      LargeSwiftFruit.class);
	}

	/** 按植物类或果丛类查询产出（未知植物返回 null）。 */
	public static Species speciesFor(Class<?> plantClass) {
		return plantClass == null ? null : BY_CLASS.get(plantClass);
	}

	/** 该植物对应的投掷果实（野生植物散落用）。 */
	public static Class<? extends Item> fruitFor(Class<?> plantClass) {
		Species species = speciesFor(plantClass);
		return species == null ? null : species.fruit;
	}

	/** 在 center 周围的可通行格散落 count 个物品；largeChance > 0 时按概率换成大型物品。 */
	public static void scatter(Level level, int center, Class<? extends Item> itemClass,
			Class<? extends Item> largeClass, int count, float largeChance) {
		if (level == null || itemClass == null || count <= 0) return;
		ArrayList<Integer> candidates = neighbours(level, center);
		for (int i = 0; i < count && !candidates.isEmpty(); i++) {
			int cell = Random.element(candidates);
			candidates.remove((Integer)cell);
			Class<? extends Item> type = itemClass;
			if (largeClass != null && largeChance > 0f && Random.Float() < largeChance) type = largeClass;
			drop(level, cell, type, center);
		}
	}

	/** 在指定格掉落一个物品（from 为飞入动画起点）。 */
	public static void drop(Level level, int cell, Class<? extends Item> itemClass, int from) {
		if (itemClass == null) return;
		dropItem(level, cell, Reflection.newInstance(itemClass), from);
	}

	/** 在指定格掉落一个已创建好的物品。 */
	public static void dropItem(Level level, int cell, Item item, int from) {
		if (level == null || item == null) return;
		Heap heap = level.drop(item, cell);
		if (heap.sprite != null) heap.sprite.drop(from);
	}

	/** 中心格周围 8 格中可通行的格。 */
	public static ArrayList<Integer> neighbours(Level level, int center) {
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = center + offset;
			if (level.insideMap(cell) && level.passable[cell]) candidates.add(cell);
		}
		return candidates;
	}
}
