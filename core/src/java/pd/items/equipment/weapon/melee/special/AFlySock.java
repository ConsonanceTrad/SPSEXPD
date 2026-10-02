/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.special;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Terror;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.ConsumGoodsMaterialsGoodsDict;

/** AFly's tier-one sock, applying one of four control effects on every hit. */
public class AFlySock extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(AFlySock.class)
			.t("name", "单只的袜子")
			.t("desc", "不知道是谁的袜子，反正不是阿飞的。\n费洛蒙");
	}




	{
		image = ConsumGoodsMaterialsGoodsDict.WHITE_SOCK;
		tier = 1;
	}

	@Override public int min(int lvl) { return 1 + Math.max(0, lvl); }
	@Override public int max(int lvl) { return 5 + Math.max(0, lvl); }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		applyEffect(attacker, defender, Random.Int(4));
		return super.proc(attacker, defender, damage);
	}

	void applyEffect(Char attacker, Char defender, int result) {
		switch (result) {
			case 0: Buff.affect(defender, Paralysis.class, 3f); break;
			case 1: Buff.affect(defender, Charm.class, 3f).object = attacker.id(); break;
			case 2: Buff.affect(defender, Terror.class, 3f).object = attacker.id(); break;
			case 3: Buff.affect(defender, Amok.class, 3f); break;
			default: throw new IllegalArgumentException("Unknown AFlySock result: " + result);
		}
	}

	@Override public int value() { return legacyValue(); }

	private int legacyValue() {
		int result = enchantment == null ? 50 : 75;
		if (cursed && cursedKnown) result /= 2;
		if (levelKnown) {
			if (trueLevel() > 0) result *= trueLevel() + 1;
			else if (trueLevel() < 0) result /= 1 - trueLevel();
		}
		return Math.max(1, result);
	}
}
