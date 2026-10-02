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

package pd.windows;

import pd.Dungeon;
import pd.levels.traps.Trap;
import pd.messages.Messages;
import pd.tiles.TerrainFeaturesTilemap;
import pd.messages.InlineText;

public class WndInfoTrap extends WndTitledMessage {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndInfoTrap.class)
			.t("inactive", "这个陷阱已经失效了，且不会被再次触发。");
	}




	public WndInfoTrap(Trap trap) {

		super(TerrainFeaturesTilemap.tile( trap.pos, Dungeon.level.map[trap.pos]),
				Messages.titleCase(trap.name()),
				trap.desc());

	}

}
