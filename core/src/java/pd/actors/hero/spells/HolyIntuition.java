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
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.effects.Identification;
import pd.items.EquipableItem;
import pd.items.Item;
import pd.items.equipment.artifacts.HolyTome;
import pd.items.equipment.wands.Wand;
import pd.messages.Messages;
import pd.ui.HeroIcon;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class HolyIntuition extends InventoryClericSpell {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(HolyIntuition.class)
			.t("name", "神圣预知")
			.t("prompt", "选择一个物品")
			.t("cursed", "你感觉到这件物品里潜伏着一股充满恶意的魔力。")
			.t("uncursed", "这个物品没有被诅咒。")
			.t("short_desc", "鉴定一个物品有无诅咒。")
			.t("desc", "牧师将其感知集中在一个物品上，不必装备物品就能判断其诅咒有无。");
	}


	public static final HolyIntuition INSTANCE = new HolyIntuition();

	@Override
	public int icon() {
		return HeroIcon.HOLY_INTUITION;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return (item instanceof EquipableItem || item instanceof Wand) && !item.isIdentified() && !item.cursedKnown;
	}

	@Override
	public float chargeUse(Hero hero) {
		return 4 - hero.pointsInTalent(Talent.HOLY_INTUITION);
	}

	@Override
	public boolean canCast(Hero hero) {
		return super.canCast(hero) && hero.hasTalent(Talent.HOLY_INTUITION);
	}

	@Override
	protected void onItemSelected(HolyTome tome, Hero hero, Item item) {
		if (item == null){
			return;
		}

		item.cursedKnown = true;

		if (item.cursed){
			GLog.w(Messages.get(this, "cursed"));
		} else {
			GLog.i(Messages.get(this, "uncursed"));
		}

		hero.spend( 1f );
		hero.busy();
		hero.sprite.operate(hero.pos);
		hero.sprite.parent.add( new Identification( hero.sprite.center().offset( 0, -16 ) ) );

		Sample.INSTANCE.play( Assets.Sounds.READ );
		onSpellCast(tome, hero);

	}

}
