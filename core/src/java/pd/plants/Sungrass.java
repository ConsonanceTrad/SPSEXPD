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
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Healing;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.effects.CellEmitter;
import pd.effects.FloatingText;
import pd.effects.particles.ShaftParticle;
import pd.items.equipment.weapon.missiles.arrows.HealFruit;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class Sungrass extends Plant {
	//SPSEXPD: inline Chinese text (generated from messages/plants/zh)
	static {
		InlineText.of(Sungrass.class)
			.t("name", "阳春草")
			.t("desc", "阳春草因其汁液缓慢但显著的疗伤效果而家喻户晓。")
			.t("warden_desc", "_守望者_能将阳春草的治疗魔力随身携带。")
			.t("$seed.name", "阳春草之种")
			.t("$health.name", "草药疗养")
			.t("$health.desc", "阳春草拥有优秀的治疗能力，尽管治疗速率比治疗药剂慢很多。\n\n脱离植株将会打断治疗效果。\n\n剩余治疗量：%d")
			.t("$exsungrass.name", "阳春草果丛")
			.t("$exsungrass.desc", "生长疗伤果的果丛。");
	}



	
	{
		image = 4;
		seedClass = Seed.class;
	}
	
	@Override
	public void activate( Char ch ) {
		
		if (ch != null){
			if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN) {
				Buff.affect(ch, Healing.class).setHeal(ch.HT, 0, 1);
			} else {
				Buff.affect(ch, Health.class).boost(ch.HT);
			}
		}
		
		if (Dungeon.level.heroFOV[pos]) {
			CellEmitter.get( pos ).start( ShaftParticle.FACTORY, 0.2f, 3 );
		}
	}
	
	public static class Seed extends Plant.Seed {
		{
			image = ConsumPotionSeedSeedDict.SEED_SUNGRASS;

			plantClass = Sungrass.class;
			explantClass = ExSungrass.class;

			bones = true;
		}
	}

	public static class ExSungrass extends SpsFruitBush {
		{ image = 4; harvestCount = 2; harvestClass = HealFruit.class; }
	}
	
	public static class Health extends Buff {
		
		private static final float STEP = 1f;
		
		private int pos;
		private float partialHeal;
		private int level;

		{
			type = buffType.POSITIVE;
			announced = true;
		}
		
		@Override
		public boolean act() {
			if (target.pos != pos) {
				detach();
			}
			
			//for the hero, full heal takes ~50/93/111/120 turns at levels 1/10/20/30
			partialHeal += (40 + target.HT)/150f;
			
			if (partialHeal > 1){
				int healThisTurn = (int)partialHeal;
				partialHeal -= healThisTurn;
				level -= healThisTurn;

				if (target.HP < target.HT) {

					if (!pd.actors.hero.perks.BloodShield.convert(target, healThisTurn)) target.HP += healThisTurn;
					target.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(healThisTurn), FloatingText.HEALING);

					if (target.HP >= target.HT) {
						target.HP = target.HT;
						if (target instanceof Hero) {
							((Hero) target).resting = false;
						}
					}
				}
			}
			
			if (level <= 0) {
				detach();
				if (target instanceof Hero){
					((Hero)target).resting = false;
				}
			}
			spend( STEP );
			return true;
		}

		public void boost( int amount ){
			if (target != null) {
				level += amount;
				pos = target.pos;
			}
		}
		
		@Override
		public int icon() {
			return BuffIndicator.HERB_HEALING;
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (target.HT - level) / (float)target.HT);
		}

		@Override
		public String iconTextDisplay() {
			return Integer.toString(level);
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", level);
		}

		private static final String POS	= "pos";
		private static final String PARTIAL = "partial_heal";
		private static final String LEVEL = "level";

		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( POS, pos );
			bundle.put( PARTIAL, partialHeal);
			bundle.put( LEVEL, level);
		}
		
		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle( bundle );
			pos = bundle.getInt( POS );
			partialHeal = bundle.getFloat( PARTIAL );
			level = bundle.getInt( LEVEL );

		}
	}
}
