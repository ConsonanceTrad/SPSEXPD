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
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import render.noosa.Image;
import render.noosa.audio.Sample;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class Flail extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Flail.class)
			.t("name", "链枷")
			.t("stats_desc", "这是一件不太精准的武器。\n这件武器不能用来伏击。")
			.t("ability_name", "甩动")
			.t("spin_warn", "你不能再继续甩动链枷了。")
			.t("typical_ability_desc", "决斗家可以_甩动_链枷以在短时间内积蓄力量。每甩动一次，链枷一般就会造成_%d点额外伤害_，最多3次。甩动的链枷必定命中。只有开始甩动链枷消耗1点充能。")
			.t("ability_desc", "决斗家可以_甩动_链枷以在短时间内积蓄力量。每甩动一次，链枷就会造成_%d点额外伤害_，最多3次。甩动的链枷必定命中。只有开始甩动链枷消耗1点充能。")
			.t("desc", "铁链上附着的一个带刺的钢球。笨重难用，能命中的话威力极强。")
			.t("spinabilitytracker.name", "蓄势")
			.t("spinabilitytracker.desc", "决斗家正在甩动链枷，为下一击积蓄力量。每次甩动消耗一回合但会增加伤害，最多甩动三次。只要在甩动中，链枷的攻击就必定命中。\n\n当前甩动次数：%1$d%%。\n剩余回合数：%2$s。");
	}


	{
		image = EquipmentEquipWeaponBasicWeaponDict.FLAIL_0;
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch = 0.8f;

		tier = 4;
		ACC = 0.8f; //0.8x accuracy
		//also cannot surprise attack, see Hero.canSurpriseAttack
	}

	@Override
	public int max(int lvl) {
		return  Math.round(7*(tier+1)) +        //35 base, up from 25
				lvl*Math.round(1.6f*(tier+1));  //+8 per level, up from +5
	}

	private static int spinBoost = 0;

	@Override
	public int damageRoll(Char owner) {
		int dmg = super.damageRoll(owner) + spinBoost;
		if (spinBoost > 0) Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
		spinBoost = 0;
		return dmg;
	}

	@Override
	public float accuracyFactor(Char owner, Char target) {
		SpinAbilityTracker spin = owner.buff(SpinAbilityTracker.class);
		if (spin != null && spinBoost == 0) {
			Actor.add(new Actor() {
				{ actPriority = VFX_PRIO; }
				@Override
				protected boolean act() {
					spinBoost = 0;
					if (owner instanceof Hero && !target.isAlive()){
						onAbilityKill((Hero)owner, target);
					}
					Actor.remove(this);
					return true;
				}
			});
			//we detach and calculate bonus here in case the attack misses (e.g. vs. monks)
			spin.detach();
			//+(8+2*lvl) damage per spin, roughly +40% base damage, +45% scaling
			// so +120% base dmg, +135% scaling at 3 spins
			spinBoost = spin.spins * augment.damageFactor(8 + 2*buffedLvl());
			return Float.POSITIVE_INFINITY;
		} else if (spinBoost != 0) {
			return Float.POSITIVE_INFINITY;
		} else {
			return super.accuracyFactor(owner, target);
		}
	}

	@Override
	protected int baseChargeUse(Hero hero, Char target){
		if (Dungeon.hero.buff(SpinAbilityTracker.class) != null){
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {

		SpinAbilityTracker spin = hero.buff(SpinAbilityTracker.class);
		if (spin != null && spin.spins >= 3){
			GLog.w(Messages.get(this, "spin_warn"));
			return;
		}

		beforeAbilityUsed(hero, null);
		if (spin == null){
			spin = Buff.affect(hero, SpinAbilityTracker.class, 3f);
		}

		spin.spins++;
		Buff.prolong(hero, SpinAbilityTracker.class, 3f);
		Sample.INSTANCE.play(Assets.Sounds.CHAINS, 1, 1, 0.9f + 0.1f*spin.spins);
		hero.sprite.operate(hero.pos);
		hero.spendAndNext(Actor.TICK);
		BuffIndicator.refreshHero();

		afterAbilityUsed(hero);
	}

	@Override
	public String abilityInfo() {
		int dmgBoost = levelKnown ? 8 + 2*buffedLvl() : 8;
		if (levelKnown){
			return Messages.get(this, "ability_desc", augment.damageFactor(dmgBoost));
		} else {
			return Messages.get(this, "typical_ability_desc", augment.damageFactor(dmgBoost));
		}
	}

	public String upgradeAbilityStat(int level){
		return "+" + augment.damageFactor(8 + 2*level);
	}

	public static class SpinAbilityTracker extends FlavourBuff {

		{
			type = buffType.POSITIVE;
		}

		public int spins = 0;

		@Override
		public int icon() {
			return BuffIndicator.DUEL_SPIN;
		}

		@Override
		public void tintIcon(Image icon) {
			switch (spins){
				case 1: default:
					icon.hardlight(0, 1, 0);
					break;
				case 2:
					icon.hardlight(1, 1, 0);
					break;
				case 3:
					icon.hardlight(1, 0, 0);
					break;
			}
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (3 - visualcooldown()) / 3);
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", (int)Math.round((spins/3f)*100f), dispTurns());
		}

		public static String SPINS = "spins";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(SPINS, spins);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			spins = bundle.getInt(SPINS);
		}
	}
}
