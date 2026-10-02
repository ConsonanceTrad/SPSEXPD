/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.scrolls;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Water;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Dewcharge;
import pd.actors.buffs.GrowSeed;
import pd.actors.buffs.Invisibility;
import pd.actors.mobs.Mob;
import pd.items.Generator;
import pd.levels.GroundItems;
import pd.levels.Terrain;
import pd.mechanics.pathfind.PathFinder;
import pd.plants.Earthroot;
import pd.plants.Plant;
import pd.plants.Starflower;
import pd.plants.Sungrass;
import pd.scenes.GameScene;
import pd.sprites.ItemIconSheet;
import render.noosa.audio.Sample;
import render.utils.data.BArray;
import render.utils.math.Random;

import java.util.ArrayList;

public class ScrollOfRegrowth extends Scroll {
	{
		icon = ItemIconSheet.SCROLL_RECHARGE;
	}

	@Override
	public void doRead() {
		ArrayList<Integer> candidates = new ArrayList<>();
		PathFinder.buildDistanceMap(Dungeon.hero.pos, BArray.not(Dungeon.level.solid, null), 2);
		for (int cell = 0; cell < PathFinder.distance.length; cell++) {
			if (PathFinder.distance[cell] < Integer.MAX_VALUE) {
				int terrain = Dungeon.level.map[cell];
				if (terrain == Terrain.EMPTY || terrain == Terrain.EMBERS || terrain == Terrain.EMPTY_DECO
						|| terrain == Terrain.GRASS || terrain == Terrain.FURROWED_GRASS
						|| terrain == Terrain.HIGH_GRASS) {
					candidates.add(cell);
				}
				GameScene.add(Blob.seed(cell, 10, Water.class));
			}
		}
		int plants = Random.chances(new float[]{0, 6, 3, 1});
		for (int i = 0; i < plants && !candidates.isEmpty(); i++) {
			int cell = Random.element(candidates);
			GroundItems.plant( Dungeon.level, (Plant.Seed) Generator.random(Generator.Category.SEED), cell);
			candidates.remove((Integer) cell);
		}
		if (!candidates.isEmpty()) {
			int cell = Random.element(candidates);
			Plant.Seed guaranteed;
			switch (Random.chances(new float[]{0, 3, 2, 1})) {
				case 2: guaranteed = new Earthroot.Seed(); break;
				case 3: guaranteed = new Starflower.Seed(); break;
				default: guaranteed = new Sungrass.Seed();
			}
			GroundItems.plant( Dungeon.level, guaranteed, cell);
		}
		for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
			if (Dungeon.level.heroFOV[mob.pos]) Buff.affect(mob, GrowSeed.class).set(6f);
		}
		GameScene.updateMap();
		Sample.INSTANCE.play(Assets.Sounds.READ);
		Invisibility.dispel();
		setKnown();
		readAnimation();
	}

	@Override
	public void empoweredRead() {
		doRead();
		Buff.affect(curUser, Dewcharge.class, 50f);
	}

	@Override
	public int value() {
		return isKnown() ? 20 * quantity : super.value();
	}
}
