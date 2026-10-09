/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels;

import pd.Dungeon;
import pd.actors.blobs.weather.WeatherOfDead;
import pd.actors.blobs.weather.WeatherOfRain;
import pd.actors.blobs.weather.WeatherOfSun;
import pd.actors.mobs.Mob;
import pd.items.Generator;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.Item;
import pd.items.Stylus;
import pd.items.Weightstone;
import pd.items.misc.LuckyBadge;
import pd.items.consum.potions.PotionOfHealing;
import pd.items.consum.scrolls.ScrollOfMagicalInfusion;
import pd.items.consum.scrolls.ScrollOfUpgrade;
import pd.levels.builders.SpsBspLayout.Room;
import pd.levels.builders.SpsBspLayout.Type;
import pd.levels.features.LevelTransition;
import pd.levels.painters.Painter;
import render.utils.math.Random;

import java.util.ArrayList;

abstract class SpsTriangleLevel extends SpsRegularLevel {
	@Override protected Painter painter() { return null; }

	void prepareLegacyTrial() {
		GroundItems.addItemToSpawn( this, Generator.random(Generator.Category.FOOD));
		GroundItems.addItemToSpawn( this, Generator.random(Generator.Category.FOOD));
		GroundItems.addItemToSpawn( this, new ScrollOfUpgrade());
		if (Random.Int(2) == 0) {
			GroundItems.addItemToSpawn( this, new Stylus());
			GroundItems.addItemToSpawn( this, new Weightstone());
		}
		if (Random.Float() < LuckyBadge.rareRewardChance(LuckyBadge.luckBonus(Dungeon.hero))) {
			//SPSEXPD: 稀有奖励改三选一，升级/魔力灌注卷轴作为高稀有刷新物（与 FloorFeeling 一致）
			GroundItems.addItemToSpawn( this, Random.oneOf(
					new ScrollOfUpgrade(),
					new ScrollOfMagicalInfusion(),
					new PotionOfHealing()) );
		}
		if (legacyTrialDepth() == 31) {
			feeling = Feeling.DARK;
			viewDistance = 3;
		} else if (legacyTrialDepth() == 33) {
			feeling = Feeling.TRAP;
		}
	}

	@Override
	protected boolean paintLegacyDepthStandardRoom(Room room) {
		switch (legacyTrialDepth()) {
			case 31:
				paintCourageTomb(room);
				return true;
			case 32:
				paintPowerForce(room);
				return true;
			case 33:
				paintStudy(room, true);
				return true;
			default:
				return false;
		}
	}

	private void paintCourageTomb(Room room) {
		fillTrialRoom(room, 1, Terrain.GRASS);
		int w = room.width() - 1;
		int h = room.height() - 1;
		int graves = Math.max(w, h) / 2;
		if (graves <= 0) return;
		int prize = Random.Int(graves);
		int shift = Random.Int(2);
		for (int i = 0; i < graves; i++) {
			int cell = w > h
					? room.left + 1 + shift + i * 2
						+ (room.top + 2 + Random.Int(h - 2)) * width()
					: room.left + 2 + Random.Int(w - 2)
						+ (room.top + 1 + shift + i * 2) * width();
			drop(i == prize ? Generator.random() : new Gold().random(), cell).type = Heap.Type.REMAINS;
		}
		if (Random.Int(5) == 0) addWeather(room, WeatherOfDead.class);
	}

	private void paintPowerForce(Room room) {
		fillTrialRoom(room, 1, Terrain.EMPTY);
		if (room.width() > room.height()) {
			for (int x = room.left + 2; x < room.right; x += 2) {
				for (int y = room.top + 1; y < room.bottom; y++) {
					map[x + y * width()] = Terrain.OLD_HIGH_GRASS;
				}
			}
		} else {
			for (int y = room.top + 2; y < room.bottom; y += 2) {
				for (int x = room.left + 1; x < room.right; x++) {
					map[x + y * width()] = Terrain.OLD_HIGH_GRASS;
				}
			}
		}
		if (Random.Int(3) == 0) {
			addWeather(room, Random.Int(2) == 0 ? WeatherOfRain.class : WeatherOfSun.class);
		}
	}

