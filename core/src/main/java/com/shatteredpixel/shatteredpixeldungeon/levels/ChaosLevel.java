/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.BrokenRobot;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DemonFlower;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DemonGoo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Assassin;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.BambooMob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.BrownBat;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.AdventureJournal;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.SpsBspLayout;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.HallsPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DistortionTrap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.custom.SpsLegacyLevelVisual;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

/** The depth-85 chaos floor, using its smaller-room SPS BSP parameters. */
public class ChaosLevel extends SpsRegularLevel {

	private static final int LAYOUT_ATTEMPTS = 64;
	@SuppressWarnings("unchecked")
	private static final Class<? extends Mob>[] LEGACY_MOB_POOL = new Class[]{
			Rat.class, BrownBat.class, DustElement.class, LiveMoss.class,
			Swarm.class, Crab.class, PatrolUAV.class,
			Thief.class, Gnoll.class, BambooMob.class,
			Guard.class, Assassin.class, TrollWarrior.class, Zombie.class,
			Bat.class, Skeleton.class, Brute.class, TimeKeeper.class,
			GnollShaman.class, Spinner.class, BrokenRobot.class, SandMob.class, IceBug.class,
			FireElemental.class, Warlock.class, Monk.class, DragonRider.class,
			Golem.class, SpiderBot.class, Musketeer.class, DwarfLich.class,
			Succubus.class, Eye.class, DemonGoo.class,
			Scorpio.class, ThiefImp.class, DemonFlower.class, Sufferer.class,
			BlueWraith.class, Orc.class, FlyingProtector.class, GoldOrc.class, Fiend.class
	};

	{
		color1 = 0x801500;
		color2 = 0xa68521;
		viewDistance = 6;
	}

	@Override
	protected SpsBspLayout.Result generateLegacyLayout() {
		return SpsBspLayout.generate(LEGACY_WIDTH, LEGACY_HEIGHT, LAYOUT_ATTEMPTS, 6, 10);
	}

	@Override
	protected void afterLegacyRoomsPainted() {
		transitions.clear();
		transitions.add(new LevelTransition(this, entrance,
				LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
		transitions.add(new LevelTransition(this, exit,
				LevelTransition.Type.BRANCH_EXIT,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
	}

	@Override
	protected void decorateLegacyFloor() {
		for (int cell = width() + 1; cell < length() - width() - 1; cell++) {
			if (map[cell] == Terrain.EMPTY) {
				int neighbours = 0;
				for (int dy = -1; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) {
					if ((dx != 0 || dy != 0)
							&& (Terrain.flags[map[cell + dx + dy * width()]] & Terrain.PASSABLE) != 0) neighbours++;
				}
				if (Random.Int(80) < neighbours) map[cell] = Terrain.EMPTY_DECO;
			} else if (map[cell] == Terrain.WALL && map[cell - 1] != Terrain.WALL_DECO
					&& map[cell - width()] != Terrain.WALL_DECO && Random.Int(20) == 0) {
				map[cell] = Terrain.WALL_DECO;
			}
		}

		ArrayList<Integer> signCells = new ArrayList<>();
		SpsBspLayout.Room room = legacyLayout.entrance;
		for (int y = room.top + 1; y < room.bottom; y++) {
			for (int x = room.left + 1; x < room.right; x++) {
				int cell = x + y * width();
				if (cell != entrance) signCells.add(cell);
			}
		}
		if (!signCells.isEmpty()) map[Random.element(signCells)] = Terrain.SIGN;
		for (int cell = 0; cell < length(); cell++) {
			if (cell == entrance || cell == exit) map[cell] = Terrain.PEDESTAL;
			else if (map[cell] == Terrain.CHASM) map[cell] = Terrain.EMPTY;
		}
		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(
				Assets.Environment.SPS_TILES_MAGIC_CAVE, width(), height(), map));
	}

	@Override protected float legacyWaterFill() { return feeling == Feeling.WATER ? 0.55f : 0.40f; }
	@Override protected int legacyWaterClustering() { return 6; }
	@Override protected float legacyGrassFill() { return feeling == Feeling.GRASS ? 0.55f : 0.30f; }
	@Override protected int legacyGrassClustering() { return 3; }
	@Override protected float legacyChasmFill() { return 0f; }
	@Override protected int legacyChasmClustering() { return 3; }
	@Override protected Class<?>[] trapClasses() { return new Class[]{DistortionTrap.class}; }
	@Override protected float[] trapChances() { return new float[]{10}; }
	@Override protected Painter painter() { return new HallsPainter(); }

	static Class<? extends Mob>[] legacyMobPool() { return LEGACY_MOB_POOL.clone(); }

	@Override public int mobLimit() { return 16 + Random.Int(3); }

	@Override
	public Mob createMob() {
		return Reflection.newInstance(Random.element(LEGACY_MOB_POOL));
	}

	@Override
	protected void createMobs() {
		int target = mobLimit();
		int spawned = 0;
		for (int attempts = 0; spawned < target && attempts < target * 64; attempts++) {
			Mob mob = createMob();
			if (mob == null) continue;
			int cell = randomRespawnCell(mob);
			if (cell == -1 || findMob(cell) != null) continue;
			mob.pos = cell;
			mobs.add(mob);
			spawned++;
		}
	}

	@Override
	public boolean activateTransition(Hero hero, LevelTransition transition) {
		if (transition != null && transition.cell() == exit
				&& AdventureJournal.destinationForBranch(Dungeon.branch) == 22) {
			AdventureJournal.complete(22);
		}
		return super.activateTransition(hero, transition);
	}

	@Override public String tilesTex() { return Assets.Environment.TILES_HALLS; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_CITY; }
}
