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

package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.Talent;
import pd.items.equipment.armor.Armor;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.noosa.Image;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

public class HoldFast extends Buff {

	{
		type = buffType.POSITIVE;
	}

	public int pos = -1;

	@Override
	public boolean act() {
		if (pos != target.pos) {
			detach();
		} else {
			spend(TICK);
		}
		return true;
	}

	public int armorBonus(){
		if (pos == target.pos && target instanceof Hero){
			int max = 0;
			if (((Hero) target).heroClass == HeroClass.WARRIOR){
				max = 6;
			} else {
				Armor armor = ((Hero) target).belongings.armor();
				if (armor != null) {
					max = armor.tier + armor.buffedLvl();
				}
			}
			int block = Random.NormalIntRange(((Hero) target).pointsInTalent(Talent.HOLD_FAST), 2*((Hero) target).pointsInTalent(Talent.HOLD_FAST));
			return Math.min(block, max);
		} else {
			detach();
			return 0;
		}
	}

	public static float buffDecayFactor(Char target){
		HoldFast buff = target.buff(HoldFast.class);
		if (buff != null && target.pos == buff.pos && target instanceof Hero){
			switch (((Hero) target).pointsInTalent(Talent.HOLD_FAST)){
				case 1:
					return 0.5f;
				case 2:
					return 0.25f;
				case 3:
					return 0;
			}

		} else if (buff != null) {
			buff.detach();
		}
		return 1;
	}

	@Override
	public int icon() {
		return BuffIndicator.ARMOR;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(1.9f, 2.4f, 3.25f);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc",
				Dungeon.hero.pointsInTalent(Talent.HOLD_FAST),
				2*Dungeon.hero.pointsInTalent(Talent.HOLD_FAST),
				25 + 25*Dungeon.hero.pointsInTalent(Talent.HOLD_FAST));
	}

	private static final String POS = "pos";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(POS, pos);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		pos = bundle.getInt(POS);
	}
}
