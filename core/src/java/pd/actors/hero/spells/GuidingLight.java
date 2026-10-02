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
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.HeroSubClass;
import pd.effects.MagicMissile;
import pd.items.equipment.artifacts.HolyTome;
import pd.items.equipment.wands.Wand;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.ui.ActionIndicator;
import pd.ui.BuffIndicator;
import pd.ui.HeroIcon;
import pd.ui.QuickSlotButton;
import pd.utils.GLog;
import render.noosa.Image;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.math.Random;
import pd.messages.InlineText;

public class GuidingLight extends TargetedClericSpell {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(GuidingLight.class)
			.t("name", "神导之光")
			.t("short_desc", "造成远程魔法必中伤害。")
			.t("desc", "牧师发射一束魔能光矢攻击目标，造成2~8点伤害并施加光耀。对光耀敌人的下一次不超力的物理攻击必定命中。")
			.t("desc_priest", "_祭司施放该法术时效果更强。_ 每50回合首次施放该法术不消耗圣典充能，并且光耀可以被辐光、法杖、其他单位和直接作用于敌人的神器消耗。如此使用光耀时，可造成等同于祭司等级+5的额外魔法伤害。")
			.t("$guidinglightpriestcooldown.name", "神导之光")
			.t("$guidinglightpriestcooldown.desc", "祭司每经50回合可以免费施放一次神导之光。\n\n剩余回合数：%s")
			.t("$illuminated.name", "光耀")
			.t("$illuminated.desc", "该单位因被神导之光击中而获得光耀。光耀的照度不足以照亮周围区域，但其魔法会使牧师对敌人的攻击更容易命中。")
			.t("$illuminated.desc_priest", "祭司可通过破晓辐光、法杖施法、盟友攻击或某些神器效果触发消耗光耀。如此使用光耀可造成等同于祭司等级+5的额外魔法伤害。")
			.t("$illuminated.desc_generic", "其他英雄仍可通过炽热之光天赋对敌人造成额外伤害，但无法从中获得任何精准加成。");
	}




	public static final GuidingLight INSTANCE = new GuidingLight();

	@Override
	public int icon() {
		return HeroIcon.GUIDING_LIGHT;
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

		hero.busy();
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
		hero.sprite.zap(target);
		MagicMissile.boltFromChar(hero.sprite.parent, MagicMissile.LIGHT_MISSILE, hero.sprite, aim.collisionPos, new Callback() {
			@Override
			public void call() {

				Char ch = Actor.findChar( aim.collisionPos );
				if (ch != null) {
					ch.damage(Hero.heroDamageIntRange(2, 8), GuidingLight.this);
					Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f));
					ch.sprite.burst(0xFFFFFF44, 3);
					if (ch.isAlive()){
						Buff.affect(ch, Illuminated.class);
						Buff.affect(ch, WasIlluminatedTracker.class);
					}
				} else {
					Dungeon.level.pressCell(aim.collisionPos);
				}

				hero.spend( 1f );
				hero.next();

				onSpellCast(tome, hero);
				if (hero.subClass == HeroSubClass.PRIEST && hero.buff(GuidingLightPriestCooldown.class) == null) {
					Buff.prolong(hero, GuidingLightPriestCooldown.class, 50f);
					ActionIndicator.refresh();
				}

			}
		});
	}

	@Override
	public float chargeUse(Hero hero) {
		if (hero.subClass == HeroSubClass.PRIEST
			&& hero.buff(GuidingLightPriestCooldown.class) == null){
			return 0;
		} else {
			return 1;
		}
	}

	public String desc(){
		String desc = Messages.get(this, "desc");
		if (Dungeon.hero.subClass == HeroSubClass.PRIEST){
			desc += "\n\n" + Messages.get(this, "desc_priest");
		}
		return desc + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
	}

	public static class GuidingLightPriestCooldown extends FlavourBuff {

		@Override
		public int icon() {
			return BuffIndicator.ILLUMINATED;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.brightness(0.5f);
		}

		public float iconFadePercent() { return Math.max(0, visualcooldown() / 50); }

		@Override
		public void detach() {
			super.detach();
			ActionIndicator.refresh();
		}
	}

	public static class Illuminated extends Buff {

		{
			type = buffType.NEGATIVE;
		}

		@Override
		public int icon() {
			return BuffIndicator.ILLUMINATED;
		}

		@Override
		public void fx(boolean on) {
			if (on) target.sprite.add(CharSprite.State.ILLUMINATED);
			else target.sprite.remove(CharSprite.State.ILLUMINATED);
		}

		@Override
		public String desc() {
			String desc = super.desc();

			if (Dungeon.hero.subClass == HeroSubClass.PRIEST){
				desc += "\n\n" + Messages.get(this, "desc_priest");
			} else if (Dungeon.hero.heroClass != HeroClass.CLERIC){
				desc += "\n\n" + Messages.get(this, "desc_generic");
			}

			return desc;
		}
	}

	public static class WasIlluminatedTracker extends Buff {}
}
