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

package pd.levels.traps;

import pd.items.equipment.bombs.DungeonBomb;
import pd.messages.InlineText;

public class ExplosiveTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(ExplosiveTrap.class)
			.t("name", "爆炸陷阱")
			.t("desc", "这个陷阱包含一些粉状炸药和一个触发机制。激活它会导致一定范围的爆炸。");
	}




	{
		color = ORANGE;
		shape = DIAMOND;
	}

	@Override
	public void activate() {
		new DungeonBomb().explode(pos);
	}

}
