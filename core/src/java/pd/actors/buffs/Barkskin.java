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

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class Barkskin extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Barkskin.class)
			.t("name", "树肤")
			.t("desc", "你的皮肤硬化了，摸起来如同树皮般粗糙而坚固。\n\n硬化的皮肤增强了你的护甲，使你能更好地抵御物理攻击。\n\n当前增加的护甲：0~%d\n距离效果衰减还有：%s回合\n\n从多个来源获得树肤效果时，只有效果最强的那一个会起效。");
	}

	
	{
		type = buffType.POSITIVE;
	}

	private int level = 0;
	private int interval = 1;
	
	@Override
	public boolean act() {
		if (target.isAlive()) {

			spend( interval );
			if (--level <= 0) {
				detach();
			}
			
		} else {
			
			detach();
			
		}
		
		return true;
	}
	
	public int level() {
		return level;
	}
	
	public void set( int value, int time ) {
		if (level <= value) {
			level = value;
			interval = time;
			spend(time - cooldown() - 1);
		}
	}

	public void delay( float value ){
		spend(value);
	}
	
	@Override
	public int icon() {
		return BuffIndicator.BARKSKIN;
	}

	@Override
	public float iconFadePercent() {
		if (target instanceof Hero){
			float max = ((Hero) target).lvl*((Hero) target).pointsInTalent(Talent.BARKSKIN)/2;
			max = Math.max(max, 2+((Hero) target).lvl/3);
			return Math.max(0, (max-level)/max);
		}
		return 0;
	}

	@Override
	public String iconTextDisplay() {
		return Integer.toString(level);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", level, dispTurns(visualcooldown()));
	}
	
	private static final String LEVEL	    = "level";
	private static final String INTERVAL    = "interval";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( INTERVAL, interval );
		bundle.put( LEVEL, level );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		interval = bundle.getInt( INTERVAL );
		level = bundle.getInt( LEVEL );
	}

	//These two methods allow for multiple instances of barkskin to stack in terms of duration
	// but only the stronger bonus is applied

	public static int currentLevel(Char ch ){
		int level = 0;
		for (Barkskin b : ch.buffs(Barkskin.class)){
			level = Math.max(level, b.level);
		}
		return level;
	}

	//reset if a matching buff exists, otherwise append
	public static void conditionallyAppend(Char ch, int level, int interval){
		for (Barkskin b : ch.buffs(Barkskin.class)){
			if (b.interval == interval){
				b.set(level, interval);
				return;
			}
		}
		Buff.append(ch, Barkskin.class).set(level, interval);
	}
}
