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

public class HolyWeapon extends ClericSpell {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(HolyWeapon.class)
			.t("name", "神圣武器")
			.t("ench_name", "神圣%s")
			.t("ench_desc", "被神圣武器攻击的敌人会受到额外魔法伤害。")
			.t("short_desc", "临时覆盖附魔以强化武器伤害。")
			.t("desc", "牧师赋予其手持武器神圣附魔，使武器每次攻击敌人额外造成2点魔法伤害。该法术施法不耗时。\n\n该附魔持续50回合，并会在持续时间内覆盖任何武器已有的正面附魔。赤手空拳攻击敌人也能触发神圣附魔效果。")
			.t("desc_paladin", "_圣骑士施放该法术时效果更强。_神圣武器的额外魔法伤害提升至6点并不再覆盖已有的附魔，而在神圣武器生效时施放其他法术所使用的每点充能都会延长10回合的法术效果。")
			.t("$holywepbuff.name", "神圣武器")
			.t("$holywepbuff.desc", "牧师已赋予其手持武器神圣附魔，临时覆盖任何已有附魔并使武器每次攻击额外造成2点魔法伤害。\n\n剩余回合数：%s")
			.t("$holywepbuff.desc_paladin", "圣骑士已赋予其手持武器神圣附魔，使其攻击额外造成6点魔法伤害。\n\n神圣武器生效时施放其他法术所消耗的每点充能都会延长10回合的法术效果。\n\n剩余回合数：%s");
	}




	public static final HolyWeapon INSTANCE = new HolyWeapon();

	@Override
	public int icon() {
		return HeroIcon.HOLY_WEAPON;
	}

	@Override
	public float chargeUse(Hero hero) {
		return 2;
	}

	@Override
	public void onCast(HolyTome tome, Hero hero) {

		Buff.affect(hero, HolyWepBuff.class, 50f);
		Item.updateQuickslot();

		Sample.INSTANCE.play(Assets.Sounds.READ);

		hero.sprite.operate(hero.pos);
		if (hero.belongings.weapon() != null) Enchanting.show(hero, hero.belongings.weapon());

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

	public static class HolyWepBuff extends FlavourBuff {

		public static final float DURATION	= 50f;

		{
			type = buffType.POSITIVE;
		}

		@Override
		public int icon() {
			return BuffIndicator.HOLY_WEAPON;
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
