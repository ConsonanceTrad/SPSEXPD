/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.rockcode;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.MagicMissile;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.mechanics.Ballistica;
import pd.scenes.GameScene;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Trush extends RockCode {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Trush.class)
			.t("name", "墓石冲击")
			.t("desc", "来自坦克的技能芯片，投出沉重墓碑并破坏目标地面。")
			.t("stats_desc", "消耗4点能量中的1点，造成四倍等级伤害，并将普通地形变为装饰地面。");
	}

	{ collisionProperties = Ballistica.PROJECTILE; sname = "T.r"; }
	@Override protected int missileType() { return MagicMissile.EARTH; }
	@Override protected void onZap(Ballistica bolt) {
		int cell = bolt.collisionPos;
		Char target = Actor.findChar(cell);
		if (target != null) {
			int level = Math.max(1, Dungeon.hero.lvl);
			target.damage(4 * Random.Int(level, level * 3), Dungeon.hero);
		}
		int terrain = Dungeon.level.map[cell];
		if (terrain != Terrain.WELL && terrain != Terrain.EMPTY_WELL && terrain != Terrain.ENTRANCE
				&& terrain != Terrain.EXIT && terrain != Terrain.ALCHEMY && terrain != Terrain.IRON_MAKER) {
			Level.set(cell, Terrain.EMPTY_DECO, Dungeon.level);
			GameScene.updateMap(cell);
		}
	}
	@Override public void onMeleeHit(pd.items.equipment.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 1) defender.damage(3 * Random.Int(Math.max(1, weapon.damageRoll(attacker))), attacker);
	}
}
