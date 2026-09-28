package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.SpsSokobanSheep;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;

/** Runtime contract shared by the original SPS Sokoban maps. */
public interface SpsSokobanLevel {
	void afterSheepMoved(SpsSokobanSheep sheep);
	int randomFleecingCell(int start, int maxDistance);
	void resetPuzzle(Hero hero);
}
