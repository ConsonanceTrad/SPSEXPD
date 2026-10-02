/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.rockcode;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FrostIce;
import pd.effects.MagicMissile;
import pd.mechanics.Ballistica;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Lbox extends RockCode {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Lbox.class)
			.t("name", "巫妖冰盒")
			.t("desc", "来自巫妖舞者的技能芯片，将目标冻结在冰盒中。")
			.t("stats_desc", "消耗4点能量中的1点，造成等级寒冰伤害，并施加5回合冻伤。");
	}



	{ collisionProperties = Ballistica.PROJECTILE; sname = "L.b"; }
	@Override protected int missileType() { return MagicMissile.FROST; }
	@Override protected void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			int level = Math.max(1, Dungeon.hero.lvl);
			target.damage(Random.Int(level, level * 3), pd.actors.damagetype.DamageType.ICE_DAMAGE);
			if (target.isAlive()) Buff.affect(target, FrostIce.class).level(5);
		}
	}
	@Override public void onMeleeHit(pd.items.equipment.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 1) Buff.affect(defender, FrostIce.class).level(5);
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))), pd.actors.damagetype.DamageType.ICE_DAMAGE);
	}
}
