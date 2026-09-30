/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.skills;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.*;
import pd.actors.damagetype.SpsMagicDamage;
import pd.items.Generator;
import pd.items.GreatRune;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import render.utils.Random;

/** The four ascetic class skills from SPS-PD 0.9.8. */
public class AsceticSkill extends ClassSkill {
	{ image = ItemSpriteSheet.STONE_ENCHANT; }

	@Override public void doSpecial() {
		Buff.prolong(curUser, SpeedImbue.class, 40f);
		if (curUser.lvl < 56) addCooldown(10);
		finishSkillCast();
	}

	@Override public void doSpecial2() {
		dropAtHero(new GreatRune());
		if (curUser.lvl > 55) dropAtHero(Generator.random());
		addCooldown(20);
		finishSkillCast();
	}

	@Override public void doSpecial3() {
		curUser.improveAttackSkill(-1);
		curUser.improveDefenseSkill(-1);
		curUser.improveMagicSkill(2);
		if (curUser.lvl > 55) {
			switch (Random.Int(3)) {
				case 0: curUser.improveMagicSkill(1); break;
				case 1: curUser.improveAttackSkill(1); break;
				default: curUser.improveDefenseSkill(1); break;
			}
		}
		addCooldown(30);
		finishSkillCast();
	}

	@Override public void doSpecial4() {
		int damage = Math.round(Dungeon.legacyDepth() * (1f + 0.1f * curUser.magicSkill()));
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			if (!Dungeon.level.insideMap(cell) || Dungeon.level.distance(curUser.pos, cell) > 2) continue;
			int terrain = Dungeon.level.map[cell];
			if (terrain == Terrain.WALL || terrain == Terrain.WALL_DECO || terrain == Terrain.GLASS_WALL) {
				Level.set(cell, Terrain.EMBERS, Dungeon.level);
				GameScene.updateMap(cell);
			}
			Char ch = Actor.findChar(cell);
			if (ch != null && ch != curUser && ch.alignment != Char.Alignment.ALLY && damage > 0) {
				if (curUser.lvl > 55) {
					Buff.prolong(ch, Vertigo.class, 10f);
					Buff.prolong(ch, Blindness.class, 10f);
				}
				ch.damage(damage, SpsMagicDamage.ENERGY);
			}
		}
		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = curUser.pos + offset;
			if (!Dungeon.level.insideMap(cell) || isTransition(Dungeon.level.map[cell])) continue;
			Level.set(cell, Terrain.DOOR, Dungeon.level);
			GameScene.updateMap(cell);
		}
		Dungeon.observe();
		addCooldown(20);
		finishSkillCast();
	}

	private boolean isTransition(int terrain) {
		return terrain == Terrain.ENTRANCE || terrain == Terrain.ENTRANCE_SP || terrain == Terrain.EXIT
				|| terrain == Terrain.LOCKED_EXIT || terrain == Terrain.UNLOCKED_EXIT;
	}
}
