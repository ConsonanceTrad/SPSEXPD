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

import pd.actors.mobs.npcs.Blacksmith;
import pd.sprites.FungalCoreSprite;
import pd.messages.InlineText;

public class FungalCore extends Mob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(FungalCore.class)
			.t("name", "蘑菇菌核")
			.t("desc", "这株硕大无比的蘑菇想必就是洞穴中异常蘑菇活性的源头了。");
	}




	{
		HP = HT = 300;
		spriteClass = FungalCoreSprite.class;

		EXP = 20;

		state = PASSIVE;

		properties.add(Property.IMMOVABLE);
		properties.add(Property.BOSS);
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	public float spawningWeight() {
		return 0;
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		Blacksmith.Quest.beatBoss();
	}
}
