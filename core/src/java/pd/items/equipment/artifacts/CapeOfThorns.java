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

package pd.items.equipment.artifacts;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Needling;
import pd.actors.buffs.ShieldArmor;
import pd.actors.hero.Hero;
import pd.effects.particles.ElmoParticle;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

public class CapeOfThorns extends Artifact {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CapeOfThorns.class)
			.t("name", "荆棘斗篷")
			.t("ac_needling", "耗竭-激发")
			.t("desc", "矮人实验室里的研究反射魔法的副产物之一，这件由奇怪的金属片构成的坚硬斗篷能够从敌人的攻击中摄取能量，并将其反馈给攻击者。同时也会提升发现种子的几率")
			.t("desc_inactive", "斗篷令人安心的沉重压在你的肩上，它似乎能从你受的伤里获得能量。")
			.t("desc_active", "斗篷似乎在释放其存储的能量，并将其辐射出一种防护力场。")
			.t("$thorns.inert", "你的斗篷再次失效了。")
			.t("$thorns.radiating", "你的斗篷正在释放存储的能量，你感到自己正在被保护着！")
			.t("$thorns.levelup", "你的斗篷变得更强大了！")
			.t("$thorns.name", "荆棘")
			.t("$thorns.desc", "你的斗篷在你周围辐射能量，产生了一个偏斜力场！\n\n该效果下你受到的所有伤害都会被减少。此外，如果攻击者就在你旁边，被减少的伤害会反弹给攻击者。\n\n荆棘效果持续时间：%s回合");
	}




	public static final String AC_NEEDLING = "NEEDLING";

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;

		levelCap = 10;

		charge = 0;
		chargeCap = 100;
		cooldown = 0;

		defaultAction = AC_NEEDLING;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && level() > 1 && !cursed) actions.add(AC_NEEDLING);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_NEEDLING.equals(action)) {
			super.execute(hero, action);
			return;
		}

		if (!isEquipped(hero)) {
			GLog.i(Messages.get(Artifact.class, "need_to_equip"));
		} else if (cursed) {
			GLog.i(Messages.get(Artifact.class, "cursed"));
		} else if (level() > 1) {
			int duration = level() * 10;
			level(level() - 1);
			Sample.INSTANCE.play(Assets.Sounds.BURNING);
			if (hero.sprite != null) {
				hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
				hero.sprite.operate(hero.pos);
			}
			Buff.affect(hero, Needling.class, duration);
			hero.spend(1f);
			hero.busy();
			updateQuickslot();
		} else {
			GLog.i(Messages.get(Artifact.class, "cursed"));
		}
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new Thorns();
	}
	
	@Override
	public String desc() {
		String desc = Messages.get(this, "desc");
		if (isEquipped( Dungeon.hero )) {
			desc += "\n\n";
			if (cooldown == 0)
				desc += Messages.get(this, "desc_inactive");
			else
				desc += Messages.get(this, "desc_active");
		}

		return desc;
	}

	public class Thorns extends ArtifactBuff{

		@Override
		public boolean act(){
			if (cooldown > 0) {
				cooldown--;
				if (cooldown == 0) {
					BuffIndicator.refreshHero();
					GLog.w( Messages.get(this, "inert") );
				}
				updateQuickslot();
			}
			spend(TICK);
			return true;
		}

		public int proc(int damage, Char attacker, Char defender){
			if (cooldown == 0){
				charge += damage*(0.7+level()*0.1);
				if (charge >= chargeCap){
					charge = 0;
					cooldown = 10+level();
					GLog.p( Messages.get(this, "radiating") );
					Char shieldTarget = defender != null ? defender : target;
					if (shieldTarget != null) {
						Buff.affect(shieldTarget, ShieldArmor.class).level(level()*10);
					}
					BuffIndicator.refreshHero();
				}
			}

			if (cooldown != 0){
				int deflected = Random.NormalIntRange(0, damage);
				damage -= deflected;

				if (attacker != null) attacker.damage(deflected, this);

				exp+= deflected;

				if (exp >= (level()+1)*5 && level() < levelCap){
					exp -= (level()+1)*5;
					upgrade();
					Catalog.countUse(CapeOfThorns.class);
					GLog.p( Messages.get(this, "levelup") );
				}

			}
			updateQuickslot();
			return damage;
		}

		@Override
		public String toString() {
			return Messages.get(this, "name");
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", dispTurns(cooldown));
		}

		@Override
		public int icon() {
			if (cooldown == 0)
				return BuffIndicator.NONE;
			else
				return BuffIndicator.THORNS;
		}

		@Override
		public void detach(){
			cooldown = 0;
			charge = 0;
			super.detach();
		}

	}


}
