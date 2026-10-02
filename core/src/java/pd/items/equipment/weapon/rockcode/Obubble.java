/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.rockcode;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Ooze;
import pd.effects.MagicMissile;
import pd.mechanics.Ballistica;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Obubble extends RockCode {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Obubble.class)
			.t("name", "淤泥泡沫")
			.t("desc", "来自黏咕的技能芯片，发射可能使目标沾满淤泥的自然泡泡。")
			.t("stats_desc", "消耗4点能量中的1点，造成随英雄等级提高的自然伤害，并有50%%概率施加淤泥。");
	}

	{ collisionProperties = Ballistica.PROJECTILE; sname = "O.b"; }
	@Override protected int missileType() { return MagicMissile.FOLIAGE; }
	@Override protected void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			int level = Math.max(1, Dungeon.hero.lvl);
			target.damage(Random.Int(level, level * 3), pd.actors.damagetype.DamageType.EARTH_DAMAGE);
			if (target.isAlive() && Random.Int(2) == 0) Buff.affect(target, Ooze.class).set(10f);
		}
	}
	@Override public void onMeleeHit(pd.items.equipment.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 1) Buff.affect(defender, Ooze.class).set(4f);
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))), pd.actors.damagetype.DamageType.EARTH_DAMAGE);
	}
}
