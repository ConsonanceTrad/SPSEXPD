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

package pd.items.food;

import pd.atlas.items.ConsumFoodFoodDict;
import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.ArtifactRecharge;
import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.effects.FloatingText;
import pd.effects.particles.RainbowParticle;
import pd.items.potions.PotionOfExperience;
import pd.items.scrolls.ScrollOfRecharging;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.ui.TargetHealthIndicator;
import pd.utils.Holiday;
import render.noosa.audio.Sample;

public class Pasty extends Food {

	{
		reset();

		energy = Hunger.STARVING;

		bones = true;
	}
	
	@Override
	public void reset() {
		super.reset();
		switch(Holiday.getCurrentHoliday()){
			case NONE: default:
				image = SpecificPlaceHolderDict.SOMETHING_0;
				break;
			case LUNAR_NEW_YEAR:
				image = ConsumFoodFoodDict.STEAMED_FISH_0;
				break;
			case APRIL_FOOLS:
				image = SpecificPlaceHolderDict.SOMETHING_0;
				break;
			case EASTER:
				image = ConsumFoodFoodDict.EASTER_EGG_0;
				break;
			case PRIDE:
				image = ConsumFoodFoodDict.RAINBOW_POTION_0;
				break;
			case SHATTEREDPD_BIRTHDAY:
				image = SpecificPlaceHolderDict.SOMETHING_0;
				break;
			case HALLOWEEN:
				image = ConsumFoodFoodDict.PUMPKIN_PIE;
				break;
			case PD_BIRTHDAY:
				image = SpecificPlaceHolderDict.SOMETHING_0;
				break;
			case WINTER_HOLIDAYS:
				image = ConsumFoodFoodDict.CANDY_CANE_0;
				break;
			case NEW_YEARS:
				image = ConsumFoodFoodDict.SPARKLING_POTION_0;
				break;
		}
	}

	@Override
	protected void eatSFX() {
		switch(Holiday.getCurrentHoliday()){
			case PRIDE:
			case NEW_YEARS:
				Sample.INSTANCE.play( Assets.Sounds.DRINK );
				return;
		}
		super.eatSFX();
	}

	@Override
	protected void satisfy(Hero hero) {
		if (Holiday.getCurrentHoliday() == Holiday.LUNAR_NEW_YEAR){
			//main item only clears 300 hunger on lunar new year...
			energy = Hunger.HUNGRY;
		}

		super.satisfy(hero);
		
		switch(Holiday.getCurrentHoliday()){
			default:
				break; //do nothing extra
			case LUNAR_NEW_YEAR:
				//...but it also awards an extra item that restores 150 hunger
				FishLeftover left = new FishLeftover();
				if (!left.collect()){
					Dungeon.level.drop(left, hero.pos).sprite.drop();
				}
				break;
			case APRIL_FOOLS:
				Sample.INSTANCE.play(Assets.Sounds.MIMIC);
			case EASTER:
				ArtifactRecharge.chargeArtifacts(hero, 2f);
				ScrollOfRecharging.charge( hero );
				break;
			case PRIDE:
				Char target = null;

				//charms an adjacent non-boss enemy, prioritizing the one the hero is focusing on
				for (Char ch : Actor.chars()){
					if (!Char.hasProp(ch, Char.Property.BOSS)
							&& !Char.hasProp(ch, Char.Property.MINIBOSS)
							&& ch.alignment == Char.Alignment.ENEMY
							&& Dungeon.level.adjacent(hero.pos, ch.pos)){
						if (target == null || ch == TargetHealthIndicator.instance.target()){
							target = ch;
						}
					}
				}

				if (target != null){
					Buff.affect(target, Charm.class, 5f).object = hero.id();
				}
				hero.sprite.emitter().burst(RainbowParticle.BURST, 15);
				break;
			case SHATTEREDPD_BIRTHDAY:
			case PD_BIRTHDAY:
				//gives 10% of level in exp, min of 2
				int expToGive = Math.max(2, hero.maxExp()/10);
				hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(expToGive), FloatingText.EXPERIENCE);
				hero.earnExp(expToGive, PotionOfExperience.class);
				break;
			case HALLOWEEN:
				//heals for 5% max hp, min of 3
				int toHeal = Math.max(3, hero.HT/20);
				hero.HP = Math.min(hero.HP + toHeal, hero.HT);
				hero.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(toHeal), FloatingText.HEALING );
				break;
			case WINTER_HOLIDAYS:
				hero.belongings.charge(0.5f); //2 turns worth
				ScrollOfRecharging.charge( hero );
				break;
			case NEW_YEARS:
				//shields for 10% of max hp, min of 5
				int toShield = Math.max(5, hero.HT/10);
				Buff.affect(hero, Barrier.class).setShield(toShield);
				hero.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(toShield), FloatingText.SHIELDING );
				break;
		}
	}

	@Override
	public String name() {
		switch(Holiday.getCurrentHoliday()){
			case NONE: default:
				return super.name();
			case LUNAR_NEW_YEAR:
				return Messages.get(this, "fish_name");
			case APRIL_FOOLS:
				return Messages.get(this, "amulet_name");
			case EASTER:
				return Messages.get(this, "egg_name");
			case PRIDE:
				return Messages.get(this, "rainbow_name");
			case SHATTEREDPD_BIRTHDAY:
				return Messages.get(this, "shattered_name");
			case HALLOWEEN:
				return Messages.get(this, "pie_name");
			case PD_BIRTHDAY:
				return Messages.get(this, "vanilla_name");
			case WINTER_HOLIDAYS:
				return Messages.get(this, "cane_name");
			case NEW_YEARS:
				return Messages.get(this, "sparkling_name");
		}
	}

	@Override
	public String desc() {
		switch(Holiday.getCurrentHoliday()){
			case NONE: default:
				return super.desc();
			case LUNAR_NEW_YEAR:
				return Messages.get(this, "fish_desc");
			case APRIL_FOOLS:
				return Messages.get(this, "amulet_desc");
			case EASTER:
				return Messages.get(this, "egg_desc");
			case PRIDE:
				return Messages.get(this, "rainbow_desc");
			case SHATTEREDPD_BIRTHDAY:
				return Messages.get(this, "shattered_desc");
			case HALLOWEEN:
				return Messages.get(this, "pie_desc");
			case PD_BIRTHDAY:
				return Messages.get(this, "vanilla_desc");
			case WINTER_HOLIDAYS:
				return Messages.get(this, "cane_desc");
			case NEW_YEARS:
				return Messages.get(this, "sparkling_desc");
		}
	}
	
	@Override
	public int value() {
		return 20 * quantity;
	}

	public static class FishLeftover extends Food {

		{
			image = ConsumFoodFoodDict.FISH_LEFTOVER_0;
			energy = Hunger.HUNGRY/2;
		}

		@Override
		public int value() {
			return 10 * quantity;
		}
	}
}
