/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.bombs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Roots;
import pd.sprites.ItemSpriteSheet;
import watabou.utils.BArray;
import watabou.utils.PathFinder;

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
