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
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.cleric.PowerOfMany;
import pd.effects.Beam;
import pd.items.equipment.artifacts.HolyTome;
import pd.messages.Messages;
import pd.tiles.DungeonTilemap;
import pd.ui.BuffIndicator;
import pd.ui.HeroIcon;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class LifeLinkSpell extends ClericSpell {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(LifeLinkSpell.class)
			.t("name", "血色羁绊")
			.t("short_desc", "与盟友共享所受伤害，并使其获得伤害减免。")
			.t("desc", "牧师强化自身与其强化盟友之间的生命联结。该强化版生命联结持续%1$d回合，会使英雄与其盟友共享任何所受伤害，并使万物一心的伤害减免提升至%2$d%%。注意，伤害共享的优先级低于护甲，但高于万物一心的伤害减免。\n\n生命联结效果生效时，对任何一方施放3阶及以下的增益型牧师法术对双方均有效果。")
			.t("$lifelinkspellbuff.name", "血色羁绊")
			.t("$lifelinkspellbuff.desc", "牧师近期对该盟友施放了血色羁绊以建立生命联结。\n\n除生命联结的通常效果外，该单位还会获得伤害减免，任何增益型牧师法术对牧师与该盟友施放时对双方均生效，并且万物一心不会在该增益的效果期间内结束。\n\n\n剩余回合数：%s");
	}




	public static LifeLinkSpell INSTANCE = new LifeLinkSpell();

	@Override
	public int icon() {
		return HeroIcon.LIFE_LINK;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", 4 + 2*Dungeon.hero.pointsInTalent(Talent.LIFE_LINK), 30 + 5*Dungeon.hero.pointsInTalent(Talent.LIFE_LINK)) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
	}

	@Override
	public boolean canCast(Hero hero) {
		return super.canCast(hero)
				&& hero.hasTalent(Talent.LIFE_LINK)
				&& (PowerOfMany.getPoweredAlly() != null || Stasis.getStasisAlly() != null);
	}

	@Override
	public float chargeUse(Hero hero) {
		return 2;
	}

	@Override
	public void onCast(HolyTome tome, Hero hero) {

		int duration = Math.round(6.67f + 3.33f*Dungeon.hero.pointsInTalent(Talent.LIFE_LINK));

		Char ally = PowerOfMany.getPoweredAlly();

		if (ally != null) {
			hero.sprite.zap(ally.pos);
			hero.sprite.parent.add(
					new Beam.HealthRay(hero.sprite.center(), ally.sprite.center()));
			Sample.INSTANCE.play( Assets.Sounds.RAY );

			Buff.prolong(hero, LifeLink.class, duration).object = ally.id();
		} else {
			ally = Stasis.getStasisAlly();
			hero.sprite.operate(hero.pos);
			hero.sprite.parent.add(
					new Beam.HealthRay(DungeonTilemap.tileCenterToWorld(hero.pos), hero.sprite.center()));
			Sample.INSTANCE.play( Assets.Sounds.RAY );
		}

		Buff.prolong(ally, LifeLink.class, duration).object = hero.id();
		Buff.prolong(ally, LifeLinkSpellBuff.class, duration);

		if (ally == Stasis.getStasisAlly()){
			ally.buff(LifeLink.class).clearTime();
			ally.buff(LifeLinkSpellBuff.class).clearTime();
		}

		hero.spendAndNext(Actor.TICK);

		onSpellCast(tome, hero);

	}

	public static class LifeLinkSpellBuff extends FlavourBuff{

		{
			type = buffType.POSITIVE;
		}

		@Override
		public int icon() {
			return BuffIndicator.HOLY_ARMOR;
		}

		@Override
		public float iconFadePercent() {
			int duration = Math.round(6.67f + 3.33f*Dungeon.hero.pointsInTalent(Talent.LIFE_LINK));
			return Math.max(0, (duration - visualcooldown()) / duration);
		}
	}
}
