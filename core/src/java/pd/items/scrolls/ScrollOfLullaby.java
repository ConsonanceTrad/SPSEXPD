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

package pd.items.scrolls;

import pd.Assets;
import pd.Dungeon;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.AttackDown;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Drowsy;
import pd.actors.mobs.Mob;
import pd.effects.Speck;
import pd.messages.Messages;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import render.noosa.audio.Sample;

public class ScrollOfLullaby extends Scroll {

	{
		icon = ItemSpriteSheet.Icons.SCROLL_LULLABY;
	}

	@Override
	public void doRead() {

		detach(curUser.belongings.backpack);
		if (curUser.sprite != null) curUser.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.3f, 5 );
		Sample.INSTANCE.play( Assets.Sounds.LULLABY );

		for (Mob mob : Dungeon.level.mobs().toArray( new Mob[0] )) {
			if (Dungeon.level.heroFOV[mob.pos]) {
				Buff.affect( mob, Drowsy.class, Drowsy.DURATION );
				Buff.affect(mob, AttackDown.class, 10f).level(50);
				Buff.affect(mob, ArmorBreak.class, 10f).level(50);
				if (mob.sprite != null) mob.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.3f, 5 );
			}
		}

		Buff.affect( curUser, Drowsy.class, Drowsy.DURATION );
		Buff.affect(curUser, AttackDown.class, 10f).level(20);
		Buff.affect(curUser, ArmorBreak.class, 10f).level(20);

		GLog.i( Messages.get(this, "sooth") );

		identify();
		readAnimation();
	}

	@Override
	public void empoweredRead() {
		doRead();
		for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
			if (Dungeon.level.heroFOV[mob.pos]) {
				Buff drowsy = mob.buff(Drowsy.class);
				if (drowsy != null) drowsy.act();
			}
		}
	}
	
	@Override
	public int value() {
		return isKnown() ? 40 * quantity : super.value();
	}
}
