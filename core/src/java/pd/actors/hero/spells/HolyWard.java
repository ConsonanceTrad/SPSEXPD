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
import pd.actors.hero.HeroSubClass;
import pd.effects.Enchanting;
import pd.items.Item;
import pd.items.equipment.artifacts.HolyTome;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.ui.HeroIcon;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class HolyWard extends ClericSpell {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(HolyWard.class)
			.t("name", "神圣护甲")
			.t("glyph_name", "神圣%s")
			.t("glyph_desc", "这个刻印略微增加了护甲可防御的伤害量。")
			.t("short_desc", "临时覆盖刻印以强化护甲防御。")
			.t("desc", "牧师赋予其身穿护甲神圣刻印，增加护甲1点防御。该法术施法不耗时。\n\n该刻印持续50回合，并会在持续时间内覆盖任何护甲已有的正面刻印。牧师即使没有护甲也能触发神圣刻印效果。")
			.t("desc_paladin", "_圣骑士施放该法术时效果更强。_神圣护甲的额外伤害防御提升至3点并不再覆盖已有的附魔，而在神圣护甲生效时施放其他法术所使用的每点充能都会延长10回合的法术效果。")
			.t("$holyarmbuff.name", "神圣护甲")
			.t("$holyarmbuff.desc", "牧师已赋予其身穿护甲神圣刻印，临时覆盖任何已有刻印并使护甲额外防御1点伤害。\n\n剩余回合数：%s")
			.t("$holyarmbuff.desc_paladin", "圣骑士已赋予其身穿护甲神圣刻印，使护甲额外防御3点伤害。\n\n神圣护甲生效时施放其他法术所消耗的每点充能都会延长法术效果10回合。\n\n剩余回合数：%s");
	}




	public static final HolyWard INSTANCE = new HolyWard();

	@Override
	public int icon() {
		return HeroIcon.HOLY_WARD;
	}

	@Override
	public void onCast(HolyTome tome, Hero hero) {

		Buff.affect(hero, HolyArmBuff.class, 50f);
		Item.updateQuickslot();

		Sample.INSTANCE.play(Assets.Sounds.READ);

		hero.sprite.operate(hero.pos);
		if (hero.belongings.armor() != null) Enchanting.show(hero, hero.belongings.armor());

		onSpellCast(tome, hero);
	}

	@Override
	public String desc(){
		String desc = Messages.get(this, "desc");
		if (Dungeon.hero.subClass == HeroSubClass.PALADIN){
			desc += "\n\n" + Messages.get(this, "desc_paladin");
		}
		return desc + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
	}

	public static class HolyArmBuff extends FlavourBuff {

		public static final float DURATION	= 50f;

		{
			type = buffType.POSITIVE;
		}

		@Override
		public int icon() {
			return BuffIndicator.HOLY_ARMOR;
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}

		@Override
		public String desc() {
			if (Dungeon.hero.subClass == HeroSubClass.PALADIN){
				return Messages.get(this, "desc_paladin", dispTurns());
			} else {
				return Messages.get(this, "desc", dispTurns());
			}
		}

		@Override
		public void detach() {
			super.detach();
			Item.updateQuickslot();
		}

		public void extend(float extension){
			if (cooldown()+extension <= 2*DURATION){
				spend(extension);
			} else {
				postpone(2*DURATION);
			}
		}
	}

}
