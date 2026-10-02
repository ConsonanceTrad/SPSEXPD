/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.rockcode;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.MagicMissile;
import pd.items.equipment.weapon.missiles.MegaCannon;
import pd.mechanics.Ballistica;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Nshuriken extends RockCode {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Nshuriken.class)
			.t("name", "忍者手镖")
			.t("desc", "来自天狗的技能芯片，投掷强力忍者手里剑。")
			.t("stats_desc", "消耗4点能量中的1点，造成三倍等级伤害。");
	}



	{ collisionProperties = Ballistica.PROJECTILE; sname = "N.s"; }
	@Override protected int missileType() { return MagicMissile.LIGHT_MISSILE; }
	@Override protected void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			int level = Math.max(1, Dungeon.hero.lvl);
			target.damage(3 * Random.Int(level, level * 3), MegaCannon.class);
		}
	}
	@Override public void onMeleeHit(pd.items.equipment.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))), MegaCannon.class);
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))), MegaCannon.class);
	}
}
