/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.rituals.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.consum.food.Berry;
import pd.items.consum.materials.FreshGrass;
import pd.items.consum.materials.GrassSeed;
import pd.levels.Terrain;
import pd.messages.InlineText;
import pd.scenes.GameScene;
import pd.rituals.Ritual;
import pd.rituals.RitualMaterial;
import pd.rituals.Rituals;
import render.utils.math.Random;

/**
 * SPSEXPD: 丰收奠基阵——浆果与鲜草把土地催活：5×5 内长出高草并散落草种（产物类）。
 */
public class HarvestBasisRitual extends Ritual {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(HarvestBasisRitual.class)
			.t("name", "丰收奠基阵")
			.t("desc", "把浆果的甜与青草的青还还给大地：激发后阵中将长出数株高草，并散落一撮草种。\n\n材料：浆果×2 + 鲜草×2。");
	}

	@Override
	public String id() { return "harvest_basis"; }


	@Override
	public List<RitualMaterial> materials() {
		return Arrays.asList(
				new RitualMaterial(Berry.class, 2),
				new RitualMaterial(FreshGrass.class, 2));
	}

	@Override
	public void act( Hero caster, int center ) {
		//SPSEXPD: 产物类——5×5 内最多 4 格普通地面长出高草，阵心再散 3 枚草种
		if (Dungeon.level == null) return;

		ArrayList<Integer> growable = new ArrayList<>();
		for (int off : Rituals.radiusOffsets( Dungeon.level )) {
			int cell = center + off;
			if (!Dungeon.level.insideMap(cell)) continue;
			int terrain = Dungeon.level.map[cell];
			if (terrain == Terrain.EMPTY || terrain == Terrain.EMPTY_DECO || terrain == Terrain.GRASS || terrain == Terrain.EMBERS) {
				growable.add( cell );
			}
		}
		int planted = 0;
		while (!growable.isEmpty() && planted < 4) {
			int idx = Random.Int( growable.size() );
			int cell = growable.remove( idx );
			pd.levels.Level.set( cell, Terrain.HIGH_GRASS, Dungeon.level );
			GameScene.updateMap( cell );
			planted++;
		}

		Item seeds = new GrassSeed();
		seeds.quantity(3);
		Dungeon.level.drop( seeds, center ).sprite.drop();
	}
}
