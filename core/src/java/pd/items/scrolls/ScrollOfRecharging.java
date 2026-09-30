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
import pd.actors.Char;
import pd.actors.buffs.Arcane;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.Shocked;
import pd.actors.mobs.Mob;
import pd.effects.SpellSprite;
import pd.effects.particles.EnergyParticle;
import pd.messages.Messages;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.noosa.particles.Emitter;

public class ScrollOfRecharging extends Scroll {

	{
		icon = ItemSpriteSheet.Icons.SCROLL_RECHARGE;
	}

	@Override
	public void doRead() {

		detach(curUser.belongings.backpack);
		Buff.affect(curUser, Recharging.class, Recharging.DURATION);
		Buff.affect(curUser, Arcane.class, 3f);
		charge(curUser);

		Sample.INSTANCE.play( Assets.Sounds.READ );
		Sample.INSTANCE.play( Assets.Sounds.CHARGEUP );

		GLog.i( Messages.get(this, "surge") );
		SpellSprite.show( curUser, SpellSprite.CHARGE );
		identify();
		for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
			if (Dungeon.level.heroFOV[mob.pos]) Buff.affect(mob, Shocked.class).level(6);
		}

		readAnimation();
	}
	
	public static void charge( Char user ) {
		if (user.sprite != null) {
			Emitter e = user.sprite.centerEmitter();
			if (e != null) e.burst(EnergyParticle.FACTORY, 15);
		}
	}

	@Override
	public void empoweredRead() {
		doRead();
		Buff.append(curUser, Recharging.class, Recharging.DURATION / 3f);
	}
	
	@Override
	public int value() {
		return isKnown() ? 30 * quantity : super.value();
	}
}
