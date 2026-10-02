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

package pd.items.equipment.weapon.melee;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FlavourBuff;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.ui.AttackIndicator;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import pd.messages.InlineText;

public class Sickle extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Sickle.class)
			.t("name", "短柄镰")
			.t("stats_desc", "这是一件不太精准的武器。")
			.t("ability_name", "收割")
			.t("typical_ability_desc", "决斗家可以用短柄镰来_收割_敌人，这破坏性的一击必定命中，一般会造成_%d点伤害_。若敌人不免疫流血，则造成的伤害会转化为等值流血效果。")
			.t("ability_desc", "决斗家可以用短柄镰来_收割_敌人，这破坏性的一击必定命中，并会造成_%d点伤害_。若敌人不免疫流血，则造成的伤害会转化为等值流血效果。")
			.t("desc", "一种手持农具，也可被用作一把强大但笨重的武器。");
	}




	{
		image = EquipmentEquipWeaponBasicWeaponDict.SICKLE_0;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1f;

		tier = 2;
		ACC = 0.68f; //32% penalty to accuracy
	}

	@Override
	public int max(int lvl) {
		return  Math.round(6.67f*(tier+1)) +    //20 base, up from 15
				lvl*(tier+1);                   //scaling unchanged
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		//replaces damage with 15+2.5*lvl bleed, roughly 138% avg base dmg, 125% avg scaling
		int bleedAmt = augment.damageFactor(Math.round(15f + 2.5f*buffedLvl()));
		Sickle.harvestAbility(hero, target, 0f, bleedAmt, this);
	}

	@Override
	public String abilityInfo() {
		int bleedAmt = levelKnown ? Math.round(15f + 2.5f*buffedLvl()) : 15;
		if (levelKnown){
			return Messages.get(this, "ability_desc", augment.damageFactor(bleedAmt));
		} else {
			return Messages.get(this, "typical_ability_desc", bleedAmt);
		}
	}

	@Override
	public String upgradeAbilityStat(int level) {
		return Integer.toString(augment.damageFactor(Math.round(15f + 2.5f*level)));
	}

	public static void harvestAbility(Hero hero, Integer target, float bleedMulti, int bleedBoost, MeleeWeapon wep){

		if (target == null) {
			return;
		}

		Char enemy = Actor.findChar(target);
		if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
			GLog.w(Messages.get(wep, "ability_no_target"));
			return;
		}

		hero.belongings.abilityWeapon = wep;
		if (!hero.canAttack(enemy)){
			GLog.w(Messages.get(wep, "ability_target_range"));
			hero.belongings.abilityWeapon = null;
			return;
		}
		hero.belongings.abilityWeapon = null;

		hero.sprite.attack(enemy.pos, new Callback() {
			@Override
			public void call() {
				wep.beforeAbilityUsed(hero, enemy);
				AttackIndicator.target(enemy);

				Buff.affect(hero, HarvestBleedTracker.class, 0);
				if (hero.attack(enemy, bleedMulti, bleedBoost, Char.INFINITE_ACCURACY)){
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
				}

				Invisibility.dispel();
				hero.spendAndNext(hero.attackDelay());
				if (!enemy.isAlive()){
					wep.onAbilityKill(hero, enemy);
				}
				wep.afterAbilityUsed(hero);
			}
		});

	}

	public static class HarvestBleedTracker extends FlavourBuff{};

}
