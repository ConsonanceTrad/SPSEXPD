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

import pd.atlas.items.ConsumThrowsDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FlavourBuff;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.effects.Splash;
import pd.effects.particles.SparkParticle;
import pd.items.Item;
import pd.items.equipment.artifacts.HolyTome;
import pd.items.equipment.wands.Wand;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.sprites.ItemSprite;
import pd.sprites.MissileSprite;
import pd.ui.BuffIndicator;
import pd.ui.HeroIcon;
import pd.ui.QuickSlotButton;
import pd.utils.GLog;
import render.noosa.Image;
import render.noosa.audio.Sample;
import render.noosa.particles.Emitter;
import render.utils.data.Callback;
import render.utils.math.Random;
import pd.messages.InlineText;

public class HolyLance extends TargetedClericSpell {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(HolyLance.class)
			.t("name", "神圣标枪")
			.t("short_desc", "造成高额远程魔法伤害。")
			.t("desc", "祭司将大量能量聚集为一柄致命的投掷用圣枪。圣枪造成%1$d~%2$d点伤害，并且必定对亡灵和恶魔目标造成最大伤害。\n\n该法术充能消耗极高，还有30回合的冷却。")
			.t("lancecooldown.name", "神圣标枪-冷却")
			.t("lancecooldown.desc", "祭司近期施放了神圣标枪，必须等待一段时间才能再次施放。\n\n剩余回合数：%s");
	}


	public static final HolyLance INSTANCE = new HolyLance();

	@Override
	public int icon() {
		return HeroIcon.HOLY_LANCE;
	}

	@Override
	public String desc() {
		int min = 15 + 15*Dungeon.hero.pointsInTalent(Talent.HOLY_LANCE);
		int max = Math.round(27.5f + 27.5f*Dungeon.hero.pointsInTalent(Talent.HOLY_LANCE));
		return Messages.get(this, "desc", min, max) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
	}

	@Override
	public boolean canCast(Hero hero) {
		return super.canCast(hero)
				&& hero.hasTalent(Talent.HOLY_LANCE)
				&& hero.buff(LanceCooldown.class) == null;
	}

	@Override
	public float chargeUse(Hero hero) {
		return 4;
	}

	@Override
	public int targetingFlags() {
		return Ballistica.PROJECTILE;
	}

	@Override
	protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
		if (target == null){
			return;
		}

		Ballistica aim = new Ballistica(hero.pos, target, targetingFlags());

		if (Actor.findChar( aim.collisionPos ) == hero){
			GLog.i( Messages.get(Wand.class, "self_target") );
			return;
		}

		if (Actor.findChar(aim.collisionPos) != null) {
			QuickSlotButton.target(Actor.findChar(aim.collisionPos));
		} else {
			QuickSlotButton.target(Actor.findChar(target));
		}

		hero.sprite.zap( target );
		hero.busy();

		Sample.INSTANCE.play(Assets.Sounds.ZAP);

		Char enemy = Actor.findChar(aim.collisionPos);
		if (enemy != null) {
			((MissileSprite) hero.sprite.parent.recycle(MissileSprite.class)).
					reset(hero.sprite,
							enemy.sprite,
							new HolyLanceVFX(),
							new Callback() {
								@Override
								public void call() {
									int min = 15 + 15*Dungeon.hero.pointsInTalent(Talent.HOLY_LANCE);
									int max = Math.round(27.5f + 27.5f*Dungeon.hero.pointsInTalent(Talent.HOLY_LANCE));
									if (Char.hasProp(enemy, Char.Property.UNDEAD) || Char.hasProp(enemy, Char.Property.DEMONIC)){
										min = max;
									}
									enemy.damage(Hero.heroDamageIntRange(min, max), HolyLance.this);
									Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.8f, 1f) );
									Sample.INSTANCE.play( Assets.Sounds.HIT_STAB, 1, Random.Float(0.8f, 1f) );

									if (enemy.isActive()){
										Buff.affect(enemy, GuidingLight.Illuminated.class);
									}

									enemy.sprite.burst(0xFFFFFFFF, 10);
									hero.spendAndNext(1f);
									onSpellCast(tome, hero);
									FlavourBuff.affect(hero, LanceCooldown.class, 30f);
								}
							});
		} else {
			((MissileSprite) hero.sprite.parent.recycle(MissileSprite.class)).
					reset(hero.sprite,
							target,
							new HolyLanceVFX(),
							new Callback() {
								@Override
								public void call() {
									Splash.at(target, 0xFFFFFFFF, 10);
									Dungeon.level.pressCell(aim.collisionPos);
									hero.spendAndNext(1f);
									onSpellCast(tome, hero);
									FlavourBuff.affect(hero, LanceCooldown.class, 30f);
								}
							});
		}

	}

	public static class HolyLanceVFX extends Item {

		{
			image = ConsumThrowsDict.THROWING_SPIKE_0;
		}

		@Override
		public ItemSprite.Glowing glowing() {
			return new ItemSprite.Glowing(0xFFFFFF, 0.1f);
		}

		@Override
		public Emitter emitter() {
			Emitter emitter = new Emitter();
			emitter.pos( 5, 5, 0, 0);
			emitter.fillTarget = false;
			emitter.pour(SparkParticle.FACTORY, 0.025f);
			return emitter;
		}
	}

	public static class LanceCooldown extends FlavourBuff {

		@Override
		public int icon() {
			return BuffIndicator.TIME;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0.67f, 0.67f, 0);
		}

		public float iconFadePercent() { return Math.max(0, visualcooldown() / 30); }
	}
}
