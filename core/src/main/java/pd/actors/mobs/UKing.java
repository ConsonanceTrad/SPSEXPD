/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Roots;
import pd.effects.Speck;
import pd.items.misc.FourClover;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.sprites.PlantKingSprite;
import render.utils.math.Random;

/** Plant king, with the old terrain-growing rage phase and its indexing bug fixed. */
public class UKing extends BossRushBoss {
	{
		spriteClass = PlantKingSprite.class;
		baseSpeed = 1f;
		HP = HT = 2000;
		flying = true;
		loot = new FourClover();
		lootChance = 1f;
		properties.add(Property.PLANT);
	}

	@Override
	protected boolean act() {
		if (Dungeon.level.flamable[pos] && HP < HT) {
			HP++;
			if (sprite != null) sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
		}
		return super.act();
	}

	@Override
	protected void onPhaseChanged(int phase) {
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			int terrain = Dungeon.level.map[cell];
			if (terrain == Terrain.EMPTY || terrain == Terrain.EMBERS
					|| terrain == Terrain.EMPTY_DECO || terrain == Terrain.GRASS) {
				Level.set(cell, Terrain.HIGH_GRASS, Dungeon.level);
				GameScene.updateMap(cell);
			}
		}
	}

	@Override
	public void move(int step, boolean travelling) {
		super.move(step, travelling);
		int cell = step + PathFinder.NEIGHBOURS8[Random.Int(PathFinder.NEIGHBOURS8.length)];
		if (!Dungeon.level.insideMap(cell)) return;
		int terrain = Dungeon.level.map[cell];
		if (terrain == Terrain.EMPTY || terrain == Terrain.EMBERS || terrain == Terrain.EMPTY_DECO) {
			Level.set(cell, Terrain.GRASS, Dungeon.level);
			GameScene.updateMap(cell);
		}
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return Dungeon.level.distance(pos, enemy.pos) <= (breaks > 2 ? 3 : 1);
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (breaks > 2) {
			if (Random.Int(5) > 2) Buff.prolong(enemy, Roots.class, 2f);
			else Buff.affect(enemy, Poison.class).set(5f);
		}
		return damage;
	}

	@Override
	public void damage(int damage, Object source) {
		int reduced = (int)(damage * 0.4f);
		Buff.affect(this, AttackUp.class, 3f).level(15 * reduced / 85);
		super.damage(reduced, source);
	}

	@Override protected boolean createsCorruptGas() { return false; }

	@Override protected Class<? extends BossRushBoss> nextBoss() { return UIcecorps.class; }

}
