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

package pd.plants;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Haste;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.mobs.Mob;
import pd.levels.GroundItems;
import pd.levels.traps.Trap;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.ui.BuffIndicator;
import render.noosa.Image;
import render.noosa.particles.Emitter;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class Swiftthistle extends Plant {
	//SPSEXPD: inline Chinese text (generated from messages/plants/zh)
	static {
		InlineText.of(Swiftthistle.class)
			.t("name", "速行蓟")
			.t("desc", "被踩踏后，速行蓟会加速周围的时间流动，让踩踏者可以瞬间行动数次")
			.t("warden_desc", "_守望者_踩踏速行蓟不仅能获得瞬时行动的机会，还能在短时间内极速奔跑。")
			.t("$seed.name", "速行蓟之种")
			.t("$timebubble.name", "时间气泡")
			.t("$timebubble.desc", "这是一个加速时间运行的气泡。\n\n你可以在气泡中瞬间进行多次行动，但是攻击或施法都会打断这个效果。\n\n剩余时长：%s回合");
	}



	
	{
		image = 2;
		seedClass = Seed.class;
	}
	
	@Override
	public void activate( Char ch ) {
		if (ch != null) {
			Buff.affect(ch, TimeBubble.class).reset();
			if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN){
				Buff.affect(ch, Haste.class, 1f);
			}
		}
	}
	
	public static class Seed extends Plant.Seed {
		{
			image = ConsumPotionSeedSeedDict.SEED_SWIFTTHISTLE;
			
			plantClass = Swiftthistle.class;
		}
	}
	
	//FIXME lots of copypasta from time freeze here
	
	public static class TimeBubble extends Buff {
		
		{
			type = buffType.POSITIVE;
			announced = true;
		}
		
		private float left;
		ArrayList<Integer> presses = new ArrayList<>();
		
		@Override
		public int icon() {
			return BuffIndicator.TIME;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(1f, 1f, 0);
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (6f - left) / 6f);
		}

		@Override
		public String iconTextDisplay() {
			return Integer.toString((int)(left + 0.001f));
		}

		public void reset(){
			reset(6);
		}

		public void reset(int turns){
			left = turns + 1; //add 1 as we're spending it on our action
		}
		
		@Override
		public String desc() {
			return Messages.get(this, "desc", dispTurns(Math.max(0, left)));
		}
		
		public void processTime(float time){
			left -= time;

			//use 1/1,000 to account for rounding errors
			if (left < -0.001f){
				detach();
			}
			
		}
		
		public void setDelayedPress(int cell){
			if (!presses.contains(cell)) {
				presses.add(cell);
			}
		}

		public void triggerPresses(){
			ArrayList<Integer> toTrigger = presses;
			presses = new ArrayList<>();
			Actor.add(new Actor() {
				{
					actPriority = VFX_PRIO;
				}

				@Override
				protected boolean act() {
					for (int cell : toTrigger){
						Plant p = Dungeon.level.plants.get(cell);
						if (p != null){
							p.trigger();
						}
						Trap t = Dungeon.level.traps.get(cell);
						if (t != null){
							t.trigger();
						}
					}
					Actor.remove(this);
					return true;
				}
			});
		}

		public void disarmPresses(){
			for (int cell : presses){
				Plant p = Dungeon.level.plants.get(cell);
				if (p != null && !(p instanceof Rotberry)) {
					GroundItems.uproot( Dungeon.level, cell);
				}
				Trap t = Dungeon.level.traps.get(cell);
				if (t != null && t.disarmedByActivation) {
					t.disarm();
				}
			}

			presses = new ArrayList<>();
		}
		
		@Override
		public void detach(){
			super.detach();
			triggerPresses();
			target.next();
		}

		@Override
		public void fx(boolean on) {
			if (!(target instanceof Hero)) return;
			Emitter.freezeEmitters = on;
			if (on){
				for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
					if (mob.sprite != null) mob.sprite.add(CharSprite.State.PARALYSED);
				}
			} else {
				for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
					if (mob.paralysed <= 0) mob.sprite.remove(CharSprite.State.PARALYSED);
				}
			}
		}
		
		private static final String PRESSES = "presses";
		private static final String LEFT = "left";
		
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			
			int[] values = new int[presses.size()];
			for (int i = 0; i < values.length; i ++)
				values[i] = presses.get(i);
			bundle.put( PRESSES , values );
			
			bundle.put( LEFT, left);
		}
		
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			
			int[] values = bundle.getIntArray( PRESSES );
			for (int value : values)
				presses.add(value);
			
			left = bundle.getFloat(LEFT);
		}
		
	}
}
