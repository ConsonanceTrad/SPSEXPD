/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.plants;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.messages.Messages;
import render.utils.math.Random;
import render.utils.serialize.Reflection;

import java.util.ArrayList;

/**
 * Shared harvest behavior for SPS-PD's entrance-room enhanced plants.
 *
 * SPSEXPD: 统一后的精心种植行为——
 * 人工种植（入口房/帐篷房/浇水的花盆）：掉 1 个蔬菜，周围散落 2~3 枚投掷果实；
 * 花盆精心种植（手动把种子种进花盆）：掉 1 个蔬菜，周围再散落 1~2 个蔬菜与 3 枚投掷果实，
 * 每枚果实有 30% 几率变成对应的大型果实。
 * 原有的 harvestClass/harvestCategory 产出保留，不因统一种植而丢失；所有散落共用同一批相邻格，不会重复堆叠。
 */
public abstract class SpsFruitBush extends Plant {

	//旧字段：部分植物（腐莓之种/符文石/转换球/升级球…）仍用它们产出原有物品
	protected int harvestCount;
	protected Class<? extends Item> harvestClass;
	protected Generator.Category harvestCategory;
	protected Class<? extends Item> centerClass;

	/** SPSEXPD: 是否为花盆精心种植（手动把种子种进花盆）。 */
	public boolean potGrown = false;
	protected int potVegetableMin = 1;
	protected int potVegetableMax = 2;
	protected int potFruitCount = 3;
	protected float largeChance = 0.3f;

	protected Item harvestItem() {
		return harvestCategory == null ? Reflection.newInstance(harvestClass)
				: Generator.random(harvestCategory);
	}

	protected void beforeHarvest() {
		if (centerClass == null || Dungeon.level == null) return;
		Heap heap = Dungeon.level.drop(Reflection.newInstance(centerClass), pos);
		if (heap.sprite != null) heap.sprite.drop();
	}

	/** 从候选格中取出一个（并移除，保证每格只落一件）。 */
	private static int take(ArrayList<Integer> candidates) {
		int cell = Random.element(candidates);
		candidates.remove((Integer)cell);
		return cell;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}

	@Override
	public final void activate(Char ch) {
		beforeHarvest();
		if (Dungeon.level == null) return;

		ArrayList<Integer> candidates = PlantHarvest.neighbours(Dungeon.level, pos);

		//旧的收获产出（保留原 SPS 行为）
		if (harvestClass != null || harvestCategory != null) {
			for (int i = 0; i < harvestCount && !candidates.isEmpty(); i++) {
				PlantHarvest.dropItem(Dungeon.level, take(candidates), harvestItem(), pos);
			}
		}

		PlantHarvest.Species species = PlantHarvest.speciesFor(getClass());
		if (species == null) return;

		//原地掉 1 个对应蔬菜
		PlantHarvest.drop(Dungeon.level, pos, species.vegetable, pos);

		if (potGrown) {
			//花盆精心种植：周围再散落 1~2 个蔬菜 + 3 枚果实（每枚 30% 大型）
			for (int i = 0, n = Random.NormalIntRange(potVegetableMin, potVegetableMax);
					i < n && !candidates.isEmpty(); i++) {
				PlantHarvest.drop(Dungeon.level, take(candidates), species.vegetable, pos);
			}
			for (int i = 0; i < potFruitCount && !candidates.isEmpty(); i++) {
				Class<? extends Item> type = species.fruit;
				if (species.largeFruit != null && Random.Float() < largeChance) type = species.largeFruit;
				PlantHarvest.drop(Dungeon.level, take(candidates), type, pos);
			}
		} else {
			//人工种植：周围散落 2~3 枚果实
			for (int i = 0, n = Random.NormalIntRange(2, 3); i < n && !candidates.isEmpty(); i++) {
				PlantHarvest.drop(Dungeon.level, take(candidates), species.fruit, pos);
			}
		}
	}
}
