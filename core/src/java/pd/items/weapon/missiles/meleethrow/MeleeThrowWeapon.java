/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.missiles.meleethrow;

import pd.atlas.IconEntry;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.weapon.melee.special.MeleePan;
import pd.items.weapon.missiles.MissileWeapon;
import render.utils.math.Random;

import java.util.ArrayList;

/** SPS-PD event weapons that can be equipped in melee or thrown and recovered. */
public abstract class MeleeThrowWeapon extends MissileWeapon {
	private final int baseMin;
	private final int baseMax;
	private boolean destroyed;

	protected MeleeThrowWeapon(int tier, int min, int max, IconEntry image) {
		this.tier = tier;
		this.baseMin = min;
		this.baseMax = max;
		this.image = image;
		stackable = false;
		quantity = 1;
		baseUses = 100;
	}

	@Override public int min(int level) { return Math.max(0, baseMin + 2 * level); }
	@Override public int max(int level) { return Math.max(min(level), baseMax + (1 + tier) * level); }
	@Override public int STRReq(int level) { return 8 + tier * 2; }
	@Override public int defaultQuantity() { return 1; }
	@Override public boolean isUpgradable() { return true; }
	@Override public int value() { return MeleePan.legacyValue(this); }

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (!isEquipped(hero) && !actions.contains(AC_EQUIP)) actions.add(AC_EQUIP);
		return actions;
	}

	protected final void destroy(Char attacker) {
		destroyed = true;
		if (attacker instanceof Hero) {
			Hero hero = (Hero)attacker;
			if (hero.belongings.weapon == this) hero.belongings.weapon = null;
			if (hero.belongings.secondWep == this) hero.belongings.secondWep = null;
		}
		Item.updateQuickslot();
	}

	public final boolean destroyed() { return destroyed; }

	@Override protected void rangedHit(Char enemy, int cell) {
		if (!destroyed) super.rangedHit(enemy, cell);
	}

	@Override protected void rangedMiss(int cell) {
		if (!destroyed) super.rangedMiss(cell);
	}

	protected static int safeRandom(int min, int max) {
		min = Math.max(0, min);
		return max <= min ? min : Random.Int(min, max);
	}

	protected static void dropRandomItems(int cell, int count) {
		if (Dungeon.level == null || !Dungeon.level.insideMap(cell)) return;
		for (int i = 0; i < count; i++) {
			Item item = Generator.random();
			if (item == null) continue;
			Heap heap = Dungeon.level.drop(item, cell);
			if (heap.sprite != null) heap.sprite.drop();
		}
	}
}
