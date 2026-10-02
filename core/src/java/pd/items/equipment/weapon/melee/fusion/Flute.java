/* Special Surprise content rebuilt for Shattered Pixel Dungeon 4.0. GPLv3+. */
package pd.items.equipment.weapon.melee.fusion;

import pd.atlas.items.EquipmentWandBasicWandDict;

import pd.actors.Actor;
import pd.actors.Char;
import pd.items.equipment.weapon.melee.Mace;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

public class Flute extends Mace implements FusionWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Flute.class)
			.t("name", "战斗长笛")
			.t("desc", "一件命中略高的二阶乐器。每次命中都会对目标周围的敌对单位造成五分之一的伤害。");
	}



	{ image = EquipmentEquipWeaponBasicWeaponDict.FLUTE; tier = 2; ACC = 1.05f; }
	@Override public int min(int lvl) { return 3 + lvl; }
	@Override public int max(int lvl) { return 12 + 3 * lvl; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		int result = super.proc(attacker, defender, damage);
		for (int offset : PathFinder.NEIGHBOURS8) {
			Char ch = Actor.findChar(defender.pos + offset);
			if (ch != null && ch != attacker && ch != defender && ch.alignment != attacker.alignment) {
				ch.damage(Math.max(1, result / 5), this);
			}
		}
		return result;
	}
}
