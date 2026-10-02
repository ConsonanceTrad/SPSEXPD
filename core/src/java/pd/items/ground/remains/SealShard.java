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
import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.effects.FloatingText;
import pd.sprites.CharSprite;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class SealShard extends RemainsItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SealShard.class)
			.t("name", "纹章残蜡")
			.t("desc", "这些细碎的红色蜡块似是源于一名葬身于此战士的纹章。你可以感受到其上仍残留的一缕执念，你可以用它来给自己提供一些护盾。但是，伴随着这缕执念转化为护盾，这些蜡块也会烟消云散。");
	}




	{
		image = ConsumUsefulCorpseRelicsDict.SEAL_SHARD_0;
	}

	@Override
	protected void doEffect(Hero hero) {
		Buff.affect(hero, Barrier.class).incShield(Math.round(hero.HT/5f));
		hero.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(Math.round(hero.HT/5f)), FloatingText.SHIELDING );
		Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
	}

}
