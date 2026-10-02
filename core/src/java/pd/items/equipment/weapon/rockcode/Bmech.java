/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.rockcode;

import pd.actors.Char;
import pd.effects.MagicMissile;
import pd.items.equipment.bombs.DungeonBomb;
import pd.items.equipment.bombs.MiniBomb;
import pd.mechanics.Ballistica;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Bmech extends RockCode {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Bmech.class)
			.t("name", "机械炸弹")
			.t("desc", "来自DM-300的技能芯片，向目标发射机械爆破炸弹。")
			.t("stats_desc", "消耗4点能量中的1点，在目标位置引爆一枚地城炸弹。");
	}



	{ collisionProperties = Ballistica.PROJECTILE; sname = "B.m"; }
	@Override protected int missileType() { return MagicMissile.FIRE; }
	@Override protected void onZap(Ballistica bolt) { new DungeonBomb().explode(bolt.collisionPos); }
	@Override public void onMeleeHit(pd.items.equipment.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 1) new MiniBomb().explode(defender.pos);
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))), pd.actors.damagetype.DamageType.ENERGY_DAMAGE);
	}
}
