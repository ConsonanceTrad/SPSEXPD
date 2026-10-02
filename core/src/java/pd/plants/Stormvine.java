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
import pd.actors.buffs.Buff;
import pd.actors.buffs.Levitation;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.mobs.Mob;
import pd.items.equipment.weapon.missiles.arrows.ShockFruit;
import pd.levels.traps.Trap;
import pd.messages.InlineText;

public class Stormvine extends Plant {
	//SPSEXPD: inline Chinese text (generated from messages/plants/zh)
	static {
		InlineText.of(Stormvine.class)
			.t("name", "风暴藤")
			.t("desc", "引力似乎并不能正常地作用在风暴藤上，它蓝色的藤蔓能够\"挂\"在空中。任何被风暴藤缠到的生物也被这种奇怪的引力影响，并失去方向感。")
			.t("warden_desc", "守望者能够操纵风暴藤的魔力，因而在踩踏之后可以获得短暂飘浮的能力。")
			.t("seed.name", "风暴藤之种")
			.t("exstormvine.name", "风暴藤果丛")
			.t("exstormvine.desc", "生长乱流果的果丛。");
	}


	{
		image = 9;
		seedClass = Seed.class;
	}

	@Override
	public void activate( Char ch ) {
		if (Dungeon.level.heaps.get(pos) != null) Dungeon.level.heaps.get(pos).shockhit();

		if (ch != null) {
			if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN){
				Buff.affect(ch, Levitation.class, Levitation.DURATION/2f);
			} else {
				if (ch instanceof Mob){
					Buff.prolong(ch, Trap.HazardAssistTracker.class, Trap.HazardAssistTracker.DURATION);
				}
				Buff.affect(ch, Vertigo.class, Vertigo.DURATION);
			}
		}
	}

	public static class Seed extends Plant.Seed {
		{
			image = ConsumPotionSeedSeedDict.SEED_STORMVINE;

			plantClass = Stormvine.class;
			explantClass = ExStormvine.class;
		}
	}

	public static class ExStormvine extends SpsFruitBush {
		{ image = 9; harvestCount = 3; harvestClass = ShockFruit.class; }
	}
}
