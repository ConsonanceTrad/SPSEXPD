package pd.levels;

import pd.actors.hero.Hero;
import pd.actors.mobs.npcs.SpsSokobanSheep;

/** Runtime contract shared by the original SPS Sokoban maps. */
public interface SpsSokobanLevel {
	void afterSheepMoved(SpsSokobanSheep sheep);
	int randomFleecingCell(int start, int maxDistance);
	void resetPuzzle(Hero hero);
}
