/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Water;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dewcharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GrowSeed;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ScrollOfRegrowth extends Scroll {
	{
		icon = ItemSpriteSheet.Icons.SCROLL_RECHARGE;
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
			Dungeon.level.plant((Plant.Seed) Generator.random(Generator.Category.SEED), cell);
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
			Dungeon.level.plant(guaranteed, cell);
		}
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
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
