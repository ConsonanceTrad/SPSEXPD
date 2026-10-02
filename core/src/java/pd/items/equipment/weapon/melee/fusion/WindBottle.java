/* Special Surprise content rebuilt for Shattered Pixel Dungeon 4.0. GPLv3+. */
package pd.items.equipment.weapon.melee.fusion;

import pd.atlas.items.EquipmentWandBasicWandDict;

import pd.actors.Char;
import pd.items.equipment.wands.WandOfBlastWave;
import pd.items.equipment.weapon.melee.Sword;
import pd.mechanics.Ballistica;
import render.utils.math.Random;
import pd.messages.InlineText;

public class WindBottle extends Sword implements FusionWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WindBottle.class)
			.t("name", "风瓶")
			.t("desc", "能够释放受控气流的三阶容器。每次命中有五分之一概率把可移动的目标击退一格。");
	}



	{ image = EquipmentWandBasicWandDict.WAND_BLAST_WAVE_0; tier = 3; }
	@Override public int min(int lvl) { return 4 + lvl; }
	@Override public int max(int lvl) { return 17 + 4 * lvl; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		int result = super.proc(attacker, defender, damage);
		if (Random.Int(5) == 0) {
			int beyond = defender.pos + (defender.pos - attacker.pos);
			Ballistica path = new Ballistica(defender.pos, beyond, Ballistica.PROJECTILE);
			WandOfBlastWave.throwChar(defender, path, 1, false, false, this);
		}
		return result;
	}
}
