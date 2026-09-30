/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels;

import pd.Assets;
import pd.actors.mobs.Mob;
import pd.actors.mobs.SpsChallengeMobPool;
import render.utils.Random;

/** SPS-PD 0.9.8 forest arena (challenge-book room 1). */
public class SewerChallengeLevel extends SpsRegionChallengeLevel {
	{
		color1 = 0x48763c;
		color2 = 0x59994a;
		viewDistance = 6;
	}

	@Override protected void decorateTerrain() {
		super.decorateTerrain();
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.20f) map[cell] = Terrain.HIGH_GRASS;
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.25f) map[cell] = Terrain.GRASS;
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.30f) map[cell] = Terrain.SHRUB;
		}
	}

	@Override public String tilesTex() { return Assets.Environment.SPS_TILES_FOREST; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_SEWERS; }
	@Override protected Mob createChallengeMob() { return SpsChallengeMobPool.forest(); }
	@Override protected int challengeMobLimit() { return 10; }
}
