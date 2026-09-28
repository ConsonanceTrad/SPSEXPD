/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TenguDen;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.TownNpc;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.DungeonBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Firebomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.FlashBangBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.FrostBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.HolyBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.RegrowthBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.ShrapnelBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.SmokeBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.WoollyBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.Egg;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.AdventureJournal;
import com.shatteredpixel.shatteredpixeldungeon.items.summon.ActiveMrDestructo;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.SpsBspLayout;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.SpsBspLayout.Door;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.SpsBspLayout.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.SpsBspLayout.Type;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.CavesPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.plants.ReNepenth;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** The legacy four-leg BSP loop and concealed treasure room used by TenguDen. */
public class TenguDenLevel extends SpsRegularLevel {

	private static final int LAYOUT_ATTEMPTS = 64;
	private int entranceCell;
	private int sealedEntrance;
	private int bossCell;

	{
		color1 = 0x48763c;
		color2 = 0x59994a;
	}

	@Override
	protected SpsBspLayout.Result generateLegacyLayout() {
		return SpsBspLayout.generateThiefCatch(LEGACY_WIDTH, LEGACY_HEIGHT, LAYOUT_ATTEMPTS);
	}

	@Override
	protected void assignLegacyRoomTypes() {
		legacyLayout.entrance.type = Type.ENTRANCE;
		legacyLayout.bossRoom.type = Type.TENGU_BOX;
	}

	@Override
	protected void afterLegacyRoomsPainted() {
		entranceCell = entrance;
		exit = legacyLayout.entrance.top * width()
				+ (legacyLayout.entrance.left + legacyLayout.entrance.right) / 2;
		map[exit] = Terrain.WALL;
		map[entranceCell] = Terrain.PEDESTAL;
		transitions.clear();
		transitions.add(new LevelTransition(this, entranceCell,
				LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
		paintTreasureRoom(legacyLayout.bossRoom);

	}

	private void paintTreasureRoom(Room room) {
		Painter.fill(this, room.left, room.top, room.width() + 1, room.height() + 1, Terrain.WALL);
		Painter.fill(this, room.left + 1, room.top + 1,
				room.width() - 1, room.height() - 1, Terrain.EMPTY_SP);
		Door entrance = room.connected.values().iterator().next();
		entrance.set(Door.Type.HIDDEN);
		int door = entrance.x + entrance.y * width();
		map[door] = Terrain.SECRET_DOOR;

		for (int x = room.left + 1; x < room.right; x++) {
			addChest(x + (room.top + 1) * width(), door);
			addChest(x + (room.bottom - 1) * width(), door);
		}
		for (int y = room.top + 2; y < room.bottom - 1; y++) {
			addChest(room.left + 1 + y * width(), door);
			addChest(room.right - 1 + y * width(), door);
		}

		TownNpc hunter = TownNpc.create(TownNpc.Spec.STORM_AND_RAIN);
		hunter.pos = room.randomCell(width(), 0);
		mobs.add(hunter);
	}

	private void addChest(int cell, int door) {
		if (cell == door - 1 || cell == door + 1
				|| cell == door - width() || cell == door + width()) return;
		Item prize;
		switch (Random.Int(8)) {
			case 0: prize = new Egg(); break;
			case 1: prize = new ReNepenth.Seed(); break;
			case 2: prize = com.shatteredpixel.shatteredpixeldungeon.items.Generator.random(
					com.shatteredpixel.shatteredpixeldungeon.items.Generator.Category.BERRY); break;
			case 3: prize = new Starflower.Seed(); break;
			case 5: prize = new ActiveMrDestructo(); break;
			case 6: prize = randomBomb(); break;
			default: prize = new Gold(Random.IntRange(1, 5)); break;
		}
		drop(prize, cell).type = Heap.Type.CHEST;
	}

	private Item randomBomb() {
		switch (Random.Int(11)) {
			case 0:
			case 1:
			case 2: return new DungeonBomb();
			case 3: return new Firebomb();
			case 4: return new FrostBomb();
			case 5: return new FlashBangBomb();
			case 6: return new RegrowthBomb();
			case 7: return new HolyBomb();
			case 8: return new ShrapnelBomb();
			case 9: return new SmokeBomb();
			default: return new WoollyBomb();
		}
	}

	@Override
	protected void decorateLegacyFloor() {
		Room room = legacyLayout.entrance;
		for (int cell = room.top * width() + room.left + 1;
				cell < room.top * width() + room.right; cell++) {
			if (cell != exit) {
				map[cell] = Terrain.WALL_DECO;
				map[cell + width()] = Terrain.WATER;
			} else {
				map[cell + width()] = Terrain.EMPTY;
			}
		}
		map[entranceCell] = Terrain.PEDESTAL;
		bossCell = chooseBossCell();
	}

	private int chooseBossCell() {
		ArrayList<Integer> candidates = new ArrayList<>();
		for (Room room : legacyLayout.rooms) {
			if (room.type != Type.STANDARD) continue;
			for (int y = room.top + 1; y < room.bottom; y++) {
				for (int x = room.left + 1; x < room.right; x++) {
					int cell = x + y * width();
					if ((Terrain.flags[map[cell]] & Terrain.PASSABLE) != 0
							&& heaps.get(cell) == null && traps.get(cell) == null) candidates.add(cell);
				}
			}
		}
		return candidates.isEmpty() ? entranceCell : Random.element(candidates);
	}

	@Override protected float legacyWaterFill() { return 0.50f; }
	@Override protected int legacyWaterClustering() { return 5; }
	@Override protected float legacyGrassFill() { return 0.40f; }
	@Override protected int legacyGrassClustering() { return 4; }
	@Override protected float legacyChasmFill() { return 0f; }
	@Override protected int legacyChasmClustering() { return 3; }
	@Override protected int nTraps() { return 0; }
	@Override protected Painter painter() { return new CavesPainter(); }

	@Override
	protected void createMobs() {
		if (completed()) return;
		TenguDen boss = new TenguDen();
		boss.pos = bossCell;
		mobs.add(boss);
	}

	@Override protected void createItems() { }
	@Override public Mob createMob() { return null; }
	@Override public Actor addRespawner() { return null; }
	@Override public int randomRespawnCell(Char ch) { return -1; }

	private boolean completed() {
		if (Dungeon.tenguDenKilled) return true;
		if (Dungeon.hero == null) return false;
		AdventureJournal journal = Dungeon.hero.belongings.getItem(AdventureJournal.class);
		return journal != null && journal.isCompleted(10);
	}

	@Override
	public void seal() {
		if (sealedEntrance == 0) {
			sealedEntrance = entranceCell;
			set(entranceCell, Terrain.WATER);
		}
	}

	@Override
	public void unseal() {
		super.unseal();
		if (sealedEntrance != 0) {
			set(sealedEntrance, Terrain.PEDESTAL);
			sealedEntrance = 0;
		}
	}

	int bossCellForTesting() { return bossCell; }

	private static final String ENTRANCE_CELL = "entrance_cell";
	private static final String SEALED_ENTRANCE = "sealed_entrance";
	private static final String BOSS_CELL = "boss_cell";
	@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put(ENTRANCE_CELL, entranceCell); b.put(SEALED_ENTRANCE, sealedEntrance); b.put(BOSS_CELL, bossCell); }
	@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); entranceCell = b.getInt(ENTRANCE_CELL); sealedEntrance = b.getInt(SEALED_ENTRANCE); bossCell = b.getInt(BOSS_CELL); }

	@Override public String tilesTex() { return Assets.Environment.SPS_TILES_CAVES_LEGACY; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_CAVES; }
}
