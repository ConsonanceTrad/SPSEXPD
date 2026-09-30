/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels;

import pd.Assets;
import pd.actors.mobs.Mob;
import pd.actors.mobs.SpsChallengeMobPool;
import pd.items.Gold;
import com.watabou.utils.Random;

/** SPS-PD 0.9.8 treasure-vault arena (challenge-book room 4). */
public class CityChallengeLevel extends SpsRegionChallengeLevel {
	{
		color1 = 0x4b6636;
		color2 = 0xf2f2f2;
	}

	@Override protected void decorateTerrain() {
		super.decorateTerrain();
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.15f) map[cell] = Terrain.STATUE;
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.10f) map[cell] = Terrain.HIGH_GRASS;
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.10f) map[cell] = Terrain.GRASS;
		}
	}

	@Override protected void createItems() {
		for (int i = 0; i < 10; i++) drop(new Gold(Random.Int(10, 30)), randomDestination(null));
	}

	@Override public String tilesTex() { return Assets.Environment.SPS_TILES_VAULT; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_CITY; }
	@Override protected Mob createChallengeMob() { return SpsChallengeMobPool.city(); }
	@Override protected int challengeMobLimit() { return 16; }
}
