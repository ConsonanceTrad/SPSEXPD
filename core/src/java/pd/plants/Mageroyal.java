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

package pd.plants;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.Char;
import pd.actors.buffs.BlobImmunity;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.items.consum.potions.PotionOfHealing;
import pd.messages.Messages;
import pd.utils.GLog;
import pd.messages.InlineText;

public class Mageroyal extends Plant {
	//SPSEXPD: inline Chinese text (generated from messages/plants/zh)
	static {
		InlineText.of(Mageroyal.class)
			.t("name", "魔皇草")
			.t("refreshed", "你感觉浑身清爽。")
			.t("desc", "魔皇草的带刺花朵含有一种化学物质，因其强大的中和性质而闻名。任何踏入这株植物的东西将会被净化掉许多负面效果。")
			.t("warden_desc", "当踩踏一株魔皇草时，_守望者_在获得中和效果以外，还能短暂地对所有环境影响免疫。")
			.t("seed.name", "魔皇草之种");
	}


	{
		image = 7;
		seedClass = Seed.class;
	}

	@Override
	public void activate( Char ch ) {

		if (ch != null) {
			PotionOfHealing.cure(ch);

			if (ch instanceof Hero) {
				GLog.i( Messages.get(this, "refreshed") );

				if (((Hero) ch).subClass == HeroSubClass.WARDEN){
					Buff.affect(ch, BlobImmunity.class, BlobImmunity.DURATION/2f);
				}
			}
		}
	}

	public static class Seed extends Plant.Seed {
		{
			image = ConsumPotionSeedSeedDict.SEED_MAGEROYAL_0;

			plantClass = Mageroyal.class;
		}
	}
}