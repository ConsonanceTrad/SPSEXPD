/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.plants;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.Heap;
import pd.items.Generator;
import pd.items.Item;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import render.utils.Random;
import render.utils.Reflection;

import java.util.ArrayList;

/** Shared harvest behavior for SPS-PD's entrance-room enhanced plants. */
public abstract class SpsFruitBush extends Plant {

	protected int harvestCount;
	protected Class<? extends Item> harvestClass;
	protected Generator.Category harvestCategory;
	protected Class<? extends Item> centerClass;

	protected Item harvestItem() {
		return harvestCategory == null ? Reflection.newInstance(harvestClass)
				: Generator.random(harvestCategory);
	}

	protected void beforeHarvest() {
		if (centerClass == null || Dungeon.level == null) return;
		Heap heap = Dungeon.level.drop(Reflection.newInstance(centerClass), pos);
		if (heap.sprite != null) heap.sprite.drop();
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}

	@Override
	public final void activate(Char ch) {
		beforeHarvest();
		if (Dungeon.level == null) return;

		ArrayList<Integer> candidates = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = pos + offset;
			if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell]) candidates.add(cell);
		}

		for (int i = 0; i < harvestCount && !candidates.isEmpty(); i++) {
			int cell = Random.element(candidates);
			candidates.remove((Integer)cell);
			Heap heap = Dungeon.level.drop(harvestItem(), cell);
			if (heap.sprite != null) heap.sprite.drop(pos);
		}
	}
}
