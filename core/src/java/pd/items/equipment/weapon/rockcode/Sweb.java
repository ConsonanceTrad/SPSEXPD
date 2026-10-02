/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.rockcode;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Web;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Roots;
import pd.effects.MagicMissile;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.GroundGroundingItemsDict;

public class Sweb extends RockCode {
	{
		image = GroundGroundingItemsDict.TREASURE_SPOT_2;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Sweb.class)
			.t("name", "蛛网陷阱")
			.t("desc", "来自蜘蛛女王的技能芯片，在目标周围铺设蛛网。")
			.t("stats_desc", "消耗4点能量中的1点，造成等级伤害，并在相邻可通行地面生成蛛网。");
	}



	{ collisionProperties = Ballistica.PROJECTILE; sname = "S.w"; }
	@Override protected int missileType() { return MagicMissile.LIGHT_MISSILE; }
	@Override protected void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			int level = Math.max(1, Dungeon.hero.lvl);
			target.damage(Random.Int(level, level * 3), Dungeon.hero);
		}
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = bolt.collisionPos + offset;
			if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell]) GameScene.add(Blob.seed(cell, 4, Web.class));
		}
	}
	@Override public void onMeleeHit(pd.items.equipment.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 1) Buff.affect(defender, Roots.class, 3f);
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))), attacker);
	}
}
