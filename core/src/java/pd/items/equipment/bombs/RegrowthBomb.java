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

package pd.items.equipment.bombs;

import pd.atlas.items.EquipmentEquipWeaponBombDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Regrowth;
import pd.effects.Splash;
import pd.items.Generator;
import pd.items.consum.potions.PotionOfHealing;
import pd.items.equipment.wands.WandOfRegrowth;
import pd.levels.GroundItems;
import pd.levels.Terrain;
import pd.mechanics.pathfind.PathFinder;
import pd.plants.Plant;
import pd.plants.Starflower;
import pd.scenes.GameScene;
import render.utils.data.BArray;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

public class RegrowthBomb extends Bomb {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RegrowthBomb.class)
			.t("name", "再生炸弹")
			.t("desc", "这枚改造过的炸弹不会爆炸，而会在3格范围内四处溅射再生药液。被药液影响的区域会迅速生长出草木与植物，而你自己和爆炸范围内的所有盟友都会受到如同饮用治疗药剂般的治疗效果。")
			.t("discover_hint", "你可通过炼金合成该物品。");
	}

	
	{
		image = EquipmentEquipWeaponBombDict.REGROWTH_BOMB_0;
	}
	
	@Override
	public boolean explodesDestructively() {
		return false;
	}

	@Override
	protected int explosionRange() {
		return 3;
	}

	@Override
	public void explode(int cell) {
		super.explode(cell);
		
		if (Dungeon.level.heroFOV[cell]) {
			Splash.at(cell, 0x00FF00, 30);
		}
		
		ArrayList<Integer> plantCandidates = new ArrayList<>();
		
		PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), explosionRange() );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				Char ch = Actor.findChar(i);
				int t = Dungeon.level.map[i];
				if (ch != null){
					if (ch.alignment == Dungeon.hero.alignment) {
						//same as a healing potion
						PotionOfHealing.cure(ch);
						PotionOfHealing.heal(ch);
					}
				} else if ((t == Terrain.EMPTY || t == Terrain.EMPTY_DECO || t == Terrain.EMBERS
						|| t == Terrain.GRASS || t == Terrain.FURROWED_GRASS || t == Terrain.HIGH_GRASS)
						&& Dungeon.level.plants.get(i) == null){
					plantCandidates.add(i);
				}
				GameScene.add( Blob.seed( i, 10, Regrowth.class ) );
			}
		}

		int plants = Random.chances(new float[]{0, 0, 2, 1});

		for (int i = 0; i < plants; i++) {
			Integer plantPos = Random.element(plantCandidates);
			if (plantPos != null) {
				GroundItems.plant( Dungeon.level, (Plant.Seed) Generator.randomUsingDefaults(Generator.Category.SEED), plantPos);
				plantCandidates.remove(plantPos);
			}
		}
		
		Integer plantPos = Random.element(plantCandidates);
		if (plantPos != null){
			Plant.Seed plant;
			switch (Random.chances(new float[]{0, 6, 3, 1})){
				case 1: default:
					plant = new WandOfRegrowth.Dewcatcher.Seed();
					break;
				case 2:
					plant = new WandOfRegrowth.Seedpod.Seed();
					break;
				case 3:
					plant = new Starflower.Seed();
					break;
			}
			GroundItems.plant( Dungeon.level,  plant, plantPos);
		}
	}
	
	@Override
	public int value() {
		//prices of ingredients
		return quantity * (20 + 30);
	}
}
