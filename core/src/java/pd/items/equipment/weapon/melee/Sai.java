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
import pd.actors.buffs.Combo;
import pd.actors.buffs.Corruption;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.ui.AttackIndicator;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class Sai extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Sai.class)
			.t("name", "双钗")
			.t("stats_desc", "这是一件非常快的武器。")
			.t("ability_name", "连击")
			.t("typical_ability_desc", "决斗家可以使用双钗进行_连击_。这次攻击必定命中，并且决斗家每使用近战或者投掷武器成功命中一次，这次攻击的伤害一般就_增加%d点_。连续5回合(击杀后为15回合)未成功命中会重置连击。")
			.t("ability_desc", "决斗家可以使用双钗进行_连击_。这次攻击必定命中，并且决斗家每使用近战或者投掷武器成功命中一次，这次攻击的伤害就_增加%d点_。连续5回合(击杀后为15回合)未成功命中会重置连击。")
			.t("desc", "一对轻薄的铁刃，刚好一手一把。在快速斩击方面十分出色。")
			.t("$combostriketracker.name", "连击")
			.t("$combostriketracker.desc", "决斗家正在积累连击数以增加连击武技的伤害。通常，每次近战或投掷武器成功攻击都会记入连击，但连续5回合(击杀敌人后为15回合)未成功攻击会重置连击。\n\n当前连击数：%1$d\n\n重置连击剩余回合数：%2$s");
	}




	{
		image = EquipmentEquipWeaponBasicWeaponDict.SAI_0;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1.3f;

		tier = 3;
		DLY = 0.5f; //2x speed
	}

	@Override
	public int max(int lvl) {
		return  Math.round(2.5f*(tier+1)) +     //10 base, down from 20
				lvl*Math.round(0.5f*(tier+1));  //+2 per level, down from +4
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		//+(4+lvl) damage, roughly +60% base damage, +67% scaling
		int dmgBoost = augment.damageFactor(4 + buffedLvl());
		Sai.comboStrikeAbility(hero, target, 0, dmgBoost, this);
	}

	@Override
	public String abilityInfo() {
		int dmgBoost = levelKnown ? 4 + buffedLvl() : 4;
		if (levelKnown){
			return Messages.get(this, "ability_desc", augment.damageFactor(dmgBoost));
		} else {
			return Messages.get(this, "typical_ability_desc", augment.damageFactor(dmgBoost));
		}
	}

	public String upgradeAbilityStat(int level){
		return "+" + augment.damageFactor(4 + level);
	}

	public static void comboStrikeAbility(Hero hero, Integer target, float multiPerHit, int boostPerHit, MeleeWeapon wep){
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

				int recentHits = 0;
				ComboStrikeTracker buff = hero.buff(ComboStrikeTracker.class);
				if (buff != null){
					recentHits = buff.hits;
					buff.detach();
				}

				boolean hit = hero.attack(enemy, 1f + multiPerHit*recentHits, boostPerHit*recentHits, Char.INFINITE_ACCURACY);
				if (hit && !enemy.isAlive()){
					wep.onAbilityKill(hero, enemy);
				}

				Invisibility.dispel();
				hero.spendAndNext(hero.attackDelay());
				if (recentHits >= 2 && hit){
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
				}

				wep.afterAbilityUsed(hero);
			}
		});
	}

	public static class ComboStrikeTracker extends Buff {

		{
			type = buffType.POSITIVE;
		}

		public static int DURATION = 5;
		private float comboTime = 0f;
		private float initialTime = 5f;
		public int hits = 0;

		@Override
		public int icon() {
			if (Dungeon.hero.belongings.weapon() instanceof Gloves
					|| Dungeon.hero.belongings.weapon() instanceof Sai
					|| Dungeon.hero.belongings.weapon() instanceof Gauntlet
					|| Dungeon.hero.belongings.secondWep() instanceof Gloves
					|| Dungeon.hero.belongings.secondWep() instanceof Sai
					|| Dungeon.hero.belongings.secondWep() instanceof Gauntlet) {
				return BuffIndicator.DUEL_COMBO;
			} else {
				return BuffIndicator.NONE;
			}
		}

		@Override
		public boolean act() {
			comboTime-=TICK;
			spend(TICK);
			if (comboTime <= 0) {
				detach();
			}
			return true;
		}

		public void addHit( Char enemy ){
			hits++;
			if (comboTime <= 5f) {
				comboTime = 5f;
				initialTime = 5f;
			}

			if (!enemy.isAlive() || (enemy.buff(Corruption.class) != null && enemy.HP == enemy.HT)){
				comboTime = 15f;
				initialTime = 15f;
			}

			if (hits >= 2 && icon() != BuffIndicator.NONE){
				GLog.p( Messages.get(Combo.class, "combo", hits) );
			}
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (initialTime - comboTime)/ initialTime);
		}

		@Override
		public String iconTextDisplay() {
			return Integer.toString((int)comboTime);
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", hits, dispTurns(comboTime));
		}

		private static final String TIME  = "combo_time";
		private static final String INITIAL_TIME  = "initial_time";
		public static String RECENT_HITS = "recent_hits";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(TIME, comboTime);
			bundle.put(INITIAL_TIME, initialTime);
			bundle.put(RECENT_HITS, hits);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			comboTime = bundle.getInt(TIME);
			initialTime = bundle.getInt(INITIAL_TIME);
			hits = bundle.getInt(RECENT_HITS);
		}
	}

}
