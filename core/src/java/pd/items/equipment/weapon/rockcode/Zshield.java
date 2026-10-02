/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.rockcode;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.EnergyArmor;
import pd.effects.MagicMissile;
import pd.mechanics.Ballistica;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Zshield extends RockCode {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Zshield.class)
			.t("name", "尸块护盾")
			.t("desc", "来自矮人国王的技能芯片，将黑暗能量转化为防护盾。")
			.t("stats_desc", "消耗4点能量中的1点，造成等级黑暗伤害，并获得英雄等级5倍的护盾。");
	}



	{ collisionProperties = Ballistica.PROJECTILE; sname = "Z.s"; }
	@Override protected int missileType() { return MagicMissile.SHADOW; }
	@Override protected void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			int level = Math.max(1, Dungeon.hero.lvl);
			target.damage(Random.Int(level, level * 3), pd.actors.damagetype.DamageType.DARK_DAMAGE);
			Buff.affect(Dungeon.hero, EnergyArmor.class).level(level * 5);
		}
	}
	@Override public void onMeleeHit(pd.items.equipment.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 1 && attacker instanceof pd.actors.hero.Hero)
			Buff.affect(attacker, EnergyArmor.class).level(Math.max(1, ((pd.actors.hero.Hero)attacker).lvl) * 3);
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))), pd.actors.damagetype.DamageType.DARK_DAMAGE);
	}
}
