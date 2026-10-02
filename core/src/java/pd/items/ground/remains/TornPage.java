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

package pd.items.ground.remains;

import pd.atlas.items.ConsumUsefulCorpseRelicsDict;

import pd.Assets;
import pd.actors.hero.Hero;
import pd.effects.FloatingText;
import pd.sprites.CharSprite;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class TornPage extends RemainsItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TornPage.class)
			.t("name", "圣典残页")
			.t("desc", "这片书页看起来是从一位已安息主怀的牧师的圣典上撕扯下来的。其上依旧残存有一丝神力，你可以使用它恢复少许生命值。但若如此书页也会随之烟消云散。");
	}




	{
		image = ConsumUsefulCorpseRelicsDict.TORN_PAGE_0;
	}

	@Override
	protected void doEffect(Hero hero) {
		int toHeal = Math.round(hero.HT/10f);
		hero.HP = Math.min(hero.HP + toHeal, hero.HT);
		hero.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(toHeal), FloatingText.HEALING );
		Sample.INSTANCE.play( Assets.Sounds.READ );
	}

}
