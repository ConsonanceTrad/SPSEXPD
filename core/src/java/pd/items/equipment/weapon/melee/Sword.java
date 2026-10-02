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
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import pd.messages.InlineText;

public class Sword extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Sword.class)
			.t("name", "单手剑")
			.t("ability_name", "顺劈")
			.t("typical_ability_desc", "决斗家可以用单手剑_顺劈_敌人。这一般会造成_%1$d~%2$d点伤害_且必定命中。如果顺劈击杀了一名敌人，这一击将不消耗时间，并且决斗家可在5回合内额外使用一次不消耗充能数的顺劈。")
			.t("ability_desc", "决斗家可以用单手剑_顺劈_敌人，造成_%1$d~%2$d点伤害_且必定命中。如果顺劈击杀了一名敌人，这一击将不消耗时间，并且决斗家可在5回合内额外使用一次不消耗充能数的顺劈。")
			.t("desc", "平衡性良好的剑。不算太大，但是依然比短剑长上不少。")
			.t("$cleavetracker.name", "顺劈")
			.t("$cleavetracker.desc", "决斗家已经准备好进行连续的顺劈。这一次顺劈将不会消耗武技充能。\n\n剩余回合数：%s。");
	}



	
	{
		image = EquipmentEquipWeaponBasicWeaponDict.SWORD_0;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1f;

		tier = 3;
	}

	@Override
	protected int baseChargeUse(Hero hero, Char target){
		if (hero.buff(Sword.CleaveTracker.class) != null){
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		//+(5+lvl) damage, roughly +45% base dmg, +40% scaling
		int dmgBoost = augment.damageFactor(5 + buffedLvl());
		Sword.cleaveAbility(hero, target, 1, dmgBoost, this);
	}

	@Override
	public String abilityInfo() {
		int dmgBoost = levelKnown ? 5 + buffedLvl() : 5;
		if (levelKnown){
			return Messages.get(this, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
		} else {
			return Messages.get(this, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
		}
	}

	public String upgradeAbilityStat(int level){
		int dmgBoost = 5 + level;
		return augment.damageFactor(min(level)+dmgBoost) + "-" + augment.damageFactor(max(level)+dmgBoost);
	}

	public static void cleaveAbility(Hero hero, Integer target, float dmgMulti, int dmgBoost, MeleeWeapon wep){
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
				if (hero.attack(enemy, dmgMulti, dmgBoost, Char.INFINITE_ACCURACY)){
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
				}

				Invisibility.dispel();

				if (!enemy.isAlive()){
					hero.next();
					wep.onAbilityKill(hero, enemy);
					if (hero.buff(CleaveTracker.class) != null) {
						hero.buff(CleaveTracker.class).detach();
					} else {
						Buff.prolong(hero, CleaveTracker.class, 4f); //1 less as attack was instant
					}
				} else {
					hero.spendAndNext(hero.attackDelay());
					if (hero.buff(CleaveTracker.class) != null) {
						hero.buff(CleaveTracker.class).detach();
					}
				}
				wep.afterAbilityUsed(hero);
			}
		});
	}

	public static class CleaveTracker extends FlavourBuff {

		{
			type = buffType.POSITIVE;
		}

		@Override
		public int icon() {
			return BuffIndicator.DUEL_CLEAVE;
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (5 - visualcooldown()) / 5);
		}
	}

}
