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
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FlavourBuff;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.cleric.PowerOfMany;
import pd.effects.SpellSprite;
import pd.items.equipment.artifacts.HolyTome;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.ui.BuffIndicator;
import pd.ui.HeroIcon;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class DivineSense extends ClericSpell {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(DivineSense.class)
			.t("name", "神圣感知")
			.t("short_desc", "短时间内获得大范围的灵视感知。")
			.t("desc", "牧师将其感知集中在周遭环境上，获得持续50回合，%d格范围的灵视感知。该法术施法不耗时。")
			.t("$divinesensetracker.name", "神圣感知")
			.t("$divinesensetracker.desc", "该单位暂时能以其灵视感知看到附近其他生物！\n\n剩余回合数：%s");
	}




	public static final DivineSense INSTANCE = new DivineSense();

	@Override
	public int icon() {
		return HeroIcon.DIVINE_SENSE;
	}

	@Override
	public float chargeUse(Hero hero) {
		return 2;
	}

	@Override
	public boolean canCast(Hero hero) {
		return super.canCast(hero) && hero.hasTalent(Talent.DIVINE_SENSE);
	}

	@Override
	public void onCast(HolyTome tome, Hero hero) {
		Buff.prolong(hero, DivineSenseTracker.class, DivineSenseTracker.DURATION);
		Dungeon.observe();

		Sample.INSTANCE.play(Assets.Sounds.READ);

		SpellSprite.show(hero, SpellSprite.VISION);
		hero.sprite.operate(hero.pos);
		hero.next();

		Char ally = PowerOfMany.getPoweredAlly();
		if (ally != null && ally.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null){
			Buff.prolong(ally, DivineSenseTracker.class, DivineSenseTracker.DURATION);
			SpellSprite.show(ally, SpellSprite.VISION);
		}

		onSpellCast(tome, hero);
	}

	public String desc(){
		return Messages.get(this, "desc", 4+4*Dungeon.hero.pointsInTalent(Talent.DIVINE_SENSE)) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
	}

	public static class DivineSenseTracker extends FlavourBuff {

		public static final float DURATION = 50f;

		{
			type = buffType.POSITIVE;
		}

		@Override
		public int icon() {
			return BuffIndicator.HOLY_SIGHT;
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}

		@Override
		public void detach() {
			super.detach();
			Dungeon.observe();
			GameScene.updateFog();
		}
	}

}
