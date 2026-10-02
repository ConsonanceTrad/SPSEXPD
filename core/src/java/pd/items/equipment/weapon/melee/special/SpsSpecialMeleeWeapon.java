/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.special;

import pd.atlas.IconEntry;

import pd.Dungeon;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import render.utils.math.Random;

/** Shared stat and safety rules for SPS-PD's event melee weapons. */
abstract class SpsSpecialMeleeWeapon extends MeleeWeapon {

	private final int baseMin;
	private final int baseMax;

	SpsSpecialMeleeWeapon(int tier, float accuracy, float delay, int reach,
			int min, int max, IconEntry image) {
		this.tier = tier;
		this.ACC = accuracy;
		this.DLY = delay;
		this.RCH = reach;
		this.baseMin = min;
		this.baseMax = max;
		this.image = image;
	}

	@Override public int min(int level) { return Math.max(0, baseMin + Math.max(0, level)); }
	@Override public int max(int level) { return Math.max(min(level), baseMax + Math.max(0, level) * (1 + tier / 2)); }
	@Override public int value() { return MeleePan.legacyValue(this); }

	static int safeRandom(int min, int max) {
		min = Math.max(0, min);
		return max <= min ? min : Random.Int(min, max);
	}

	static void dropRandomItems(int cell, int count) {
		if (Dungeon.level == null || !Dungeon.level.insideMap(cell)) return;
		for (int i = 0; i < count; i++) {
			Item item = Generator.random();
			if (item == null) continue;
			Heap heap = Dungeon.level.drop(item, cell);
			if (heap.sprite != null) heap.sprite.drop();
		}
	}
}
