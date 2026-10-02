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

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.ArmorAbility;
import pd.effects.CellEmitter;
import pd.effects.MagicMissile;
import pd.effects.Pushing;
import pd.items.equipment.armor.ClassArmor;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.levels.Level;
import pd.levels.Transitions;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.InterlevelScene;
import pd.ui.AttackIndicator;
import pd.ui.HeroIcon;
import pd.utils.GLog;
import pd.windows.WndOptions;
import render.noosa.Game;
import render.noosa.Image;
import render.noosa.audio.Sample;
import render.noosa.particles.Emitter;
import render.utils.data.BArray;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class WarpBeacon extends ArmorAbility {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(WarpBeacon.class)
			.t("name", "空间信标")
			.t("depths", "你无法传送到其它楼层！")
			.t("locked_floor", "楼层被封锁了，你无法离开！")
			.t("too_far", "这个位置太远了！")
			.t("invalid_beacon", "你不能将信标放在那里！")
			.t("window_desc", "你的信标目前设置在了第%d层。")
			.t("window_tele", "返回信标位置")
			.t("window_clear", "移除信标")
			.t("window_cancel", "取消")
			.t("short_desc", "法师在当前位置设置了一个_空间信标_，他能瞬间传送回信标所在位置。")
			.t("desc", "法师设置了一个可以随时返回的信标。信标设置需要1回合，但传送回不消耗时间。\n\n法师在初始条件下无法传送至别的楼层，不能将其放入不可达的区域如上锁的房间。法师返回时若是目标地点有敌人，则会将其弹开。");
	}




	{
		baseChargeUse = 35f;
	}

	@Override
	public String targetingPrompt() {
		if (Dungeon.hero.buff(WarpBeaconTracker.class) == null
				&& Dungeon.hero.hasTalent(Talent.REMOTE_BEACON)){
			return Messages.get(this, "prompt");
		}
		return super.targetingPrompt();
	}

	@Override
	public int targetedPos(Char user, int dst) {
		return dst;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		if (target == null){
			return;
		}

		if (hero.buff(WarpBeaconTracker.class) != null){
			final WarpBeaconTracker tracker = hero.buff(WarpBeaconTracker.class);

			GameScene.show( new WndOptions(
					new Image(hero.sprite),
					Messages.titleCase(name()),
					Messages.get(WarpBeacon.class, "window_desc", tracker.depth),
					Messages.get(WarpBeacon.class, "window_tele"),
					Messages.get(WarpBeacon.class, "window_clear"),
					Messages.get(WarpBeacon.class, "window_cancel")){

				@Override
				protected void onSelect(int index) {
					if (index == 0){

						if (tracker.depth != Dungeon.depth && !hero.hasTalent(Talent.LONGRANGE_WARP)){
							GLog.w( Messages.get(WarpBeacon.class, "depths") );
							return;
						}

						float chargeNeeded = chargeUse(hero);

						if (tracker.depth != Dungeon.depth){
							chargeNeeded *= 1.833f - 0.333f*Dungeon.hero.pointsInTalent(Talent.LONGRANGE_WARP);
						}

						if (armor.charge < chargeNeeded){
							GLog.w( Messages.get(ClassArmor.class, "low_charge") );
							return;
						}

						armor.charge -= chargeNeeded;
						armor.updateQuickslot();

						if (tracker.depth == Dungeon.depth && tracker.branch == Dungeon.branch){
							Char existing = Actor.findChar(tracker.pos);

							if (existing != null && existing != hero){
								if (hero.hasTalent(Talent.TELEFRAG)){
									int heroHP = hero.HP + hero.shielding();
									int heroDmg = 5 * hero.pointsInTalent(Talent.TELEFRAG);
									hero.damage(Math.min(heroDmg, heroHP-1), WarpBeacon.this);

									int damage = Hero.heroDamageIntRange(10*hero.pointsInTalent(Talent.TELEFRAG), 15*hero.pointsInTalent(Talent.TELEFRAG));
									existing.sprite.flash();
									existing.sprite.bloodBurstA(existing.sprite.center(), damage);
									existing.damage(damage, WarpBeacon.this);

									Sample.INSTANCE.play(Assets.Sounds.HIT_CRUSH);
									Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);

									//handle rare cases where damage causes char swapping or movement
									if (Actor.findChar(existing.pos) != existing){
										existing = Actor.findChar(existing.pos);
									}
								}

								if (existing != null && existing.isAlive() && existing.pos == tracker.pos){
									Char toPush = Char.hasProp(existing, Char.Property.IMMOVABLE) ? hero : existing;

									ArrayList<Integer> candidates = new ArrayList<>();
									for (int n : PathFinder.NEIGHBOURS8) {
										int cell = tracker.pos + n;
										if (!Dungeon.level.solid[cell] && Actor.findChar( cell ) == null
												&& (!Char.hasProp(toPush, Char.Property.LARGE) || Dungeon.level.openSpace[cell])) {
											candidates.add( cell );
										}
									}
									Random.shuffle(candidates);

									if (!candidates.isEmpty()){
										ScrollOfTeleportation.appear(hero, tracker.pos);
										Actor.add( new Pushing( toPush, toPush.pos, candidates.get(0) ));

										toPush.pos = candidates.get(0);
										Dungeon.level.occupyCell(toPush);
										hero.next();
									} else {
										GLog.w( Messages.get(ScrollOfTeleportation.class, "no_tele") );
									}
								} else {
									ScrollOfTeleportation.appear(hero, tracker.pos);
								}
							} else {
								ScrollOfTeleportation.appear(hero, tracker.pos);
							}

							Invisibility.dispel();
							Dungeon.observe();
							GameScene.updateFog();
							hero.checkVisibleMobs();
							AttackIndicator.updateState();

						} else {

							if (!Dungeon.interfloorTeleportAllowed()){
								GLog.w( Messages.get(ScrollOfTeleportation.class, "no_tele") );
								return;
							}

							//transition before dispel, to cancel out trap effects
							Transitions.beforeTransition();
							Invisibility.dispel();
							InterlevelScene.mode = InterlevelScene.Mode.RETURN;
							InterlevelScene.returnDepth = tracker.depth;
							InterlevelScene.returnBranch = tracker.branch;
							InterlevelScene.returnPos = tracker.pos;
							Game.switchScene( InterlevelScene.class );
						}

					} else if (index == 1){
						hero.buff(WarpBeaconTracker.class).detach();
					}
				}
			} );

		} else {
			if (!Dungeon.level.mapped[target] && !Dungeon.level.visited[target]){
				return;
			}

			if (Dungeon.level.distance(hero.pos, target) > 4*hero.pointsInTalent(Talent.REMOTE_BEACON)){
				GLog.w( Messages.get(WarpBeacon.class, "too_far") );
				return;
			}

			PathFinder.buildDistanceMap(target, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
			if (Dungeon.level.pit[target] ||
					(Dungeon.level.solid[target] && !Dungeon.level.passable[target]) ||
					!(Dungeon.level.passable[target] || Dungeon.level.avoid[target]) ||
					PathFinder.distance[hero.pos] == Integer.MAX_VALUE){
				GLog.w( Messages.get(WarpBeacon.class, "invalid_beacon") );
				return;
			}

			WarpBeaconTracker tracker = new WarpBeaconTracker();
			tracker.pos = target;
			tracker.depth = Dungeon.depth;
			tracker.branch = Dungeon.branch;
			tracker.attachTo(hero);

			hero.sprite.operate(target);
			Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
			Invisibility.dispel();
			hero.spendAndNext(Actor.TICK);
		}
	}

	public static class WarpBeaconTracker extends Buff {

		{
			revivePersists = true;
		}

		int pos;
		int depth;
		int branch;

		Emitter e;

		@Override
		public void fx(boolean on) {
			if (on && depth == Dungeon.depth) {
				e = CellEmitter.center(pos);
				e.pour(MagicMissile.WardParticle.UP, 0.05f);
			}
			else if (e != null) e.on = false;
		}

		public static final String POS = "pos";
		public static final String DEPTH = "depth";
		public static final String BRANCH = "branch";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(POS, pos);
			bundle.put(DEPTH, depth);
			bundle.put(BRANCH, branch);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			pos = bundle.getInt(POS);
			depth = bundle.getInt(DEPTH);
			branch = bundle.getInt(BRANCH);
		}
	}

	@Override
	public int icon() {
		return HeroIcon.WARP_BEACON;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.TELEFRAG, Talent.REMOTE_BEACON, Talent.LONGRANGE_WARP, Talent.HEROIC_ENERGY};
	}
}
