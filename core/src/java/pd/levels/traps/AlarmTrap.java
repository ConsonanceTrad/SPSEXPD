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

import pd.Assets;
import pd.Dungeon;
import pd.actors.mobs.Mob;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.messages.Messages;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class AlarmTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(AlarmTrap.class)
			.t("name", "警报陷阱")
			.t("alarm", "陷阱产生的尖锐的警报声在地牢里回荡！")
			.t("desc", "这个陷阱看起来有着能造成很大响动的机制。触发它将使本层所有生物对这里产生警觉。");
	}


	{
		color = RED;
		shape = DOTS;
	}

	@Override
	public void activate() {

		for (Mob mob : Dungeon.level.mobs()) {
				mob.beckon( pos );
		}

		if (Dungeon.level.heroFOV[pos]) {
			GLog.w( Messages.get(this, "alarm") );
			CellEmitter.center( pos ).start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
		}

		Sample.INSTANCE.play( Assets.Sounds.ALERT );
	}
}
