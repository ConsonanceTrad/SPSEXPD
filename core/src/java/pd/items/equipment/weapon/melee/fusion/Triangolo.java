/* Special Surprise content rebuilt for Shattered Pixel Dungeon 4.0. GPLv3+. */
package pd.items.equipment.weapon.melee.fusion;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.actors.Actor;
import pd.actors.Char;
import pd.items.equipment.weapon.melee.Shortsword;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.InlineText;

public class Triangolo extends Shortsword implements FusionWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Triangolo.class)
			.t("name", "三角铁")
			.t("desc", "由舞台乐器重制而成的一阶轻武器。威力不高，但每次命中都会对目标周围的敌对单位造成四分之一的脉冲伤害，不会误伤友军。");
	}

	{ image = EquipmentEquipWeaponBasicWeaponDict.SAI_0; tier = 1; }
	@Override public int min(int lvl) { return 2 + lvl; }
	@Override public int max(int lvl) { return 7 + 2 * lvl; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		int result = super.proc(attacker, defender, damage);
		for (int offset : PathFinder.NEIGHBOURS8) {
			Char ch = Actor.findChar(defender.pos + offset);
			if (ch != null && ch != attacker && ch != defender && ch.alignment != attacker.alignment) {
				ch.damage(Math.max(1, result / 4), this);
			}
		}
		return result;
	}
}
