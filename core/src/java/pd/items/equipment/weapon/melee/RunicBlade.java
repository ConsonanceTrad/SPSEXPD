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

import pd.atlas.items.SpecificPlaceHolderDict;

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

public class RunicBlade extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RunicBlade.class)
			.t("name", "符文之刃")
			.t("stats_desc", "这件武器从升级中获得更多伤害。")
			.t("ability_name", "符文挥砍")
			.t("typical_ability_desc", "决斗家可以用符文之刃使出_符文挥砍_。这一击必定命中且通常附带_%d%%的附魔强化_。")
			.t("ability_desc", "决斗家可以用符文之刃使出_符文挥砍_。这一击必定命中且有_%d%%的附魔强化_。")
			.t("upgrade_ability_stat_name", "武技强化")
			.t("desc", "来自失落之地的神秘武器，有着明亮的蓝色刀刃。");
	}


	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1f;

		tier = 4;
	}

	//Essentially it's a tier 4 weapon, with tier 3 base max damage, and tier 5 scaling.
	//equal to tier 4 in damage at +5

	@Override
	public int max(int lvl) {
		return  5*(tier) +                	//20 base, down from 25
				Math.round(lvl*(tier+2));	//+6 per level, up from +5
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		if (target == null) {
			return;
		}

		Char enemy = Actor.findChar(target);
		if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
			GLog.w(Messages.get(this, "ability_no_target"));
			return;
		}

		//we apply here because of projecting
		RunicSlashTracker tracker = Buff.affect(hero, RunicSlashTracker.class);
		tracker.boost = 3f + 0.50f*buffedLvl();
		hero.belongings.abilityWeapon = this;
		if (!hero.canAttack(enemy)){
			GLog.w(Messages.get(this, "ability_target_range"));
			tracker.detach();
			hero.belongings.abilityWeapon = null;
			return;
		}
		hero.belongings.abilityWeapon = null;

		hero.sprite.attack(enemy.pos, new Callback() {
			@Override
			public void call() {
				beforeAbilityUsed(hero, enemy);
				AttackIndicator.target(enemy);
				if (hero.attack(enemy, 1f, 0, Char.INFINITE_ACCURACY)){
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
					if (!enemy.isAlive()){
						onAbilityKill(hero, enemy);
					}
				}
				tracker.detach();
				Invisibility.dispel();
				hero.spendAndNext(hero.attackDelay());
				afterAbilityUsed(hero);
			}
		});
	}

	@Override
	public String abilityInfo() {
		if (levelKnown){
			return Messages.get(this, "ability_desc", 300+50*buffedLvl());
		} else {
			return Messages.get(this, "typical_ability_desc", 300);
		}
	}

	@Override
	public String upgradeAbilityStat(int level) {
		return "+" + (300+50*level) + "%";
	}


	public static class RunicSlashTracker extends FlavourBuff{

		public float boost = 2f;

	};

}
