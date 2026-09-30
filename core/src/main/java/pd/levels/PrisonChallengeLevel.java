/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels;

import pd.Assets;
import pd.actors.mobs.Mob;
import pd.actors.mobs.SpsChallengeMobPool;
import pd.items.Heap;
import pd.items.bombs.LightBomb;
import com.watabou.utils.Random;

/** SPS-PD 0.9.8 overgrown prison arena (challenge-book room 2). */
public class PrisonChallengeLevel extends SpsRegionChallengeLevel {
	{
		color1 = 0x6a723d;
		color2 = 0x88924c;
		viewDistance = 8;
	}

	@Override protected void decorateTerrain() {
		super.decorateTerrain();
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.20f) map[cell] = Terrain.HIGH_GRASS;
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.25f) map[cell] = Terrain.GRASS;
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.30f) map[cell] = Terrain.SHRUB;
		}
	}

	@Override protected void createItems() {
		drop(new LightBomb(5), randomDestination(null)).type = Heap.Type.CHEST;
	}

	@Override public String tilesTex() { return Assets.Environment.SPS_TILES_PRISON_LEGACY; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_PRISON; }
	@Override protected Mob createChallengeMob() { return SpsChallengeMobPool.prison(); }
	@Override protected int challengeMobLimit() { return 16; }
	@Override protected float challengeRespawnCooldown() { return 30f; }
}
