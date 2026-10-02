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
import pd.items.Generator;
import pd.sprites.HermitCrabSprite;
import pd.messages.InlineText;

public class HermitCrab extends Crab {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(HermitCrab.class)
			.t("name", "寄居蟹")
			.t("def_verb", "格挡")
			.t("desc", "出于某种原因，这只下水道螃蟹决定把一个破桶戴在头上！额外的负重使其移速降到了一般水平，但也使其获得了更多防御。你感觉你听到了有什么东西在桶中碰撞作响...");
	}


	{
		spriteClass = HermitCrabSprite.class;

		HP = HT = 25; //+67% HP
		baseSpeed = 1f; //-50% speed

		//3x more likely to drop meat, and drops a guaranteed armor
		lootChance = 0.5f;
		properties.add(Property.BEAST);
		properties.add(Property.BOSS);
	}

	@Override
	public void rollToDropLoot() {
		super.rollToDropLoot();

		if (Dungeon.hero.lvl <= maxLvl + 2){
			Dungeon.level.drop(Generator.randomArmor(), pos).sprite.drop();
		}
	}

	@Override
	public int drRoll() {
		return super.drRoll() + 2; //2-6 DR total, up from 0-4
	}

}
