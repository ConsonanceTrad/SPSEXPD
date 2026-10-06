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
import pd.actors.blobs.Blob;
import pd.actors.blobs.weather.WeatherOfDead;
import pd.actors.blobs.weather.WeatherOfQuite;
import pd.actors.blobs.weather.WeatherOfRain;
import pd.actors.blobs.weather.WeatherOfSand;
import pd.actors.blobs.weather.WeatherOfSnow;
import pd.actors.blobs.weather.WeatherOfSun;
import pd.actors.buffs.Buff;
import pd.actors.buffs.ExProtect;
import pd.actors.buffs.GlassShield;
import pd.actors.buffs.MagicArmor;
import pd.actors.buffs.ShieldArmor;
import pd.actors.mobs.Mob;
import pd.actors.mobs.SpsExitMobs;
import pd.items.Generator;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.Item;
import pd.items.YellowDewdrop;
import pd.items.equipment.artifacts.DriedRose;
import pd.items.specific.keys.GoldenKey;
import pd.items.consum.potions.PotionOfLevitation;
import pd.items.quest.ChallengeJournal;
import pd.items.quest.MapFragment;
import pd.items.specific.reward.BoundReward;
import pd.items.consum.scrolls.Scroll;
import pd.levels.builders.SpsBspLayout.Door;
import pd.levels.builders.SpsBspLayout.Room;
import pd.levels.builders.SpsBspLayout.Type;
import pd.levels.builders.SpsBspLayout;
import pd.levels.features.LevelTransition;
import pd.levels.rooms.connection.ConnectionRoom;
import pd.levels.rooms.special.ArmoryRoom;
import pd.levels.rooms.special.CryptRoom;
import pd.levels.rooms.special.CrystalChoiceRoom;
import pd.levels.rooms.special.CrystalPathRoom;
import pd.levels.rooms.special.CrystalVaultRoom;
import pd.levels.rooms.special.FusionTrialRoom;
import pd.levels.rooms.special.GardenRoom;
import pd.levels.rooms.special.LaboratoryRoom;
import pd.levels.rooms.special.LibraryRoom;
import pd.levels.rooms.special.MagicalFireRoom;
import pd.levels.rooms.special.PitRoom;
import pd.levels.rooms.special.PoolRoom;
import pd.levels.rooms.special.RunestoneRoom;
import pd.levels.rooms.special.SacrificeRoom;
import pd.levels.rooms.special.SentryRoom;
import pd.levels.rooms.special.SpecialRoom;
import pd.levels.rooms.special.SpsBarricadedRoom;
import pd.levels.rooms.special.SpsCookingRoom;
import pd.levels.rooms.special.SpsGlassRoom;
import pd.levels.rooms.special.SpsHiddenShopRoom;
import pd.levels.rooms.special.SpsJungleRoom;
import pd.levels.rooms.special.SpsMagicWellRoom;
import pd.levels.rooms.special.SpsMaterialRoom;
import pd.levels.rooms.special.SpsMemoryRoom;
import pd.levels.rooms.special.SpsPitRoom;
import pd.levels.rooms.special.SpsRuinRoom;
import pd.levels.rooms.special.SpsShopRoom;
import pd.levels.rooms.special.SpsTentRoom;
import pd.levels.rooms.special.SpsWishPoolRoom;
import pd.levels.rooms.special.StatueRoom;
import pd.levels.rooms.special.StorageRoom;
import pd.levels.rooms.special.ToxicGasRoom;
import pd.levels.rooms.special.TreasuryRoom;
import pd.levels.rooms.special.WeakFloorRoom;
import pd.levels.rooms.standard.EmptyRoom;
import pd.levels.rooms.standard.StandardRoom;
import pd.levels.rooms.standard.entrance.EntranceRoom;
import pd.levels.rooms.standard.exit.ExitRoom;
import pd.levels.traps.ConfusionTrap;
import pd.levels.traps.DisintegrationTrap;
import pd.levels.traps.ExplosiveTrap;
import pd.levels.traps.GrimTrap;
import pd.levels.traps.ParalyticTrap;
import pd.levels.traps.SpearTrap;
import pd.levels.traps.SummoningTrap;
import pd.levels.traps.ToxicTrap;
import pd.levels.traps.Trap;
import pd.levels.traps.VenomTrap;
import pd.levels.traps.bufftrap.DarkBuff2Trap;
import pd.levels.traps.bufftrap.EarthBuff2Trap;
import pd.levels.traps.bufftrap.FireBuff2Trap;
import pd.levels.traps.bufftrap.IceBuff2Trap;
import pd.levels.traps.bufftrap.LightBuff2Trap;
import pd.levels.traps.bufftrap.ShockBuff2Trap;
import pd.levels.traps.damagetrap.FireDamageTrap;
import pd.mechanics.pathfind.PathFinder;
import pd.plants.Plant;
import render.utils.geom.Point;
import render.utils.geom.Rect;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;

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
			Type.COOKING, Type.VAULT, Type.TENTROOM,
			//SPSEXPD: 破碎（Shattered）特殊房间并入普通层轮换
			Type.SENTRY, Type.MAGICAL_FIRE, Type.TOXIC_GAS, Type.SACRIFICE, Type.WEAK_FLOOR,
			Type.FUSION_TRIAL, Type.RUNESTONE, Type.ARMORY, Type.CRYSTAL_CHOICE,
			Type.CRYSTAL_VAULT, Type.CRYSTAL_PATH, Type.PIT
	};
	//SPSEXPD: 破碎里每层最多一个水晶钥匙房间（对应 SpecialRoom.CRYSTAL_KEY_SPECIALS）
	private static final ArrayList<Type> CRYSTAL_TYPES = new ArrayList<>(Arrays.asList(
			Type.PIT, Type.CRYSTAL_VAULT, Type.CRYSTAL_CHOICE, Type.CRYSTAL_PATH
	));
	//SPSEXPD: 已去掉 SPS 的记忆火房间（SpsMemoryRoom），隐藏房间池不再抽取 MEMORY
	private static final Type[] HIDDEN_TYPES = {
			Type.MAGIC_WELL, Type.BARRICADED, Type.HIDE_SHOP,
			Type.MAGIC_WELL, Type.HIDE_SHOP, Type.WISH_POOL, Type.GLASSROOM,
			Type.PRISON_PIT, Type.BARRICADED, Type.WISH_POOL, Type.PRISON_PIT
	};
	private static final String LEGACY_SPECIAL_ROOMS = "sps_special_rooms";
	private static final String LEGACY_PIT_NEEDED = "sps_pit_needed";
	private static final ArrayList<Type> legacySpecialRotation = new ArrayList<>();
	//SPSEXPD: 破碎 WeakFloorRoom → 下一层 PitRoom 的联动（对应 Shattered 的 pitNeededDepth）
	private static int pitNeededDepth = -1;
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
		//SPSEXPD: 地形全部生成之后再定稿门与门后连通性 —— 装饰阶段新放的雕像/家具、
		//以及虚空都可能堵住门口，先修就会被后面的 pass 重新堵上（断头路/孤岛）
		for (Room room : legacyLayout.rooms) {
			openLegacyDoorInside(room);
			connectLegacyDoorsInside(room);
			paintDoors(room);
		}
		buildRoomAdapters();
		//SPSEXPD: 下水道不生成破碎的区域装饰（REGION_DECO/ALT 在排水道主题下是"储物木桶"）
		if (this instanceof SewerLevel) {
			for (int i = 0; i < length(); i++) {
				if (map[i] == Terrain.REGION_DECO || map[i] == Terrain.REGION_DECO_ALT) {
					map[i] = Terrain.EMPTY;
				}
			}
		}
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

		//SPSEXPD: 实验室房间每章一次，优先占据本层的特殊房间位（对齐 SpecialRoom.initForFloor）
		boolean labRoom = Dungeon.branch == 0 && Dungeon.legacyDepth() > 1 && Dungeon.labRoomNeeded();
		if (labRoom) Dungeon.LimitedDrops.LAB_ROOM.count++;
		//SPSEXPD: 破碎 WeakFloorRoom 的下一层必为 PitRoom
		boolean pitRoom = Dungeon.branch == 0 && pitNeededDepth >= 0
				&& Dungeon.legacyDepth() == pitNeededDepth;
		if (pitRoom) pitNeededDepth = -1;

		for (Room room : legacyLayout.rooms) {
			if (room.type != Type.NULL || room.connected.size() != 1) continue;
			if (!secrets.isEmpty() && room.width() > 5 && room.height() > 5
					&& hiddenRooms == 0) {
				room.type = secrets.get(Random.Int(secrets.size()));
				useLegacySpecialRoom(room.type);
				hiddenRooms++;
			} else if (!specials.isEmpty() && room.width() > 5 && room.height() > 5
					&& Random.Int(Math.max(1, specialRooms)) < 3) {
				Type picked = null;
				if (labRoom) {
					picked = Type.LABORATORY;
				} else if (pitRoom) {
					picked = Type.PIT;
				} else if (Dungeon.floorInChapter(Dungeon.legacyDepth()) == 2 && specials.contains(Type.COOKING)) {
					picked = Type.COOKING;
				} else if (Dungeon.floorInChapter(Dungeon.legacyDepth()) == 3 && specials.contains(Type.RUIN_ROOM)) {
					picked = Type.RUIN_ROOM;
				} else {
					int size = specials.size();
					picked = specials.get(Math.min(Random.Int(size), Random.Int(size)));
				}
				picked = fitLegacySpecialRoom(room, picked, specials);
				if (picked == null) {
					//SPSEXPD: 房间放不下任何候选（破碎房间各有最小尺寸要求），退化为普通房间
					maybeConnectLegacyRoom(room);
					continue;
				}
				room.type = picked;
				useLegacySpecialRoom(picked);
				specials.remove(picked);
				if (CRYSTAL_TYPES.contains(picked)) specials.removeAll(CRYSTAL_TYPES);
				if (picked == Type.WEAK_FLOOR) pitNeededDepth = Dungeon.legacyDepth() + 1;
				if (picked == Type.LABORATORY) labRoom = false;
				if (picked == Type.PIT) pitRoom = false;
				specialRooms++;
			} else if (Random.Int(2) == 0) {
				maybeConnectLegacyRoom(room);
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
		bundle.put(LEGACY_PIT_NEEDED, pitNeededDepth);
	}

	public static void restoreLegacySpecialRooms(Bundle bundle) {
		legacySpecialRotation.clear();
		pitNeededDepth = bundle.contains(LEGACY_PIT_NEEDED) ? bundle.getInt(LEGACY_PIT_NEEDED) : -1;
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

	private void maybeConnectLegacyRoom(Room room) {
		ArrayList<Room> candidates = new ArrayList<>();
		for (Room neighbour : room.neighbours) {
			if (!room.connected.containsKey(neighbour)
					&& !isSpecial(neighbour.type) && !isHidden(neighbour.type)) {
				candidates.add(neighbour);
			}
		}
		if (candidates.size() > 1) room.connect(Random.element(candidates));
	}

	/** 首选特殊房间放不下（房间小于其最小尺寸）时，改挑轮换表里第一个放得下的。 */
	private static Type fitLegacySpecialRoom(Room room, Type preferred, ArrayList<Type> specials) {
		if (fitsLegacyRoom(room, preferred)) return preferred;
		for (Type candidate : specials) {
			if (fitsLegacyRoom(room, candidate)) return candidate;
		}
		return null;
	}

	private static boolean fitsLegacyRoom(Room room, Type type) {
		SpecialRoom painter = specialRoomPainter(type);
		if (painter == null) return true;
		return room.width() >= painter.minWidth() && room.height() >= painter.minHeight();
	}

	private void placeLegacyDoors() {
		for (Room room : legacyLayout.rooms) {
			for (Room neighbour : new ArrayList<>(room.connected.keySet())) {
				if (room.connected.get(neighbour) != null) continue;
				Rect overlap = room.intersect(neighbour);
				int spanX = overlap.right - overlap.left;
				int spanY = overlap.bottom - overlap.top;
				Door door;
				if (overlap.width() == 0 && spanY >= 1) {
					door = new Door(overlap.left,
							spanY == 1 ? overlap.top : Random.Int(overlap.top + 1, overlap.bottom));
				} else if (overlap.height() == 0 && spanX >= 1) {
					door = new Door(spanX == 1 ? overlap.left : Random.Int(overlap.left + 1, overlap.right),
							overlap.top);
				} else {
					//SPSEXPD: 两个房间没有贴合（connected 里有这条边，地图上却开不出门）——
					//把边从双方图里去掉，保证"图上连通"与"地图上连通"一致，否则会出现整片孤岛
					room.connected.remove(neighbour);
					neighbour.connected.remove(room);
					continue;
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
		markAsOriginal(mob);
		Buff.affect(mob, ExProtect.class);
		Buff.affect(mob, ShieldArmor.class).level(Dungeon.depth * 5);
		Buff.affect(mob, MagicArmor.class).level(Dungeon.depth * 5);
		mobs().add(mob);
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
		GroundItems.explant( this, seed, Random.element(candidates));
	}

	private void paintLegacyStandardRoom(Room room) {
		//SPSEXPD: 一半概率改用破碎的标准房间结构（StandardRoom 池）
		if (shatteredStructureAllowed() && Random.Int(2) == 0 && paintLegacyShatteredRoom(room)) return;
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
					if (Dungeon.depth > 1 && Dungeon.depth < 33
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
					GroundItems.setTrap( this, trap, cell);
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
				Item item = GroundItems.findPrizeItem( this );
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
						&& (Dungeon.depth < 34 || Dungeon.depth > 40) && Random.Int(3) == 0
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

	/**
	 * SPSEXPD: 用破碎房间自己的 paint 在 SPS 房间上绘制（普通房间 / 走廊 / 特殊房间共用）：
	 * 绘制矩形收敛到该房间允许的尺寸、门所在边对齐、另一维以门为中心展开；
	 * 房间小于该房间最小尺寸、或容不下该门位时返回 null，调用方退回 SPS 自绘。
	 */
	private pd.levels.rooms.Room.Door paintShatteredRoom(Room room,
			pd.levels.rooms.Room painter, Door legacyDoor) {
		if (painter == null || legacyDoor == null) return null;
		int roomW;
		int roomH;
		if (painter.maxWidth() > 0 || painter.minWidth() > 0
				|| painter.maxHeight() > 0 || painter.minHeight() > 0) {
			if (room.width() < painter.minWidth() || room.height() < painter.minHeight()) return null;
			roomW = painter.maxWidth() > 0 ? Math.min(room.width(), painter.maxWidth()) : room.width();
			roomH = painter.maxHeight() > 0 ? Math.min(room.height(), painter.maxHeight()) : room.height();
		} else {
			roomW = room.width();
			roomH = room.height();
		}
		int left;
		int top;
		if (legacyDoor.x <= room.left || legacyDoor.x >= room.right) {
			int naturalTop = legacyDoor.y - roomH / 2;
			if (naturalTop < room.top || naturalTop > room.bottom - roomH) return null;
			left = legacyDoor.x <= room.left ? room.left : room.right - roomW;
			top = naturalTop;
		} else {
			int naturalLeft = legacyDoor.x - roomW / 2;
			if (naturalLeft < room.left || naturalLeft > room.right - roomW) return null;
			top = legacyDoor.y <= room.top ? room.top : room.bottom - roomH;
			left = naturalLeft;
		}

		//收紧后房间外沿保持墙体，避免露出未绘制区域
		fill(room, Terrain.WALL);
		painter.set(left, top, left + roomW, top + roomH);
		EmptyRoom neighbour = new EmptyRoom();
		neighbour.set(room.left - 1, room.top - 1, room.right + 1, room.bottom + 1);
		pd.levels.rooms.Room.Door door =
				new pd.levels.rooms.Room.Door(legacyDoor.x, legacyDoor.y);
		painter.connected.put(neighbour, door);
		neighbour.connected.put(painter, door);
		painter.paint(this);
		return door;
	}

	/** SPSEXPD: 用破碎的标准房间结构绘制普通房间（房间/门位不合适时退回 SPS 自绘结构）。 */
	private boolean paintLegacyShatteredRoom(Room room) {
		Door legacyDoor = room.connected.isEmpty() ? null : room.connected.values().iterator().next();
		if (legacyDoor == null) return false;
		pd.levels.rooms.Room.Door painted =
				paintShatteredRoom(room, StandardRoom.createRoom(), legacyDoor);
		if (painted == null) return false;
		legacyDoor.set(convertDoorType(painted.type));
		return true;
	}

	/** SPSEXPD: 用破碎的连接房间结构绘制走廊/通道（失败时退回 SPS 自绘）。 */
	private boolean paintLegacyShatteredCorridor(Room room) {
		Door legacyDoor = room.connected.isEmpty() ? null : room.connected.values().iterator().next();
		if (legacyDoor == null) return false;
		pd.levels.rooms.Room.Door painted =
				paintShatteredRoom(room, ConnectionRoom.createRoom(), legacyDoor);
		if (painted == null) return false;
		legacyDoor.set(convertDoorType(painted.type));
		return true;
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

		//SPSEXPD: 破碎（Shattered）房间按各自的最小/最大尺寸绘制（水晶三房还会向两侧铺开
		//6 个子房），SPS 房间本身为 8-10 格设计、保持原样。破碎房间把绘制矩形收紧到它允许的
		//尺寸：门所在的边与房间轮廓对齐，另一维以门为中心展开；房间容不下该门位时按普通房间绘制。
		painter.set(room.left, room.top, room.right, room.bottom);
		if (!isSpsRoom(painter)) {
			int roomW = Math.max(painter.minWidth(), Math.min(room.width(), painter.maxWidth()));
			int roomH = Math.max(painter.minHeight(), Math.min(room.height(), painter.maxHeight()));
			int left;
			int top;
			if (legacyDoor.x <= room.left || legacyDoor.x >= room.right) {
				int naturalTop = legacyDoor.y - roomH / 2;
				if (naturalTop < room.top || naturalTop > room.bottom - roomH) {
					paintOpenRoom(room, isHidden(room.type) ? Door.Type.HIDDEN : Door.Type.REGULAR);
					return;
				}
				left = legacyDoor.x <= room.left ? room.left : room.right - roomW;
				top = naturalTop;
			} else {
				int naturalLeft = legacyDoor.x - roomW / 2;
				if (naturalLeft < room.left || naturalLeft > room.right - roomW) {
					paintOpenRoom(room, isHidden(room.type) ? Door.Type.HIDDEN : Door.Type.REGULAR);
					return;
				}
				top = legacyDoor.y <= room.top ? room.top : room.bottom - roomH;
				left = naturalLeft;
			}
			//收紧后房间外沿保持墙体，避免露出未绘制区域
			fill(room, Terrain.WALL);
			painter.set(left, top, left + roomW, top + roomH);
		}

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

	/**
	 * SPSEXPD: 破碎的房间/走廊结构只作用于五个区域的主线普通层。
	 * 固定地图（天狗隐匿处、首领图、城镇等）与分支层保持 SPS 自绘，避免破坏既有连通性。
	 */
	protected boolean shatteredStructureAllowed() {
		if (Dungeon.branch != 0) return false;
		return this instanceof SewerLevel || this instanceof PrisonLevel
				|| this instanceof CavesLevel || this instanceof CityLevel
				|| this instanceof HallsLevel;
	}

	/** SPS 自绘房间（SpsXxxRoom）为 8-10 格的旧布局设计，不参与破碎房间的尺寸收紧。 */
	private static boolean isSpsRoom(SpecialRoom painter) {
		return painter.getClass().getSimpleName().startsWith("Sps");
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
					if (trap != null) GroundItems.setTrap( this, trap.reveal(), cell);
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
		GroundItems.addItemToSpawn( this, new PotionOfLevitation());
	}

	private Item legacyTrapRoomPrize() {
		if (Random.Int(4) != 0) {
			Item prize = GroundItems.findPrizeItem( this );
			if (prize != null) return prize;
		}

		Item prize = Generator.random(Random.oneOf(Generator.Category.MELEEWEAPON, Generator.Category.ARMOR));
		for (int i = 0; i < 3; i++) {
			Item candidate = Generator.random(Random.oneOf(Generator.Category.MELEEWEAPON, Generator.Category.ARMOR));
			if (candidate.level() > prize.level()) prize = candidate;
		}
		return prize;
	}

	private static SpecialRoom specialRoomPainter(Type type) {
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
			//SPSEXPD: 破碎（Shattered）特殊房间
			case SENTRY: return new SentryRoom();
			case MAGICAL_FIRE: return new MagicalFireRoom();
			case TOXIC_GAS: return new ToxicGasRoom();
			case SACRIFICE: return new SacrificeRoom();
			case WEAK_FLOOR: return new WeakFloorRoom();
			case FUSION_TRIAL: return new FusionTrialRoom();
			case RUNESTONE: return new RunestoneRoom();
			case ARMORY: return new ArmoryRoom();
			case CRYSTAL_CHOICE: return new CrystalChoiceRoom();
			case CRYSTAL_VAULT: return new CrystalVaultRoom();
			case CRYSTAL_PATH: return new CrystalPathRoom();
			case PIT: return new PitRoom();
			case LABORATORY: return new LaboratoryRoom();
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
		//SPSEXPD: 一半概率改用破碎的连接房间结构（TunnelRoom/BridgeRoom/PerimeterRoom/WalkwayRoom/...）
		if (shatteredStructureAllowed() && Random.Int(2) == 0 && paintLegacyShatteredCorridor(room)) return;
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
		//SPSEXPD: 一半概率改用破碎的连接房间结构（失败时退回 SPS 的环形通道）
		if (shatteredStructureAllowed() && Random.Int(2) == 0 && paintLegacyShatteredCorridor(room)) return;
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

	/**
	 * SPSEXPD: 破碎结构只为它自己认得的那一个门开口，房间其余门在 fill(WALL) 后里侧仍是墙，
	 * 破碎房间自己画的虚空也会落在门后 —— 两者都会把门后的走廊切断。
	 * 这里为每个门沿法线向内打通到第一个可站人格（没走破碎结构的房间本来就有地板，循环立即停止）。
	 */
	private void openLegacyDoorInside(Room room) {
		for (Door door : room.connected.values()) {
			if (door == null) continue;
			int stepX = door.x <= room.left ? 1 : door.x >= room.right ? -1 : 0;
			int stepY = door.y <= room.top ? 1 : door.y >= room.bottom ? -1 : 0;
			if (stepX == 0 && stepY == 0) continue;
			for (int x = door.x + stepX, y = door.y + stepY;
					x > room.left && x < room.right && y > room.top && y < room.bottom;
					x += stepX, y += stepY) {
				int cell = x + y * width();
				if (!insideMap(cell)) break;
				//虚空(PIT)不算通路：它和墙一样会切断门后的走廊，需要填成地板
				boolean pit = (Terrain.flags[map[cell]] & Terrain.PIT) != 0;
				if (!pit && legacyTraversable(cell)) break;
				map[cell] = Terrain.EMPTY;
			}
		}
	}

	/**
	 * SPSEXPD: 同一个房间的门可能被墙/虚空隔开（破碎结构只认一个门，TUNNEL/PASSAGE 的通道
	 * 也可能只覆盖部分门），于是"门在这头、通道在那头"，玩家进门就撞墙。
	 * 这里把房间内各门的里侧格按连通分量分组，只把小块补通到最大块（尽量不动原有结构）；
	 * 只有 1 个门的房间则确保门里侧能走到房间的主区域。
	 */
	private void connectLegacyDoorsInside(Room room) {
		ArrayList<int[]> insides = new ArrayList<>();
		for (Door door : room.connected.values()) {
			if (door == null) continue;
			int stepX = door.x <= room.left ? 1 : door.x >= room.right ? -1 : 0;
			int stepY = door.y <= room.top ? 1 : door.y >= room.bottom ? -1 : 0;
			int x = door.x + stepX;
			int y = door.y + stepY;
			if (x <= room.left || x >= room.right || y <= room.top || y >= room.bottom) continue;
			insides.add(new int[]{x, y});
		}
		if (insides.isEmpty()) return;

		if (insides.size() == 1) {
			int[] only = insides.get(0);
			int anchor = legacyLargestWalkableCell(room);
			if (anchor >= 0 && !legacyConnectedWithin(room, anchor, only[0] + only[1] * width())) {
				drawLegacyCorridor(new int[]{anchor % width(), anchor / width()}, only);
			}
			return;
		}

		ArrayList<ArrayList<int[]>> groups = new ArrayList<>();
		for (int[] cell : insides) {
			ArrayList<int[]> group = null;
			for (ArrayList<int[]> existing : groups) {
				int[] head = existing.get(0);
				if (legacyConnectedWithin(room, head[0] + head[1] * width(), cell[0] + cell[1] * width())) {
					group = existing;
					break;
				}
			}
			if (group == null) {
				group = new ArrayList<>();
				groups.add(group);
			}
			group.add(cell);
		}
		ArrayList<int[]> main = groups.get(0);
		for (ArrayList<int[]> group : groups) if (group.size() > main.size()) main = group;
		int[] anchor = main.get(0);
		for (ArrayList<int[]> group : groups) {
			if (group == main) continue;
			drawLegacyCorridor(anchor, group.get(0));
		}
	}

	/** 房间内面积最大的可站连通块里的一个代表格，找不到返回 -1。 */
	private int legacyLargestWalkableCell(Room room) {
		boolean[] seen = new boolean[length()];
		int bestCell = -1;
		int bestSize = 0;
		for (int y = room.top + 1; y < room.bottom; y++) {
			for (int x = room.left + 1; x < room.right; x++) {
				int start = x + y * width();
				if (seen[start] || !legacyTraversable(start)) continue;
				seen[start] = true;
				ArrayList<Integer> stack = new ArrayList<>();
				stack.add(start);
				int size = 0;
				while (!stack.isEmpty()) {
					int cell = stack.remove(stack.size() - 1);
					size++;
					int cx = cell % width();
					int cy = cell / width();
					if (cx - 1 > room.left) {
						int next = cell - 1;
						if (!seen[next] && legacyTraversable(next)) { seen[next] = true; stack.add(next); }
					}
					if (cx + 1 < room.right) {
						int next = cell + 1;
						if (!seen[next] && legacyTraversable(next)) { seen[next] = true; stack.add(next); }
					}
					if (cy - 1 > room.top) {
						int next = cell - width();
						if (!seen[next] && legacyTraversable(next)) { seen[next] = true; stack.add(next); }
					}
					if (cy + 1 < room.bottom) {
						int next = cell + width();
						if (!seen[next] && legacyTraversable(next)) { seen[next] = true; stack.add(next); }
					}
				}
				if (size > bestSize) {
					bestSize = size;
					bestCell = start;
				}
			}
		}
		return bestCell;
	}

	private boolean legacyConnectedWithin(Room room, int from, int to) {
		if (from == to) return true;
		boolean[] seen = new boolean[length()];
		ArrayList<Integer> pending = new ArrayList<>();
		pending.add(from);
		while (!pending.isEmpty()) {
			int cell = pending.remove(pending.size() - 1);
			if (cell == to) return true;
			if (!insideMap(cell) || seen[cell] || !legacyTraversable(cell)) continue;
			int x = cell % width();
			int y = cell / width();
			if (x <= room.left || x >= room.right || y <= room.top || y >= room.bottom) continue;
			seen[cell] = true;
			if (x > 0) pending.add(cell - 1);
			if (x < width() - 1) pending.add(cell + 1);
			if (y > 0) pending.add(cell - width());
			if (y < height() - 1) pending.add(cell + width());
		}
		return false;
	}

	private void drawLegacyCorridor(int[] from, int[] to) {
		int floor = tunnelTile();
		for (int x = Math.min(from[0], to[0]); x <= Math.max(from[0], to[0]); x++) carveLegacy(x, from[1], floor);
		for (int y = Math.min(from[1], to[1]); y <= Math.max(from[1], to[1]); y++) carveLegacy(to[0], y, floor);
	}

	/** SPSEXPD: 挖掉真正挡路的东西（墙、玻璃墙、实心装饰等），可通行的格与门一概不动。 */
	private void carveLegacy(int x, int y, int terrain) {
		if (x < 0 || y < 0 || x >= width() || y >= height()) return;
		int cell = x + y * width();
		if (legacyTraversable(cell)) return;
		map[cell] = terrain;
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
					|| room.type == Type.SHOP || room.type == Type.HIDE_SHOP
					//SPSEXPD: 铁匠房自己有火焰陷阱环，不能被随机陷阱覆盖
					|| room.type == Type.BLACKSMITH) continue;
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
			GroundItems.setTrap( this, trap, cell);
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

		//SPSEXPD: 藏宝地（E_DUST）每层 1~5 个（原为固定 10）；「探索点」（M_WEB）已整体移除
		int dustySpots = Random.IntRange(1, 5);
		for (int i = 0; i < dustySpots; i++) {
			Item item = Random.Int(5) == 0 ? Generator.random() : new YellowDewdrop();
			dropLegacyItem(item, Heap.Type.E_DUST);
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
		if (legacyDepth < Dungeon.NORMAL_FLOORS_PER_CHAPTER + 1 && !Statistics.amuletObtained) {
			return 10 + legacyDepth + Random.Int(3);
		} else if (!Statistics.amuletObtained) {
			return 15 + legacyDepth % 3 + Random.Int(3);
		} else {
			return 10 + (Dungeon.FLOORS_PER_CHAPTER - Dungeon.floorInChapter(legacyDepth)) + Random.Int(3);
		}
	}

	@Override
	protected void createMobs() {
		HashSet<Mob> existing = mobs().snapshot();
		super.createMobs();
		for (Mob mob : mobs()) {
			if (!existing.contains(mob)) applyLegacyInitialMobTraits(mob);
		}
	}

	void applyLegacyInitialMobTraits(Mob mob) {
		markAsOriginal(mob);
		int multiplier;
		if (this instanceof CavesLevel) multiplier = 5;
		else if (this instanceof CityLevel) multiplier = 10;
		else if (this instanceof HallsLevel) multiplier = 15;
		else return;
		Buff.affect(mob, ShieldArmor.class).level(Dungeon.legacyDepth() * multiplier);
		Buff.affect(mob, MagicArmor.class).level(Dungeon.legacyDepth() * multiplier);
		if (this instanceof HallsLevel) Buff.affect(mob, GlassShield.class).turns(1);
	}

	//SPSEXPD: 初始怪物统一标记（露珠爆破不再默认给予，改由露珠神像/露珠果实等提供）
	private void markAsOriginal( Mob mob ) {
		mob.spsOriginalGeneration = true;
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
			if (legacy.type == Type.NULL) continue;
			//SPSEXPD: 特殊房间也暴露成对应的 Shattered 房间实例——探索度评分、传送卷轴、
			//水晶钥匙房间判定等都靠 rooms() 里的房间类型识别（走廊仍不暴露，保持原行为）。
			pd.levels.rooms.Room adapter = legacySpecialAdapter(legacy.type);
			if (adapter == null) {
				if (legacy.type != Type.STANDARD) continue;
				adapter = new EmptyRoom();
			}
			adapter.set(legacy.left, legacy.top, legacy.right, legacy.bottom);
			rooms.add(adapter);
		}
	}

	/** 用绘制器映射生成与 legacy 房间类型对应的 Shattered 房间实例（无对应则返回 null）。 */
	private static pd.levels.rooms.Room legacySpecialAdapter(Type type) {
		return isSpecial(type) || isHidden(type) ? specialRoomPainter(type) : null;
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
				|| terrain == Terrain.BARRICADE || terrain == Terrain.BOOKSHELF
				//SPSEXPD: 其它门类地形（破损门/水晶门/骷髅钥匙门）也是通路，不能当成墙
				|| terrain == Terrain.BROKEN_DOOR || terrain == Terrain.CRYSTAL_DOOR
				|| terrain == Terrain.HERO_LKD_DR;
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
		//SPSEXPD: 第一层的陷阱池只有知识陷阱，按需求把这一层的陷阱数量压到很少
		if (Dungeon.legacyDepth() == 1) return Random.NormalIntRange(3, 5);
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