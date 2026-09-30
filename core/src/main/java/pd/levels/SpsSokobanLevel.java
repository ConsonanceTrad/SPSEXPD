package pd.levels;

import pd.actors.mobs.npcs.SpsSokobanSheep;
import pd.actors.hero.Hero;

/** Runtime contract shared by the original SPS Sokoban maps. */
public interface SpsSokobanLevel {
	void afterSheepMoved(SpsSokobanSheep sheep);
	int randomFleecingCell(int start, int maxDistance);
	void resetPuzzle(Hero hero);
}
