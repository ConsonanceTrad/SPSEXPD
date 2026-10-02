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

package pd.actors.hero.abilities.huntress;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.AllyBuff;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.BlobImmunity;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.ArmorAbility;
import pd.actors.mobs.npcs.DirectableAlly;
import pd.effects.Speck;
import pd.effects.particles.ShaftParticle;
import pd.items.equipment.armor.ClassArmor;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.levels.FieldOfView;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.MobSprite;
import pd.ui.HeroIcon;
import pd.utils.GLog;
import render.noosa.TextureFilm;
import render.utils.math.GameMath;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class SpiritHawk extends ArmorAbility {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(SpiritHawk.class)
			.t("name", "灵能飞鹰")
			.t("no_space", "你附近没有可用于召唤的空地。")
			.t("short_desc", "女猎手召唤一只_灵能飞鹰_使魔，协助侦查并吸引敌人注意。")
			.t("desc", "女猎手召唤一只灵能飞鹰，飞鹰存在时使用此能力可指引其行动。飞鹰将存在100回合，指引飞鹰不消耗任何充能。\n\n灵能飞鹰脆弱且缺乏攻击力，但其迅捷的移动、灵敏的躲避与精准的攻击对这些缺点有所弥补。飞鹰与女猎手共享视野，免疫所有环境效果，例如火焰、毒气等。飞鹰只在女猎手指引下发起攻击。")
			.t("$hawkally.name", "灵能飞鹰")
			.t("$hawkally.direct_defend", "你的灵能飞鹰移动到了那个位置。")
			.t("$hawkally.direct_follow", "你的飞鹰正在跟随你。")
			.t("$hawkally.direct_attack", "你的飞鹰正在发动攻击！")
			.t("$hawkally.desc", "一只女猎手召唤的灵能飞鹰，全身散发着明亮而空灵的蓝光。它不断扭头探查着周围环境。\n\n飞鹰并不适合武力战斗，但其速度与视距使其能够胜任高效的侦查与危险的诱敌任务。")
			.t("$hawkally.desc_remaining", "剩余回合数：%d回合")
			.t("$hawkally.desc_dodges", "飞鹰将必定闪避接下来的%d次攻击。")
			.t("$hawkally.discover_hint", "你可通过某个英雄护甲技能遇到该单位。");
	}




	@Override
	public String targetingPrompt() {
		if (getHawk() == null) {
			return super.targetingPrompt();
		} else {
			return Messages.get(this, "prompt");
		}
	}

	@Override
	public boolean useTargeting(){
		return false;
	}

	{
		baseChargeUse = 35f;
	}

	@Override
	public float chargeUse(Hero hero) {
		if (getHawk() == null) {
			return super.chargeUse(hero);
		} else {
			return 0;
		}
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		HawkAlly ally = getHawk();

		if (ally != null){
			if (target == null){
				return;
			} else {
				ally.directTocell(target);
			}
		} else {
			ArrayList<Integer> spawnPoints = new ArrayList<>();
			for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
				int p = hero.pos + PathFinder.NEIGHBOURS8[i];
				if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
					spawnPoints.add(p);
				}
			}

			if (!spawnPoints.isEmpty()){
				armor.charge -= chargeUse(hero);
				armor.updateQuickslot();

				ally = new HawkAlly();
				ally.pos = Random.element(spawnPoints);
				GameScene.add(ally);

				ScrollOfTeleportation.appear(ally, ally.pos);
				Dungeon.observe();

				Invisibility.dispel();
				hero.spendAndNext(Actor.TICK);

			} else {
				GLog.w(Messages.get(this, "no_space"));
			}
		}

	}

	@Override
	public int icon() {
		return HeroIcon.SPIRIT_HAWK;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.EAGLE_EYE, Talent.GO_FOR_THE_EYES, Talent.SWIFT_SPIRIT, Talent.HEROIC_ENERGY};
	}

	private static HawkAlly getHawk(){
		for (Char ch : Actor.chars()){
			if (ch instanceof HawkAlly){
				return (HawkAlly) ch;
			}
		}
		return null;
	}

	public static class HawkAlly extends DirectableAlly {

		{
			spriteClass = HawkSprite.class;

			HP = HT = 10;
			defenseSkill = 60;

			flying = true;
			if (Dungeon.hero != null) {
				viewDistance = (int) GameMath.gate(6, 6 + Dungeon.hero.pointsInTalent(Talent.EAGLE_EYE), 8);
				baseSpeed = 2f + Dungeon.hero.pointsInTalent(Talent.SWIFT_SPIRIT) / 2f;
			} else {
				viewDistance = 6;
				baseSpeed = 2f;
			}
			attacksAutomatically = false;

			immunities.addAll(new BlobImmunity().immunities());
			immunities.add(AllyBuff.class);
		}

		@Override
		public int attackSkill(Char target) {
			return 60;
		}

		private int dodgesUsed = 0;
		private float timeRemaining = 100f;

		@Override
		public int defenseSkill(Char enemy) {
			if (Dungeon.hero.hasTalent(Talent.SWIFT_SPIRIT) &&
					dodgesUsed < 2*Dungeon.hero.pointsInTalent(Talent.SWIFT_SPIRIT)) {
				dodgesUsed++;
				return Char.INFINITE_EVASION;
			}
			return super.defenseSkill(enemy);
		}

		@Override
		public int damageRoll() {
			return Random.NormalIntRange(5, 10);
		}

		@Override
		public int attackProc(Char enemy, int damage) {
			damage = super.attackProc( enemy, damage );
			switch (Dungeon.hero.pointsInTalent(Talent.GO_FOR_THE_EYES)){
				case 1:
					Buff.prolong( enemy, Blindness.class, 2);
					break;
				case 2:
					Buff.prolong( enemy, Blindness.class, 5);
					break;
				case 3:
					Buff.prolong( enemy, Blindness.class, 5);
					Buff.prolong( enemy, Cripple.class, 2);
					break;
				case 4:
					Buff.prolong( enemy, Blindness.class, 5);
					Buff.prolong( enemy, Cripple.class, 5);
					break;
				default:
					//do nothing
			}

			return damage;
		}

		@Override
		protected boolean act() {
			if (timeRemaining <= 0){
				die(null);
				Dungeon.hero.interrupt();
				return true;
			}
			viewDistance = 6+Dungeon.hero.pointsInTalent(Talent.EAGLE_EYE);
			baseSpeed = 2f + Dungeon.hero.pointsInTalent(Talent.SWIFT_SPIRIT)/2f;
			boolean result = super.act();
			FieldOfView.update( Dungeon.level,  this, fieldOfView );
			GameScene.updateFog(pos, viewDistance+(int)Math.ceil(speed()));
			return result;
		}

		@Override
		public void die(Object cause) {
			flying = false;
			super.die(cause);
		}

		@Override
		protected void spend(float time) {
			super.spend(time);
			timeRemaining -= time;
		}

		@Override
		public void destroy() {
			super.destroy();
			Dungeon.observe();
			GameScene.updateFog();
		}

		@Override
		public void defendPos(int cell) {
			GLog.i(Messages.get(this, "direct_defend"));
			super.defendPos(cell);
		}

		@Override
		public void followHero() {
			GLog.i(Messages.get(this, "direct_follow"));
			super.followHero();
		}

		@Override
		public void targetChar(Char ch) {
			GLog.i(Messages.get(this, "direct_attack"));
			super.targetChar(ch);
		}

		@Override
		public String description() {
			String message = Messages.get(this, "desc", (int)timeRemaining);
			if (Actor.chars().contains(this)){
				message += "\n\n" + Messages.get(this, "desc_remaining", (int)timeRemaining);
				if (dodgesUsed < 2*Dungeon.hero.pointsInTalent(Talent.SWIFT_SPIRIT)){
					message += "\n" + Messages.get(this, "desc_dodges", (2*Dungeon.hero.pointsInTalent(Talent.SWIFT_SPIRIT) - dodgesUsed));
				}
			}
			return message;
		}

		private static final String DODGES_USED     = "dodges_used";
		private static final String TIME_REMAINING  = "time_remaining";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(DODGES_USED, dodgesUsed);
			bundle.put(TIME_REMAINING, timeRemaining);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			dodgesUsed = bundle.getInt(DODGES_USED);
			timeRemaining = bundle.getFloat(TIME_REMAINING);
		}
	}

	public static class HawkSprite extends MobSprite {

		public HawkSprite() {
			super();

			texture( Assets.Sprites.SPIRIT_HAWK );

			TextureFilm frames = new TextureFilm( texture, 15, 15 );

			int c = 0;

			idle = new Animation( 6, true );
			idle.frames( frames, 0, 1 );

			run = new Animation( 8, true );
			run.frames( frames, 0, 1 );

			attack = new Animation( 12, false );
			attack.frames( frames, 2, 3, 0, 1 );

			die = new Animation( 12, false );
			die.frames( frames, 4, 5, 6 );

			play( idle );
		}

		@Override
		public int blood() {
			return 0xFF00FFFF;
		}

		@Override
		public void die() {
			super.die();
			emitter().start( ShaftParticle.FACTORY, 0.3f, 4 );
			emitter().start( Speck.factory( Speck.LIGHT ), 0.2f, 3 );
		}
	}
}
