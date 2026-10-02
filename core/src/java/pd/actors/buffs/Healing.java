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

import pd.actors.hero.Hero;
import pd.effects.FloatingText;
import pd.items.equipment.trinkets.VialOfBlood;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.ui.BuffIndicator;
import render.utils.math.GameMath;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class Healing extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Healing.class)
			.t("name", "治疗")
			.t("desc", "一股治愈魔力让你的伤口开始愈合。\n\n你的生命将逐回合稳定回复，直到治疗效果终止。治疗量将随着时间逐渐减少。\n\n下一回合治疗量：%d\n\n剩余治疗量：%d。");
	}




	private int healingLeft;
	
	private float percentHealPerTick;
	private int flatHealPerTick;

	private int bloodVialLimitingLevel = -1;
	
	{
		//unlike other buffs, this one acts after the hero and takes priority against other effects
		//healing is much more useful if you get some of it off before taking damage
		actPriority = HERO_PRIO - 1;
		
		type = buffType.POSITIVE;
	}
	
	@Override
	public boolean act(){

		if (target.HP < target.HT) {
			target.HP = Math.min(target.HT, target.HP + healingThisTick());

			if (target.HP == target.HT && target instanceof Hero) {
				((Hero) target).resting = false;
			}
		}

		target.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(healingThisTick()), FloatingText.HEALING);
		healingLeft -= healingThisTick();
		
		if (healingLeft <= 0){
			if (target instanceof Hero) {
				((Hero) target).resting = false;
			}
			detach();
		}
		
		spend( TICK );
		
		return true;
	}
	
	private int healingThisTick(){
		int heal = (int)GameMath.gate(1,
				Math.round(healingLeft * percentHealPerTick) + flatHealPerTick,
				healingLeft);
		if (bloodVialLimitingLevel != -1 && heal > VialOfBlood.maxHealPerTurn(bloodVialLimitingLevel)){
			heal = VialOfBlood.maxHealPerTurn(bloodVialLimitingLevel);
		}
		return heal;
	}

	public void setHeal(int amount, float percentPerTick, int flatPerTick){
		setHeal(amount, percentPerTick, flatPerTick, false);
	}

	public void setHeal(int amount, float percentPerTick, int flatPerTick, boolean applyVialEffect){
		if (applyVialEffect && VialOfBlood.delayBurstHealing()){
			amount = Math.round(amount*VialOfBlood.totalHealMultiplier());
			bloodVialLimitingLevel = VialOfBlood.bloodVialLevel();
		} else {
			bloodVialLimitingLevel = -1;
		}
		//multiple sources of healing do not overlap, but do combine the best of their properties
		healingLeft = Math.max(healingLeft, amount);
		percentHealPerTick = Math.max(percentHealPerTick, percentPerTick);
		flatHealPerTick = Math.max(flatHealPerTick, flatPerTick);
	}
	
	public void increaseHeal( int amount ){
		healingLeft += amount;
	}
	
	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add( CharSprite.State.HEALING );
		else    target.sprite.remove( CharSprite.State.HEALING );
	}
	
	private static final String LEFT = "left";
	private static final String PERCENT = "percent";
	private static final String FLAT = "flat";

	private static final String VIAL_LEVEL = "vial_level";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEFT, healingLeft);
		bundle.put(PERCENT, percentHealPerTick);
		bundle.put(FLAT, flatHealPerTick);
		bundle.put(VIAL_LEVEL, bloodVialLimitingLevel);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		healingLeft = bundle.getInt(LEFT);
		percentHealPerTick = bundle.getFloat(PERCENT);
		flatHealPerTick = bundle.getInt(FLAT);
		//pre-v4.0
		if (bundle.contains(VIAL_LEVEL)){
			bloodVialLimitingLevel = bundle.getInt(VIAL_LEVEL);
		}
	}
	
	@Override
	public int icon() {
		return BuffIndicator.HEALING;
	}

	@Override
	public String iconTextDisplay() {
		return Integer.toString(healingLeft);
	}
	
	@Override
	public String desc() {
		return Messages.get(this, "desc", healingThisTick(), healingLeft);
	}
}
