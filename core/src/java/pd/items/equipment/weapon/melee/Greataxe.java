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
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.ui.AttackIndicator;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import pd.messages.InlineText;

public class Greataxe extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Greataxe.class)
			.t("name", "巨斧")
			.t("stats_desc", "这件武器非常沉重。")
			.t("ability_name", "报复")
			.t("typical_ability_desc", "决斗家在血量低于50%%时可以使用巨斧进行_报复_攻击。这种毁灭性的攻击必定命中，且一般造成_%1$d~%2$d点伤害_，若是击杀了一名敌人，这一击将不消耗时间。")
			.t("ability_desc", "决斗家在血量低于50%%时可以使用巨斧进行_报复_攻击。这种毁灭性的攻击必定命中，且造成_%1$d~%2$d点伤害_，若是击杀了一名敌人，这一击将不消耗时间。")
			.t("desc", "这个巨型的战斧无坚不摧，重如泰山，举过肩才有空间挥舞。");
	}


	{
		image = EquipmentEquipWeaponBasicWeaponDict.GREATAXE_0;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1f;

		tier = 5;
	}

	@Override
	public int max(int lvl) {
		return  5*(tier+4) +    //45 base, up from 30
				lvl*(tier+1);   //scaling unchanged
	}

	@Override
	public int STRReq(int lvl) {
		int req = STRReq(tier+1, lvl); //20 base strength req, up from 18
		if (masteryPotionBonus){
			req -= 2;
		}
		return req;
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		if (hero.HP / (float)hero.HT >= 0.5f){
			GLog.w(Messages.get(this, "ability_cant_use"));
			return;
		}

		if (target == null) {
			return;
		}

		Char enemy = Actor.findChar(target);
		if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
			GLog.w(Messages.get(this, "ability_no_target"));
			return;
		}

		hero.belongings.abilityWeapon = this;
		if (!hero.canAttack(enemy)){
			GLog.w(Messages.get(this, "ability_target_range"));
			hero.belongings.abilityWeapon = null;
			return;
		}
		hero.belongings.abilityWeapon = null;

		hero.sprite.attack(enemy.pos, new Callback() {
			@Override
			public void call() {
				beforeAbilityUsed(hero, enemy);
				AttackIndicator.target(enemy);

				//+(15+(2*lvl)) damage, roughly +60% base damage, +55% scaling
				int dmgBoost = augment.damageFactor(15 + 2*buffedLvl());

				if (hero.attack(enemy, 1, dmgBoost, Char.INFINITE_ACCURACY)){
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
				}

				Invisibility.dispel();
				if (!enemy.isAlive()){
					hero.next();
					onAbilityKill(hero, enemy);
				} else {
					hero.spendAndNext(hero.attackDelay());
				}
				afterAbilityUsed(hero);
			}
		});
	}

	@Override
	public String abilityInfo() {
		int dmgBoost = levelKnown ? 15 + 2*buffedLvl() : 15;
		if (levelKnown){
			return Messages.get(this, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
		} else {
			return Messages.get(this, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
		}
	}

	public String upgradeAbilityStat(int level){
		int dmgBoost = 15 + 2*level;
		return augment.damageFactor(min(level)+dmgBoost) + "-" + augment.damageFactor(max(level)+dmgBoost);
	}
}
