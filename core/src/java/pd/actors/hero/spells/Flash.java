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

package pd.actors.hero.spells;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.cleric.AscendedForm;
import pd.items.equipment.artifacts.HolyTome;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.messages.Messages;
import pd.ui.HeroIcon;
import pd.utils.GLog;

public class Flash extends TargetedClericSpell {

	public static Flash INSTANCE = new Flash();

	@Override
	public int icon() {
		return HeroIcon.FLASH;
	}

	@Override
	public float chargeUse(Hero hero) {
		if (hero.buff(AscendedForm.AscendBuff.class) != null){
			return 2 + hero.buff(AscendedForm.AscendBuff.class).flashCasts;
		} else {
			return 2;
		}
	}

	@Override
	public boolean canCast(Hero hero) {
		return super.canCast(hero)
				&& hero.hasTalent(Talent.FLASH)
				&& hero.buff(AscendedForm.AscendBuff.class) != null;
	}

	@Override
	public int targetingFlags() {
		return -1; //targets an empty cell, not an enemy
	}

	@Override
	protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {

		if (target == null){
			return;
		}

		if (Dungeon.level.solid[target] || (!Dungeon.level.mapped[target] && !Dungeon.level.visited[target])
				|| Dungeon.level.distance(hero.pos, target) > 2+hero.pointsInTalent(Talent.FLASH)){
			GLog.w(Messages.get(this, "invalid_target"));
			return;
		}

		if (ScrollOfTeleportation.teleportToLocation(hero, target)){
			hero.spendAndNext( 1f );
			onSpellCast(tome, hero);
			hero.buff(AscendedForm.AscendBuff.class).flashCasts++;
		}

	}
}
