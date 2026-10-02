/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.start;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.items.equipment.weapon.melee.normalweapon.NormalMeleeWeapon;
import pd.messages.InlineText;

public class BunnyDagger extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BunnyDagger.class)
			.t("name", "兔人小刀")
			.t("desc", "一把带有兔人印记、久经沙场的小刀。每次命中都会额外造成一次武器伤害一半至全部的伤害。");
	}

	public BunnyDagger() {
		super(1, 1.2f, 1f, 1, 5, 10, SpecificPlaceHolderDict.SOMETHING_0);
		unique = true;
		reinforced = true;
		cursed = true;
	}
	@Override protected void applyLegacyUpgrade(Stats stats) { stats.min++; stats.max++; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		int roll = attackerRoll(attacker);
		defender.damage(safeRandom(roll / 2, roll), this);
		return super.proc(attacker, defender, damage);
	}
}
