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
import pd.actors.buffs.ShieldBuff;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.cleric.AscendedForm;
import pd.effects.Flare;
import pd.items.equipment.artifacts.HolyTome;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.ui.HeroIcon;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class DivineIntervention extends ClericSpell {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(DivineIntervention.class)
			.t("name", "神圣干预")
			.t("short_desc", "大幅强化牧师与盟友的护盾。")
			.t("desc", "牧师借助圣典向自身与盟友体内注入无懈可击的神力，提升全体护盾至%1$d点并延长%2$d回合超凡升天。该法术无可匹敌的防御力与其极高的充能消耗相匹配。\n\n与超凡升天提供的其他护盾一样，这种护盾不会正常衰减，但在超凡升天结束时会立即消失。");
	}




	public static DivineIntervention INSTANCE = new DivineIntervention();

	@Override
	public int icon() {
		return HeroIcon.DIVINE_INTERVENTION;
	}

	@Override
	public float chargeUse(Hero hero) {
		return 5;
	}

	@Override
	public boolean canCast(Hero hero) {
		return super.canCast(hero)
				&& hero.hasTalent(Talent.DIVINE_INTERVENTION)
				&& hero.buff(AscendedForm.AscendBuff.class) != null
				&& !hero.buff(AscendedForm.AscendBuff.class).divineInverventionCast;
	}

	@Override
	public void onCast(HolyTome tome, Hero hero) {

		Sample.INSTANCE.play(Assets.Sounds.CHARGEUP, 1, 1.2f);
		hero.sprite.operate(hero.pos);

		for (Char ch : Actor.chars()){
			if (ch.alignment == Char.Alignment.ALLY && ch != hero){
				Buff.affect(ch, DivineShield.class).setShield(100 + 50*hero.pointsInTalent(Talent.DIVINE_INTERVENTION));
				new Flare(6, 32).color(0xFFFF00, true).show(ch.sprite, 2f);
			}
		}

		hero.spendAndNext( 1f );
		onSpellCast(tome, hero);

		//we apply buffs here so that the 5 charge cost and shield boost do not stack
		hero.buff(AscendedForm.AscendBuff.class).setShield(100 + 50*hero.pointsInTalent(Talent.DIVINE_INTERVENTION));
		new Flare(6, 32).color(0xFFFF00, true).show(hero.sprite, 2f);

		hero.buff(AscendedForm.AscendBuff.class).divineInverventionCast = true;
		hero.buff(AscendedForm.AscendBuff.class).extend(2+hero.pointsInTalent(Talent.DIVINE_INTERVENTION));

	}

	@Override
	public String desc() {
		int shield = 100 + 50*Dungeon.hero.pointsInTalent(Talent.DIVINE_INTERVENTION);
		int leftBonus = 2+Dungeon.hero.pointsInTalent(Talent.DIVINE_INTERVENTION);
		return Messages.get(this, "desc", shield, leftBonus) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
	}

	public static class DivineShield extends ShieldBuff{

		{
			shieldUsePriority = 1;
		}

		@Override
		public boolean act() {

			if (Dungeon.hero == null || Dungeon.hero.buff(AscendedForm.AscendBuff.class) == null){
				detach();
			}

			spend(TICK);
			return true;
		}

		@Override
		public int shielding() {
			if (Dungeon.hero == null || Dungeon.hero.buff(AscendedForm.AscendBuff.class) == null){
				return 0;
			}
			return super.shielding();
		}

		@Override
		public void fx(boolean on) {
			if (on) target.sprite.add(CharSprite.State.SHIELDED);
			else    target.sprite.remove(CharSprite.State.SHIELDED);
		}
	}
}
