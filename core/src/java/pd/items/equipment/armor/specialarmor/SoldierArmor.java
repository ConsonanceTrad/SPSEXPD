/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.specialarmor;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.TargetShoot;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.equipment.armor.normalarmor.NormalArmor;
import pd.items.equipment.weapon.guns.GunWeapon;
import render.utils.math.Random;
import pd.messages.InlineText;

public class SoldierArmor extends NormalArmor {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SoldierArmor.class)
			.t("name", "星兵背心")
			.t("desc", "制式星兵服装，装载了大量便携弹夹和瞄准系统。\n英雄护甲");
	}



	public SoldierArmor() { super(5, 1f, 1f, 2, 20, 40, 1, 0, 3, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(8) == 0) {
			if (defender instanceof Hero) {
				for (Item item : ((Hero)defender).belongings) if (item instanceof GunWeapon) ((GunWeapon)item).addRound();
			}
			Buff.affect(defender, TargetShoot.class, 10f);
		}
		return super.proc(attacker, defender, damage);
	}
}
