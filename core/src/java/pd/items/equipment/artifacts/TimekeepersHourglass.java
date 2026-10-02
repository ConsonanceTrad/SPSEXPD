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

import pd.atlas.items.GroundFunctionalFallingDict;
import pd.atlas.items.SpecificPlaceHolderDict;

import com.badlogic.gdx.Gdx;
import pd.Assets;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.Item;
import pd.items.specific.keys.Key;
import pd.journal.Catalog;
import pd.levels.GroundItems;
import pd.levels.traps.Trap;
import pd.messages.Messages;
import pd.plants.Plant;
import pd.plants.Rotberry;
import pd.scenes.GameScene;
import pd.scenes.InterlevelScene;
import pd.sprites.CharSprite;
import pd.sprites.ItemSprite;
import pd.utils.GLog;
import pd.windows.WndOptions;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.noosa.particles.Emitter;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class TimekeepersHourglass extends Artifact {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TimekeepersHourglass.class)
			.t("name", "时光沙漏")
			.t("ac_activate", "激活")
			.t("ac_restart", "耗竭-重置")
			.t("in_use", "你的沙漏正在使用中。")
			.t("deactivate", "你取消了时间冻结。")
			.t("no_charge", "你的沙漏充能还不足以用来激活。")
			.t("cursed", "你不能使用被诅咒的沙漏。")
			.t("onstasis", "你周遭的世界似乎就在这一瞬间变化了。")
			.t("onfreeze", "你周围的一切突然都彻底静止下来。")
			.t("stasis", "使我彻底停滞")
			.t("freeze", "冻结周围时间")
			.t("prompt", "你想怎样使用沙漏的魔法？\n\n当你被静止，周围的时间会正常流动，而你则会冻结并变得无敌。\n\n当时间被冻结，你的行动仿佛不需要任何时间。注意，进行攻击将打破该效果。")
			.t("desc", "这只大型的华贵沙漏看起来却并不怎么起眼，但你仍觉得它精雕细刻的框架内蕴含着某种强大的力量。在翻转沙漏、在看着沙子流下的同时，你能感受到一种魔法正在拉扯着你，使用这种魔法肯定能给你一些控制时间的方法。")
			.t("desc_hint", "沙漏似乎失去了一些沙子，如果你能再找到一些...")
			.t("desc_cursed", "被诅咒的沙漏把它自己锁在了你的身边，你可以感觉它试图操纵你的时间流动。")
			.t("timefreeze.name", "时间冻结")
			.t("timefreeze.desc", "外界的时间已被冻结，现在你可以在一瞬间完成任何行动。该状态会一直持续下去，除非你主动取消了它，或者你的沙漏用尽了充能。实施攻击或使用魔法同样会取消该效果。\n\n距离下次消耗充能还有：%s回合")
			.t("sandbag.name", "一包魔力流沙")
			.t("sandbag.levelup", "你将沙子填入到你的沙漏中。")
			.t("sandbag.maxlevel", "你的沙漏填满了魔法沙子！")
			.t("sandbag.no_hourglass", "你没有沙漏来存放这些沙子。")
			.t("sandbag.desc", "这一小包细沙应该能够在你的沙漏上完美使用。\n\n每次当你需要这种东西时，店主刚好都有摆上店面的存货，这相当奇怪...");
	}


	{
		image = SpecificPlaceHolderDict.SOMETHING_0;

		levelCap = 5;

		charge = 5+level();
		partialCharge = 0;
		chargeCap = 5+level();

		defaultAction = AC_ACTIVATE;
	}

	@Override
	public void resetForTrinity(int visibleLevel) {
		super.resetForTrinity(visibleLevel);
		charge = visibleLevel/2 - 1; //grants 4-10 turns of time freeze
	}

	public static final String AC_ACTIVATE = "ACTIVATE";
	public static final String AC_RESTART = "RESTART";

	//keeps track of generated sandbags.
	public int sandBags = 0;

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (isEquipped(hero) && charge > 0 && !cursed) actions.add(AC_ACTIVATE);
		if (!isEquipped(hero) && level() > 4 && !cursed) actions.add(AC_RESTART);
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute(hero, action);

		if (AC_ACTIVATE.equals(action)){

			if (!isEquipped( hero ))        GLog.i( Messages.get(Artifact.class, "need_to_equip") );
			else if (activeBuff != null)     GLog.i(Messages.get(this, "in_use"));
			else if (charge <= 1)            GLog.i( Messages.get(this, "no_charge") );
			else if (cursed)                GLog.i( Messages.get(this, "cursed") );
			else GameScene.show(
						new WndOptions(new ItemSprite(this),
								Messages.titleCase(name()),
								Messages.get(this, "prompt"),
								Messages.get(this, "stasis"),
								Messages.get(this, "freeze")) {
							@Override
							protected void onSelect(int index) {
								if (index == 0) {
									beginStasis(Dungeon.hero);
								} else if (index == 1) {
									beginFreeze(Dungeon.hero);
								}
							}
						}
				);
		} else if (AC_RESTART.equals(action)) restartFloor(hero);
	}

	protected void beginStasis(Hero hero) {
		if (hero == null || activeBuff != null || charge <= 1 || cursed) return;
		GLog.i(Messages.get(TimekeepersHourglass.class, "onstasis"));
		GameScene.flash(0xFFFFFF);
		if (Gdx.audio != null) Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
		activeBuff = new timeStasis();
		if (!activeBuff.attachTo(hero)) activeBuff = null;
	}

	protected void beginFreeze(Hero hero) {
		if (hero == null || activeBuff != null || charge <= 1 || cursed) return;
		GLog.i(Messages.get(TimekeepersHourglass.class, "onfreeze"));
		GameScene.flash(0xFFFFFF);
		if (Gdx.audio != null) Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
		activeBuff = new timeFreeze();
		if (!activeBuff.attachTo(hero)) activeBuff = null;
	}

	protected boolean restartFloor(Hero hero) {
		if (hero == null || level() <= 4 || isEquipped(hero) || cursed) return false;
		level(0);
		chargeCap = 5;
		charge = Math.min(charge, chargeCap);
		if (charge == chargeCap) partialCharge = 0;
		for (Item item : hero.belongings.backpack.items.toArray(new Item[0])) {
			if (item instanceof Key && ((Key)item).depth == Dungeon.depth) {
				item.detachAll(hero.belongings.backpack);
			}
		}
		InterlevelScene.returnDepth = Dungeon.depth;
		InterlevelScene.mode = InterlevelScene.Mode.RESET;
		switchToResetScene();
		return true;
	}

	protected void switchToResetScene() {
		Game.switchScene(InterlevelScene.class);
	}

	@Override
	public void activate(Char ch) {
		super.activate(ch);
		if (activeBuff != null)
			activeBuff.attachTo(ch);
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if (super.doUnequip(hero, collect, single)){
			if (activeBuff != null){
				activeBuff.detach();
				activeBuff = null;
			}
			return true;
		} else
			return false;
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new hourglassRecharge();
	}
	
	@Override
	public void charge(Hero target, float amount) {
		// SPS-PD 0.9.8 charges only through the equipped passive buff.
	}

	@Override
	public Item upgrade() {
		chargeCap+= 1;

		//for artifact transmutation.
		while (level()+1 > sandBags)
			sandBags ++;

		return super.upgrade();
	}

	@Override
	public String desc() {
		String desc = super.desc();

		if (isEquipped( Dungeon.hero )){
			if (!cursed) {
				if (level() < levelCap )
					desc += "\n\n" + Messages.get(this, "desc_hint");

			} else
				desc += "\n\n" + Messages.get(this, "desc_cursed");
		}
		return desc;
	}


	private static final String SANDBAGS =  "sandbags";
	private static final String BUFF =      "buff";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( SANDBAGS, sandBags );

		if (activeBuff != null)
			bundle.put( BUFF , activeBuff );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		int savedCharge = bundle.getInt("charge");
		super.restoreFromBundle(bundle);
		if (level() > levelCap) level(levelCap);
		chargeCap = 5 + level();
		charge = Math.max(0, Math.min(savedCharge, chargeCap));
		sandBags = bundle.getInt( SANDBAGS );

		//these buffs belong to hourglass, need to handle unbundling within the hourglass class.
		if (bundle.contains( BUFF )){
			Bundle buffBundle = bundle.getBundle( BUFF );

			if (buffBundle.contains(timeFreeze.PRESSES)
					|| buffBundle.contains(timeFreeze.PARTIALTIME)
					|| buffBundle.contains("turnsToCost"))
				activeBuff = new timeFreeze();
			else
				activeBuff = new timeStasis();

			activeBuff.restoreFromBundle(buffBundle);
		}
	}

	public class hourglassRecharge extends ArtifactBuff {
		@Override
		public boolean act() {

			if (charge < chargeCap && !cursed) {
				partialCharge += 1 / (60f - (chargeCap - charge) * 2f);
				if (partialCharge >= 1) {
					partialCharge--;
					charge++;
					if (charge == chargeCap) partialCharge = 0;
				}
			} else if (cursed && Random.Int(10) == 0)
				((Hero) target).spend( TICK );

			updateQuickslot();

			spend( TICK );

			return true;
		}

		@Override
		public boolean isCursed() {
			return cursed;
		}
	}

	public class timeStasis extends ArtifactBuff {

		{
			type = buffType.POSITIVE;
			actPriority = BUFF_PRIO-3;
		}

		@Override
		public boolean attachTo(Char target) {

			if (super.attachTo(target)) {
				spend(4f);
				((Hero)target).spendAndNext(4f);

				//shouldn't punish the player for going into stasis frequently
				Hunger hunger = target.buff(Hunger.class);
				if (hunger != null && !hunger.isStarving()) hunger.satisfy(4f);

				charge--;

				target.invisible++;

				updateQuickslot();

				if (Dungeon.hero != null) {
					Dungeon.observe();
				}

				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean act() {
			detach();
			return true;
		}

		@Override
		public void detach() {
			if (activeBuff != this || target == null) return;
			if (target.invisible > 0) target.invisible--;
			super.detach();
			activeBuff = null;
			if (Dungeon.hero != null) Dungeon.observe();
		}
	}

	public class timeFreeze extends ArtifactBuff {

		{
			type = buffType.POSITIVE;
		}

		float partialTime = 0f;

		ArrayList<Integer> presses = new ArrayList<>();

		public void processTime(float time){
			partialTime += time;
			while (partialTime >= 4f) {
				partialTime -= 4f;
				charge--;
			}

			updateQuickslot();

			if (charge <= 0) detach();

		}

		public void setDelayedPress(int cell){
			if (!presses.contains(cell))
				presses.add(cell);
		}

		public void triggerPresses(){
			if (presses.isEmpty() || Dungeon.level == null) {
				presses.clear();
				return;
			}
			ArrayList<Integer> toTrigger = presses;
			presses = new ArrayList<>();
			Actor.add(new Actor() {
				{
					actPriority = VFX_PRIO;
				}

				@Override
				protected boolean act() {
					for (int cell : toTrigger){
						Plant p = Dungeon.level.plants.get(cell);
						if (p != null){
							p.trigger();
						}
						Trap t = Dungeon.level.traps.get(cell);
						if (t != null){
							t.trigger();
						}
					}
					Actor.remove(this);
					return true;
				}
			});
		}

		public void disarmPresses(){
			if (Dungeon.level == null) {
				presses.clear();
				return;
			}
			for (int cell : presses){
				Plant p = Dungeon.level.plants.get(cell);
				if (p != null && !(p instanceof Rotberry)) {
					GroundItems.uproot( Dungeon.level, cell);
				}
				Trap t = Dungeon.level.traps.get(cell);
				if (t != null && t.disarmedByActivation) {
					t.disarm();
				}
			}

			presses = new ArrayList<>();
		}

		@Override
		public void detach(){
			if (activeBuff != this || target == null) return;
			Char frozenTarget = target;
			charge = Math.max(0, charge - 1);
			updateQuickslot();
			super.detach();
			activeBuff = null;
			triggerPresses();
			frozenTarget.next();
		}

		@Override
		public void fx(boolean on) {
			if (!(target instanceof Hero) || Dungeon.level == null) return;
			Emitter.freezeEmitters = on;
			if (on){
				for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
					if (mob.sprite != null) mob.sprite.add(CharSprite.State.PARALYSED);
				}
			} else {
				for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
					if (mob.paralysed <= 0 && mob.sprite != null) mob.sprite.remove(CharSprite.State.PARALYSED);
				}
			}
		}

		private static final String PRESSES = "presses";
		private static final String PARTIALTIME = "partialtime";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);

			int[] values = new int[presses.size()];
			for (int i = 0; i < values.length; i ++)
				values[i] = presses.get(i);
			bundle.put( PRESSES , values );

			bundle.put(PARTIALTIME, partialTime);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);

			if (bundle.contains(PRESSES)) {
				int[] values = bundle.getIntArray(PRESSES);
				if (values != null) {
					for (int value : values) presses.add(value);
				}
			}

			if (bundle.contains(PARTIALTIME)) {
				partialTime = bundle.getFloat(PARTIALTIME);
			} else if (bundle.contains("turnsToCost")) {
				float legacyTurns = bundle.getFloat("turnsToCost");
				partialTime = Math.max(0, (2f - legacyTurns) * 2f);
			}
		}
	}

	public static class sandBag extends Item {

		{
			image = GroundFunctionalFallingDict.SANDBAG_0;
		}

		@Override
		public boolean doPickUp(Hero hero, int pos) {
			Catalog.setSeen(getClass());
			Statistics.itemTypesDiscovered.add(getClass());
			TimekeepersHourglass hourglass = hero.belongings.getItem( TimekeepersHourglass.class );
			if (hourglass != null && !hourglass.cursed && hourglass.level() < hourglass.levelCap) {
				hourglass.upgrade();
				Catalog.countUses(hourglass.getClass(), 2);
				Sample.INSTANCE.play( Assets.Sounds.DEWDROP );
				if (hourglass.level() == hourglass.levelCap)
					GLog.p( Messages.get(this, "maxlevel") );
				else
					GLog.i( Messages.get(this, "levelup") );
				GameScene.pickUp(this, pos);
				hero.spendAndNext(pickupDelay());
				return true;
			} else {
				GLog.w( Messages.get(this, "no_hourglass") );
				return false;
			}
		}

		@Override
		public int value() {
			return 10;
		}

		@Override
		public boolean isUpgradable() {
			return false;
		}

		@Override
		public boolean isIdentified() {
			return true;
		}
	}


}
