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
import pd.actors.Actor;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FlavourBuff;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.ArmorAbility;
import pd.effects.particles.LeafParticle;
import pd.items.equipment.armor.ClassArmor;
import pd.ui.BuffIndicator;
import pd.ui.HeroIcon;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class NaturesPower extends ArmorAbility {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(NaturesPower.class)
			.t("name", "自然之力")
			.t("short_desc", "女猎手呼唤_自然之力_的协助，在短时间内增加其移动速度与灵能弓射击速度。")
			.t("desc", "女猎手与她的弓注入了自然的力量！这种力量持续8回合。\n\n在自然之力的加持下，女猎手拥有双倍的移动速度与+33%的灵能弓射击速度。这种力量不影响狙击手专精的追加攻击。")
			.t("$naturespowertracker.name", "自然之力")
			.t("$naturespowertracker.desc", "女猎手拥有了暂时的自然之力，增加其移动速度与灵能弓射击速度。\n\n剩余回合数：%s");
	}




	{
		baseChargeUse = 35f;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {

		Buff.prolong(hero, naturesPowerTracker.class, naturesPowerTracker.DURATION);
		hero.buff(naturesPowerTracker.class).extensionsLeft = 2;
		hero.sprite.operate(hero.pos);
		Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
		hero.sprite.emitter().burst(LeafParticle.GENERAL, 10);

		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();
		Invisibility.dispel();
		hero.spendAndNext(Actor.TICK);

	}

	@Override
	public int icon() {
		return HeroIcon.NATURES_POWER;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.GROWING_POWER, Talent.NATURES_WRATH, Talent.WILD_MOMENTUM, Talent.HEROIC_ENERGY};
	}

	public static class naturesPowerTracker extends FlavourBuff{

		{
			type = buffType.POSITIVE;
		}

		public static final float DURATION = 8f;

		public int extensionsLeft = 2;

		public void extend( int turns ){
			if (extensionsLeft > 0 && turns > 0) {
				spend(turns);
				extensionsLeft--;
			}
		}

		@Override
		public int icon() {
			return BuffIndicator.NATURE_POWER;
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}

	}
}
