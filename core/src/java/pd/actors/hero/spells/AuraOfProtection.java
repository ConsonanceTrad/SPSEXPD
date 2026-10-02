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
import pd.actors.buffs.Buff;
import pd.actors.buffs.FlavourBuff;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.effects.Speck;
import pd.items.equipment.artifacts.HolyTome;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.ui.HeroIcon;
import render.noosa.audio.Sample;
import render.noosa.particles.Emitter;
import pd.messages.InlineText;

public class AuraOfProtection extends ClericSpell {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(AuraOfProtection.class)
			.t("name", "守御灵光")
			.t("short_desc", "强化圣骑士与附近盟友的防御。")
			.t("desc", "圣骑士开始辐射出保护性能量，在自身周围形成一片持续20回合的守御灵光。在圣骑士2格范围内的任何盟友(包括圣骑士自身)都会获得%1$d%%的伤害减免与%2$d%%的圣骑士护甲刻印强化。\n\n该伤害减免优先于其他减伤效果(例如护甲)生效。刻印强化会始终生效，但如果某单位(例如圣骑士自身、虹卫)已从圣骑士的刻印中获得效益，则该法术无法使其再次从中获得效益。")
			.t("aurabuff.name", "守御灵光")
			.t("aurabuff.desc", "圣骑士正在自身周围辐射出保护性能量。\n\n附近的任何盟友(包括圣骑士自身)都会获得伤害减免与圣骑士护甲刻印强化。\n\n剩余回合数：%s");
	}


	public static AuraOfProtection INSTANCE = new AuraOfProtection();

	@Override
	public int icon() {
		return HeroIcon.AURA_OF_PROTECTION;
	}

	@Override
	public String desc() {
		int dmgReduction = 10 + 10*Dungeon.hero.pointsInTalent(Talent.AURA_OF_PROTECTION);
		int glyphPow = 25 + 25*Dungeon.hero.pointsInTalent(Talent.AURA_OF_PROTECTION);
		return Messages.get(this, "desc", dmgReduction, glyphPow) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
	}

	@Override
	public float chargeUse(Hero hero) {
		return 2f;
	}

	@Override
	public boolean canCast(Hero hero) {
		return super.canCast(hero) && hero.hasTalent(Talent.AURA_OF_PROTECTION);
	}

	@Override
	public void onCast(HolyTome tome, Hero hero) {

		Buff.affect(hero,AuraBuff.class, AuraBuff.DURATION);

		Sample.INSTANCE.play(Assets.Sounds.READ);

		hero.spend( 1f );
		hero.busy();
		hero.sprite.operate(hero.pos);

		onSpellCast(tome, hero);

	}

	public static class AuraBuff extends FlavourBuff {

		public static float DURATION = 20f;

		private Emitter particles;

		{
			type = buffType.POSITIVE;
		}

		@Override
		public int icon() {
			return BuffIndicator.PROT_AURA;
		}

		@Override
		public void fx(boolean on) {
			if (on && (particles == null || particles.parent == null)){
				particles = target.sprite.emitter(); //emitter is much bigger than char so it needs to manage itself
				particles.pos(target.sprite, -32, -32, 80, 80);
				particles.fillTarget = false;
				particles.pour(Speck.factory(Speck.LIGHT), 0.02f);
			} else if (!on && particles != null){
				particles.on = false;
			}
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}

	}

}
