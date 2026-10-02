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

package pd.actors.hero.abilities.cleric;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.AllyBuff;
import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FlavourBuff;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.PrismaticGuard;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.ArmorAbility;
import pd.actors.hero.spells.BeamingRay;
import pd.actors.hero.spells.ClericSpell;
import pd.actors.hero.spells.LifeLinkSpell;
import pd.actors.hero.spells.Stasis;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.DirectableAlly;
import pd.effects.Speck;
import pd.effects.particles.ShaftParticle;
import pd.items.equipment.armor.ClassArmor;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.items.equipment.wands.WandOfLivingEarth;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.sprites.HeroSprite;
import pd.sprites.MobSprite;
import pd.ui.BuffIndicator;
import pd.ui.HeroIcon;
import pd.utils.GLog;
import render.noosa.TextureFilm;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class PowerOfMany extends ArmorAbility {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(PowerOfMany.class)
			.t("name", "万物一心")
			.t("prompt_default", "选择一个盟友或位置")
			.t("prompt_ally", "指挥你的圣天誓盟")
			.t("ally_exists", "你已经有一位强化盟友了。")
			.t("no_vision", "你无法以视野外的位置为目标。")
			.t("only_allies", "你只能强化盟友。")
			.t("short_desc", "牧师引导_万物一心_的力量，可强化一位已有盟友或召唤一位全新盟友。")
			.t("desc", "牧师引导_万物一心_的力量，可强化一位已有盟友或召唤一位持续100回合的全新盟友。牧师对空格施放该护甲技能即可召唤一位名为圣天誓盟的全新盟友。\n\n被万物一心强化时，任何盟友都会获得25%伤害加成与25%伤害减免，并与牧师共享视野。使用护甲技能还会使盟友获得25点护盾。\n\n牧师还会获得三种仅能对强化盟友施放的全新法术。万物一心不会在上述法术之一的效果期间内结束。")
			.t("$powerbuff.name", "万物一心")
			.t("$powerbuff.desc", "该盟友已被万物一心强化，获得25%%伤害加成与25%%伤害减免。盟友还会与牧师共享视野，并可获益于万物一心法术\n\n万物一心会在盟友消散时(例如虹卫)维持强化效果，然而强化效果的持续时长仍会进行倒计时。\n\n剩余回合数：%s")
			.t("$lightally.name", "圣天誓盟")
			.t("$lightally.direct_defend", "你的盟友移动到了那个位置。")
			.t("$lightally.direct_follow", "你的盟友正在跟随你。")
			.t("$lightally.direct_attack", "你的盟友正在发动攻击！")
			.t("$lightally.desc", "一位牧师冒险伙伴的克隆人，通体由凝聚态圣光制成。即使圣天誓盟具有另一位英雄的外形，却不具有他们的任何独特能力。只要万物一心的强化效果仍在，该盟友就会维持其存在。\n\n该盟友可通过再次使用万物一心护甲技能不消耗充能地指挥。")
			.t("$lightally.discover_hint", "你可通过某个英雄护甲技能遇到该单位。");
	}




	@Override
	public float chargeUse(Hero hero) {
		if (getPoweredAlly() instanceof LightAlly){
			return 0;
		}
		return super.chargeUse(hero);
	}

	@Override
	public String targetingPrompt() {
		Char ally = getPoweredAlly();

		boolean allyExists = ally != null;

		if (Dungeon.hero.buff(PrismaticGuard.class) != null
				&& Dungeon.hero.buff(PrismaticGuard.class).isEmpowered()){
			allyExists = true;
		}

		if (Dungeon.hero.buff(WandOfLivingEarth.RockArmor.class) != null
				&& Dungeon.hero.buff(WandOfLivingEarth.RockArmor.class).isEmpowered()){
			allyExists = true;
		}

		if (Stasis.getStasisAlly() != null){
			allyExists = true;
		}

		if (ally instanceof LightAlly){
			return Messages.get(this, "prompt_ally");
		} else if (!allyExists){
			return Messages.get(this, "prompt_default");
		} else {
			return null;
		}
	}

	public boolean useTargeting(){
		return false;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {

		Char ally = getPoweredAlly();

		boolean allyExists = ally != null;

		if (hero.buff(PrismaticGuard.class) != null
				&& hero.buff(PrismaticGuard.class).isEmpowered()){
			allyExists = true;
		}

		if (hero.buff(WandOfLivingEarth.RockArmor.class) != null
				&& hero.buff(WandOfLivingEarth.RockArmor.class).isEmpowered()){
			allyExists = true;
		}

		if (Stasis.getStasisAlly() != null){
			allyExists = true;
		}

		if (ally instanceof LightAlly){
			if (target == null){
				return;
			} else {
				((LightAlly) ally).directTocell(target);
			}
		} else if (allyExists) {
			GLog.w( Messages.get(this, "ally_exists"));
		} else {
			if (target == null){
				return;
			}

			if (!Dungeon.level.heroFOV[target]){
				GLog.w(Messages.get(this, "no_vision"));
				return;
			}

			//pre-calculate as cost becomes 0 if light ally starts to exist
			float chargeUse = chargeUse(hero);

			Char ch = Actor.findChar(target);
			if (ch != null){
				if (ch.alignment != Char.Alignment.ALLY || ch == Dungeon.hero){
					GLog.w(Messages.get(this, "only_allies"));
					return;
				}
			} else {

				if (!Dungeon.level.passable[target] || Dungeon.level.avoid[target]){
					GLog.w(Messages.get(ClericSpell.class, "invalid_target"));
					return;
				}

				ch = new LightAlly(hero.lvl);
				ch.pos = target;
				GameScene.add((Mob) ch);
				ScrollOfTeleportation.appear(ch, ch.pos);
			}

			Buff.affect(ch, PowerBuff.class, 100f);
			Buff.affect(ch, Barrier.class).setShield(25);

			armor.charge -= chargeUse;
			armor.updateQuickslot();

			hero.sprite.zap(target);
			Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);

			Invisibility.dispel();
			hero.spendAndNext(Actor.TICK);

		}

	}

	@Override
	public int icon() {
		return HeroIcon.POWER_OF_MANY;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.BEAMING_RAY, Talent.LIFE_LINK, Talent.STASIS, Talent.HEROIC_ENERGY};
	}

	public static Char getPoweredAlly(){
		for (Char ch : Actor.chars()){
			if (ch.buff(PowerBuff.class) != null){
				return ch;
			}
		}
		return null;
	}

	public static class PowerBuff extends FlavourBuff {

		public static float DURATION = 100f;

		{
			type = buffType.POSITIVE;
			announced = true;
		}

		@Override
		public int icon() {
			return BuffIndicator.MANY_POWER;
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}

		@Override
		public void fx(boolean on) {
			if (on) target.sprite.add(CharSprite.State.GLOWING);
			else    target.sprite.remove(CharSprite.State.GLOWING);
		}

		@Override
		public boolean act() {
			if (target.buff(BeamingRay.BeamingRayBoost.class) != null
				|| target.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null){
				spend(TICK);
				return true;
			}
			return super.act();
		}

		@Override
		public void detach() {
			super.detach();
			Dungeon.observe();
			GameScene.updateFog();
		}
	}

	public static class LightAlly extends DirectableAlly {

		{
			spriteClass = LightAllySprite.class;

			HP = HT = 80;

			immunities.add(AllyBuff.class);

			properties.add(Property.INORGANIC);
		}

		HeroClass cls;

		public LightAlly(){
			super();
			cls = HeroClass.values()[Random.Int(5)];
		}

		public LightAlly(int heroLevel ){
			this();
			defenseSkill = heroLevel + 4; //equal to base hero defense skill
		}

		@Override
		protected boolean act() {
			if (buff(PowerOfMany.PowerBuff.class) == null){
				die(null);
				return true;
			}
			int oldPos = pos;
			boolean result = super.act();
			//partially simulates how the hero switches to idle animation
			if ((pos == target || oldPos == pos) && sprite.looping()){
				sprite.idle();
			}
			return result;
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
		public int attackSkill(Char target) {
			return defenseSkill+5; //equal to base hero attack skill
		}

		@Override
		public int damageRoll() {
			return Random.NormalIntRange(5, 30); //+0 greatsword
		}

		@Override
		public int drRoll() {
			return super.drRoll() + Random.NormalIntRange(1, 5); //+0 plate
		}

		@Override
		public float speed() {
			float speed = super.speed();

			//moves 2 tiles at a time when returning to the hero
			if (state == WANDERING
					&& defendingPos == -1
					&& Dungeon.level.distance(pos, Dungeon.hero.pos) > 1){
				speed *= 2;
			}

			return speed;
		}

		@Override
		public CharSprite sprite() {
			CharSprite sprite = super.sprite();
			((LightAllySprite)sprite).setup(cls);
			return sprite;
		}

		private static final String HERO_CLS = "hero_cls";
		private static final String DEF_SKILL = "def_skill";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(HERO_CLS, cls);
			bundle.put(DEF_SKILL, defenseSkill);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			cls = bundle.getEnum(HERO_CLS, HeroClass.class);
			defenseSkill = bundle.getInt(DEF_SKILL);
		}
	}

	public static class LightAllySprite extends MobSprite {

		public LightAllySprite() {
			super();

			setup(HeroClass.values()[Random.Int(5)]);
		}

		public void setup(HeroClass cls){
			texture(cls.spritesheet());

			TextureFilm film = new TextureFilm( HeroSprite.tiers(), 6, 12, 15 );

			idle = new Animation( 1, true );
			idle.frames( film, 0, 0, 0, 1, 0, 0, 1, 1 );

			run = new Animation( 20, true );
			run.frames( film, 2, 3, 4, 5, 6, 7 );

			die = new Animation( 20, false );
			die.frames( film, 0 );

			attack = new Animation( 15, false );
			attack.frames( film, 13, 14, 15, 0 );

			play(idle, true);
			resetColor();
		}

		@Override
		public void link(Char ch) {
			super.link(ch);
			if (ch instanceof LightAlly){
				setup(((LightAlly) ch).cls);
			}
		}

		@Override
		public void resetColor() {
			super.resetColor();
			alpha(0.8f);
			tint(1.33f, 1.33f, 0.8f, 0.6f);
			rm = gm = bm = 0;
		}

		@Override
		public void die() {
			super.die();
			emitter().start( ShaftParticle.FACTORY, 0.3f, 4 );
			emitter().start( Speck.factory( Speck.LIGHT ), 0.2f, 3 );
		}

		@Override
		public void draw() {
			if (alpha() >= 0.8f) alpha(0.8f);
			rm = gm = bm = 0; //always flat and transparent
			super.draw();
		}
	}

}
