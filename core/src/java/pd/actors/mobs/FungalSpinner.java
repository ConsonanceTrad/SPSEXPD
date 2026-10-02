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

package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Regrowth;
import pd.levels.Terrain;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.sprites.FungalSpinnerSprite;
import pd.messages.InlineText;

public class FungalSpinner extends Spinner {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(FungalSpinner.class)
			.t("name", "菌生蜘蛛")
			.t("desc", "这只矿洞蜘蛛似乎被一种寄生真菌控制。真菌改变了宿主的能力，以真菌生长替换了宿主的蛛网和猛毒。\n\n菌生蜘蛛会喷吐孢子，而非蛛网。孢子不仅会传播真菌，还可能将你缠绕在原地。这种蜘蛛虽然无毒，但却可以在邻近任何蘑菇的时候获得高额伤害抗性。");
	}


	{
		spriteClass = FungalSpinnerSprite.class;

		HP = HT = 40;
		defenseSkill = 16;

		EXP = 7;
		maxLvl = -2;
	}

	@Override
	protected void applyWebToCell(int cell) {
		GameScene.add(Blob.seed(cell, 40, Regrowth.class));
	}

	@Override
	public void damage(int dmg, Object src) {
		int grassCells = 0;
		for (int i : PathFinder.NEIGHBOURS9) {
			if (Dungeon.level.map[pos+i] == Terrain.FURROWED_GRASS
					|| Dungeon.level.map[pos+i] == Terrain.HIGH_GRASS){
				grassCells++;
			}
		}
		//first adjacent grass cell reduces damage taken by 30%, each one after reduces by another 10%
		if (grassCells > 0) dmg = Math.round(dmg * (8-grassCells)/10f);

		super.damage(dmg, src);
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		return damage; //does not apply poison
	}

	{
		immunities.add(Regrowth.class);
	}

}
