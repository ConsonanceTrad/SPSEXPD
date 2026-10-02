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

package pd.items.consum.spells;

import pd.atlas.items.ConsumScrollAmuletCrystalDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.utils.GLog;
import pd.messages.InlineText;

public class PhaseShift extends TargetedSpell {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PhaseShift.class)
			.t("name", "转移结晶")
			.t("no_target", "那里没什么可传送的东西。")
			.t("desc", "这个充满混沌能量的结晶会将目标单位传送到本层随机位置。被传送的角色会被麻痹相当长的一段时间，而足够强大的敌人则可抵抗该效果。这个结晶可以对目标单位或施法者自身使用。");
	}



	
	{
		image = ConsumScrollAmuletCrystalDict.PHASE_SHIFT_0;

		usesTargeting = true;

		talentChance = 1/(float)Recipe.OUT_QUANTITY;
	}
	
	@Override
	protected void affectTarget(Ballistica bolt, Hero hero) {
		final Char ch = Actor.findChar(bolt.collisionPos);
		
		if (ch != null) {
			if (ScrollOfTeleportation.teleportChar(ch)){

				if (ch instanceof Mob) {
					if (((Mob) ch).state == ((Mob) ch).HUNTING) ((Mob) ch).state = ((Mob) ch).WANDERING;
					((Mob) ch).beckon(Dungeon.level.randomDestination( ch ));
				}
				if (!Char.hasProp(ch, Char.Property.BOSS) && !Char.hasProp(ch, Char.Property.MINIBOSS)) {
					Buff.affect(ch, Paralysis.class, Paralysis.DURATION);
				}
				
			}
		} else {
			GLog.w( Messages.get(this, "no_target") );
		}
		onSpellused();
	}
	
	@Override
	public int value() {
		return (int)(60 * (quantity/(float)Recipe.OUT_QUANTITY));
	}

	@Override
	public int energyVal() {
		return (int)(12 * (quantity/(float)Recipe.OUT_QUANTITY));
	}
	
	public static class Recipe extends pd.items.Recipe.SimpleRecipe {

		private static final int OUT_QUANTITY = 6;
		
		{
			inputs =  new Class[]{ScrollOfTeleportation.class};
			inQuantity = new int[]{1};
			
			cost = 10;
			
			output = PhaseShift.class;
			outQuantity = OUT_QUANTITY;
		}
		
	}
	
}
