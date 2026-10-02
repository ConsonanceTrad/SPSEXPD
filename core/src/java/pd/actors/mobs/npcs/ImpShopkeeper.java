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

package pd.actors.mobs.npcs;

import pd.Dungeon;
import pd.actors.buffs.AscensionChallenge;
import pd.messages.Messages;
import pd.sprites.ImpSprite;
import pd.messages.InlineText;

public class ImpShopkeeper extends Shopkeeper {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(ImpShopkeeper.class)
			.t("name", "野心勃勃的小恶魔")
			.t("greetings", "你好，%s！")
			.t("greetings_ascent", "%s你都做了些什么？要做买卖就快点，我可不想在这里久留！")
			.t("thief", "我本以为我可以相信你！")
			.t("buyback", "小恶魔爽快地退还了你的物品。")
			.t("desc", "小恶魔在恶魔大厅的入口前摆了个小摊。在这能看到一张友好的面孔是挺不错的，但它商品的标价看起来可一点儿也不友好。");
	}


	{
		spriteClass = ImpSprite.class;
		properties.add(Property.DEMONIC);
	}
	
	private boolean seenBefore = false;
	
	@Override
	protected boolean act() {

		if (!seenBefore && Dungeon.level.heroFOV[pos]) {
			if (Dungeon.hero.buff(AscensionChallenge.class) == null) {
				yell(Messages.get(this, "greetings", Messages.titleCase(Dungeon.hero.name())));
			} else {
				yell(Messages.get(this, "greetings_ascent", Messages.titleCase(Dungeon.hero.name())));
			}
			seenBefore = true;
		}

		return super.act();
	}
}
