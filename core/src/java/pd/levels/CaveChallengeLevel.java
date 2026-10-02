/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels;

import pd.Assets;
import pd.actors.mobs.Mob;
import pd.actors.mobs.SpsChallengeMobPool;
import pd.items.Heap;
import pd.items.equipment.bombs.FishingBomb;
import render.utils.math.Random;

/** SPS-PD 0.9.8 flooded beach arena (challenge-book room 3). */
public class CaveChallengeLevel extends SpsRegionChallengeLevel {
	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
		viewDistance = 8;
	}

	@Override protected void shapeTerrain() {
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.95f) map[cell] = Terrain.WATER;
		}
		boolean[] patch = Patch.generate(width(), height(), 0.45f, 6, false);
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.WATER && patch[cell]) map[cell] = Terrain.EMPTY;
		}
	}

	@Override protected void createItems() {
		drop(new FishingBomb(5), legacyEntranceCell() + 1).type = Heap.Type.CHEST;
	}

	@Override public String tilesTex() { return Assets.Environment.SPS_TILES_BEACH; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_PRISON; }
	@Override protected Mob createChallengeMob() { return SpsChallengeMobPool.cave(); }
	@Override protected int challengeMobLimit() { return 30; }
	@Override protected boolean challengeMobsRequireWater() { return true; }
}
