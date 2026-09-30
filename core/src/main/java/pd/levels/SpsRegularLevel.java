/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 */

package pd.levels;

import pd.Dungeon;
import pd.Statistics;
import pd.actors.buffs.Buff;
import pd.actors.buffs.ExProtect;
import pd.actors.buffs.GlassShield;
import pd.actors.buffs.MagicArmor;
import pd.actors.buffs.ShieldArmor;
import pd.actors.blobs.Blob;
import pd.actors.blobs.weather.WeatherOfDead;
import pd.actors.blobs.weather.WeatherOfQuite;
import pd.actors.blobs.weather.WeatherOfRain;
import pd.actors.blobs.weather.WeatherOfSand;
import pd.actors.blobs.weather.WeatherOfSnow;
import pd.actors.blobs.weather.WeatherOfSun;
import pd.actors.mobs.Mob;
import pd.actors.mobs.SpsExitMobs;
import pd.items.Generator;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.Item;
import pd.items.YellowDewdrop;
import pd.items.artifacts.DriedRose;
import pd.items.food.SmallMeat;
import pd.items.keys.GoldenKey;
import pd.items.potions.PotionOfLevitation;
import pd.items.quest.ChallengeJournal;
import pd.items.quest.MapFragment;
import pd.items.reward.BoundReward;
import pd.items.scrolls.Scroll;
import pd.levels.builders.SpsBspLayout;
import pd.levels.builders.SpsBspLayout.Door;
import pd.levels.builders.SpsBspLayout.Room;
import pd.levels.builders.SpsBspLayout.Type;
import pd.levels.features.LevelTransition;
import pd.levels.rooms.standard.EmptyRoom;
import pd.levels.rooms.standard.entrance.EntranceRoom;
import pd.levels.rooms.standard.exit.ExitRoom;
import pd.levels.rooms.special.CryptRoom;
import pd.levels.rooms.special.GardenRoom;
import pd.levels.rooms.special.LibraryRoom;
import pd.levels.rooms.special.LaboratoryRoom;
import pd.levels.rooms.special.PoolRoom;
import pd.levels.rooms.special.SpecialRoom;
import pd.levels.rooms.special.SpsShopRoom;
import pd.levels.rooms.special.SpsHiddenShopRoom;
import pd.levels.rooms.special.SpsBarricadedRoom;
import pd.levels.rooms.special.SpsGlassRoom;
import pd.levels.rooms.special.SpsCookingRoom;
import pd.levels.rooms.special.SpsJungleRoom;
import pd.levels.rooms.special.SpsMaterialRoom;
import pd.levels.rooms.special.SpsMemoryRoom;
import pd.levels.rooms.special.SpsMagicWellRoom;
import pd.levels.rooms.special.SpsPitRoom;
import pd.levels.rooms.special.SpsRuinRoom;
import pd.levels.rooms.special.SpsWishPoolRoom;
import pd.levels.rooms.special.SpsTentRoom;
import pd.levels.rooms.special.StatueRoom;
import pd.levels.rooms.special.StorageRoom;
import pd.levels.rooms.special.TreasuryRoom;
import pd.levels.traps.Trap;
import pd.levels.traps.ConfusionTrap;
import pd.levels.traps.DisintegrationTrap;
import pd.levels.traps.ExplosiveTrap;
import pd.levels.traps.GrimTrap;
import pd.levels.traps.ParalyticTrap;
import pd.levels.traps.SpearTrap;
import pd.levels.traps.SummoningTrap;
import pd.levels.traps.ToxicTrap;
import pd.levels.traps.VenomTrap;
import pd.levels.traps.bufftrap.DarkBuff2Trap;
import pd.levels.traps.bufftrap.EarthBuff2Trap;
import pd.levels.traps.bufftrap.FireBuff2Trap;
import pd.levels.traps.bufftrap.IceBuff2Trap;
import pd.levels.traps.bufftrap.LightBuff2Trap;
import pd.levels.traps.bufftrap.ShockBuff2Trap;
import pd.levels.traps.damagetrap.FireDamageTrap;
import pd.plants.Plant;
import com.watabou.utils.Point;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;
import com.watabou.utils.Reflection;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.HashSet;

/**
 * Ordinary-floor generation compatible with the SPS-PD 0.9.8 BSP topology.
 * Shattered's room classes remain available for branches and retained content.
 */
public abstract class SpsRegularLevel extends RegularLevel {

	public static final int LEGACY_WIDTH = 48;
	public static final int LEGACY_HEIGHT = 48;
	private static final int LAYOUT_ATTEMPTS = 64;

	private static final Type[] SPECIAL_TYPES = {
			Type.RUIN_ROOM, Type.CRYPT, Type.POOL, Type.GARDEN, Type.LIBRARY,
			Type.MATERIAL, Type.JUNGLE, Type.TRAPS, Type.STORAGE, Type.STATUE,
			Type.COOKING, Type.VAULT, Type.TENTROOM
	};
	private static final Type[] HIDDEN_TYPES = {
			Type.MAGIC_WELL, Type.MEMORY, Type.BARRICADED, Type.HIDE_SHOP,
			Type.MAGIC_WELL, Type.HIDE_SHOP, Type.WISH_POOL, Type.GLASSROOM,
			Type.PRISON_PIT, Type.BARRICADED, Type.WISH_POOL, Type.PRISON_PIT
	};
	private static final String LEGACY_SPECIAL_ROOMS = "sps_special_rooms";
	private static final ArrayList<Type> legacySpecialRotation = new ArrayList<>();
	@SuppressWarnings("unchecked")
	private static final Class<? extends Trap>[] LEGACY_TRAP_ROOM_TYPES = new Class[]{
			ToxicTrap.class, ConfusionTrap.class, ExplosiveTrap.class, ParalyticTrap.class,
			VenomTrap.class, DisintegrationTrap.class, GrimTrap.class, SpearTrap.class,
			SummoningTrap.class, FireBuff2Trap.class, IceBuff2Trap.class, EarthBuff2Trap.class,
			ShockBuff2Trap.class, LightBuff2Trap.class, DarkBuff2Trap.class
	};

	protected SpsBspLayout.Result legacyLayout;
	private int legacySecretDoors;

	@Override
	protected boolean build() {
		setSize(LEGACY_WIDTH, LEGACY_HEIGHT);
		legacyLayout = generateLegacyLayout();
		if (legacyLayout == null) return false;

		assignLegacyRoomTypes();
		placeLegacyDoors();
		paintLegacyRooms();
		afterLegacyRoomsPainted();
		paintLegacyWater();
		paintLegacyGrass();
		paintLegacyChasms();
		placeLegacyTraps();
		decorateLegacyFloor();
		buildRoomAdapters();
		return legacyPathExists();
	}

	protected SpsBspLayout.Result generateLegacyLayout() {
		return SpsBspLayout.generate(LEGACY_WIDTH, LEGACY_HEIGHT, LAYOUT_ATTEMPTS);
	}

	protected void afterLegacyRoomsPainted() {
	}

