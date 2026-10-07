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
 * SPSEXPD: 人工/精心作物的统一收获行为。
 *
 * <ul>
 *   <li>人工种植（玩家把种子种在地上、帐篷房的花盆、浇水的花盆）：
 *       掉 1 个蔬菜（落在踩踏格）+ 散落 1~2 枚投掷果实；</li>
 *   <li>精心种植（手动把种子种进花盆 / 精制种子种在普通地板）：
 *       掉 2~3 个蔬菜（落在踩踏格）+ 散落 2~3 枚投掷果实，每枚果实 3% 几率是大型果实。</li>
 * </ul>
 *
 * 野生植物不继承本类，由 {@link Plant#trigger} 散落 1 枚果实、不掉蔬菜。
 * 少数果丛保留原有额外产出（腐莓果丛的中心腐莓之种、浆果果丛的浆果）。
 */
public abstract class SpsFruitBush extends Plant {

	/** SPSEXPD: 精心种植（手动种进花盆 / 精制种子）时为 true。 */
	public boolean potGrown = false;

	/** SPSEXPD: 保留的额外产出——中心返还物（腐莓果丛的腐莓之种）。 */
	protected Class<? extends Item> centerClass;
	/** SPSEXPD: 保留的额外产出——散落的额外物品类别（浆果果丛的浆果）。 */
	protected Generator.Category harvestCategory;
	protected int harvestCount;

	/** SPSEXPD: 精心种植时每枚果实变成大型果实的几率。 */
	protected static final float LARGE_FRUIT_CHANCE = 0.03f;

	protected Item harvestItem() {
		return harvestCategory == null ? null : Generator.random(harvestCategory);
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

		PlantHarvest.Species species = PlantHarvest.speciesFor(getClass());
		if (species == null) return;

		ArrayList<Integer> candidates = PlantHarvest.neighbours(Dungeon.level, pos);

		//SPSEXPD: 果实散落到相邻格——人工 1~2 枚，精心 2~3 枚（每枚 3% 几率大型）
		int fruitCount = potGrown ? Random.NormalIntRange(2, 3) : Random.NormalIntRange(1, 2);
		for (int i = 0; i < fruitCount && !candidates.isEmpty(); i++) {
			Class<? extends Item> type = species.fruit;
			if (potGrown && species.largeFruit != null && Random.Float() < LARGE_FRUIT_CHANCE) type = species.largeFruit;
			PlantHarvest.drop(Dungeon.level, take(candidates), type, pos);
		}

		//SPSEXPD: 蔬菜不再散落，全部落在踩踏地——人工 1 个，精心 2~3 个
		int vegetableCount = potGrown ? Random.NormalIntRange(2, 3) : 1;
		for (int i = 0; i < vegetableCount; i++) {
			PlantHarvest.drop(Dungeon.level, pos, species.vegetable, pos);
		}

		//SPSEXPD: 保留的额外产出（浆果等）
		if (harvestCategory != null) {
			for (int i = 0; i < harvestCount && !candidates.isEmpty(); i++) {
				PlantHarvest.dropItem(Dungeon.level, take(candidates), harvestItem(), pos);
			}
		}
	}
}
