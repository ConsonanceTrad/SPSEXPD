/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;

public class EarthBomb extends Bomb {

	{ image = ItemSpriteSheet.EARTH_BOMB; }

	@Override
	public void explode(int cell) {
		super.explode(cell);
		if (Dungeon.level == null || !Dungeon.level.insideMap(cell)) return;
		PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), 2);
		for (int target = 0; target < PathFinder.distance.length; target++) {
			if (PathFinder.distance[target] == Integer.MAX_VALUE) continue;
			Char ch = Actor.findChar(target);
			if (ch != null && ch.isAlive()) applyEarthEffects(ch);
		}
	}

	public static void applyEarthEffects(Char target) {
		Buff.prolong(target, Roots.class, 5f);
		Buff.affect(target, Ooze.class).set(10f);
	}

	@Override public EarthBomb random() { return this; }
	@Override public int value() { return 20 * quantity; }
}
