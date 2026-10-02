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

package pd.actors.hero.abilities.mage;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FlavourBuff;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.ArmorAbility;
import pd.items.Item;
import pd.items.equipment.armor.ClassArmor;
import pd.items.equipment.trinkets.WondrousResin;
import pd.items.equipment.wands.CursedWand;
import pd.items.equipment.wands.Wand;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.ui.HeroIcon;
import pd.utils.GLog;
import render.noosa.Game;
import render.noosa.tweeners.Delayer;
import render.utils.data.Callback;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

public class WildMagic extends ArmorAbility {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(WildMagic.class)
			.t("name", "狂野魔法")
			.t("no_wands", "你没有法杖可供施法！")
			.t("short_desc", "法师引导出法杖中的_狂野魔法_，在单回合内对指定目标随机释放这些法杖多次。")
			.t("desc", "法师引导出法杖的魔力，在一回合内随机释放法杖至多4次。该能力不会释放法师魔杖。\n\n以这个能力使用法杖时，法杖视为上升2级，最高可至+3。用该能力释放法术只消耗一半的充能，每根法杖最多随机释放两次。");
	}


	{
		baseChargeUse = 25f;
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		if (target == null){
			return;
		}

		if (target == hero.pos){
			GLog.w(Messages.get(this, "self_target"));
			return;
		}

		ArrayList<Wand> wands = hero.belongings.getAllItems(Wand.class);
		Random.shuffle(wands);

		float chargeUsePerShot = 0.5f * (float)Math.pow(0.67f, hero.pointsInTalent(Talent.CONSERVED_MAGIC));

		for (Wand w : wands.toArray(new Wand[0])){
			if (w.curCharges < 1 && w.partialCharge < chargeUsePerShot){
				wands.remove(w);
			}
		}

		int maxWands = 4 + Dungeon.hero.pointsInTalent(Talent.FIRE_EVERYTHING);

		//second and third shots
		if (wands.size() < maxWands){
			ArrayList<Wand> seconds = new ArrayList<>(wands);
			ArrayList<Wand> thirds = new ArrayList<>(wands);

			for (Wand w : wands){
				float totalCharge = w.curCharges + w.partialCharge;
				if (totalCharge < 2*chargeUsePerShot){
					seconds.remove(w);
				}
				if (totalCharge < 3*chargeUsePerShot
					|| Random.Int(4) >= Dungeon.hero.pointsInTalent(Talent.FIRE_EVERYTHING)){
					thirds.remove(w);
				}
			}

			Random.shuffle(seconds);
			while (!seconds.isEmpty() && wands.size() < maxWands){
				wands.add(seconds.remove(0));
			}

			Random.shuffle(thirds);
			while (!thirds.isEmpty() && wands.size() < maxWands){
				wands.add(thirds.remove(0));
			}
		} else {
			while (wands.size() > maxWands){
				wands.remove(0);
			}
		}

		if (wands.size() == 0){
			GLog.w(Messages.get(this, "no_wands"));
			return;
		}

		hero.busy();

		Random.shuffle(wands);

		Buff.affect(hero, WildMagicTracker.class, 0f);

		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();

		zapWand(wands, hero, target);

	}

	public static class WildMagicTracker extends FlavourBuff{};

	Actor wildMagicActor = null;

	private void zapWand( ArrayList<Wand> wands, Hero hero, int cell){
		Wand cur = wands.remove(0);

		Ballistica aim = new Ballistica(hero.pos, cell, cur.collisionProperties(cell));

		hero.sprite.zap(cell);

		float startTime = Game.timeTotal;
		if (cur.tryToZap(hero, cell)) {
			if (!cur.cursed) {
				cur.fx(aim, new Callback() {
					@Override
					public void call() {
						cur.onZap(aim);
						boolean alsoCursedZap = Random.Float() < WondrousResin.extraCurseEffectChance();
						if (Game.timeTotal - startTime < 0.33f) {
							hero.sprite.parent.add(new Delayer(0.33f - (Game.timeTotal - startTime)) {
								@Override
								protected void onComplete() {
									if (alsoCursedZap){
										WondrousResin.forcePositive = true;
										CursedWand.cursedZap(cur,
												hero,
												new Ballistica(hero.pos, cell, Ballistica.MAGIC_BOLT),
												new Callback() {
													@Override
													public void call() {
														WondrousResin.forcePositive = false;
														afterZap(cur, wands, hero, cell);
													}
												});
									} else {
										afterZap(cur, wands, hero, cell);
									}
								}
							});
						} else {
							if (alsoCursedZap){
								WondrousResin.forcePositive = true;
								CursedWand.cursedZap(cur,
										hero,
										new Ballistica(hero.pos, cell, Ballistica.MAGIC_BOLT),
										new Callback() {
											@Override
											public void call() {
												WondrousResin.forcePositive = false;
												afterZap(cur, wands, hero, cell);
											}
										});
							} else {
								afterZap(cur, wands, hero, cell);
							}
						}
					}
				});

			} else {
				CursedWand.cursedZap(cur,
						hero,
						new Ballistica(hero.pos, cell, Ballistica.MAGIC_BOLT),
						new Callback() {
							@Override
							public void call() {
								if (Game.timeTotal - startTime < 0.33f) {
									hero.sprite.parent.add(new Delayer(0.33f - (Game.timeTotal - startTime)) {
										@Override
										protected void onComplete() {
											afterZap(cur, wands, hero, cell);
										}
									});
								} else {
									afterZap(cur, wands, hero, cell);
								}
							}
						});
			}
		} else {
			afterZap(cur, wands, hero, cell);
		}
	}

	private void afterZap( Wand cur, ArrayList<Wand> wands, Hero hero, int target){
		cur.partialCharge -= 0.5f * (float)Math.pow(0.67f, hero.pointsInTalent(Talent.CONSERVED_MAGIC));
		if (cur.partialCharge < 0) {
			cur.partialCharge++;
			cur.curCharges--;
		}
		if (wildMagicActor != null){
			wildMagicActor.next();
			wildMagicActor = null;
		}

		Char ch = Actor.findChar(target);
		if (!wands.isEmpty() && hero.isAlive()) {
			Actor.add(new Actor() {
				{
					actPriority = VFX_PRIO-1;
				}

				@Override
				protected boolean act() {
					wildMagicActor = this;
					zapWand(wands, hero, ch == null ? target : ch.pos);
					Actor.remove(this);
					return false;
				}
			});
			hero.next();
		} else {
			if (hero.buff(WildMagicTracker.class) != null) {
				hero.buff(WildMagicTracker.class).detach();
			}
			Item.updateQuickslot();
			Invisibility.dispel();
			if (Random.Int(4) >= hero.pointsInTalent(Talent.CONSERVED_MAGIC)) {
				hero.spendAndNext(Actor.TICK);
			} else {
				hero.next();
			}
		}
	}

	@Override
	public int icon() {
		return HeroIcon.WILD_MAGIC;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.WILD_POWER, Talent.FIRE_EVERYTHING, Talent.CONSERVED_MAGIC, Talent.HEROIC_ENERGY};
	}
}
