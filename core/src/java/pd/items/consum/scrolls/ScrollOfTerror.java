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

package pd.items.consum.scrolls;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.CountDown;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.ShadowCurse;
import pd.actors.buffs.Terror;
import pd.actors.mobs.Mob;
import pd.effects.Flare;
import pd.messages.Messages;
import pd.sprites.ItemIconSheet;
import pd.utils.GLog;
import render.noosa.audio.Sample;

public class ScrollOfTerror extends Scroll {

	{
		icon = ItemIconSheet.SCROLL_TERROR;
	}

	@Override
	public void doRead() {

		detach(curUser.belongings.backpack);
		if (curUser.sprite != null) new Flare( 5, 32 ).color( 0xFF0000, true ).show( curUser.sprite, 2f );
		Sample.INSTANCE.play( Assets.Sounds.READ );
		
		int count = 0;
		Mob affected = null;
		for (Mob mob : Dungeon.level.mobs().toArray( new Mob[0] )) {
			if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
				Buff.affect( mob, Terror.class, Terror.DURATION ).object = curUser.id();
				Buff.affect(mob, HasteBuff.class, Terror.DURATION * 0.5f);
				Buff.affect(mob, ShadowCurse.class);

				if (mob.buff(Terror.class) != null){
					count++;
					affected = mob;
				}
			}
		}
		
		switch (count) {
		case 0:
			GLog.i( Messages.get(this, "none") );
			break;
		case 1:
			GLog.i( Messages.get(this, "one", affected.name()) );
			break;
		default:
			GLog.i( Messages.get(this, "many") );
		}
		identify();

		readAnimation();
	}

	@Override
	public void empoweredRead() {
		doRead();
		for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
			if (Dungeon.level.heroFOV[mob.pos] && mob.buff(Terror.class) != null) {
				Buff.prolong(mob, Terror.class, Terror.DURATION * 1.5f).object = curUser.id();
				Buff.affect(mob, Paralysis.class, Terror.DURATION * 0.5f);
				Buff.affect(mob, HasteBuff.class, Terror.DURATION * 0.5f);
				Buff.affect(mob, CountDown.class);
			}
		}
	}
	
	@Override
	public int value() {
		return isKnown() ? 40 * quantity : super.value();
	}
}
