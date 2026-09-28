/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class DungeonBomb extends Bomb {

	{ image = ItemSpriteSheet.BOMB; }

	@Override
	public void explode(int cell) {
		super.explode(cell);
		boolean terrainAffected = false;
		for (int offset : PathFinder.NEIGHBOURS9) {
			int target = cell + offset;
			if (target < 0 || target >= Dungeon.level.length() || !Dungeon.level.insideMap(target)) continue;
			if (GameScene.hasActiveScene() && Dungeon.level.heroFOV[target]) CellEmitter.get(target).burst(SmokeParticle.FACTORY, 4);
			if (Dungeon.level.flamable[target]) {
				Level.set(target, Terrain.EMBERS, Dungeon.level);
				GameScene.updateMap(target);
				terrainAffected = true;
			} else if (Dungeon.level.map[target] == Terrain.WALL || Dungeon.level.map[target] == Terrain.WALL_DECO) {
				Level.set(target, Terrain.EMPTY, Dungeon.level);
				GameScene.updateMap(target);
				terrainAffected = true;
			}
			Char ch = Actor.findChar(target);
			if (ch == null || !ch.isAlive()) continue;
			int min = Char.hasProp(ch, Char.Property.BOSS) || Char.hasProp(ch, Char.Property.MINIBOSS) ? ch.HT / 10 : ch.HT / 5;
			int max = Char.hasProp(ch, Char.Property.BOSS) || Char.hasProp(ch, Char.Property.MINIBOSS) ? ch.HT / 5 : ch.HT / 4;
			int damage = Random.NormalIntRange(min, Math.max(min, max));
			if (Char.hasProp(ch, Char.Property.BOSS) || Char.hasProp(ch, Char.Property.MINIBOSS)) damage -= Math.max(ch.drRoll(), 0);
			if (damage > 0) ch.damage(damage, this);
		}
		if (terrainAffected) Dungeon.observe();
	}

	@Override public Item random() { return Random.Int(2) == 0 ? this : new DoubleBomb(); }
	@Override public int value() { return 10 * quantity; }

	public static class DoubleBomb extends DungeonBomb {
		{ image = ItemSpriteSheet.DBL_BOMB; stackable = false; }
		@Override public boolean doPickUp(com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero hero, int pos) {
			DungeonBomb bomb = new DungeonBomb();
			bomb.quantity(2);
			return bomb.doPickUp(hero, pos);
		}
	}
}
