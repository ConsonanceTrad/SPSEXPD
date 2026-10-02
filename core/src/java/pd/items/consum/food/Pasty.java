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

package pd.items.consum.food;

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
import pd.items.consum.potions.PotionOfExperience;
import pd.items.consum.scrolls.ScrollOfRecharging;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.ui.TargetHealthIndicator;
import pd.utils.Holiday;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class Pasty extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Pasty.class)
			.t("name", "馅饼")
			.t("fish_name", "清蒸荷叶鱼")
			.t("amulet_name", "Yendor护符？")
			.t("egg_name", "复活节彩蛋")
			.t("rainbow_name", "虹色药剂")
			.t("shattered_name", "绿色蛋糕")
			.t("pie_name", "南瓜派")
			.t("vanilla_name", "蓝色蛋糕")
			.t("cane_name", "拐杖糖")
			.t("sparkling_name", "气泡药剂")
			.t("desc", "这是份正宗康郡肉馅饼，内含牛肉加土豆的传统馅料。美味十足！")
			.t("fish_desc", "被荷叶包裹，以法术贮藏的清蒸鲈鱼。逢此年月，有将鱼剩下的习俗，取年年有余之意，于是你也决定不将这条蒸鱼一次吃完。\n\n新年快乐！")
			.t("amulet_desc", "你终于找到它了，神奇的护...噢不，这只是一块以箔纸包装的巧克力而已，它只是看起来像护符！它带不来无限的力量，但却能让你完全吃饱，还能提供短暂的神器充能效果。\n\n愚人节快乐！")
			.t("egg_desc", "一个硕大的巧克力蛋，被五彩斑斓的箔纸包装着。这种体量的巧克力可以轻松将你填饱，其中的糖分还能为神器提供短暂的额外充能效果。\n\n复活节快乐！")
			.t("rainbow_desc", "这瓶多彩的药剂是一种液态食物。它不仅可以充饥，其中还蕴含着能魅惑一名相邻非boss敌人的小型法术，能令其暂时不愿与你交战。\n\n节日快乐！")
			.t("shattered_desc", "这一大块香草蛋糕上裹上了一层绿色糖霜，其上洒满各色糖碎。这样的蛋糕被用于庆祝某人事物积累若干年履历的时刻，而这份履历也会在你吃下时作为经验分得一部分。\n\n破碎的像素地牢于2014年8月5日问世。祝破碎地牢生日快乐！")
			.t("pie_desc", "好大的一块南瓜派！甘甜又微辣，它会填饱你的肚子并让你恢复少量生命。\n\n万圣节快乐！")
			.t("vanilla_desc", "这一大块香草蛋糕上裹上了一层蓝色糖霜，其上洒满各色糖碎。这样的蛋糕被用于庆祝某人事物积累若干年履历的时刻，而这份履历也会在你吃下时作为经验分得一部分。\n\n原版像素地牢于2012年12月4日问世。祝像素地牢生日快乐！")
			.t("cane_desc", "甜度爆表的巨型拐杖糖！大到够你一次吃饱，其中的糖分或许还能让你的法杖获得少量额外充能。\n\n假日快乐！")
			.t("sparkling_desc", "这瓶起泡的药剂是一种液态食物。这种果腹之物尝起来和气泡酒别无二致，但实际上并不含酒精。这股流经腹部的暖流能为你提供少量护盾。\n\n元旦快乐！")
			.t("$fishleftover.name", "余鱼")
			.t("$fishleftover.eat_msg", "吃起来还行。")
			.t("$fishleftover.desc", "你上一顿吃剩的鱼。你可以随时把它吃完，这能恢复你少量的饱食度。");
	}




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
