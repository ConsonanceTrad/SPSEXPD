/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 */

package pd.items.equipment.bombs;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.CellEmitter;
import pd.effects.particles.BlastParticle;
import pd.effects.particles.SmokeParticle;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponBombDict;

/** The basic SPS crafted bomb and ingredient for the elemental bomb recipes. */
public class BuildBomb extends Bomb {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BuildBomb.class)
			.t("name", "土制炸弹")
			.t("desc", "造成范围伤害，可用于制作其他炸弹。");
	}




	{
		image = EquipmentEquipWeaponBombDict.SPS_MINI_BOMB;
	}

	@Override
	public boolean explodesDestructively() {
		return false;
	}

	@Override
	public void explode(int cell) {
		super.explode(cell);
		if (Dungeon.level.heroFOV[cell]) {
			CellEmitter.center(cell).burst(BlastParticle.FACTORY, 30);
		}

		boolean terrainAffected = false;
		for (int offset : PathFinder.NEIGHBOURS9) {
			int target = cell + offset;
			if (target < 0 || target >= Dungeon.level.length()
					|| Dungeon.level.distance(cell, target) > 1.5f) continue;
			if (Dungeon.level.heroFOV[target]) {
				CellEmitter.get(target).burst(SmokeParticle.FACTORY, 4);
			}
			if (Dungeon.level.flamable[target]) {
				Level.set(target, Terrain.EMBERS, Dungeon.level);
				GameScene.updateMap(target);
				terrainAffected = true;
			}

			Char ch = Actor.findChar(target);
			if (ch != null && ch.isAlive()) {
				int damage = Random.NormalIntRange(ch.HT / 12, ch.HT / 5);
				if (damage > 0) ch.damage(damage, this);
			}
		}
		if (terrainAffected) Dungeon.observe();
	}

	@Override
	public int value() {
		return 10 * quantity();
	}

	@Override public BuildBomb random() { return this; }
}
