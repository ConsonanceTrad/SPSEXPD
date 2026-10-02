/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Special Surprise adventure maps rebuilt for the Shattered 4.0 level system.
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.levels;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.AdventureGuardian;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.AdventureGuide;
import pd.actors.mobs.npcs.SokobanBlock;
import pd.items.quest.AdventureJournal;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.levels.builders.Builder;
import pd.levels.builders.LineBuilder;
import pd.levels.features.LevelTransition;
import pd.levels.painters.CavesPainter;
import pd.levels.painters.CityPainter;
import pd.levels.painters.HallsPainter;
import pd.levels.painters.Painter;
import pd.levels.painters.PrisonPainter;
import pd.levels.painters.SewerPainter;
import pd.levels.rooms.Room;
import pd.levels.rooms.standard.EmptyRoom;
import pd.levels.rooms.standard.entrance.EntranceRoom;
import pd.levels.rooms.standard.exit.ExitRoom;
import pd.levels.traps.BurningTrap;
import pd.levels.traps.ChillingTrap;
import pd.levels.traps.ConfusionTrap;
import pd.levels.traps.ExplosiveTrap;
import pd.levels.traps.GrippingTrap;
import pd.levels.traps.OozeTrap;
import pd.levels.traps.PoisonDartTrap;
import pd.levels.traps.ShockingTrap;
import pd.levels.traps.TeleportationTrap;
import pd.levels.traps.Trap;
import pd.levels.traps.WornDartTrap;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

public class AdventureLevel extends RegularLevel {

	private int goalCell;
	private int[] puzzleStartPositions = new int[0];
	private int puzzleSwitch = -1;
	private int portalFrom = -1;
	private int portalTo = -1;
	private boolean portalActive;

	private static final String[][] PUZZLE_LAYOUTS = {
		{
			"#################",
			"#...............#",
			"#...............#",
			"#......X........#",
			"#......$........#",
			"#......@........#",
			"#...............#",
			"#...............#",
			"#################"
		}, {
			"#################",
			"#...............#",
			"#...............#",
			"#.....XX........#",
			"#.....$$........#",
			"#......@........#",
			"#...............#",
			"#...............#",
			"#################"
		}, {
			"#################",
			"#.......#.......#",
			"#..@....#..X....#",
			"#..$....#.......#",
			"#..S....#.......#",
			"#.......#.......#",
			"#..$a...#..A....#",
			"#.......#.......#",
			"#################"
		}, {
			"#################",
			"#...............#",
			"#....XXX........#",
			"#....$$$........#",
			"#.....@.........#",
			"#..###...###....#",
			"#...............#",
			"#...............#",
			"#################"
		}
	};

	private static final String[][] HUB_LAYOUTS = {
		{
			"#################",
			"#.......N.......#",
			"#..~~~.....~~~..#",
			"#...gg.....gg...#",
			"#...............#",
			"#..###.....###..#",
			"#.......@.......#",
			"#...............#",
			"#################"
		}, {
			"#################",
			"#..###.....###..#",
			"#..#.#..N..#.#..#",
			"#..###.....###..#",
			"#...............#",
			"#..##.......##..#",
			"#.......@.......#",
			"#...............#",
			"#################"
		}, {
			"#################",
			"#.......N.......#",
			"#..gg.......gg..#",
			"#...............#",
			"#..###.....###..#",
			"#...............#",
			"#.......@.......#",
			"#...............#",
			"#################"
		}
	};

	public AdventureLevel() {
		int theme = theme();
		switch (theme) {
			case 0: color1 = 0x48763c; color2 = 0x59994a; break;
			case 1: color1 = 0x6a723d; color2 = 0x88924c; break;
			case 2: color1 = 0x534f3e; color2 = 0xb9d661; break;
			case 3: color1 = 0x4b6636; color2 = 0xf2f2f2; break;
			default: color1 = 0x801500; color2 = 0xa68521; break;
		}
	}

	private int destination() {
		return Math.max(0, AdventureJournal.destinationForBranch(Dungeon.branch));
	}

	private int theme() {
		int destination = destination();
		if (destination == 0 || destination == 5 || destination == 8) return 3;
		if (destination >= 1 && destination <= 4) return 2;
		return destination % 5;
	}

	private boolean peaceful() {
		int destination = destination();
		return destination <= 5 || destination == 8;
	}

	private boolean puzzle() {
		return destination() >= 1 && destination() <= 4;
	}

