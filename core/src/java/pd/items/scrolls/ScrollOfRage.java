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
import pd.actors.buffs.Amok;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.Silent;
import pd.actors.mobs.Mimic;
import pd.actors.mobs.Mob;
import pd.effects.Speck;
import pd.items.Heap;
import pd.messages.Messages;
import pd.sprites.ItemIconSheet;
import pd.utils.GLog;
import render.noosa.audio.Sample;

public class ScrollOfRage extends Scroll {

	{
		icon = ItemIconSheet.SCROLL_RAGE;
	}

	@Override
	public void doRead() {

		detach(curUser.belongings.backpack);
		for (Mob mob : Dungeon.level.mobs().toArray( new Mob[0] )) {
			mob.beckon( curUser.pos );
			if (Dungeon.level.heroFOV[mob.pos]) {
				Buff.prolong(mob, Amok.class, 5f);
				Buff.affect(mob, Silent.class, 20f);
			}
		}
		for (Heap heap : Dungeon.level.heaps.valueList().toArray(new Heap[0])) {
			if (heap.type == Heap.Type.MIMIC) {
				Mimic mimic = Mimic.spawnAt(heap.pos, heap.items);
				if (mimic != null) {
					mimic.beckon(curUser.pos);
					heap.destroy();
				}
			}
		}

		GLog.w( Messages.get(this, "roar") );
		identify();
		
		if (curUser.sprite != null) curUser.sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
		Sample.INSTANCE.play( Assets.Sounds.CHALLENGE );

		readAnimation();
	}

	@Override
	public void empoweredRead() {
		for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
			if (Dungeon.level.heroFOV[mob.pos]) {
				Buff.prolong(mob, Amok.class, 10f);
				Buff.affect(mob, Silent.class, 40f);
			}
		}
		setKnown();
		if (curUser.sprite != null) curUser.sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.3f, 3);
		Sample.INSTANCE.play(Assets.Sounds.READ);
		Invisibility.dispel();
		curUser.spendAndNext(TIME_TO_READ);
	}
	
	@Override
	public int value() {
		return isKnown() ? 40 * quantity : super.value();
	}
}
