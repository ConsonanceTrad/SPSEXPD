/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.melee.special;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.KindOfWeapon;
import pd.items.weapon.melee.MeleeWeapon;
import pd.messages.Messages;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

/** XixiZero's box, which breaks into four equipment rewards after 101 strong hits. */
public class XiXiBox extends MeleeWeapon {

	static final Generator.Category[] REWARD_CATEGORIES = {
			Generator.Category.OLDWEAPON,
			Generator.Category.ARMOR,
			Generator.Category.ARTIFACT,
			Generator.Category.RING
	};

	private int charge;

	{
		image = ItemSpriteSheet.XIXI_BOX;
		tier = 1;
		usesTargeting = true;
	}

	@Override public int min(int lvl) { return 1 + Math.max(0, lvl); }
	@Override public int max(int lvl) { return 10 + Math.max(0, lvl); }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 40) Buff.prolong(defender, Paralysis.class, 4f);
		int result = super.proc(attacker, defender, damage);
		if (chargeForDamage(damage)) breakOpen(defender.pos, createRewards());
		return result;
	}

	boolean chargeForDamage(int damage) {
		if (damage > 7) charge++;
		return charge > 100;
	}

	int charge() { return charge; }

	static Item[] createRewards() {
		Item[] rewards = new Item[REWARD_CATEGORIES.length];
		for (int i = 0; i < rewards.length; i++) rewards[i] = Generator.random(REWARD_CATEGORIES[i]);
		return rewards;
	}

	void breakOpen(int cell, Item[] rewards) {
		if (Dungeon.hero != null && Dungeon.hero.belongings.weapon() == this) {
			Dungeon.hero.belongings.weapon = null;
		}
		GLog.n(Messages.get(KindOfWeapon.class, "destory"));
		if (Dungeon.level == null) return;
		for (Item reward : rewards) {
			if (reward == null) continue;
			Heap heap = Dungeon.level.drop(reward, cell);
			if (heap != null && heap.sprite != null) heap.sprite.drop();
		}
	}

	@Override public int value() { return MeleePan.legacyValue(this); }

	private static final String CHARGE = "charge";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge = bundle.getInt(CHARGE);
	}
}