	@Override
	protected void assignLegacyRoomTypes() {
		legacyLayout.entrance.type = Type.ENTRANCE;
		legacyLayout.exit.type = Type.EXIT;
		Type special = trialRoomType();
		boolean specialAssigned = false;

		if (special != null) {
			for (Room room : legacyLayout.rooms) {
				if (room.type != Type.NULL || room.connected.size() != 1) continue;
				if (!specialAssigned && room.width() > 3 && room.height() > 3) {
					room.type = special;
					specialAssigned = true;
				} else if (Random.Int(2) == 0) {
					ArrayList<Room> candidates = new ArrayList<>();
					for (Room neighbour : room.neighbours) {
						if (!room.connected.containsKey(neighbour) && neighbour.type == Type.NULL) {
							candidates.add(neighbour);
						}
					}
					if (candidates.size() > 1) room.connect(Random.element(candidates));
				}
			}
		}

		int standardRooms = 0;
		for (Room room : legacyLayout.rooms) {
			if (room.type != Type.NULL) continue;
			int connections = room.connected.size();
			if (connections == 0) continue;
			if (Random.Int(connections * connections) == 0) {
				room.type = Type.STANDARD;
				standardRooms++;
			} else {
				room.type = Type.TUNNEL;
			}
		}

		ArrayList<Room> tunnels = new ArrayList<>();
		for (Room room : legacyLayout.rooms) if (room.type == Type.TUNNEL) tunnels.add(room);
		Random.shuffle(tunnels);
		for (Room room : tunnels) {
			if (standardRooms >= 4) break;
			room.type = Type.STANDARD;
			standardRooms++;
		}
		if (usesPassages()) {
			for (Room room : legacyLayout.rooms) if (room.type == Type.TUNNEL) room.type = Type.PASSAGE;
		}
	}

	@Override
	protected void afterLegacyRoomsPainted() {
		Type special = trialRoomType();
		if (special == null) return;
		for (Room room : legacyLayout.rooms) {
			if (room.type == special) paintTrialRoom(room);
		}
	}

	protected Type trialRoomType() { return null; }
	protected boolean usesPassages() { return false; }
	protected void paintTrialRoom(Room room) { }

	protected void fillTrialRoom(Room room, int inset, int terrain) {
		for (int y = room.top + inset; y <= room.bottom - inset; y++) {
			for (int x = room.left + inset; x <= room.right - inset; x++) {
				map[x + y * width()] = terrain;
			}
		}
	}

	protected int trialRoomCell(Room room, int margin) {
		return room.randomCell(width(), margin);
	}

	public int trialRoomCount() {
		int result = 0;
		Type special = trialRoomType();
		if (special != null && legacyLayout != null) {
			for (Room room : legacyLayout.rooms) if (room.type == special) result++;
		}
		return result;
	}
	@Override protected boolean build() {
		if (!super.build()) return false;
		int start = entrance;
		int goal = exit;
		transitions.clear();
		transitions.add(new LevelTransition(this, start, LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
		map[start] = entranceTerrain();
		map[goal] = Terrain.PEDESTAL;
		if (!rewardCollected()) drop(reward(), goal);
		decorateTrial();
		return true;
	}

	protected int entranceTerrain() { return Terrain.PEDESTAL; }
	protected void decorateTrial() { }
	protected abstract boolean rewardCollected();
	protected abstract Item reward();
	protected abstract int legacyTrialDepth();

	@Override protected void createMobs() {
		int count = 15 + legacyTrialDepth() % 3 + Random.Int(3);
		for (int i = 0; i < count; i++) {
			Mob mob = createMob();
			int cell = -1;
			for (int tries = 0; tries < 20 && cell < 0; tries++) {
				int candidate = randomRespawnCell(mob);
				if (candidate >= 0 && mobs().findMob(candidate) == null) cell = candidate;
			}
			if (cell < 0) {
				for (int candidate = 0; candidate < length(); candidate++) {
					if (candidate != entrance && candidate != exit && passable[candidate]
							&& mobs().findMob(candidate) == null) {
						cell = candidate;
						break;
					}
				}
			}
			if (cell < 0) break;
			mob.pos = cell;
			mobs().add(mob);
		}
	}

	@Override protected int nTraps() { return 0; }
	@Override protected float legacyChasmFill() { return 0f; }
}
