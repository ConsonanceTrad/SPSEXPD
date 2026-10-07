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

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Recharging;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.effects.Flare;
import pd.effects.SpellSprite;
import pd.items.Generator;
import pd.messages.InlineText;

public class Starflower extends Plant {
	//SPSEXPD: inline Chinese text (generated from messages/plants/zh)
	static {
		InlineText.of(Starflower.class)
			.t("name", "星陨花")
			.t("desc", "星陨花较为罕见，据说其能够为任何接触它的人赋予神圣之力。")
			.t("warden_desc", "_守望者_在踩踏星陨花时不仅能受到祝福，还能获得较长时间的法杖充能。")
			.t("$seed.name", "星陨花之种")
			.t("$exstarflower.name", "星陨花果丛")
			.t("$exstarflower.desc", "生长诺恩石的果丛。");
	}




	{
		image = 11;
		seedClass = Seed.class;
	}

	@Override
	public void activate( Char ch ) {

		if (ch != null) {
			Buff.prolong(ch, Bless.class, Bless.DURATION);
			if (Dungeon.level.heroFOV[ch.pos]){
				new Flare(6, 32).color(0xFFFF00, true).show(ch.sprite, 2f);
			}
			if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN){
				Buff.prolong(ch, Recharging.class, Recharging.DURATION);
				SpellSprite.show( ch, SpellSprite.CHARGE );
			}
		}

	}

	public static class Seed extends Plant.Seed{

		{
			image = ConsumPotionSeedSeedDict.SEED_STARFLOWER_0;

			plantClass = Starflower.class;
			explantClass = ExStarflower.class;
		}
		
		@Override
		public int value() {
			return 30 * quantity;
		}

		@Override
		public int energyVal() {
			return 3 * quantity;
		}
	}

	public static class ExStarflower extends SpsFruitBush {
		//SPSEXPD: 星陨花果丛不再额外掉落魔法矿石（诺恩石）
		{ image = 11; }
	}
}