	protected void assignLegacyRoomTypes() {
		Room entrance = legacyLayout.entrance;
		Room exit = legacyLayout.exit;
		entrance.type = Type.ENTRANCE;
		exit.type = Type.EXIT;

		if (legacySpecialRotation.isEmpty()) initLegacySpecialRooms();
		ArrayList<Type> specials = new ArrayList<>(legacySpecialRotation);
		if (Dungeon.legacyDepth() > 50 && Dungeon.legacyDepth() < 100) specials.clear();
		ArrayList<Type> secrets = new ArrayList<>(Arrays.asList(HIDDEN_TYPES));
		int specialRooms = 0;
		int hiddenRooms = 0;

		for (Room room : legacyLayout.rooms) {
			if (room.type != Type.NULL || room.connected.size() != 1) continue;
			if (!secrets.isEmpty() && room.width() > 5 && room.height() > 5
					&& hiddenRooms == 0) {
				room.type = secrets.get(Random.Int(secrets.size()));
				useLegacySpecialRoom(room.type);
				hiddenRooms++;
			} else if (!specials.isEmpty() && room.width() > 5 && room.height() > 5
					&& Random.Int(Math.max(1, specialRooms)) < 3) {
				if (Dungeon.legacyDepth() % 5 == 2 && specials.contains(Type.COOKING)) {
					room.type = Type.COOKING;
				} else if (Dungeon.legacyDepth() % 5 == 3 && specials.contains(Type.RUIN_ROOM)) {
					room.type = Type.RUIN_ROOM;
				} else {
					int size = specials.size();
					room.type = specials.get(Math.min(Random.Int(size), Random.Int(size)));
				}
				useLegacySpecialRoom(room.type);
				specials.remove(room.type);
				specialRooms++;
			} else if (Random.Int(2) == 0) {
				ArrayList<Room> candidates = new ArrayList<>();
				for (Room neighbour : room.neighbours) {
					if (!room.connected.containsKey(neighbour)
							&& !isSpecial(neighbour.type) && !isHidden(neighbour.type)) {
						candidates.add(neighbour);
					}
				}
				if (candidates.size() > 1) room.connect(Random.element(candidates));
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
				room.type = isPassageRegion() ? Type.PASSAGE : Type.TUNNEL;
			}
		}

		ArrayList<Room> tunnels = new ArrayList<>();
		for (Room room : legacyLayout.rooms) {
			if (room.type == Type.TUNNEL || room.type == Type.PASSAGE) tunnels.add(room);
		}
		Random.shuffle(tunnels);
		for (Room room : tunnels) {
			if (standardRooms >= 4) break;
			room.type = Type.STANDARD;
			standardRooms++;
		}
	}

	public static void initLegacySpecialRooms() {
		legacySpecialRotation.clear();
		legacySpecialRotation.addAll(Arrays.asList(SPECIAL_TYPES));
		for (int i = 0; i < legacySpecialRotation.size() - 1; i++) {
			int target = Random.Int(i, legacySpecialRotation.size());
			if (target != i) Collections.swap(legacySpecialRotation, i, target);
		}
	}

	private static void useLegacySpecialRoom(Type type) {
		if (legacySpecialRotation.remove(type)) legacySpecialRotation.add(type);
	}

	public static void storeLegacySpecialRooms(Bundle bundle) {
		String[] names = new String[legacySpecialRotation.size()];
		for (int i = 0; i < names.length; i++) names[i] = legacySpecialRotation.get(i).name();
		bundle.put(LEGACY_SPECIAL_ROOMS, names);
	}

	public static void restoreLegacySpecialRooms(Bundle bundle) {
		legacySpecialRotation.clear();
		if (!bundle.contains(LEGACY_SPECIAL_ROOMS)) {
			initLegacySpecialRooms();
			return;
		}
		for (String name : bundle.getStringArray(LEGACY_SPECIAL_ROOMS)) {
			try {
				Type type = Type.valueOf(name);
				if (Arrays.asList(SPECIAL_TYPES).contains(type)
						&& !legacySpecialRotation.contains(type)) legacySpecialRotation.add(type);
			} catch (IllegalArgumentException ignored) {
				// Ignore removed room types from malformed or future saves.
			}
		}
		for (Type type : SPECIAL_TYPES) {
			if (!legacySpecialRotation.contains(type)) legacySpecialRotation.add(type);
		}
	}

	private boolean isPassageRegion() {
		return this instanceof PrisonLevel || this instanceof CityLevel;
	}

	private static boolean isSpecial(Type type) {
		for (Type special : SPECIAL_TYPES) if (type == special) return true;
		return false;
	}

	private static boolean isHidden(Type type) {
		for (Type hidden : HIDDEN_TYPES) if (type == hidden) return true;
		return false;
	}

	private void placeLegacyDoors() {
		for (Room room : legacyLayout.rooms) {
			for (Room neighbour : room.connected.keySet()) {
				if (room.connected.get(neighbour) != null) continue;
				Rect overlap = room.intersect(neighbour);
				Door door;
				if (overlap.width() == 0) {
					if (overlap.bottom - overlap.top < 2) continue;
					door = new Door(overlap.left, Random.Int(overlap.top + 1, overlap.bottom));
				} else {
					if (overlap.right - overlap.left < 2) continue;
					door = new Door(Random.Int(overlap.left + 1, overlap.right), overlap.top);
				}
				room.connected.put(neighbour, door);
				neighbour.connected.put(room, door);
			}
		}
	}

	private void paintLegacyRooms() {
		for (Room room : legacyLayout.rooms) {
			switch (room.type) {
				case NULL:
					if (feeling == Feeling.CHASM && Random.Int(2) == 0) fill(room, Terrain.WALL);
					break;
				case TUNNEL:
					paintTunnel(room);
					break;
				case PASSAGE:
					paintPassage(room);
					break;
				case ENTRANCE:
					paintOpenRoom(room, Door.Type.REGULAR);
					entrance = randomInteriorCell(room, 1);
					map[entrance] = Terrain.ENTRANCE;
					transitions.add(new LevelTransition(this, entrance,
							Dungeon.depth == 1 ? LevelTransition.Type.SURFACE
									: LevelTransition.Type.REGULAR_ENTRANCE));
					placeLegacyEntrancePlant(room);
					break;
				case EXIT:
					paintOpenRoom(room, Door.Type.REGULAR);
					placeLegacyExitGuard(room);
					exit = randomInteriorCell(room, 1);
					map[exit] = Terrain.EXIT;
					transitions.add(new LevelTransition(this, exit, LevelTransition.Type.REGULAR_EXIT));
					break;
				case STANDARD:
					paintLegacyStandardRoom(room);
					break;
				default:
					paintLegacySpecialRoom(room);
					break;
			}
		}

		for (Room room : legacyLayout.rooms) paintDoors(room);
	}

	private void placeLegacyExitGuard(Room room) {
		if (Dungeon.branch != 0 || Dungeon.shopOnLevel()) return;
		Mob mob = SpsExitMobs.randomForDepth(Dungeon.depth);
		if (mob == null) return;
		mob.pos = randomInteriorCell(room, 0);
		if (mob.pos < 0) return;
		mob.spsOriginalGeneration = true;
		Buff.affect(mob, ExProtect.class);
		Buff.affect(mob, ShieldArmor.class).level(Dungeon.depth * 5);
		Buff.affect(mob, MagicArmor.class).level(Dungeon.depth * 5);
		mobs.add(mob);
	}

