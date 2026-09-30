package pd.levels;

import pd.Assets;
import pd.Dungeon;
import pd.actors.blobs.Blob;
import pd.actors.blobs.weather.WeatherOfRain;
import pd.actors.blobs.weather.WeatherOfSand;
import pd.actors.blobs.weather.WeatherOfSnow;
import pd.actors.blobs.weather.WeatherOfSun;
import pd.actors.mobs.Brute;
import pd.actors.mobs.GoldOrc;
import pd.actors.mobs.Greatmoss;
import pd.actors.mobs.Mob;
import pd.actors.mobs.Orc;
import pd.items.Item;
import pd.items.TriforceOfPower;
import pd.items.Vialupdater;
import pd.levels.builders.SpsBspLayout.Room;
import pd.levels.builders.SpsBspLayout.Type;
import pd.plants.NutPlant;
import pd.plants.Seedpod;
import render.utils.math.Random;

public class TrianglePLevel extends SpsTriangleLevel {
	{ color1 = 0x4b6636; color2 = 0xf2f2f2; }
	@Override public String tilesTex() { return Assets.Environment.SPS_TILES_FOREST; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_SEWERS; }
	@Override protected boolean rewardCollected() { return Dungeon.triforceOfPower; }
	@Override protected Item reward() { return new TriforceOfPower(); }
	@Override protected int legacyTrialDepth() { return 32; }
	@Override protected int legacyWaterClustering() { return 4; }
	@Override protected int legacyGrassClustering() { return 3; }
	@Override protected Type trialRoomType() { return Type.POWER_ROOM; }
	@Override protected boolean usesPassages() { return true; }
	@Override protected void paintTrialRoom(Room room) {
		fillTrialRoom(room, 0, Terrain.WALL);
		int plantCell = trialRoomCell(room, 0);
		if (Random.Int(2) == 0) {
			fillTrialRoom(room, 1, Terrain.HIGH_GRASS);
			fillTrialRoom(room, 2, Terrain.WATER);
			plant(new Seedpod.Seed(), plantCell);
		} else {
			fillTrialRoom(room, 1, Terrain.WATER);
			fillTrialRoom(room, 2, Terrain.HIGH_GRASS);
			plant(new NutPlant.Seed(), plantCell);
		}
		Greatmoss moss = new Greatmoss();
		moss.pos = trialRoomCell(room, 0);
		mobs().add(moss);
		if (Random.Int(5) == 0) addTrialWeather(room, Random.Int(4));
	}

	private void addTrialWeather(Room room, int type) {
		Blob weather = type == 0 ? new WeatherOfRain() : type == 1 ? new WeatherOfSand()
				: type == 2 ? new WeatherOfSnow() : new WeatherOfSun();
		for (int y = room.top + 1; y < room.bottom; y++) {
			for (int x = room.left + 1; x < room.right; x++) weather.seed(this, x + y * width(), 1);
		}
		blobs.put(weather.getClass(), weather);
	}
	@Override protected void createItems() { addItemToSpawn(new Vialupdater()); super.createItems(); }
	@Override public Mob createMob() {
		switch (Random.chances(new float[]{1f, 0.05f, 0.5f, 0.4f})) {
			case 1: return new GoldOrc();
			case 2: return new Greatmoss();
			case 3: return new Brute();
			default: return new Orc();
		}
	}
}
