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

import pd.Badges;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.effects.CellEmitter;
import pd.effects.particles.PoisonParticle;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import render.noosa.Image;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class Poison extends Buff implements Hero.Doom, Buff.DOTbuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Poison.class)
			.t("name", "中毒")
			.t("heromsg", "你中毒了！")
			.t("ondeath", "你被毒死了...")
			.t("rankings_desc", "毒发身亡")
			.t("desc", "毒素传遍全身，缓慢地损伤着各个脏器。\n\n毒素每回合造成的伤害与其剩余的回合数成正比。\n\n中毒效果剩余时长：%s回合");
	}



	
	protected float left;
	
	private static final String LEFT	= "left";

	{
		type = buffType.NEGATIVE;
		announced = true;
	}
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( LEFT, left );
		
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		left = bundle.getFloat( LEFT );
	}
	
	public void set( float duration ) {
		this.left = Math.max(duration, left);
		if (target != null) target.needsIncomingDOTUpdate = true;
	}

	public void extend( float duration ) {
		this.left += duration;
		if (target != null) target.needsIncomingDOTUpdate = true;
	}

	public void delay( float turns ){
		spend(turns);
	}
	
	@Override
	public int icon() {
		return BuffIndicator.POISON;
	}
	
	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0.6f, 0.2f, 0.6f);
	}

	public String iconTextDisplay(){
		return Integer.toString((int) left);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns(left));
	}

	@Override
	public boolean attachTo(Char target) {
		if (super.attachTo(target) && target.sprite != null){
			CellEmitter.center(target.pos).burst( PoisonParticle.SPLASH, 5 );
			return true;
		} else
			return false;
	}

	@Override
	public void detach() {
		target.needsIncomingDOTUpdate = true;
		super.detach();
	}

	@Override
	public boolean act() {
		if (target.isAlive()) {
			
			target.damage( (int)(left / 3) + 1, this );
			spend( TICK );
			
			if ((left -= TICK) <= 0) {
				detach();
			}
			target.needsIncomingDOTUpdate = true;
			
		} else {
			
			detach();
			
		}
		
		return true;
	}

	@Override
	public int totalIncomingDMG() {
		int total = 0;
		for (int i = (int)Math.ceil(left); i > 0; i--){
			total += i/3 + 1;
		}
		return total;
	}

	@Override
	public void onDeath() {
		Badges.validateDeathFromPoison();
		
		Dungeon.fail( this );
		GLog.n( Messages.get(this, "ondeath") );
	}
}
