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
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FlavourBuff;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.noosa.Image;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class RoundShield extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RoundShield.class)
			.t("name", "圆盾")
			.t("typical_stats_desc", "这件武器通常能格挡0~%d点伤害。格挡量随升级而增长。")
			.t("stats_desc", "这件武器能格挡0~%d点伤害。格挡量随升级而增长。")
			.t("ability_name", "护卫")
			.t("typical_ability_desc", "决斗家可以用圆盾_护卫_自己，一般会在_%d回合_内完全抵挡针对自己的物理或魔法攻击。一旦决斗家抵挡过攻击，再行还击或施法，都会终止护卫状态。")
			.t("ability_desc", "决斗家可以用圆盾_护卫_自己，在_%d回合_内完全抵挡针对自己的所有物理或魔法攻击。一旦决斗家抵挡过攻击，再行还击或施法，都会终止护卫状态。")
			.t("upgrade_ability_stat_name", "武技持续时间")
			.t("desc", "这个大盾可以有效格挡攻击，在危机时刻也可以作为不错的武器使用。")
			.t("$guardtracker.name", "护卫中")
			.t("$guardtracker.guarded", "被护卫")
			.t("$guardtracker.desc", "决斗家已经准备好她的盾牌以应对即将到来的攻击。针对她的物理或魔法攻击将被完全抵挡。\n\n剩余回合数：%s。");
	}




	{
		image = EquipmentEquipWeaponBasicWeaponDict.ROUND_SHIELD_0;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;

		tier = 3;
	}

	@Override
	public int max(int lvl) {
		return  Math.round(3f*(tier+1)) +   //12 base, down from 20
				lvl*(tier-1);               //+2 per level, down from +4
	}

	@Override
	public int defenseFactor( Char owner ) {
		return DRMax();
	}

	public int DRMax(){
		return DRMax(buffedLvl());
	}

	//4 extra defence, plus 1 per level
	public int DRMax(int lvl){
		return 4 + lvl;
	}
	
	public String statsInfo(){
		if (isIdentified()){
			return Messages.get(this, "stats_desc", 4+buffedLvl());
		} else {
			return Messages.get(this, "typical_stats_desc", 4);
		}
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		RoundShield.guardAbility(hero, 5+buffedLvl(), this);
	}

	@Override
	public String abilityInfo() {
		if (levelKnown){
			return Messages.get(this, "ability_desc", 5+buffedLvl());
		} else {
			return Messages.get(this, "typical_ability_desc", 5);
		}
	}

	@Override
	public String upgradeAbilityStat(int level) {
		return Integer.toString(5 + level);
	}

	public static void guardAbility(Hero hero, int duration, MeleeWeapon wep){
		wep.beforeAbilityUsed(hero, null);
		Buff.prolong(hero, GuardTracker.class, duration).hasBlocked = false;
		hero.sprite.operate(hero.pos);
		hero.spendAndNext(Actor.TICK);
		wep.afterAbilityUsed(hero);
	}

	public static class GuardTracker extends FlavourBuff {

		{
			announced = true;
			type = buffType.POSITIVE;
		}

		public boolean hasBlocked = false;

		@Override
		public int icon() {
			return BuffIndicator.DUEL_GUARD;
		}

		@Override
		public void tintIcon(Image icon) {
			if (hasBlocked){
				icon.tint(0x651f66, 0.5f);
			} else {
				icon.resetColor();
			}
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (5 - visualcooldown()) / 5);
		}

		private static final String BLOCKED = "blocked";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(BLOCKED, hasBlocked);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			hasBlocked = bundle.getBoolean(BLOCKED);
		}
	}
}