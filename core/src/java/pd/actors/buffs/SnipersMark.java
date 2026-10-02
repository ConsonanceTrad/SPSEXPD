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

package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.equipment.weapon.SpiritBow;
import pd.messages.Messages;
import pd.ui.ActionIndicator;
import pd.ui.BuffIndicator;
import pd.ui.HeroIcon;
import pd.ui.QuickSlotButton;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class SnipersMark extends FlavourBuff implements ActionIndicator.Action {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(SnipersMark.class)
			.t("name", "狙击标记")
			.t("action_name_snapshot", "速射")
			.t("action_name_volley", "连射")
			.t("action_name_sniper", "狙杀")
			.t("desc", "狙击手专注于最近射击的目标。她能够用灵能弓进行一次特殊攻击，攻击方式取决于弓的强化方式。\n\n未被强化的弓可以施展_速射_，进行一次低伤害但不消耗回合的射击。\n\n强化速度的弓可以施展三箭_连射_。每支箭矢伤害虽低，但仍能触发附魔效果。连射消耗1回合。\n\n强化伤害的弓可以施展_狙杀_。这支箭矢必定命中，根据目标距离造成额外伤害。狙杀消耗2回合。\n\n效果剩余时长：%s回合");
	}




	public int object = 0;
	public float percentDmgBonus = 0;

	private static final String OBJECT    = "object";
	private static final String BONUS    = "bonus";

	public static final float DURATION = 4f;

	{
		type = buffType.POSITIVE;
	}

	public void set(int object, float bonus){
		this.object = object;
		this.percentDmgBonus = bonus;
		SpiritBow bow = Dungeon.hero.belongings.getItem(SpiritBow.class);
		if (bow != null) {
			ActionIndicator.setAction(this);
		}
	}

	@Override
	public void detach() {
		super.detach();
		ActionIndicator.clearAction(this);
	}
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( OBJECT, object );
		bundle.put( BONUS, percentDmgBonus );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		object = bundle.getInt( OBJECT );
		percentDmgBonus = bundle.getFloat( BONUS );
	}

	@Override
	public int icon() {
		return BuffIndicator.MARK;
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

	@Override
	public String actionName() {
		SpiritBow bow = Dungeon.hero.belongings.getItem(SpiritBow.class);

		if (bow == null) return null;

		switch (bow.augment){
			case NONE: default:
				return Messages.get(this, "action_name_snapshot");
			case SPEED:
				return Messages.get(this, "action_name_volley");
			case DAMAGE:
				return Messages.get(this, "action_name_sniper");
		}
	}

	@Override
	public int actionIcon() {
		return HeroIcon.SNIPERS_MARK;
	}

	@Override
	public int indicatorColor() {
		return 0x444444;
	}

	@Override
	public void doAction() {
		
		Hero hero = Dungeon.hero;
		if (hero == null) return;
		
		SpiritBow bow = hero.belongings.getItem(SpiritBow.class);
		if (bow == null) return;
		
		SpiritBow.SpiritArrow arrow = bow.knockArrow();
		if (arrow == null) return;
		
		Char ch = (Char) Actor.findById(object);
		if (ch == null) return;
		
		int cell = QuickSlotButton.autoAim(ch, arrow);
		if (cell == -1) return;
		
		bow.sniperSpecial = true;
		bow.sniperSpecialBonusDamage = percentDmgBonus;
		
		arrow.cast(hero, cell);
		detach();
		
	}
}
