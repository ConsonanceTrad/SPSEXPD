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

package pd.items.consum.stones;

import pd.atlas.items.ConsumScrollAmuletAmuletDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FlavourBuff;
import pd.actors.mobs.Mob;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.ui.BuffIndicator;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class StoneOfAggression extends Runestone {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(StoneOfAggression.class)
			.t("name", "敌意符石")
			.t("desc", "当把这颗符石丢向一个盟友或敌人时，附近所有敌人都会在短时间内优先攻击该单位。\n\n敌意符石无法直接以boss为目标，但可对其随从正常发挥效果。")
			.t("$aggression.name", "众矢之的")
			.t("$aggression.desc", "支配魔法正使附近所有敌人优先攻击该单位。\n\n剩余回合数：%s");
	}



	
	{
		image = ConsumScrollAmuletAmuletDict.STONE_AGGRESSION;
	}
	
	@Override
	protected void activate(int cell) {
		
		Char ch = Actor.findChar( cell );
		
		if (ch != null
				&& !Char.hasProp(ch, Char.Property.BOSS)
				&& !Char.hasProp(ch, Char.Property.MINIBOSS)) {
			Buff.prolong(ch, Aggression.class, Aggression.DURATION);
		}

		CellEmitter.center(cell).start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
		Sample.INSTANCE.play( Assets.Sounds.READ );
		
	}

	public static class Aggression extends FlavourBuff {
		
		public static final float DURATION = 20f;
		
		{
			type = buffType.NEGATIVE;
			announced = true;
		}

		@Override
		public int icon() {
			return BuffIndicator.TARGETED;
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}

		@Override
		public void detach() {
			//if our target is an enemy, reset any enemy-to-enemy aggro involving it
			if (target.isAlive()) {
				if (target.alignment == Char.Alignment.ENEMY) {
					for (Mob m : Dungeon.level.mobs()) {
						if (m.alignment == Char.Alignment.ENEMY && m.isTargeting(target)) {
							m.aggro(null);
						}
						if (target instanceof Mob && ((Mob) target).isTargeting(m)){
							((Mob) target).aggro(null);
						}
					}
				}
			}
			super.detach();
			
		}

	}
	
}
