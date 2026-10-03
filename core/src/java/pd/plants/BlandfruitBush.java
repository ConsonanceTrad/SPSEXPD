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

package pd.plants;

import pd.atlas.items.ConsumPotionSeedSeedDict;
import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.consum.food.Blandfruit;
import pd.messages.InlineText;

public class BlandfruitBush extends Plant {
	//SPSEXPD: inline Chinese text (generated from messages/plants/zh)
	static {
		InlineText.of(BlandfruitBush.class)
			.t("name", "无味果")
			.t("desc", "腐莓的远亲，来自无味果树丛的梨状产物，尝起来犹如一团泥巴。果实粗糙且松软，但并没有毒性。也许可以煮食。")
			.t("discover_hint", "你可在地牢各处找到该植物。")
			.t("$seed.name", "无味果之种")
			.t("$exblandfruitbush.name", "无味果果丛")
			.t("$exblandfruitbush.desc", "生长无味果的果丛。");
	}




	{
		image = 8;
		seedClass = Seed.class;
	}

	@Override
	public void activate( Char ch ) {
		Dungeon.level.drop( new Blandfruit(), pos ).sprite.drop();
	}

	//seed is never dropped
	public static class Seed extends Plant.Seed {
		{
			image = ConsumPotionSeedSeedDict.SEED_FADELEAF_0;
			plantClass = BlandfruitBush.class;
			explantClass = ExBlandfruitBush.class;
		}

	}

	public static class ExBlandfruitBush extends SpsFruitBush {
		{ image = 8; harvestCount = 2; harvestClass = Blandfruit.class; }
	}
}
