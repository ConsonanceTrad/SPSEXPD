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
import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.cleric.PowerOfMany;
import pd.effects.FloatingText;
import pd.items.equipment.artifacts.HolyTome;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.ui.HeroIcon;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class LayOnHands extends TargetedClericSpell {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(LayOnHands.class)
			.t("name", "圣疗之触")
			.t("short_desc", "立即治疗附近一个单位或使圣骑士获得护盾。")
			.t("desc", "圣骑士以其双手引导出神圣能量，治疗或保护其所触的任何单位。\n\n圣骑士可施放该法术使附近任何单位获得%1$d点治疗，或使自身获得%1$d点护盾。该法术的溢出治疗将被转化为护盾。\n\n该法术可被立即施放、重复施放，但无法立即施加超过三次施法数值的护盾。");
	}




	public static LayOnHands INSTANCE = new LayOnHands();

	@Override
	public int icon() {
		return HeroIcon.LAY_ON_HANDS;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", 10 + 5*Dungeon.hero.pointsInTalent(Talent.LAY_ON_HANDS)) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
	}

	@Override
	public int targetingFlags(){
		return -1; //auto-targeting behaviour is often wrong, so we don't use it
	}

	@Override
	public boolean canCast(Hero hero) {
		return super.canCast(hero) && hero.hasTalent(Talent.LAY_ON_HANDS);
	}

	@Override
	protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
		if (target == null) {
			return;
		}

		if (Dungeon.level.distance(hero.pos, target) > 1){
			GLog.w(Messages.get(this, "invalid_target"));
			return;
		}

		Char ch = Actor.findChar(target);
		if (ch == null){
			GLog.w(Messages.get(this, "no_target"));
			return;
		}

		Sample.INSTANCE.play(Assets.Sounds.TELEPORT);

		affectChar(hero, ch);

		if (ch == hero){
			hero.sprite.operate(ch.pos);
			hero.next();
		} else {
			hero.sprite.zap(ch.pos);
			hero.next();
		}

		Char ally = PowerOfMany.getPoweredAlly();
		if (ally != null && ally.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null){
			if (ch == hero){
				affectChar(hero, ally); //if cast on hero, duplicate to ally
			} else if (ally == ch) {
				affectChar(hero, hero); //if cast on ally, duplicate to hero
			}
		}

		onSpellCast(tome, hero);

	}

	private void affectChar(Hero hero, Char ch){
		int totalHeal = 10 + 5*hero.pointsInTalent(Talent.LAY_ON_HANDS);
		int totalBarrier = 0;
		if (ch == hero){
			Barrier barrier = Buff.affect(ch, Barrier.class);
			totalBarrier = totalHeal;
			totalBarrier = Math.min(3*totalHeal - barrier.shielding(), totalBarrier);
			totalBarrier = Math.max(0, totalBarrier);
			Buff.affect(ch, Barrier.class).incShield(totalBarrier);
			ch.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(totalBarrier), FloatingText.SHIELDING );
		} else {
			if (ch.HT - ch.HP < totalHeal){
				totalBarrier = totalHeal - (ch.HT - ch.HP);
				if (ch.HP != ch.HT) {
					ch.HP = ch.HT;
					ch.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(totalHeal - totalBarrier), FloatingText.HEALING);
				}
				if (totalBarrier > 0) {
					Barrier barrier = Buff.affect(ch, Barrier.class);
					totalBarrier = Math.min(3 * totalHeal - barrier.shielding(), totalBarrier);
					totalBarrier = Math.max(0, totalBarrier);
					if (totalBarrier > 0) {
						barrier.incShield(totalBarrier);
						ch.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(totalBarrier), FloatingText.SHIELDING);
					}
				}
			} else {
				ch.HP = ch.HP + totalHeal;
				ch.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(totalHeal), FloatingText.HEALING );
			}
		}
	}
}