	@Override
	protected boolean build() {
		feeling = Feeling.NONE;
		if (puzzle()) return buildPuzzle();
		if (fixedHub()) return buildHub();
		if (!super.build()) return false;
		int entrance = entrance();
		goalCell = exit();
		transitions.clear();
		transitions.add(new LevelTransition(this, entrance,
				LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
		map[entrance] = Terrain.ENTRANCE;
		map[goalCell] = Terrain.PEDESTAL;

		for (int i = 0; i < length(); i++) {
			Trap trap = traps.get(i);
			if (trap != null) {
				trap.visible = true;
				map[i] = Terrain.TRAP;
			}
		}
		applyDestinationTerrain();
		return true;
	}

	private boolean fixedHub() {
		return destination() == 0 || destination() == 5 || destination() == 8;
	}

	private boolean buildHub() {
		int layoutIndex = destination() == 0 ? 0 : destination() == 5 ? 1 : 2;
		String[] layout = HUB_LAYOUTS[layoutIndex];
		setSize(layout[0].length(), layout.length);
		rooms = new ArrayList<>();
		goalCell = -1;
		for (int y = 0; y < layout.length; y++) {
			if (layout[y].length() != width()) return false;
			for (int x = 0; x < width(); x++) {
				int cell = x + y * width();
				switch (layout[y].charAt(x)) {
					case '#': map[cell] = Terrain.WALL; break;
					case '@':
						map[cell] = Terrain.ENTRANCE;
						transitions.add(new LevelTransition(this, cell,
								LevelTransition.Type.BRANCH_ENTRANCE,
								Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
						break;
					case 'N': map[cell] = Terrain.PEDESTAL; goalCell = cell; break;
					case '~': map[cell] = Terrain.WATER; break;
					case 'g': map[cell] = Terrain.GRASS; break;
					default: map[cell] = Terrain.EMPTY; break;
				}
			}
		}
		return !transitions.isEmpty() && goalCell >= 0;
	}

	private void applyDestinationTerrain() {
		int destination = destination();
		for (int cell = width() + 1; cell < length() - width() - 1; cell++) {
			if ((Terrain.flags[map[cell]] & Terrain.PASSABLE) == 0
					|| cell == entrance() || cell == goalCell || traps.get(cell) != null) continue;
			int pattern = Math.floorMod(cell * 17 + destination * 31, 29);
			if (pattern > 2) continue;
			switch (destination) {
				case 6: map[cell] = pattern == 0 ? Terrain.HIGH_GRASS : Terrain.GRASS; break;
				case 7: map[cell] = Terrain.EMPTY_DECO; break;
				case 9: map[cell] = Terrain.WATER; break;
				case 10: map[cell] = Terrain.EMPTY_SP; break;
				case 11: map[cell] = Terrain.EMBERS; break;
				case 12: map[cell] = Terrain.WATER; break;
				case 13: map[cell] = Terrain.EMPTY_DECO; break;
				case 14: map[cell] = Terrain.GRASS; break;
				case 15: map[cell] = Terrain.EMPTY_SP; break;
				case 16: map[cell] = Terrain.EMBERS; break;
				case 17: map[cell] = pattern == 0 ? Terrain.EMBERS : Terrain.EMPTY_SP; break;
				case 18: map[cell] = Terrain.EMPTY_DECO; break;
				case 19: map[cell] = Terrain.EMBERS; break;
				case 20: map[cell] = Terrain.PEDESTAL; break;
				case 21: map[cell] = Terrain.EMPTY_SP; break;
				case 22: map[cell] = pattern == 0 ? Terrain.WATER : Terrain.EMBERS; break;
				case 23: map[cell] = Terrain.PEDESTAL; break;
				case 24: map[cell] = pattern == 0 ? Terrain.EMBERS : Terrain.EMPTY_SP; break;
			}
		}
	}

	private boolean buildPuzzle() {
		String[] layout = PUZZLE_LAYOUTS[destination() - 1];
		setSize(layout[0].length(), layout.length);
		rooms = new ArrayList<>();
		ArrayList<Integer> starts = new ArrayList<>();
		goalCell = -1;
		puzzleSwitch = portalFrom = portalTo = -1;
		portalActive = false;

		for (int y = 0; y < layout.length; y++) {
			if (layout[y].length() != width()) return false;
			for (int x = 0; x < width(); x++) {
				int cell = x + y * width();
				switch (layout[y].charAt(x)) {
					case '#': map[cell] = Terrain.WALL; break;
					case '@':
						map[cell] = Terrain.ENTRANCE;
						transitions.add(new LevelTransition(this, cell,
								LevelTransition.Type.BRANCH_ENTRANCE,
								Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
						break;
					case '$': map[cell] = Terrain.EMPTY; starts.add(cell); break;
					case 'X': map[cell] = Terrain.PEDESTAL; goalCell = cell; break;
					case 'S': map[cell] = Terrain.PEDESTAL; puzzleSwitch = cell; break;
					case 'a': map[cell] = Terrain.EMPTY_SP; portalFrom = cell; break;
					case 'A': map[cell] = Terrain.EMPTY_SP; portalTo = cell; break;
					default: map[cell] = Terrain.EMPTY; break;
				}
			}
		}

		puzzleStartPositions = new int[starts.size()];
		for (int i = 0; i < starts.size(); i++) puzzleStartPositions[i] = starts.get(i);
		return !transitions.isEmpty() && goalCell >= 0;
	}

	@Override
	protected ArrayList<Room> initRooms() {
		ArrayList<Room> rooms = new ArrayList<>();
		rooms.add(roomEntrance = new EntranceRoom());
		int roomCount = destination() >= 21 ? 9 : destination() >= 9 ? 7 : 5;
		for (int i = 0; i < roomCount; i++) rooms.add(new EmptyRoom());
		rooms.add(roomExit = new ExitRoom());
		return rooms;
	}

	@Override
	protected Builder builder() {
		return new LineBuilder()
				.setPathVariance(destination() >= 21 ? 0.55f : 0.35f)
				.setPathLength(1f, new float[]{1})
				.setTunnelLength(new float[]{1, 2, 1}, new float[]{1});
	}

	@Override
	protected Painter painter() {
		int destination = destination();
		int trapCount = peaceful() ? (destination >= 1 && destination <= 4 ? 5 : 0)
				: destination >= 21 ? 8 : 3;
		switch (theme()) {
			case 0:
				return new SewerPainter().setWater(0.32f, 4).setGrass(0.15f, 3)
						.setTraps(trapCount, new Class[]{OozeTrap.class, WornDartTrap.class}, new float[]{1, 1});
			case 1:
				return new PrisonPainter().setWater(0.12f, 4).setGrass(0.08f, 3)
						.setTraps(trapCount, new Class[]{PoisonDartTrap.class, BurningTrap.class}, new float[]{1, 1});
			case 2:
				return new CavesPainter().setWater(0.18f, 4).setGrass(0.18f, 3)
						.setTraps(trapCount, new Class[]{GrippingTrap.class, ExplosiveTrap.class}, new float[]{1, 1});
			case 3:
				return new CityPainter().setWater(0.10f, 4).setGrass(0.10f, 3)
						.setTraps(trapCount, new Class[]{ShockingTrap.class, TeleportationTrap.class}, new float[]{1, 1});
			default:
				return new HallsPainter().setWater(0.18f, 4).setGrass(0.05f, 3)
						.setTraps(trapCount, new Class[]{ChillingTrap.class, ConfusionTrap.class}, new float[]{1, 1});
		}
	}

	@Override
	protected void createMobs() {
		if (puzzle()) {
			createPuzzleBlocks();
			return;
		}
		if (peaceful()) {
			AdventureGuide guide = new AdventureGuide().configure(destination());
			guide.pos = goalCell;
			mobs().add(guide);
			return;
		}
		int destination = destination();
		AdventureGuardian guardian = new AdventureGuardian().configure(destination, true);
		guardian.pos = goalCell;
		mobs().add(guardian);

		int echoes = destination == 21 ? 0 : destination >= 21 ? 3 + destination % 3
				: destination >= 9 ? 1 + destination % 3 : 1;
		for (int i = 0; i < echoes; i++) {
			AdventureGuardian echo = new AdventureGuardian().configure(destination, false);
			int pos = randomRespawnCell(echo);
			if (pos >= 0 && pos != goalCell && mobs().findMob(pos) == null) {
				echo.pos = pos;
				mobs().add(echo);
			}
		}
	}

	private void createPuzzleBlocks() {
		for (int start : puzzleStartPositions) {
			SokobanBlock block = new SokobanBlock();
			block.pos = start;
			block.setHomePos(start);
			mobs().add(block);
		}
	}

	public void resetPuzzle(Hero hero) {
		if (!puzzle() || puzzleStartPositions.length == 0) return;
		ArrayList<SokobanBlock> blocks = new ArrayList<>();
		for (Mob mob : mobs()) {
			if (mob instanceof SokobanBlock) blocks.add((SokobanBlock)mob);
		}
		if (blocks.size() != puzzleStartPositions.length) return;

		for (SokobanBlock block : blocks) block.pos = -1;
		ScrollOfTeleportation.appear(hero, entrance());

		ArrayList<Integer> availableStarts = new ArrayList<>();
		for (int start : puzzleStartPositions) availableStarts.add(start);
		for (SokobanBlock block : blocks) {
			int start = block.homePos();
			if (!availableStarts.remove((Integer)start)) start = availableStarts.remove(0);
			block.setHomePos(start);
			block.pos = start;
			if (block.sprite != null) block.sprite.place(start);
			occupyCell(block);
		}
		portalActive = false;
		GameScene.updateMap();
		Dungeon.observe();
		GameScene.updateFog();
		hero.spendAndNext(1f);
	}

	@Override
	protected void createItems() {
		// Adventure rewards are progression unlocks, not repeatable random resources.
	}

	@Override
	public void occupyCell(Char ch) {
		super.occupyCell(ch);
		if (puzzle() && ch instanceof SokobanBlock) {
			checkPuzzleSolved();
		} else if (puzzle() && ch == Dungeon.hero && portalActive) {
			teleportHeroFromPortal((Hero)ch);
		}
	}

	public void afterBlockPushed(SokobanBlock block) {
		if (!puzzle()) return;
		boolean wasActive = portalActive;
		portalActive = puzzleSwitch >= 0 && Actor.findChar(puzzleSwitch) instanceof SokobanBlock;
		if (portalActive && !wasActive) GLog.p(Messages.get(this, "portal_activated"));
		if (portalActive && block.pos == portalFrom) {
			int arrival = portalTo - width();
			if (passable[arrival] && Actor.findChar(arrival) == null) {
				ScrollOfTeleportation.appear(block, arrival);
			}
		}
		checkPuzzleSolved();
	}

	private void teleportHeroFromPortal(Hero hero) {
		int target = hero.pos == portalFrom ? portalTo : hero.pos == portalTo ? portalFrom : -1;
		if (target >= 0 && Actor.findChar(target) == null) {
			ScrollOfTeleportation.appear(hero, target);
		}
	}

	private void checkPuzzleSolved() {
		for (Mob mob : mobs()) {
			if (mob instanceof SokobanBlock && map[mob.pos] != Terrain.PEDESTAL) return;
		}
		AdventureJournal.complete(destination());
	}

	@Override
	public Mob createMob() {
		return new AdventureGuardian().configure(destination(), false);
	}

	@Override
	public Actor addRespawner() {
		return null;
	}

	@Override
	public String tilesTex() {
		switch (theme()) {
			case 0: return Assets.Environment.SPS_TILES_SEWERS_LEGACY;
			case 1: return Assets.Environment.SPS_TILES_PRISON_LEGACY;
			case 2: return Assets.Environment.SPS_TILES_CAVES_LEGACY;
			case 3: return Assets.Environment.SPS_TILES_CITY_LEGACY;
			default: return Assets.Environment.SPS_TILES_HALLS_LEGACY;
		}
	}

	@Override
	public String waterTex() {
		switch (theme()) {
			case 0: return Assets.Environment.SPS_WATER_SEWERS;
			case 1: return Assets.Environment.SPS_WATER_PRISON;
			case 2: return Assets.Environment.SPS_WATER_CAVES;
			case 3: return Assets.Environment.SPS_WATER_CITY;
			default: return Assets.Environment.SPS_WATER_HALLS;
		}
	}

	private static final String GOAL_CELL = "goal_cell";
	private static final String PUZZLE_STARTS = "puzzle_starts";
	private static final String PUZZLE_SWITCH = "puzzle_switch";
	private static final String PORTAL_FROM = "portal_from";
	private static final String PORTAL_TO = "portal_to";
	private static final String PORTAL_ACTIVE = "portal_active";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(GOAL_CELL, goalCell);
		bundle.put(PUZZLE_STARTS, puzzleStartPositions);
		bundle.put(PUZZLE_SWITCH, puzzleSwitch);
		bundle.put(PORTAL_FROM, portalFrom);
		bundle.put(PORTAL_TO, portalTo);
		bundle.put(PORTAL_ACTIVE, portalActive);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		goalCell = bundle.contains(GOAL_CELL) ? bundle.getInt(GOAL_CELL) : exit();
		puzzleStartPositions = bundle.getIntArray(PUZZLE_STARTS);
		if (puzzleStartPositions == null) puzzleStartPositions = new int[0];
		puzzleSwitch = bundle.contains(PUZZLE_SWITCH) ? bundle.getInt(PUZZLE_SWITCH) : -1;
		portalFrom = bundle.contains(PORTAL_FROM) ? bundle.getInt(PORTAL_FROM) : -1;
		portalTo = bundle.contains(PORTAL_TO) ? bundle.getInt(PORTAL_TO) : -1;
		portalActive = bundle.getBoolean(PORTAL_ACTIVE);
	}
}
