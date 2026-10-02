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
 */

package pd.levels;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Awareness;
import pd.actors.hero.Talent;
import pd.actors.hero.spells.Stasis;
import pd.items.equipment.artifacts.TimekeepersHourglass;
import pd.items.consum.scrolls.exotic.ScrollOfChallenge;
import pd.levels.features.LevelTransition;
import pd.messages.Messages;
import pd.plants.Swiftthistle;
import pd.utils.GLog;

/**
 * 关卡转场：按类型/按格子查找转场点，以及转场前的收尾。
 *
 * 从 Level 中独立出来：两者都不参与任何子类覆写，是纯查询与纯收尾逻辑。
 * 因此按「零覆写就真搬走」的原则，Level 上不留转发壳，约 30 处调用点直接改到本类。
 *
 * beforeTransition 会把时间冻结/时间泡挂起的踩踏先结算、摘掉不跨层的 buff
 * （铁胃、挑战竞技场、awareness），并让英雄与跟在其前面的 actor 把零碎回合走完，
 * 以免出现跨层半回合。
 */
public final class Transitions {

	private Transitions() { }
	public static LevelTransition get( Level level, LevelTransition.Type type ){
		if (level.transitions.isEmpty()){
			return null;
		}
		for (LevelTransition transition : level.transitions){
			//if we don't specify a type, prefer to return any entrance
			if (type == null &&
					(transition.type == LevelTransition.Type.REGULAR_ENTRANCE
							|| transition.type == LevelTransition.Type.BRANCH_ENTRANCE
							|| transition.type == LevelTransition.Type.SURFACE)){
				return transition;
			} else if (transition.type == type){
				return transition;
			}
		}
		return type != null ? get( level, null ) : level.transitions.get(0);
	}

	public static LevelTransition get( Level level, int cell ){
		for (LevelTransition transition : level.transitions){
			if (transition.inside(cell)){
				return transition;
			}
		}
		return null;
	}

	public static void beforeTransition(){

		//time freeze effects need to resolve their pressed cells before transitioning
		TimekeepersHourglass.timeFreeze timeFreeze = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
		if (timeFreeze != null) timeFreeze.disarmPresses();
		Swiftthistle.TimeBubble timeBubble = Dungeon.hero.buff(Swiftthistle.TimeBubble.class);
		if (timeBubble != null) timeBubble.disarmPresses();

		//iron stomach and challenge arena do not persist between floors
		Talent.WarriorFoodImmunity foodImmune = Dungeon.hero.buff(Talent.WarriorFoodImmunity.class);
		if (foodImmune != null) foodImmune.detach();
		ScrollOfChallenge.ChallengeArena arena = Dungeon.hero.buff(ScrollOfChallenge.ChallengeArena.class);
		if (arena != null) arena.detach();
		//awareness also doesn't, honestly it's weird that it's a buff
		Awareness awareness = Dungeon.hero.buff(Awareness.class);
		if (awareness != null) awareness.detach();

		Char ally = Stasis.getStasisAlly();
		if (Char.hasProp(ally, Char.Property.IMMOVABLE)){
			Dungeon.hero.buff(Stasis.StasisBuff.class).act();
			GLog.w(Messages.get(Stasis.StasisBuff.class, "left_behind"));
		}

		//spend the hero's partial turns,  so the hero cannot take partial turns between floors
		Dungeon.hero.spendToWhole();
		for (Actor a : Actor.all()){
			//also adjust any other actors that are now ahead of the hero due to this
			if (a.cooldown() < Dungeon.hero.cooldown()){
				a.spendToWhole();
			}
		}
	}
}
