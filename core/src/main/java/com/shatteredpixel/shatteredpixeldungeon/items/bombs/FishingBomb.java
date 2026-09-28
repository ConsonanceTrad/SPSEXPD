/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class FishingBomb extends Bomb {

	{
		image = ItemSpriteSheet.FISHING_BOMB;
	}

	public FishingBomb() { this(1); }
	public FishingBomb(int quantity) { this.quantity = quantity; }

	@Override public void explode(int cell) {
		super.explode(cell);
		PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), 2);
		for (int target = 0; target < PathFinder.distance.length; target++) {
			if (PathFinder.distance[target] == Integer.MAX_VALUE) continue;
			if (Dungeon.level.heroFOV[target]) {
				CellEmitter.get(target).burst(SmokeParticle.FACTORY, 4);
			}
			Char ch = Actor.findChar(target);
			if (!(ch instanceof Mob) || ch instanceof NPC) continue;
			int destination = randomDryCell();
			if (destination == -1) {
				GLog.w(Messages.get(this, "no_tp"));
				continue;
			}
			ch.pos = destination;
			if (ch.sprite != null) {
				ch.sprite.place(destination);
				ch.sprite.visible = Dungeon.level.heroFOV[destination];
			}
			GLog.i(Messages.get(this, "tp"));
		}
		Dungeon.observe();
	}

	private int randomDryCell() {
		for (int tries = 0; tries < 200; tries++) {
			int cell = Random.Int(Dungeon.level.length());
			int terrain = Dungeon.level.map[cell];
			if (Dungeon.level.passable[cell] && Actor.findChar(cell) == null
					&& (terrain == Terrain.EMPTY || terrain == Terrain.EMPTY_SP
					|| terrain == Terrain.GRASS || terrain == Terrain.HIGH_GRASS)) return cell;
		}
		return -1;
	}

	@Override public FishingBomb random() { return this; }
	@Override public int value() { return 20 * quantity; }
}
