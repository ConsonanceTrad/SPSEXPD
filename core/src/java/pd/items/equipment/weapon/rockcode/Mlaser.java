/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.rockcode;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.damagetype.DamageType;
import pd.effects.MagicMissile;
import pd.mechanics.Ballistica;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Mlaser extends RockCode {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Mlaser.class)
			.t("name", "混合射线")
			.t("desc", "来自混源体的技能芯片，释放七元素混合射线。")
			.t("stats_desc", "消耗4点能量中的1点，分别造成能量、自然、火焰、寒冰、雷电、光明和黑暗伤害。");
	}



	{ collisionProperties = Ballistica.PROJECTILE; sname = "M.l"; }
	@Override protected int missileType() { return MagicMissile.RAINBOW; }
	@Override protected void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target == null) return;
		int level = Math.max(1, Dungeon.hero.lvl);
		int part = Math.max(1, Random.Int(level, level * 3) / 6);
		dealElements(target, part);
	}
	@Override public void onMeleeHit(pd.items.equipment.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 1) dealElements(defender, Math.max(1, weapon.damageRoll(attacker) / 6));
	}
	private static void dealElements(Char target, int damage) {
		target.damage(damage, DamageType.ENERGY_DAMAGE); target.damage(damage, DamageType.EARTH_DAMAGE);
		target.damage(damage, DamageType.FIRE_DAMAGE); target.damage(damage, DamageType.ICE_DAMAGE);
		target.damage(damage, DamageType.SHOCK_DAMAGE); target.damage(damage, DamageType.LIGHT_DAMAGE);
		target.damage(damage, DamageType.DARK_DAMAGE);
	}
}
