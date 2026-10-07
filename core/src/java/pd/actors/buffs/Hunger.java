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

package pd.actors.buffs;

import pd.Badges;
import pd.Challenges;
import pd.Dungeon;
import pd.SPDSettings;
import pd.actors.hero.Hero;
import pd.items.consum.scrolls.exotic.ScrollOfChallenge;
import pd.items.equipment.trinkets.SaltCube;
import pd.journal.Document;
import pd.levels.VaultLevel;
import pd.messages.Messages;
import render.utils.math.Random;
import pd.scenes.GameScene;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class Hunger extends Buff implements Hero.Doom {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Hunger.class)
			.t("hungry", "饥饿")
			.t("starving", "极度饥饿")
			.t("onhungry", "你有点饿了。")
			.t("onstarving", "你已经饥肠辘辘！")
			.t("ondeath", "你活活饿死了...")
			.t("cursedhorn", "就在你吃东西的时候被诅咒的号角偷走了一部分食物的能量。")
			.t("rankings_desc", "饥饿致死")
			.t("desc_intro_hungry", "你能感受到自己的肚子在不断寻求食物，不过还不算那么严重。")
			.t("desc_intro_starving", "你的饥饿程度已经危及生命了。")
			.t("desc", "\n\n饥饿会在你在地牢里花费时间的同时缓慢累计，直到你饿得难以忍受。在你极度饥饿时生命值会停止回复并且开始缓慢减少。\n\n合理利用食物非常重要！如果你有足够的生命值来维持饥饿，你就该等到一会儿食物更多的时候再吃。正确的配给可以让食物更有效地发挥作用！");
	}




	//SPSEXPD: 饥饿体系整体翻倍（阈值 300/450 → 600/900，每回合消耗 1 → 2），
	//使同一份食物相对更不耐饿；物品的饱食度恢复量改为固定绝对值，不随常量变化。
	public static final float HUNGRY	= 600f;
	public static final float STARVING	= 900f;

	private float level;
	private float partialDamage;
	//SPSEXPD: 饥饿掉血的回合计数（每 3~6 回合扣 1 点）
	private int starvingTimer = Random.IntRange( 3, 6 );

	private static final String LEVEL			= "level";
	private static final String PARTIALDAMAGE 	= "partialDamage";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( LEVEL, level );
		bundle.put( PARTIALDAMAGE, partialDamage );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		level = bundle.getFloat( LEVEL );
		partialDamage = bundle.getFloat(PARTIALDAMAGE);
	}

	@Override
	public boolean act() {

		if (Dungeon.level.locked
				|| target.buff(WellFed.class) != null
				|| SPDSettings.intro()
				|| target.buff(ScrollOfChallenge.ChallengeArena.class) != null){
			spend(TICK);
			return true;
		}

		if (target.isAlive() && target instanceof Hero) {

			Hero hero = (Hero)target;

			if (isStarving()) {

				//SPSEXPD: 饥饿掉血改为「每 3~6 回合扣 1 点」，不再随最大生命值几乎每回合掉血
				if (--starvingTimer <= 0) {
					target.damage( 1, this );
					starvingTimer = Random.IntRange( 3, 6 );
				}

			} else {

				float hungerDelay = 1f;
				if (target.buff(Shadows.class) != null){
					hungerDelay *= 1.5f;
				}
				hungerDelay /= SaltCube.hungerGainMultiplier();

				//SPSEXPD: 饥饿消耗基础速度翻倍（原 1f/hungerDelay）
				float newLevel = level + (2f/hungerDelay);
				if (newLevel >= cap()) {

					GLog.n( Messages.get(this, "onstarving") );
					hero.damage( 1, this );

					hero.interrupt();
					newLevel = cap();

				} else if (newLevel >= hungryCap() && level < hungryCap()) {

					GLog.w( Messages.get(this, "onhungry") );

					if (!Document.ADVENTURERS_GUIDE.isPageRead(Document.GUIDE_FOOD)){
						GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_FOOD);
					}

				}
				level = newLevel;

			}
			
			spend( TICK );

		} else {

			diactivate();

		}

		return true;
	}

	public void satisfy( float energy ) {
		if (energy > 0 && Dungeon.isChallenged(Challenges.ENERGY_LOST)) {
			energy = Math.round(energy * 0.4f);
		}
		affectHunger( energy, false );
	}

	public void affectHunger(float energy ){
		affectHunger( energy, false );
	}

	public void affectHunger(float energy, boolean overrideLimits ) {

		if (energy < 0 && target.buff(WellFed.class) != null){
			target.buff(WellFed.class).left += energy;
			BuffIndicator.refreshHero();
			return;
		}

		float oldLevel = level;

		level -= energy;
		if (level < 0 && !overrideLimits) {
			level = 0;
		} else if (level > cap()) {
			float excess = level - cap();
			level = cap();
			partialDamage += excess * (target.HT/1000f);
			if (partialDamage > 1f){
				target.damage( (int)partialDamage, this );
				partialDamage -= (int)partialDamage;
			}
		}

		if (oldLevel < hungryCap() && level >= hungryCap()){
			GLog.w( Messages.get(this, "onhungry") );
		} else if (oldLevel < cap() && level >= cap()){
			GLog.n( Messages.get(this, "onstarving") );
			target.damage( 1, this );
		}

		BuffIndicator.refreshHero();
	}

	//SPSEXPD: 特质可提高饥饿上限（如「坚忍肠胃」）
	private float extraCap() {
		return (target instanceof Hero) ? pd.actors.hero.perks.HardenedStomach.capBonus( (Hero)target ) : 0f;
	}

	private float cap() {
		return STARVING + extraCap();
	}

	private float hungryCap() {
		return HUNGRY + extraCap();
	}

	//SPSEXPD: 特质对饥饿累积速率的影响（节食 <1 更耐饿；暴食 >1 饿得更快）
	public static float traitRateFactor( Hero hero ) {
		float f = 1f;
		if (hero != null && hero.heroPerk != null) {
			pd.actors.hero.perks.Dieting d = hero.heroPerk.get( pd.actors.hero.perks.Dieting.class );
			if (d != null) f *= d.hungerMultiplier();
			pd.actors.hero.perks.RavenousAppetite r = hero.heroPerk.get( pd.actors.hero.perks.RavenousAppetite.class );
			if (r != null) f *= r.hungerMultiplier();
		}
		return Math.max( 0.1f, f );
	}

	public boolean isStarving() {
		return level >= cap();
	}

	public int hunger() {
		return (int)Math.ceil(level);
	}

	@Override
	public int icon() {
		if (level < hungryCap()) {
			return BuffIndicator.NONE;
		} else if (level < cap()) {
			return BuffIndicator.HUNGER;
		} else {
			return BuffIndicator.STARVATION;
		}
	}

	@Override
	public String name() {
		if (level < cap()) {
			return Messages.get(this, "hungry");
		} else {
			return Messages.get(this, "starving");
		}
	}

	@Override
	public String desc() {
		String result;
		if (level < cap()) {
			result = Messages.get(this, "desc_intro_hungry");
		} else {
			result = Messages.get(this, "desc_intro_starving");
		}

		result += Messages.get(this, "desc");

		return result;
	}

	@Override
	public void onDeath() {

		Badges.validateDeathFromHunger();

		Dungeon.fail( this );
		GLog.n( Messages.get(this, "ondeath") );
	}
}
