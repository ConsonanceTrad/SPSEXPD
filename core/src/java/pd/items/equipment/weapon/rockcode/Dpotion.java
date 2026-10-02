/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.rockcode;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.ShadowCurse;
import pd.effects.MagicMissile;
import pd.mechanics.Ballistica;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Dpotion extends RockCode {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Dpotion.class)
			.t("name", "暗黑药水")
			.t("desc", "来自瘟疫医生的能力，向一个地方投掷黑暗药水。")
			.t("stats_desc", "消耗4点能量中的1点，造成随英雄等级提高的黑暗伤害，并有概率施加延迟爆发的暗影诅咒。");
	}



	{ collisionProperties = Ballistica.PROJECTILE; sname = "D.p"; }
	@Override protected int missileType() { return MagicMissile.SHADOW; }

	@Override protected void onZap(Ballistica bolt) {
		int level = Math.max(1, Dungeon.hero.lvl);
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			target.damage(Random.Int(level, level * 3), this);
			if (target.isAlive() && Random.Int(2) == 0) Buff.affect(target, ShadowCurse.class);
		}
	}

	@Override public void onMeleeHit(pd.items.equipment.weapon.melee.MeleeWeapon weapon,
			Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 1) Buff.affect(defender, ShadowCurse.class);
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))),
				pd.actors.damagetype.DamageType.DARK_DAMAGE);
	}
}
