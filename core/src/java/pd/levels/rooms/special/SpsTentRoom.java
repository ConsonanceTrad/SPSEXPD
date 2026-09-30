/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 */

package pd.levels.rooms.special;

import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.ExProtect;
import pd.actors.mobs.Mob;
import pd.actors.mobs.SpsExitMobs;
import pd.actors.mobs.npcs.GiftNpc;
import pd.items.Generator;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.painters.Painter;
import pd.plants.Plant;
import pd.tiles.custom.SpsFeatureVisual;
import render.utils.geom.Point;
import render.utils.math.Random;

import java.util.ArrayList;

/** The rest-and-crafting annex attached to each SPS transition floor. */
public class SpsTentRoom extends SpecialRoom {

	@Override
	public int minWidth() {
		return 8;
	}

	@Override
	public int minHeight() {
		return 8;
	}

	@Override
	public void paint(Level level) {
		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.EMBERS);
		Painter.fill(level, this, 2, Terrain.EMPTY);

		Point plant = new Point(right - 2, top + 2);
		Point pot = new Point(right - 2, bottom - 2);
		Point tent = new Point(left + 2, bottom - 2);
		Point anvil = new Point(left + 2, top + 2);
		Painter.set(level, plant, Terrain.FLOWER_POT);
		Painter.set(level, pot, Terrain.ALCHEMY);
		Painter.set(level, tent, Terrain.TENT);
		Painter.set(level, anvil, Terrain.IRON_MAKER);
		Painter.set(level, center(), Terrain.STATUE_SP);
		SpsFeatureVisual tentVisual = new SpsFeatureVisual(SpsFeatureVisual.TENT);
		tentVisual.pos(level.pointToCell(tent), level);
		level.customTiles.add(tentVisual);
		SpsFeatureVisual anvilVisual = new SpsFeatureVisual(SpsFeatureVisual.IRON_MAKER);
		anvilVisual.pos(level.pointToCell(anvil), level);
		level.customTiles.add(anvilVisual);

		Plant.Seed seed = (Plant.Seed)Generator.random(Generator.Category.SEED);
		level.explant(seed, level.pointToCell(plant));
		populate(level);
		entrance().set(Door.Type.REGULAR);
	}

	private void populate(Level level) {
		ArrayList<Integer> embers = new ArrayList<>();
		for (int y = top + 1; y < bottom; y++) {
			for (int x = left + 1; x < right; x++) {
				int cell = level.pointToCell(new Point(x, y));
				if (level.map[cell] == Terrain.EMBERS) embers.add(cell);
			}
		}
		if (embers.isEmpty()) return;

		if (Dungeon.shopOnLevel()) {
			GiftNpc resident = GiftNpc.randomResident();
			resident.pos = embers.remove(Random.Int(embers.size()));
			level.mobs().add(resident);
		} else {
			for (int i = 0; i < 2 && !embers.isEmpty(); i++) {
				Mob guard = SpsExitMobs.randomTentGuard();
				guard.pos = embers.remove(Random.Int(embers.size()));
				Buff.affect(guard, ExProtect.class);
				level.mobs().add(guard);
			}
		}
	}
}