	private void placeLegacyEntrancePlant(Room room) {
		if (Dungeon.branch != 0 || Dungeon.shopOnLevel()) return;
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int y = room.top + 1; y < room.bottom; y++) {
			for (int x = room.left + 1; x < room.right; x++) {
				int cell = x + y * width();
				if (cell != entrance && traps.get(cell) == null && plants.get(cell) == null) {
					candidates.add(cell);
				}
			}
		}
		if (candidates.isEmpty()) return;
		Plant.Seed seed = (Plant.Seed)Generator.random(Generator.Category.SPS_SEED);
		explant(seed, Random.element(candidates));
	}

	private void paintLegacyStandardRoom(Room room) {
		fill(room, Terrain.WALL);
		for (Door door : room.connected.values()) if (door != null) door.set(Door.Type.REGULAR);
		if (paintLegacyDepthStandardRoom(room)) return;

		if (!Dungeon.bossLevel() && Random.Int(2) == 0) {
			switch (Random.Int(7)) {
				case 0:
					if (feeling != Feeling.GRASS) {
						if (Math.min(room.width(), room.height()) >= 5
								&& Math.max(room.width(), room.height()) >= 6) {
							paintGraveyard(room);
							return;
						}
						break;
					}
					// Grass floors fall through to the striped-room variant.
				case 1:
					if (Math.max(room.width(), room.height()) >= 4) {
						paintStriped(room);
						return;
					}
					break;
				case 2:
					if (room.width() >= 6 && room.height() >= 6) {
						paintStudy(room, true);
						return;
					}
					break;
				case 3:
					if (room.width() >= 6 && room.height() >= 6) {
						paintStudy(room, false);
						return;
					}
					break;
				case 4:
					if (feeling != Feeling.WATER) {
						if (room.connected.size() == 2 && room.width() >= 4 && room.height() >= 4) {
							paintBridge(room);
							return;
						}
						break;
					}
					// Water floors fall through to the fissure-room variant.
				case 5:
					if (Dungeon.depth > 1 && Dungeon.depth < 21
							&& !Dungeon.bossLevel(Dungeon.depth + 1)
							&& Math.min(room.width(), room.height()) >= 5) {
						paintFissure(room);
						return;
					}
					break;
				case 6:
					if (Dungeon.depth > 1) {
						paintBurned(room);
						return;
					}
					break;
				default:
					break;
			}
		}
		fill(room.left + 1, room.top + 1, room.right - room.left - 1,
				room.bottom - room.top - 1, Terrain.EMPTY);
		if (Random.Int(5) == 0) addWeather(room, Random.oneOf(
				WeatherOfRain.class, WeatherOfSand.class, WeatherOfSnow.class, WeatherOfSun.class));
	}

	/** Allows legacy branch depths to replace every ordinary room with their fixed theme. */
	protected boolean paintLegacyDepthStandardRoom(Room room) {
		return false;
	}

	private void paintBurned(Room room) {
		Class<?>[] types = {ToxicTrap.class, ConfusionTrap.class, ExplosiveTrap.class,
				ParalyticTrap.class, VenomTrap.class, DisintegrationTrap.class, GrimTrap.class,
				SummoningTrap.class, FireBuff2Trap.class, IceBuff2Trap.class, EarthBuff2Trap.class,
				ShockBuff2Trap.class, LightBuff2Trap.class, DarkBuff2Trap.class};
		Class<?> trapClass = Random.element(types);
		for (int y = room.top + 1; y < room.bottom; y++) {
			for (int x = room.left + 1; x < room.right; x++) {
				int cell = x + y * width();
				int roll = Random.Int(5);
				map[cell] = roll == 0 ? Terrain.EMPTY : roll == 1 ? Terrain.TRAP
						: roll == 2 ? Terrain.SECRET_TRAP : roll == 3 ? Terrain.INACTIVE_TRAP
						: Terrain.EMBERS;
				if (map[cell] == Terrain.TRAP || map[cell] == Terrain.SECRET_TRAP
						|| map[cell] == Terrain.INACTIVE_TRAP) {
					Trap trap = (Trap) Reflection.newInstance(trapClass);
					if (trap == null) {
						map[cell] = Terrain.EMBERS;
						continue;
					}
					if (map[cell] == Terrain.SECRET_TRAP) trap.hide(); else trap.reveal();
					if (map[cell] == Terrain.INACTIVE_TRAP) trap.active = false;
					setTrap(trap, cell);
				}
			}
		}
		if (Random.Int(3) == 0) addWeather(room,
				Random.Int(2) == 0 ? WeatherOfSand.class : WeatherOfSun.class);
	}

	private void paintGraveyard(Room room) {
		fill(room.left + 1, room.top + 1, room.right - room.left - 1,
				room.bottom - room.top - 1, Terrain.GRASS);
		int w = room.width() - 1;
		int h = room.height() - 1;
		int graves = Math.max(w, h) / 2;
		if (graves <= 0) return;
		int prize = Random.Int(graves);
		int shift = Random.Int(2);
		for (int i = 0; i < graves; i++) {
			int x;
			int y;
			if (w > h) {
				x = room.left + 1 + shift + i * 2;
				y = room.top + 2 + Random.Int(h - 2);
			} else {
				x = room.left + 2 + Random.Int(w - 2);
				y = room.top + 1 + shift + i * 2;
			}
			Item item = i == prize ? Generator.random() : new Gold().random();
			drop(item, x + y * width()).type = Heap.Type.TOMB;
		}
		if (Random.Int(4) == 0) addWeather(room, Random.oneOf(
				WeatherOfRain.class, WeatherOfDead.class, WeatherOfSnow.class, WeatherOfSun.class));
	}

	private void paintStriped(Room room) {
		fill(room.left + 1, room.top + 1, room.right - room.left - 1,
				room.bottom - room.top - 1, Terrain.EMPTY_SP);
		if (room.width() > room.height()) {
			for (int x = room.left + 2; x < room.right; x += 2) {
				fill(x, room.top + 1, 1, room.bottom - room.top - 1, Terrain.HIGH_GRASS);
			}
		} else {
			for (int y = room.top + 2; y < room.bottom; y += 2) {
				fill(room.left + 1, y, room.right - room.left - 1, 1, Terrain.HIGH_GRASS);
			}
		}
		if (Random.Int(3) == 0) addWeather(room,
				Random.Int(2) == 0 ? WeatherOfRain.class : WeatherOfSun.class);
	}

	protected void paintStudy(Room room, boolean shelves) {
		if (shelves) {
			fill(room.left + 1, room.top + 1, room.right - room.left - 1,
					room.bottom - room.top - 1, Terrain.BOOKSHELF);
			fill(room.left + 2, room.top + 2, room.right - room.left - 3,
					room.bottom - room.top - 3, Terrain.EMPTY_SP);
		} else {
			fill(room.left + 1, room.top + 1, room.right - room.left - 1,
					room.bottom - room.top - 1, Terrain.EMPTY_SP);
			if (room.width() > room.height()) {
				for (int x = room.left + 2; x < room.right; x += 2)
					fill(x, room.top + 2, 1, room.bottom - room.top - 3, Terrain.BROKEN_DOOR);
			} else {
				for (int y = room.top + 2; y < room.bottom; y += 2)
					fill(room.left + 2, y, room.right - room.left - 3, 1, Terrain.BROKEN_DOOR);
			}
		}
		for (Door door : room.connected.values()) {
			if (door == null) continue;
			int floor = shelves ? Terrain.EMPTY : Terrain.EMPTY_SP;
			if (door.x == room.left) set(door.x + 1, door.y, floor);
			else if (door.x == room.right) set(door.x - 1, door.y, floor);
			else if (door.y == room.top) set(door.x, door.y + 1, floor);
			else if (door.y == room.bottom) set(door.x, door.y - 1, floor);
		}
		if (shelves) {
			Point centerPoint = legacyRoomCenter(room);
			int center = centerPoint.x + centerPoint.y * width();
			map[center] = Terrain.PEDESTAL;
			if (Random.Int(2) != 0) {
				Item item = findPrizeItem();
				if (item != null) {
					drop(item, center);
					return;
				}
			}
			drop(Generator.random(Random.oneOf(Generator.Category.POTION,
					Generator.Category.SCROLL)), center);
		}
		if (Random.Int(5) == 0) addWeather(room, WeatherOfQuite.class);
	}

	private void paintBridge(Room room) {
		fill(room.left + 1, room.top + 1, room.right - room.left - 1,
				room.bottom - room.top - 1,
				!Dungeon.bossLevel() && !Dungeon.bossLevel(Dungeon.depth + 1)
						&& (Dungeon.depth < 22 || Dungeon.depth > 26) && Random.Int(3) == 0
						? Terrain.CHASM : Terrain.WATER);
		Door[] doors = room.connected.values().toArray(new Door[0]);
		if (doors.length != 2 || doors[0] == null || doors[1] == null) return;
		Door a = doors[0];
		Door b = doors[1];
		if ((a.x == room.left && b.x == room.right) || (a.x == room.right && b.x == room.left)) {
			int distance = room.width() / 2;
			drawInside(room, a, distance, Terrain.EMPTY_SP);
			drawInside(room, b, distance, Terrain.EMPTY_SP);
			Point center = legacyRoomCenter(room);
			fill(center.x, Math.min(a.y, b.y), 1, Math.abs(a.y - b.y) + 1, Terrain.EMPTY_SP);
		} else if ((a.y == room.top && b.y == room.bottom)
				|| (a.y == room.bottom && b.y == room.top)) {
			int distance = room.height() / 2;
			drawInside(room, a, distance, Terrain.EMPTY_SP);
			drawInside(room, b, distance, Terrain.EMPTY_SP);
			Point center = legacyRoomCenter(room);
			fill(Math.min(a.x, b.x), center.y, Math.abs(a.x - b.x) + 1, 1, Terrain.EMPTY_SP);
		} else if (a.x == b.x) {
			fill(a.x == room.left ? room.left + 1 : room.right - 1,
					Math.min(a.y, b.y), 1, Math.abs(a.y - b.y) + 1, Terrain.EMPTY_SP);
		} else if (a.y == b.y) {
			fill(Math.min(a.x, b.x), a.y == room.top ? room.top + 1 : room.bottom - 1,
					Math.abs(a.x - b.x) + 1, 1, Terrain.EMPTY_SP);
		} else if (a.y == room.top || a.y == room.bottom) {
			drawInside(room, a, Math.abs(a.y - b.y), Terrain.EMPTY_SP);
			drawInside(room, b, Math.abs(a.x - b.x), Terrain.EMPTY_SP);
		} else if (a.x == room.left || a.x == room.right) {
			drawInside(room, a, Math.abs(a.x - b.x), Terrain.EMPTY_SP);
			drawInside(room, b, Math.abs(a.y - b.y), Terrain.EMPTY_SP);
		}
		for (Door door : doors) door.set(Door.Type.REGULAR);
		if (Random.Int(5) == 0) addWeather(room, Random.oneOf(
				WeatherOfRain.class, WeatherOfSand.class, WeatherOfSnow.class, WeatherOfSun.class));
	}

	private void paintFissure(Room room) {
		fill(room.left + 1, room.top + 1, room.right - room.left - 1,
				room.bottom - room.top - 1, Terrain.EMPTY);
		for (int y = room.top + 2; y < room.bottom - 1; y++) {
			for (int x = room.left + 2; x < room.right - 1; x++) {
				int vertical = Math.min(y - room.top, room.bottom - y);
				int horizontal = Math.min(x - room.left, room.right - x);
				if (Math.min(vertical, horizontal) > 2 || Random.Int(2) == 0)
					set(x, y, Terrain.STATUE_SP);
			}
		}
		if (Random.Int(5) == 0) addWeather(room, Random.oneOf(
				WeatherOfRain.class, WeatherOfSand.class, WeatherOfSnow.class, WeatherOfSun.class));
	}

	protected void addWeather(Room room, Class<? extends Blob> type) {
		Blob weather = blobs.get(type);
		if (weather == null) weather = Reflection.newInstance(type);
		if (weather == null) return;
		for (int y = Math.max(0, room.top + 1); y < Math.min(height(), room.bottom); y++) {
			for (int x = Math.max(0, room.left + 1); x < Math.min(width(), room.right); x++) {
				weather.seed(this, x + y * width(), 1);
			}
		}
		blobs.put(type, weather);
	}

	private void drawInside(Room room, Door door, int distance, int terrain) {
		int stepX = door.x == room.left ? 1 : door.x == room.right ? -1 : 0;
		int stepY = door.y == room.top ? 1 : door.y == room.bottom ? -1 : 0;
		int x = door.x + stepX;
		int y = door.y + stepY;
		for (int i = 0; i < distance; i++) {
			set(x, y, terrain);
			x += stepX;
			y += stepY;
		}
	}

	private void paintOpenRoom(Room room, Door.Type doorType) {
		fill(room, Terrain.WALL);
		fill(room.left + 1, room.top + 1, room.right - room.left - 1,
				room.bottom - room.top - 1, Terrain.EMPTY);
		for (Door door : room.connected.values()) if (door != null) door.set(doorType);
	}

	private void paintLegacySpecialRoom(Room room) {
		if (paintLegacyQuestRoom(room)) return;
		if (room.connected.isEmpty()) {
			paintOpenRoom(room, isHidden(room.type) ? Door.Type.HIDDEN : Door.Type.REGULAR);
			return;
		}

		Door legacyDoor = room.connected.values().iterator().next();
		if (legacyDoor == null) {
			paintOpenRoom(room, isHidden(room.type) ? Door.Type.HIDDEN : Door.Type.REGULAR);
			return;
		}
		if (room.type == Type.TRAPS) {
			paintLegacyTrapsRoom(room, legacyDoor);
			return;
		}

		SpecialRoom painter = specialRoomPainter(room.type);
		if (painter == null) {
			paintOpenRoom(room, isHidden(room.type) ? Door.Type.HIDDEN : Door.Type.REGULAR);
			return;
		}

		painter.set(room.left, room.top, room.right, room.bottom);
		EmptyRoom neighbour = new EmptyRoom();
		neighbour.set(room.left - 1, room.top - 1, room.right + 1, room.bottom + 1);
		pd.levels.rooms.Room.Door door =
				new pd.levels.rooms.Room.Door(legacyDoor.x, legacyDoor.y);
		painter.connected.put(neighbour, door);
		neighbour.connected.put(painter, door);
		painter.paint(this);

		if (room.type == Type.PRISON_PIT) {
			legacyDoor.set(Door.Type.ONEWAY);
		} else if (isHidden(room.type)) {
			legacyDoor.set(Door.Type.HIDDEN);
		} else {
			legacyDoor.set(convertDoorType(door.type));
		}
	}

	/** Region levels can paint fixed quest rooms into the legacy BSP layout. */
	protected boolean paintLegacyQuestRoom(Room room) {
		return false;
	}

	private void paintLegacyTrapsRoom(Room room, Door door) {
		fill(room, Terrain.WALL);

		Class<? extends Trap> trapClass;
		switch (Random.Int(5)) {
			case 0:
			default:
				trapClass = SpearTrap.class;
				break;
			case 1:
				trapClass = Dungeon.bossLevel(Dungeon.depth + 1) ? SummoningTrap.class : null;
				break;
			case 2:
			case 3:
			case 4:
				trapClass = Random.element(LEGACY_TRAP_ROOM_TYPES);
				break;
		}

		int interior = trapClass == null ? Terrain.CHASM : Terrain.TRAP;
		fill(room.left + 1, room.top + 1, room.width() - 1, room.height() - 1, interior);
		door.set(Door.Type.REGULAR);

		int safeTerrain = map[room.left + 1 + (room.top + 1) * width()] == Terrain.CHASM
				? Terrain.CHASM : Terrain.EMPTY;
		int rewardX;
		int rewardY;
		if (door.x == room.left) {
			rewardX = room.right - 1;
			rewardY = room.top + room.height() / 2;
			fill(rewardX, room.top + 1, 1, room.height() - 1, safeTerrain);
		} else if (door.x == room.right) {
			rewardX = room.left + 1;
			rewardY = room.top + room.height() / 2;
			fill(rewardX, room.top + 1, 1, room.height() - 1, safeTerrain);
		} else if (door.y == room.top) {
			rewardX = room.left + room.width() / 2;
			rewardY = room.bottom - 1;
			fill(room.left + 1, rewardY, room.width() - 1, 1, safeTerrain);
		} else {
			rewardX = room.left + room.width() / 2;
			rewardY = room.top + 1;
			fill(room.left + 1, rewardY, room.width() - 1, 1, safeTerrain);
		}

		if (trapClass != null) {
			for (int y = room.top + 1; y < room.bottom; y++) {
				for (int x = room.left + 1; x < room.right; x++) {
					int cell = x + y * width();
					if (map[cell] != Terrain.TRAP) continue;
					Trap trap = Reflection.newInstance(trapClass);
					if (trap != null) setTrap(trap.reveal(), cell);
				}
			}
		}

		int rewardCell = rewardX + rewardY * width();
		if (Random.Int(3) == 0) {
			if (safeTerrain == Terrain.CHASM) map[rewardCell] = Terrain.EMPTY;
			drop(legacyTrapRoomPrize(), rewardCell).type = Heap.Type.CHEST;
		} else {
			map[rewardCell] = Terrain.PEDESTAL;
			drop(legacyTrapRoomPrize(), rewardCell);
		}
		addItemToSpawn(new PotionOfLevitation());
	}

	private Item legacyTrapRoomPrize() {
		if (Random.Int(4) != 0) {
			Item prize = findPrizeItem();
			if (prize != null) return prize;
		}

		Item prize = Generator.random(Random.oneOf(Generator.Category.MELEEWEAPON, Generator.Category.ARMOR));
		for (int i = 0; i < 3; i++) {
			Item candidate = Generator.random(Random.oneOf(Generator.Category.MELEEWEAPON, Generator.Category.ARMOR));
			if (candidate.level() > prize.level()) prize = candidate;
		}
		return prize;
	}

	private SpecialRoom specialRoomPainter(Type type) {
		switch (type) {
			case CRYPT: return new CryptRoom();
			case POOL: return new PoolRoom();
			case GARDEN: return new GardenRoom();
			case LIBRARY: return new LibraryRoom();
			case STORAGE: return new StorageRoom();
			case STATUE: return new StatueRoom();
			case VAULT: return new TreasuryRoom();
			case TENTROOM: return new SpsTentRoom();
			case MAGIC_WELL: return new SpsMagicWellRoom();
			case PRISON_PIT: return new SpsPitRoom();
			case HIDE_SHOP: return new SpsHiddenShopRoom();
			case WISH_POOL: return new SpsWishPoolRoom();
			case MEMORY: return new SpsMemoryRoom();
			case COOKING: return new SpsCookingRoom();
			case MATERIAL: return new SpsMaterialRoom();
			case GLASSROOM: return new SpsGlassRoom();
			case BARRICADED: return new SpsBarricadedRoom();
			case JUNGLE: return new SpsJungleRoom();
			case RUIN_ROOM: return new SpsRuinRoom();
			default: return null;
		}
	}

	private Door.Type convertDoorType(
			pd.levels.rooms.Room.Door.Type type) {
		switch (type) {
			case TUNNEL:
			case WATER: return Door.Type.TUNNEL;
			case REGULAR: return Door.Type.REGULAR;
			case UNLOCKED: return Door.Type.UNLOCKED;
			case HIDDEN:
			case WALL: return Door.Type.HIDDEN;
			case BARRICADE: return Door.Type.BARRICADE;
			case LOCKED:
			case CRYSTAL: return Door.Type.LOCKED;
			default: return Door.Type.EMPTY;
		}
	}

	private void paintTunnel(Room room) {
		int floor = tunnelTile();
		Point center = legacyRoomCenter(room);
		if (room.width() > room.height()
				|| (room.width() == room.height() && Random.Int(2) == 0)) {
			int from = room.right - 1;
			int to = room.left + 1;
			for (Door door : room.connected.values()) {
				if (door == null) continue;
				int step = door.y < center.y ? 1 : -1;
				if (door.x == room.left) {
					from = room.left + 1;
					for (int y = door.y; y != center.y; y += step) set(from, y, floor);
				} else if (door.x == room.right) {
					to = room.right - 1;
					for (int y = door.y; y != center.y; y += step) set(to, y, floor);
				} else {
					from = Math.min(from, door.x);
					to = Math.max(to, door.x);
					for (int y = door.y + step; y != center.y; y += step) set(door.x, y, floor);
				}
			}
			for (int x = from; x <= to; x++) set(x, center.y, floor);
		} else {
			int from = room.bottom - 1;
			int to = room.top + 1;
			for (Door door : room.connected.values()) {
				if (door == null) continue;
				int step = door.x < center.x ? 1 : -1;
				if (door.y == room.top) {
					from = room.top + 1;
					for (int x = door.x; x != center.x; x += step) set(x, from, floor);
				} else if (door.y == room.bottom) {
					to = room.bottom - 1;
					for (int x = door.x; x != center.x; x += step) set(x, to, floor);
				} else {
					from = Math.min(from, door.y);
					to = Math.max(to, door.y);
					for (int x = door.x + step; x != center.x; x += step) set(x, door.y, floor);
				}
			}
			for (int y = from; y <= to; y++) set(center.x, y, floor);
		}
		for (Door door : room.connected.values()) if (door != null) door.set(Door.Type.TUNNEL);
	}

	private void paintPassage(Room room) {
		int floor = tunnelTile();
		int passageWidth = room.width() - 2;
		int passageHeight = room.height() - 2;
		int perimeter = passageWidth * 2 + passageHeight * 2;
		ArrayList<Integer> joints = new ArrayList<>();
		for (Door door : room.connected.values()) {
			if (door != null) joints.add(passagePerimeterPosition(room, door, passageWidth, passageHeight));
		}
		if (joints.isEmpty() || perimeter <= 0) return;
		Collections.sort(joints);

		int start = 0;
		int maxDistance = joints.get(0) + perimeter - joints.get(joints.size() - 1);
		for (int i = 1; i < joints.size(); i++) {
			int distance = joints.get(i) - joints.get(i - 1);
			if (distance > maxDistance) {
				maxDistance = distance;
				start = i;
			}
		}
		int end = (start + joints.size() - 1) % joints.size();
		int position = joints.get(start);
		do {
			Point point = passagePoint(room, position, passageWidth, passageHeight);
			set(point.x, point.y, floor);
			position = (position + 1) % perimeter;
		} while (position != joints.get(end));
		Point point = passagePoint(room, position, passageWidth, passageHeight);
		set(point.x, point.y, floor);
		for (Door door : room.connected.values()) if (door != null) door.set(Door.Type.TUNNEL);
	}

	private int passagePerimeterPosition(Room room, Door door, int passageWidth, int passageHeight) {
		if (door.y == room.top) return door.x - room.left - 1;
		if (door.x == room.right) return door.y - room.top - 1 + passageWidth;
		if (door.y == room.bottom) return room.right - door.x - 1 + passageWidth + passageHeight;
		return door.y == room.top + 1 ? 0
				: room.bottom - door.y - 1 + passageWidth * 2 + passageHeight;
	}

	private Point passagePoint(Room room, int position, int passageWidth, int passageHeight) {
		if (position < passageWidth) {
			return new Point(room.left + 1 + position, room.top + 1);
		} else if (position < passageWidth + passageHeight) {
			return new Point(room.right - 1, room.top + 1 + position - passageWidth);
		} else if (position < passageWidth * 2 + passageHeight) {
			return new Point(room.right - 1 - (position - passageWidth - passageHeight), room.bottom - 1);
		} else {
			return new Point(room.left + 1,
					room.bottom - 1 - (position - passageWidth * 2 - passageHeight));
		}
	}

	private void paintDoors(Room room) {
		for (Map.Entry<Room, Door> connection : room.connected.entrySet()) {
			if (joinLegacyRooms(room, connection.getKey())) continue;
			Door door = connection.getValue();
			if (door == null || !insideMap(door.x + door.y * width())) continue;
			int cell = door.x + door.y * width();
			switch (door.type) {
				case EMPTY: map[cell] = Terrain.EMPTY; break;
				case TUNNEL: map[cell] = tunnelTile(); break;
				case REGULAR:
					int legacyDepth = Dungeon.legacyDepth();
					boolean secret = legacyDepth > 1
							&& (legacyDepth < 6 ? Random.Int(Math.max(1, 12 - legacyDepth))
							: Random.Int(6)) == 0;
					map[cell] = secret ? Terrain.SECRET_DOOR : Terrain.DOOR;
					if (secret) legacySecretDoors++;
					break;
				case UNLOCKED: map[cell] = Terrain.DOOR; break;
				case HIDDEN: map[cell] = Terrain.SECRET_DOOR; break;
				case BARRICADE:
					map[cell] = Random.Int(3) == 0 ? Terrain.BOOKSHELF : Terrain.BARRICADE;
					break;
				case LOCKED: map[cell] = Terrain.LOCKED_DOOR; break;
				case ONEWAY: map[cell] = Terrain.BROKEN_DOOR; break;
			}
		}
	}

	private boolean joinLegacyRooms(Room room, Room neighbour) {
		if (room.type != Type.STANDARD || neighbour.type != Type.STANDARD) return false;
		Rect overlap = room.intersect(neighbour);
		if (overlap.left == overlap.right) {
			if (overlap.bottom - overlap.top < 3
					|| overlap.height() == Math.max(room.height(), neighbour.height())
					|| room.width() + neighbour.width() > SpsBspLayout.MAX_ROOM_SIZE) return false;
			fill(overlap.left, overlap.top + 1, 1, overlap.height() - 1, Terrain.EMPTY);
		} else {
			if (overlap.right - overlap.left < 3
					|| overlap.width() == Math.max(room.width(), neighbour.width())
					|| room.height() + neighbour.height() > SpsBspLayout.MAX_ROOM_SIZE) return false;
			fill(overlap.left + 1, overlap.top, overlap.width() - 1, 1, Terrain.EMPTY);
		}
		return true;
	}

	private void paintLegacyWater() {
		boolean[] patch = legacyPatch(legacyWaterFill(), legacyWaterClustering());
		for (int i = 0; i < length(); i++) {
			if (map[i] == Terrain.EMPTY && patch[i]) {
				map[i] = Random.Int(25) == 0 ? Terrain.OLD_HIGH_GRASS : Terrain.WATER;
			}
		}
	}

	private void paintLegacyGrass() {
		boolean[] patch = legacyPatch(legacyGrassFill(), legacyGrassClustering());
		if (feeling == Feeling.GRASS) {
			for (Room room : legacyLayout.rooms) {
				if (room.type == Type.NULL || room.type == Type.PASSAGE || room.type == Type.TUNNEL) continue;
				markPatch(patch, room.left + 1, room.top + 1);
				markPatch(patch, room.right - 1, room.top + 1);
				markPatch(patch, room.left + 1, room.bottom - 1);
				markPatch(patch, room.right - 1, room.bottom - 1);
			}
		}
		for (int y = 1; y < height() - 1; y++) {
			for (int x = 1; x < width() - 1; x++) {
				int cell = x + y * width();
				if (map[cell] == Terrain.EMPTY && patch[cell]) {
					int count = 1;
					for (int yy = -1; yy <= 1; yy++) {
						for (int xx = -1; xx <= 1; xx++) {
							if ((xx != 0 || yy != 0) && patch[cell + xx + yy * width()]) count++;
						}
					}
					map[cell] = Random.Float() < count / 12f ? Terrain.HIGH_GRASS : Terrain.GRASS;
				} else if (map[cell] == Terrain.EMPTY && Random.Int(40) == 0) {
					map[cell] = Terrain.OLD_HIGH_GRASS;
				}
			}
		}
	}

	private void paintLegacyChasms() {
		boolean[] patch = legacyPatch(legacyChasmFill(), legacyChasmClustering());
		for (int y = 1; y < height() - 1; y++) {
			for (int x = 1; x < width() - 1; x++) {
				int cell = x + y * width();
				if (patch[cell] && (map[cell] == Terrain.WALL || map[cell] == Terrain.GLASS_WALL)) {
					map[cell] = Terrain.CHASM;
				}
			}
		}
	}

	private boolean[] legacyPatch(float fill, int passes) {
		boolean[] current = new boolean[length()];
		boolean[] next = new boolean[length()];
		for (int i = 0; i < length(); i++) current[i] = Random.Float() < fill;
		for (int pass = 0; pass < passes; pass++) {
			Arrays.fill(next, false);
			for (int y = 1; y < height() - 1; y++) {
				for (int x = 1; x < width() - 1; x++) {
					int cell = x + y * width();
					int count = 0;
					for (int yy = -1; yy <= 1; yy++) {
						for (int xx = -1; xx <= 1; xx++) {
							if ((xx != 0 || yy != 0) && current[cell + xx + yy * width()]) count++;
						}
					}
					next[cell] = current[cell] ? count >= 4 : count >= 5;
				}
			}
			boolean[] swap = current;
			current = next;
			next = swap;
		}
		return current;
	}

	private void placeLegacyTraps() {
		Class<?>[] classes = trapClasses();
		float[] chances = trapChances();
		if (classes.length == 0 || classes.length != chances.length) return;
		ArrayList<Integer> valid = new ArrayList<>();
		for (int i = 0; i < length(); i++) {
			if (map[i] != Terrain.EMPTY && map[i] != Terrain.WATER && map[i] != Terrain.HIGH_GRASS) continue;
			Room room = legacyRoom(i);
			if (room == null || room.type == Type.ENTRANCE || room.type == Type.EXIT
					|| room.type == Type.SHOP || room.type == Type.HIDE_SHOP) continue;
			if (Dungeon.legacyDepth() == 1 && room.type == Type.TUNNEL) continue;
			valid.add(i);
		}
		Random.shuffle(valid);
		int count = Math.min(nTraps(), valid.size() / 3);
		for (int i = 0; i < count; i++) {
			int index = Random.chances(chances);
			if (index < 0 || index >= classes.length) continue;
			Trap trap = (Trap)Reflection.newInstance((Class<?>)classes[index]);
			if (trap == null) continue;
			if (Random.Int(2) == 0) trap.hide(); else trap.reveal();
			int cell = valid.get(i);
			setTrap(trap, cell);
			map[cell] = trap.visible ? Terrain.TRAP : Terrain.SECRET_TRAP;
		}
	}

	protected void decorateLegacyFloor() {
		if (this instanceof SewerLevel) decorateSewers();
		else if (this instanceof PrisonLevel) decoratePrison();
		else if (this instanceof CavesLevel) decorateCaves();
		else if (this instanceof CityLevel) decorateCity();
		else if (this instanceof HallsLevel) decorateHalls();

		if (feeling == Feeling.SPECIAL_FLOOR) replaceInteriorWallsWithGlass();
		placeEntranceSign();

		if (this instanceof CavesLevel && !Dungeon.bossLevel(Dungeon.depth + 1)) {
			placeCaveBoundaryChasms();
		}
		if (this instanceof HallsLevel) map[exit] = Terrain.LOCKED_EXIT;
	}

	@Override
	protected void createItems() {
		int ordinaryItems = 3 + pd.items.misc.LuckyBadge
				.rollExtraItems(Dungeon.hero);
		for (int i = 0; i < ordinaryItems; i++) {
			Item item = Generator.random();
			if (item == null) continue;
			int cell = legacyItemCell(item instanceof Scroll);
			if (cell < 0) break;
			switch (Random.Int(20)) {
				case 0:
					drop(item, cell).type = Heap.Type.SKELETON;
					break;
				case 1: case 2: case 3: case 4:
					drop(item, cell).type = Heap.Type.CHEST;
					break;
				case 5:
					drop(item, cell).type = Dungeon.legacyDepth() > 1 ? Heap.Type.MIMIC : Heap.Type.CHEST;
					break;
				default:
					drop(item, cell).type = Heap.Type.HEAP;
					break;
			}
		}

		for (int i = 0; i < 10; i++) {
			Item item = Random.Int(5) == 0 ? Generator.random() : new YellowDewdrop();
			dropLegacyItem(item, Heap.Type.E_DUST);
		}
		for (int i = 0; i < 3; i++) {
			Item item = Random.Int(3) == 0
					? Generator.random(Random.oneOf(Generator.Category.ARMOR,
							Generator.Category.MELEEWEAPON, Generator.Category.ARTIFACT,
							Generator.Category.RING))
					: new SmallMeat();
			dropLegacyItem(item, Heap.Type.M_WEB);
		}

		if (Random.Int(5) > 0) {
			dropLegacyItem(new GoldenKey(ChallengeJournal.keyDepth(Dungeon.depth, Dungeon.branch)), Heap.Type.HEAP);
			dropLegacyItem(legacyLockedReward(), Heap.Type.LOCKED_CHEST);
		} else {
			dropLegacyItem(legacyMonsterBoxReward(), Heap.Type.G_MIMIC);
		}

		DriedRose rose = Dungeon.hero == null ? null
				: Dungeon.hero.belongings.getItem(DriedRose.class);
		if (rose != null && !rose.cursed) {
			int petals = (int)Math.ceil((Dungeon.depth / 2f - rose.droppedPetals) / 3f);
			for (int i = 0; i < petals && rose.droppedPetals < 12; i++) {
				itemsToSpawn.add(new DriedRose.Petal());
				rose.droppedPetals++;
			}
		}

		int fragment = ChallengeJournal.fragmentForDepth(Dungeon.depth);
		if (Dungeon.branch == 0 && fragment >= 0) {
			itemsToSpawn.add(new MapFragment().forChallenge(fragment));
		}
		for (Item item : itemsToSpawn) dropLegacyItem(item, Heap.Type.HEAP);
	}

	private Item legacyLockedReward() {
		switch (Random.Int(20)) {
			case 0: case 1: case 2: case 3:
				return new BoundReward();
			case 4: case 5: case 6:
				return generatedOrFallback(Generator.Category.HIGHFOOD);
			case 7: case 8: case 9: case 10: case 11: case 12:
				return generatedOrFallback(Generator.Category.NORNSTONE);
			case 13: case 14: case 15: case 16:
				return generatedOrFallback(Generator.Category.PILL);
			case 17: case 18:
				return generatedOrFallback(Generator.Category.SUMMONED);
			case 19:
				return generatedOrFallback(Generator.Category.EGGS);
			default:
				return new BoundReward();
		}
	}

	private Item legacyMonsterBoxReward() {
		switch (Random.Int(5)) {
			case 0: return generatedOrFallback(Generator.Category.HIGHFOOD);
			case 1: return generatedOrFallback(Generator.Category.NORNSTONE);
			case 2: return generatedOrFallback(Generator.Category.WEAPON);
			case 3: return generatedOrFallback(Generator.Category.SUMMONED);
			default: return generatedOrFallback(Generator.Category.EGGS);
		}
	}

	private Item generatedOrFallback(Generator.Category category) {
		Item item = Generator.random(category);
		return item == null ? new BoundReward() : item;
	}

	private void dropLegacyItem(Item item, Heap.Type type) {
		if (item == null) return;
		int cell = legacyItemCell(item instanceof Scroll);
		if (cell >= 0) drop(item, cell).type = type;
	}

	private int legacyItemCell(boolean protectScroll) {
		for (int attempt = 0; attempt < 128; attempt++) {
			int cell = randomDropCell();
			if (cell < 0) continue;
			if (protectScroll && traps.get(cell) instanceof FireDamageTrap) continue;
			return cell;
		}
		return -1;
	}

	@Override
	protected int initialMobCount() {
		int legacyDepth = Dungeon.legacyDepth();
		if (legacyDepth < 5 && !Statistics.amuletObtained) {
			return 10 + legacyDepth + Random.Int(3);
		} else if (!Statistics.amuletObtained) {
			return 15 + legacyDepth % 3 + Random.Int(3);
		} else {
			return 10 + (5 - legacyDepth % 5) + Random.Int(3);
		}
	}

	@Override
	protected void createMobs() {
		HashSet<Mob> existing = new HashSet<>(mobs);
		super.createMobs();
		for (Mob mob : mobs) {
			if (!existing.contains(mob)) applyLegacyInitialMobTraits(mob);
		}
	}

	void applyLegacyInitialMobTraits(Mob mob) {
		mob.spsOriginalGeneration = true;
		int multiplier;
		if (this instanceof CavesLevel) multiplier = 5;
		else if (this instanceof CityLevel) multiplier = 10;
		else if (this instanceof HallsLevel) multiplier = 15;
		else return;
		Buff.affect(mob, ShieldArmor.class).level(Dungeon.legacyDepth() * multiplier);
		Buff.affect(mob, MagicArmor.class).level(Dungeon.legacyDepth() * multiplier);
		if (this instanceof HallsLevel) Buff.affect(mob, GlassShield.class).turns(1);
	}

	@Override
	protected void markSpsOriginalMobs() {
		// Initial enemies are marked in createMobs so quest NPCs remain excluded.
	}

	private void decorateSewers() {
		for (int i = 0; i < width(); i++) {
			if (map[i] == Terrain.WALL && map[i + width()] == Terrain.WATER
					&& Random.Int(4) == 0) map[i] = Terrain.WALL_DECO;
		}
		for (int i = width(); i < length() - width(); i++) {
			if (map[i] == Terrain.WALL && map[i - width()] == Terrain.WALL
					&& map[i + width()] == Terrain.WATER && Random.Int(2) == 0) {
				map[i] = Terrain.WALL_DECO;
			}
		}
		for (int i = width() + 1; i < length() - width() - 1; i++) {
			if (map[i] != Terrain.EMPTY) continue;
			int count = (map[i + 1] == Terrain.WALL ? 1 : 0)
					+ (map[i - 1] == Terrain.WALL ? 1 : 0)
					+ (map[i + width()] == Terrain.WALL ? 1 : 0)
					+ (map[i - width()] == Terrain.WALL ? 1 : 0);
			if (Random.Int(16) < count * count) map[i] = Terrain.EMPTY_DECO;
		}
	}

	private void decoratePrison() {
		for (int i = width() + 1; i < length() - width() - 1; i++) {
			if (map[i] != Terrain.EMPTY) continue;
			float chance = 0.05f;
			if (map[i + 1] == Terrain.WALL && map[i + width()] == Terrain.WALL) chance += 0.2f;
			if (map[i - 1] == Terrain.WALL && map[i + width()] == Terrain.WALL) chance += 0.2f;
			if (map[i + 1] == Terrain.WALL && map[i - width()] == Terrain.WALL) chance += 0.2f;
			if (map[i - 1] == Terrain.WALL && map[i - width()] == Terrain.WALL) chance += 0.2f;
			if (Random.Float() < chance) map[i] = Terrain.EMPTY_DECO;
		}
		for (int i = 0; i < width(); i++) {
			if (map[i] == Terrain.WALL
					&& (map[i + width()] == Terrain.EMPTY || map[i + width()] == Terrain.EMPTY_SP)
					&& Random.Int(6) == 0) map[i] = Terrain.WALL_DECO;
		}
		for (int i = width(); i < length() - width(); i++) {
			if (map[i] == Terrain.WALL && map[i - width()] == Terrain.WALL
					&& (map[i + width()] == Terrain.EMPTY || map[i + width()] == Terrain.EMPTY_SP)
					&& Random.Int(3) == 0) map[i] = Terrain.WALL_DECO;
		}
	}

	private void decorateCaves() {
		for (Room room : legacyLayout.rooms) {
			if (room.type != Type.STANDARD || room.width() <= 3 || room.height() <= 3) continue;
			int square = room.square();
			int corner = room.left + 1 + (room.top + 1) * width();
			if (Random.Int(square) > 8 && map[corner - 1] == Terrain.WALL
					&& map[corner - width()] == Terrain.WALL) map[corner] = Terrain.WALL;
			corner = room.right - 1 + (room.top + 1) * width();
			if (Random.Int(square) > 8 && map[corner + 1] == Terrain.WALL
					&& map[corner - width()] == Terrain.WALL) map[corner] = Terrain.WALL;
			corner = room.left + 1 + (room.bottom - 1) * width();
			if (Random.Int(square) > 8 && map[corner - 1] == Terrain.WALL
					&& map[corner + width()] == Terrain.WALL) map[corner] = Terrain.WALL;
			corner = room.right - 1 + (room.bottom - 1) * width();
			if (Random.Int(square) > 8 && map[corner + 1] == Terrain.WALL
					&& map[corner + width()] == Terrain.WALL) map[corner] = Terrain.WALL;

			for (Room neighbour : room.connected.keySet()) {
				Door door = room.connected.get(neighbour);
				if (door != null && (neighbour.type == Type.STANDARD || neighbour.type == Type.TUNNEL)
						&& Random.Int(3) == 0) map[door.x + door.y * width()] = Terrain.EMPTY_DECO;
			}
		}
		for (int i = width() + 1; i < length() - width(); i++) {
			if (map[i] != Terrain.EMPTY) continue;
			int walls = 0;
			if (map[i + 1] == Terrain.WALL) walls++;
			if (map[i - 1] == Terrain.WALL) walls++;
			if (map[i + width()] == Terrain.WALL) walls++;
			if (map[i - width()] == Terrain.WALL) walls++;
			if (Random.Int(6) <= walls) map[i] = Terrain.EMPTY_DECO;
		}
		for (int i = 0; i < length(); i++) {
			if (map[i] == Terrain.WALL && Random.Int(8) == 0) map[i] = Terrain.WALL_DECO;
		}
	}

	private void decorateCity() {
		for (int i = 0; i < length(); i++) {
			if (map[i] == Terrain.EMPTY && Random.Int(10) == 0) map[i] = Terrain.EMPTY_DECO;
			else if (map[i] == Terrain.WALL && Random.Int(8) == 0) map[i] = Terrain.WALL_DECO;
		}
	}

	private void decorateHalls() {
		for (int i = width() + 1; i < length() - width() - 1; i++) {
			if (map[i] == Terrain.EMPTY) {
				int passableNeighbours = 0;
				for (int offset : PathFinder.NEIGHBOURS8) {
					if ((Terrain.flags[map[i + offset]] & Terrain.PASSABLE) != 0) passableNeighbours++;
				}
				if (Random.Int(80) < passableNeighbours) map[i] = Terrain.EMPTY_DECO;
			} else if (map[i] == Terrain.WALL && map[i - 1] != Terrain.WALL_DECO
					&& map[i - width()] != Terrain.WALL_DECO && Random.Int(20) == 0) {
				map[i] = Terrain.WALL_DECO;
			}
		}
	}

	private void replaceInteriorWallsWithGlass() {
		for (int y = 1; y < height() - 1; y++) {
			for (int x = 1; x < width() - 1; x++) {
				int cell = x + y * width();
				if (map[cell] == Terrain.WALL) map[cell] = Terrain.GLASS_WALL;
			}
		}
	}

	private void placeCaveBoundaryChasms() {
		for (Room room : legacyLayout.rooms) {
			if (room.type != Type.STANDARD) continue;
			for (Room neighbour : room.neighbours) {
				if (neighbour.type != Type.STANDARD || room.connected.containsKey(neighbour)) continue;
				Rect boundary = room.intersect(neighbour);
				if (boundary.left == boundary.right && boundary.bottom - boundary.top >= 5) {
					boundary.top += 2;
					boundary.bottom -= 1;
					fill(boundary.left, boundary.top, 1, boundary.height(), Terrain.CHASM);
				} else if (boundary.top == boundary.bottom && boundary.right - boundary.left >= 5) {
					boundary.left += 2;
					boundary.right -= 1;
					fill(boundary.left, boundary.top, boundary.width(), 1, Terrain.CHASM);
				}
			}
		}
	}

	private void placeEntranceSign() {
		ArrayList<Integer> candidates = new ArrayList<>();
		Room room = legacyLayout.entrance;
		for (int y = room.top + 1; y < room.bottom; y++) {
			for (int x = room.left + 1; x < room.right; x++) {
				int cell = x + y * width();
				if (cell != entrance && traps.get(cell) == null && plants.get(cell) == null
						&& map[cell] != Terrain.DEW_BLESS) {
					candidates.add(cell);
				}
			}
		}
		if (!candidates.isEmpty()) map[Random.element(candidates)] = Terrain.SIGN;
	}

	private void buildRoomAdapters() {
		rooms = new ArrayList<>();
		EntranceRoom entranceRoom = new EntranceRoom();
		entranceRoom.set(legacyLayout.entrance.left, legacyLayout.entrance.top,
				legacyLayout.entrance.right, legacyLayout.entrance.bottom);
		roomEntrance = entranceRoom;
		rooms.add(entranceRoom);

		ExitRoom exitRoom = new ExitRoom();
		exitRoom.set(legacyLayout.exit.left, legacyLayout.exit.top,
				legacyLayout.exit.right, legacyLayout.exit.bottom);
		roomExit = exitRoom;
		rooms.add(exitRoom);

		for (Room legacy : legacyLayout.rooms) {
			if (legacy.type != Type.STANDARD) continue;
			EmptyRoom room = new EmptyRoom();
			room.set(legacy.left, legacy.top, legacy.right, legacy.bottom);
			rooms.add(room);
		}
	}

	private Room legacyRoom(int cell) {
		int x = cell % width();
		int y = cell / width();
		for (Room room : legacyLayout.rooms) {
			if (room.type != Type.NULL && x > room.left && x < room.right
					&& y > room.top && y < room.bottom) return room;
		}
		return null;
	}

	private boolean legacyPathExists() {
		if (!insideMap(entrance) || !insideMap(exit)) return false;
		boolean[] seen = new boolean[length()];
		ArrayList<Integer> pending = new ArrayList<>();
		pending.add(entrance);
		while (!pending.isEmpty()) {
			int cell = pending.remove(pending.size() - 1);
			if (cell == exit) return true;
			if (!insideMap(cell) || seen[cell] || !legacyTraversable(cell)) continue;
			seen[cell] = true;
			int x = cell % width();
			int y = cell / width();
			if (x > 0) pending.add(cell - 1);
			if (x < width() - 1) pending.add(cell + 1);
			if (y > 0) pending.add(cell - width());
			if (y < height() - 1) pending.add(cell + width());
		}
		return false;
	}

	private boolean legacyTraversable(int cell) {
		int terrain = map[cell];
		return (Terrain.flags[terrain] & Terrain.SOLID) == 0
				|| terrain == Terrain.DOOR || terrain == Terrain.SECRET_DOOR || terrain == Terrain.LOCKED_DOOR
				|| terrain == Terrain.BARRICADE || terrain == Terrain.BOOKSHELF;
	}

	protected int randomInteriorCell(Room room, int margin) {
		return room.randomCell(width(), margin);
	}

	private Point legacyRoomCenter(Room room) {
		return new Point((room.left + room.right) / 2
				+ (((room.right - room.left) & 1) == 1 ? Random.Int(2) : 0),
				(room.top + room.bottom) / 2
						+ (((room.bottom - room.top) & 1) == 1 ? Random.Int(2) : 0));
	}

	private void markPatch(boolean[] patch, int x, int y) {
		if (x > 0 && y > 0 && x < width() - 1 && y < height() - 1) patch[x + y * width()] = true;
	}

	protected void fill(Room room, int terrain) {
		fill(room.left, room.top, room.right - room.left + 1,
				room.bottom - room.top + 1, terrain);
	}

	protected void fill(int x, int y, int w, int h, int terrain) {
		for (int yy = Math.max(0, y); yy < Math.min(height(), y + h); yy++) {
			for (int xx = Math.max(0, x); xx < Math.min(width(), x + w); xx++) {
				map[xx + yy * width()] = terrain;
			}
		}
	}

	private void set(int x, int y, int terrain) {
		if (x >= 0 && y >= 0 && x < width() && y < height()) map[x + y * width()] = terrain;
	}

	@Override
	protected int nTraps() {
		return Random.NormalIntRange(13, 20 + Dungeon.legacyDepth() / 2);
	}

	protected float legacyWaterFill() {
		return feeling == Feeling.WATER ? 0.60f : 0.45f;
	}

	protected int legacyWaterClustering() {
		return 5;
	}

	protected float legacyGrassFill() {
		return feeling == Feeling.GRASS ? 0.60f : 0.40f;
	}

	protected int legacyGrassClustering() {
		return 4;
	}

	protected float legacyChasmFill() {
		return feeling == Feeling.CHASM ? 0.30f : 0.35f;
	}

	protected int legacyChasmClustering() {
		return 4;
	}

	public int legacyRoomCount() {
		return legacyLayout == null ? 0 : legacyLayout.rooms.size();
	}

	public int legacyConnectedRoomCount() {
		return legacyLayout == null ? 0 : legacyLayout.connected.size();
	}

	public int legacySpecialRoomCount() {
		if (legacyLayout == null) return 0;
		int count = 0;
		for (Room room : legacyLayout.rooms) if (isSpecial(room.type)) count++;
		return count;
	}

	public int legacyGenerationAttempts() {
		return legacyLayout == null ? 0 : legacyLayout.attempts;
	}

	public int legacySecretDoorCount() {
		return legacySecretDoors;
	}
}
