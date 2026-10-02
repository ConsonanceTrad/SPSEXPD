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

package pd.actors.hero.spells;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FlavourBuff;
import pd.actors.buffs.LifeLink;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.cleric.PowerOfMany;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.DirectableAlly;
import pd.effects.Beam;
import pd.items.equipment.artifacts.HolyTome;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.tiles.DungeonTilemap;
import pd.ui.BuffIndicator;
import pd.ui.HeroIcon;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class BeamingRay extends TargetedClericSpell {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(BeamingRay.class)
			.t("name", "光灵召唤")
			.t("no_space", "那里没有空间让你的盟友出现。")
			.t("out_of_range", "那个位置超出了范围。")
			.t("short_desc", "传送你的盟友并使其获得伤害加成。")
			.t("desc", "牧师引导其盟友的力量为可将传送盟友至某个位置的光束。若光束投射到敌人上，传送光束会将盟友传送至敌人附近并将其作为攻击目标。\n\n传送光束的最大传送距离为%1$d格，并使万物一心对4格范围内最近敌人的伤害加成提升至%2$d%%，持续10回合。该法术还可以传送通常情况下无法移动的盟友，但若如此则传送光束最大传送距离减半。")
			.t("$beamingrayboost.name", "光灵召唤")
			.t("$beamingrayboost.desc", "该盟友近期被光灵召唤的传送光束传送，获得对传送后最近敌人的伤害加成。万物一心不会在该增益的效果期间内结束。\n\n剩余回合数：%s");
	}




	public static BeamingRay INSTANCE = new BeamingRay();

	@Override
	public int icon() {
		return HeroIcon.BEAMING_RAY;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", 4*Dungeon.hero.pointsInTalent(Talent.BEAMING_RAY), 30 + 5*Dungeon.hero.pointsInTalent(Talent.BEAMING_RAY)) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
	}

	@Override
	public int targetingFlags() {
		return Ballistica.STOP_TARGET;
	}

	@Override
	public boolean canCast(Hero hero) {
		return super.canCast(hero)
				&& hero.hasTalent(Talent.BEAMING_RAY)
				&& (PowerOfMany.getPoweredAlly() != null || Stasis.getStasisAlly() != null);
	}

	@Override
	protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
		if (target == null){
			return;
		}

		Char ally = PowerOfMany.getPoweredAlly();

		if (ally == null){
			//temporary, for distance checks
			ally = Dungeon.hero;
		}

		int telePos = target;

		if (!Dungeon.level.insideMap(telePos)){
			GLog.w(Messages.get(this, "no_space"));
			return;
		}

		if (Dungeon.level.solid[telePos] || !Dungeon.level.heroFOV[telePos] || Actor.findChar(telePos) != null){
			telePos = -1;
			for (int i : PathFinder.NEIGHBOURS8){
				if (Actor.findChar(target+i) == null && Dungeon.level.heroFOV[target+i]
						&& (Dungeon.level.passable[target+i] || (ally.flying && Dungeon.level.avoid[target+i])) ){
					if (telePos == -1 || Dungeon.level.trueDistance(telePos, ally.pos) > Dungeon.level.trueDistance(target+i, ally.pos)){
						telePos =  target+i;
					}
				}
			}
		}

		if (telePos == -1){
			GLog.w(Messages.get(this, "no_space"));
			return;
		}

		if (ally == Dungeon.hero){
			ally = Stasis.getStasisAlly();
		}

		int range = 4*hero.pointsInTalent(Talent.BEAMING_RAY);
		if (Char.hasProp(ally, Char.Property.IMMOVABLE)){
			range /= 2;
		}
		if (Dungeon.level.distance(ally.pos, telePos) > range){
			GLog.w(Messages.get(this, "out_of_range"));
			return;
		}

		Char chTarget = null;
		if (Actor.findChar(target) != null && Actor.findChar(target).alignment == Char.Alignment.ENEMY){
			chTarget = Actor.findChar(target);
			if (hero.subClass == HeroSubClass.PRIEST){
				Buff.affect(chTarget, GuidingLight.Illuminated.class);
			}
		}

		if (ally == Stasis.getStasisAlly()){
			ally.pos = telePos;
			GameScene.add((Mob) ally);
			hero.buff(Stasis.StasisBuff.class).detach();
			hero.sprite.parent.add(
					new Beam.SunRay(hero.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(telePos)));
			Sample.INSTANCE.play( Assets.Sounds.RAY );

			if (ally.buff(LifeLink.class) != null){
				Buff.prolong(Dungeon.hero, LifeLink.class, ally.buff(LifeLink.class).cooldown()).object = ally.id();
			}
		} else {
			hero.sprite.parent.add(
					new Beam.SunRay(ally.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(telePos)));
			Sample.INSTANCE.play( Assets.Sounds.RAY );
		}

		hero.sprite.zap(telePos);
		ScrollOfTeleportation.appear(ally, telePos);

		if (chTarget == null){
			for (Char ch : Actor.chars()){
				if (ch.alignment == Char.Alignment.ENEMY && Dungeon.level.distance(ch.pos, telePos) <= 4){
					if (chTarget == null || Dungeon.level.trueDistance(chTarget.pos, ally.pos) < Dungeon.level.trueDistance(ch.pos,  ally.pos)) {
						chTarget = ch;
					}
				}
			}
		}

		if (chTarget != null) {
			if (ally instanceof DirectableAlly) {
				((DirectableAlly) ally).targetChar(chTarget);
			} else if (ally instanceof Mob) {
				((Mob) ally).aggro(chTarget);
			}
			FlavourBuff.prolong(ally, BeamingRayBoost.class, BeamingRayBoost.DURATION).object = chTarget.id();
		} else {
			if (ally instanceof DirectableAlly) {
				((DirectableAlly) ally).clearDefensingPos();
			}
			//just the buff with no target
			FlavourBuff.prolong(ally, BeamingRayBoost.class, BeamingRayBoost.DURATION);
		}

		hero.spendAndNext(Actor.TICK);
		Dungeon.observe();
		GameScene.updateFog();

		onSpellCast(tome, hero);
	}

	public static class BeamingRayBoost extends FlavourBuff {

		{
			type = buffType.POSITIVE;
		}

		public int object = 0;

		public static final float DURATION = 10f;

		private static final String OBJECT  = "object";

		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( OBJECT, object );
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle( bundle );
			object = bundle.getInt( OBJECT );
		}

		@Override
		public int icon() {
			return BuffIndicator.HOLY_WEAPON;
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}

	}

}
