package pd.levels;

import pd.Assets;
import pd.Dungeon;
import pd.actors.mobs.Elemental;
import pd.actors.mobs.FlyingProtector;
import pd.actors.mobs.FireElemental;
import pd.actors.mobs.LevelChecker;
import pd.actors.mobs.Eye;
import pd.actors.mobs.Mob;
import pd.actors.mobs.PatrolUAV;
import pd.actors.mobs.Sentinel;
import pd.items.Item;
import pd.items.TriforceOfWisdom;
import pd.items.potions.PotionOfLevitation;
import pd.levels.builders.SpsBspLayout.Room;
import pd.levels.builders.SpsBspLayout.Type;
import pd.mechanics.pathfind.PathFinder;
import pd.plants.BlandfruitBush;
import pd.plants.ReNepenth;
import pd.plants.StarEater;
import render.utils.Random;

public class TriangleWLevel extends SpsTriangleLevel {
	{ color1 = 0x48763c; color2 = 0x59994a; }
	@Override public String tilesTex() { return Assets.Environment.SPS_TILES_BEACH; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_SNOW; }
	@Override protected boolean rewardCollected() { return Dungeon.triforceOfWisdom; }
	@Override protected Item reward() { return new TriforceOfWisdom(); }
	@Override protected int legacyTrialDepth() { return 33; }
	@Override protected int entranceTerrain() { return Terrain.EMPTY; }
	@Override protected Type trialRoomType() { return Type.WISDOM_ROOM; }
	@Override protected boolean usesPassages() { return true; }
	@Override protected void paintTrialRoom(Room room) {
		fillTrialRoom(room, 0, Terrain.WALL);
		fillTrialRoom(room, 1, Terrain.EMPTY);
		int center = (room.left + room.right) / 2 + (room.top + room.bottom) / 2 * width();
		map[center] = Terrain.PEDESTAL;
		int plantCell = trialRoomCell(room, 0);
		switch (Random.Int(3)) {
			case 0: plant(new StarEater.Seed(), plantCell); break;
			case 1: plant(new BlandfruitBush.Seed(), plantCell); break;
			default: plant(new ReNepenth.Seed(), plantCell); break;
		}
		Sentinel sentinel = new Sentinel();
		sentinel.pos = trialRoomCell(room, 0);
		mobs.add(sentinel);
	}
	@Override protected void decorateTrial() {
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.EMPTY_SP && heaps.get(cell) == null && Random.Float() < 0.25f) {
				map[cell] = Terrain.CHASM;
			}
			if (map[cell] == Terrain.EMPTY_SP && heaps.get(cell) == null && Random.Float() < 0.05f) {
				Eye eye = new Eye();
				eye.pos = cell;
				mobs.add(eye);
			}
			if (map[cell] == Terrain.CHASM && heaps.get(cell) == null && Random.Float() < 0.05f) {
				map[cell] = Terrain.EMPTY_DECO;
			}
		}
	}
	@Override protected void createItems() {
		super.createItems();
		int cell = entrance;
		for (int offset : PathFinder.NEIGHBOURS8) {
			int candidate = entrance + offset;
			if (candidate >= 0 && candidate < length() && passable[candidate] && heaps.get(candidate) == null) {
				cell = candidate;
				break;
			}
		}
		drop(new PotionOfLevitation(), cell);
	}
	@Override public Mob createMob() {
		switch (Random.chances(new float[]{1f, 0.2f, 0.2f, 0.4f})) {
			case 1: return new FireElemental();
			case 2: return new LevelChecker();
			case 3: return new PatrolUAV();
			default: return new FlyingProtector();
		}
	}
}
