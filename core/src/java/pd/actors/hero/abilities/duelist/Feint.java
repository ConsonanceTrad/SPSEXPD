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

package pd.actors.hero.abilities.duelist;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.BlobImmunity;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FlavourBuff;
import pd.actors.buffs.Haste;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.Vulnerable;
import pd.actors.buffs.Weakness;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.ArmorAbility;
import pd.actors.mobs.Mob;
import pd.items.Item;
import pd.items.equipment.armor.ClassArmor;
import pd.levels.Terrain;
import pd.levels.features.Door;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.sprites.CharSprite;
import pd.sprites.MirrorSprite;
import pd.ui.HeroIcon;
import pd.ui.TargetHealthIndicator;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.noosa.tweeners.AlphaTweener;
import render.noosa.tweeners.Delayer;
import render.utils.data.Callback;
import pd.messages.InlineText;

public class Feint extends ArmorAbility {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Feint.class)
			.t("name", "虚晃一招")
			.t("prompt", "选择要冲到的位置")
			.t("too_far", "那个位置不与你相邻。")
			.t("bad_location", "你无法移动到那个位置。")
			.t("short_desc", "决斗家_虚晃一招_，在假装进行攻击的同时冲向一个邻近位置。诱导敌人攻击她的残影，致使敌人露出破绽。")
			.t("desc", "决斗家在假装进行攻击的同时冲向一个邻近位置，在原位留下一个残影。正在攻击决斗家的敌人会攻击到残影。\n\n攻击了残影的敌人会被迷惑，取消原本的下一个动作，并且可以被伏击。");
	}


	{
		baseChargeUse = 35;
	}

	@Override
	public int icon() {
		return HeroIcon.FEINT;
	}

	public boolean useTargeting(){
		return false;
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
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

		if (!Dungeon.level.adjacent(hero.pos, target)){
			GLog.w(Messages.get(this, "too_far"));
			return;
		}

		if (Dungeon.hero.rooted){
			PixelScene.shake( 1, 1f );
			GLog.w(Messages.get(this, "bad_location"));
			return;
		}

		if (Dungeon.level.solid[target] || Actor.findChar(target) != null){
			GLog.w(Messages.get(this, "bad_location"));
			return;
		}

		hero.busy();
		Sample.INSTANCE.play(Assets.Sounds.MISS);
		hero.sprite.jump(hero.pos, target, 0, 0.1f, new Callback() {
			@Override
			public void call() {
				if (Dungeon.level.map[hero.pos] == Terrain.OPEN_DOOR) {
					Door.leave( hero.pos );
				}
				hero.pos = target;
				Dungeon.level.occupyCell(hero);
				Invisibility.dispel();
				hero.next();
			}
		});
		hero.spend(1f);

		AfterImage image = new AfterImage();
		image.pos = hero.pos;
		GameScene.add(image);
		image.syncToHero(hero);

		int imageAttackPos;
		Char enemyTarget = TargetHealthIndicator.instance.target();
		if (enemyTarget != null && enemyTarget.alignment == Char.Alignment.ENEMY){
			imageAttackPos = enemyTarget.pos;
		} else {
			imageAttackPos = image.pos + (image.pos - target);
		}
		//do a purely visual attack
		hero.sprite.parent.add(new Delayer(0f){
			@Override
			protected void onComplete() {
				image.sprite.attack(imageAttackPos, new Callback() {
					@Override
					public void call() {
						//do nothing, attack is purely visual
					}
				});
			}
		});

		for (Mob m : Dungeon.level.mobs().toArray( new Mob[0] )){
			if ((m.isTargeting(hero) && m.state == m.HUNTING) ||
					(m.alignment == Char.Alignment.ENEMY && m.state != m.PASSIVE && m.state != m.SLEEPING && Dungeon.level.distance(m.pos, image.pos) <= 2)){
				m.aggro(image);
			}
		}

		armor.charge -= chargeUse(hero);
		Item.updateQuickslot();
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.FEIGNED_RETREAT, Talent.EXPOSE_WEAKNESS, Talent.COUNTER_ABILITY, Talent.HEROIC_ENERGY};
	}

	public static class AfterImage extends Mob {

		{
			spriteClass = AfterImageSprite.class;
			defenseSkill = 0;

			properties.add(Property.IMMOVABLE);

			alignment = Alignment.ALLY;
			state = PASSIVE;

			HP = HT = 1;

			//fades just before the hero's next action
			actPriority = Actor.HERO_PRIO+1;
		}

		@Override
		public String name() {
			return ""; //shouldn't be examinable
		}

		@Override
		public String description() {
			return ""; //shouldn't be examinable
		}

		@Override
		public boolean canInteract(Char c) {
			return false;
		}

		@Override
		protected boolean act() {
			destroy();
			sprite.die();
			return true;
		}

		public void syncToHero(Hero hero){
			if (cooldown() != hero.cooldown()){
				spendConstant(hero.cooldown() - cooldown());
			}
		}

		@Override
		public void damage( int dmg, Object src ) {

		}

		@Override
		public int defenseSkill(Char enemy) {
			if (enemy.alignment == Alignment.ENEMY) {
				if (enemy instanceof Mob) {
					((Mob) enemy).clearEnemy();
				}
				Buff.affect(enemy, FeintConfusion.class, 1);
				if (enemy.sprite != null) enemy.sprite.showLost();
				if (Dungeon.hero.hasTalent(Talent.FEIGNED_RETREAT)) {
					Buff.prolong(Dungeon.hero, Haste.class, 2f * Dungeon.hero.pointsInTalent(Talent.FEIGNED_RETREAT));
				}
				if (Dungeon.hero.hasTalent(Talent.EXPOSE_WEAKNESS)) {
					Buff.prolong(enemy, Vulnerable.class, 2f * Dungeon.hero.pointsInTalent(Talent.EXPOSE_WEAKNESS));
					Buff.prolong(enemy, Weakness.class, 2f * Dungeon.hero.pointsInTalent(Talent.EXPOSE_WEAKNESS));
				}
				if (Dungeon.hero.hasTalent(Talent.COUNTER_ABILITY)) {
					Buff.prolong(Dungeon.hero, Talent.CounterAbilityTacker.class, 3f);
				}
			}
			return 0;
		}

		@Override
		public boolean add( Buff buff ) {
			return false;
		}

		{
			immunities.addAll(new BlobImmunity().immunities());
		}

		@Override
		public CharSprite sprite() {
			CharSprite s = super.sprite();
			((AfterImageSprite)s).updateArmor();
			return s;
		}

		public static class FeintConfusion extends FlavourBuff {

		}

		public static class AfterImageSprite extends MirrorSprite {
			@Override
			public void updateArmor() {
				updateArmor(6); //we can assume heroic armor
			}

			@Override
			public void resetColor() {
				super.resetColor();
				alpha(0.6f);
			}

			@Override
			public void die() {
				//don't interrupt current animation to start fading
				//this ensures the fake attack animation plays
				if (parent != null) {
					parent.add( new AlphaTweener( this, 0, 3f ) {
						@Override
						protected void onComplete() {
							AfterImageSprite.this.killAndErase();
						}
					} );
				}
			}
		}

	}
}
