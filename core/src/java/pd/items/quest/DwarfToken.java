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

package pd.items.quest;

import pd.atlas.items.SpecificTaskDict;

import pd.actors.hero.Hero;
import pd.actors.mobs.npcs.Imp;
import pd.items.Item;
import pd.messages.Messages;
import pd.utils.GLog;
import pd.messages.InlineText;

public class DwarfToken extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DwarfToken.class)
			.t("name", "矮人徽记")
			.t("discard", "你弃置了多余的徽记。")
			.t("desc", "一块形状奇特的金属片，被宝库中的矮人守卫随身携带。或许这徽记也是将守卫禁锢于这座宝库的魔法的一部分？最好先带着它，或许还会有用。")
			.t("desc_old", "很多矮人和他们的造物都携带着这种小块金属，理由不详。兴许它是装饰物或什么身份识别牌。矮人都挺奇怪的。")
			.t("discover_hint", "你可在某个任务中找到该物品。");
	}



	
	{
		image = SpecificTaskDict.TOKEN_0;
		
		stackable = true;
		unique = true;
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean doPickUp(Hero hero, int pos) {
		if (Imp.Quest.mirrorUsed){
			GLog.i(Messages.get(this, "discard"));
			hero.next();
			return true;
		}
		return super.doPickUp(hero, pos);
	}

	@Override
	public String desc() {
		if (Imp.Quest.isOld()){
			return Messages.get(this, "desc_old");
		} else {
			return super.desc();
		}

	}
}
