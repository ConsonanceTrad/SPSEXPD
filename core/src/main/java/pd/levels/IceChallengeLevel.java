/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels;

import pd.Assets;
import pd.actors.mobs.IceBall;
import pd.actors.mobs.Mob;
import pd.items.Dewdrop;
import render.utils.Random;

/** SPS-PD 0.9.8 frost arena (challenge-book room 0). */
public class IceChallengeLevel extends SpsRegionChallengeLevel {
	{
		color1 = 0x4b6636;
		color2 = 0xf2f2f2;
	}

	@Override protected void decorateTerrain() {
		super.decorateTerrain();
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.10f) map[cell] = Terrain.STATUE;
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.10f) map[cell] = Terrain.GRASS;
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.10f) map[cell] = Terrain.WATER;
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.10f) map[cell] = Terrain.DOOR;
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.10f) map[cell] = Terrain.OLD_HIGH_GRASS;
		}
	}

	@Override protected void createItems() {
		for (int i = 0; i < 10; i++) drop(new Dewdrop(), randomDestination(null));
	}

	@Override public String tilesTex() { return Assets.Environment.SPS_TILES_SNOW_TOWN; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_SNOW; }
	@Override protected Mob createChallengeMob() { return new IceBall(); }
	@Override protected int challengeMobLimit() { return 10; }
}
