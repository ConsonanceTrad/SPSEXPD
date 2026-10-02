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

package pd.items.consum.stones;

import pd.atlas.items.ConsumScrollAmuletAmuletDict;

import pd.items.equipment.bombs.Bomb;
import pd.messages.InlineText;

public class StoneOfBlast extends Runestone {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(StoneOfBlast.class)
			.t("name", "震爆符石")
			.t("desc", "这颗符石被扔出后会在目的地立即爆炸。和炸弹一样，爆炸会对范围内的所有东西造成伤害。");
	}

	
	{
		image = ConsumScrollAmuletAmuletDict.STONE_BLAST_0;
	}
	
	@Override
	protected void activate(int cell) {
		new Bomb.ConjuredBomb().explode(cell);
	}
	
}
