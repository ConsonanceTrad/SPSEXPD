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

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Badges;
import pd.Challenges;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.effects.SpellSprite;
import pd.items.Item;
import pd.items.equipment.artifacts.Artifact;
import pd.items.equipment.artifacts.HornOfPlenty;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.utils.GLog;
import render.noosa.audio.Sample;

import java.util.ArrayList;
import pd.messages.InlineText;
import pd.atlas.items.ConsumFoodFoodDict;

public class Food extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Food.class)
			.t("name", "口粮")
			.t("ac_eat", "食用")
			.t("eat_msg", "吃起来不错！")
			.t("locked", "锁闭魔法阻止了你进食。")
			.t("desc", "里面都是些寻常玩意：一片肉干，几块饼干——诸如此类。");
	}




	public static final float TIME_TO_EAT	= 3f;
	
	public static final String AC_EAT	= "EAT";
	
	//SPSEXPD: 固定绝对值（原 Hunger.HUNGRY=300），不随饥饿体系翻倍而变化
	public float energy = 300f;
	public int hornValue = 3;
	//SPSEXPD: 血源风烹饪——标记该食物可投入平底煎锅烹制（默认不可）
	public boolean canBeCook = false;
	
	{
		stackable = true;
		image = ConsumFoodFoodDict.RATION_PACK;

		defaultAction = AC_EAT;

		bones = true;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_EAT );
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_EAT )) {
			
			detach( hero.belongings.backpack );
			Catalog.countUse(getClass());
			
			satisfy(hero);
			GLog.i( Messages.get(this, "eat_msg") );
			
			hero.sprite.operate( hero.pos );
			hero.busy();
			SpellSprite.show( hero, SpellSprite.FOOD );
			eatSFX();
			
			hero.spend( eatingTime() );

			Talent.onFoodEaten(hero, energy, this);
			
			Statistics.foodEaten++;
			Badges.validateFoodEaten();
			
		}
	}

	protected void eatSFX(){
		Sample.INSTANCE.play( Assets.Sounds.EAT );
	}
	
	/**
	 * SPSEXPD: 与食用（{@link #AC_EAT}）效果相同的进食——扣物品、天赋、统计、音效与进食特效都照常，
	 * 但不消耗回合，也不占用英雄的 busy 动画。供号角「极度饥饿时自动喂食」使用。
	 */
	public void eatQuietly( Hero hero ) {
		if (hero == null || hero.belongings == null) return;
		
		detach( hero.belongings.backpack );
		Catalog.countUse(getClass());
		
		satisfy(hero);
		GLog.i( Messages.get(this, "eat_msg") );
		
		//SPSEXPD: 无头校验等场景下 sprite 可能为 null（SpellSprite.show 会直接读 ch.sprite）
		if (hero.sprite != null) {
			SpellSprite.show( hero, SpellSprite.FOOD );
		}
		eatSFX();
		
		Talent.onFoodEaten(hero, energy, this);
		
		Statistics.foodEaten++;
		Badges.validateFoodEaten();
	}
	
	protected float eatingTime(){
		if (Dungeon.hero.hasTalent(Talent.IRON_STOMACH)
			|| Dungeon.hero.hasTalent(Talent.ENERGIZING_MEAL)
			|| Dungeon.hero.hasTalent(Talent.MYSTICAL_MEAL)
			|| Dungeon.hero.hasTalent(Talent.INVIGORATING_MEAL)
			|| Dungeon.hero.hasTalent(Talent.FOCUSED_MEAL)
			|| Dungeon.hero.hasTalent(Talent.ENLIGHTENING_MEAL)){
			return TIME_TO_EAT - 2;
		} else {
			return TIME_TO_EAT;
		}
	}
	
	/** SPSEXPD: 本次进食的基础能量值（子类可覆写以反映动态值，例如农历年的肉馅饼）。 */
	protected float baseEnergy( Hero hero ) {
		return energy;
	}

	/**
	 * SPSEXPD: 一次进食的能量值——含「无食物」挑战（÷3）与诅咒号角的修正，
	 * 不含能量流失挑战（那一层由 Hunger.applyEnergyModifiers 处理）。
	 */
	public float foodValue( Hero hero ) {
		float foodVal = baseEnergy(hero);
		if (Dungeon.isChallenged(Challenges.NO_FOOD)){
			foodVal /= 3f;
		}

		if (hero != null) {
			Artifact.ArtifactBuff buff = hero.buff( HornOfPlenty.hornRecharge.class );
			if (buff != null && buff.isCursed()){
				foodVal *= 0.67f;
			}
		}
		return foodVal;
	}

	/**
	 * SPSEXPD: 「食用」动作按钮右上角的角标——实际能回复的饱食度（能补满显示 MAX）。
	 * 已考虑无食物/能量流失挑战、诅咒号角与当前饥饿值。
	 */
	@Override
	public String actionCost( String action, Hero hero ) {
		if (!AC_EAT.equals(action) || hero == null) return super.actionCost(action, hero);
		float effective = Hunger.applyEnergyModifiers(foodValue(hero));
		return Hunger.eatBadge(hero, effective);
	}

	protected void satisfy( Hero hero ){
		Artifact.ArtifactBuff buff = hero.buff( HornOfPlenty.hornRecharge.class );
		if (buff != null && buff.isCursed()){
			GLog.n( Messages.get(Hunger.class, "cursedhorn") );
		}

		Buff.affect(hero, Hunger.class).satisfy(foodValue(hero));
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public int value() {
		return 10 * quantity;
	}
}
