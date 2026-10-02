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

public class TornPage extends RemainsItem {

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
