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
import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.AdrenalineSurge;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.effects.CellEmitter;
import pd.effects.particles.LeafParticle;
import pd.items.Gold;
import pd.levels.GroundItems;
import pd.scenes.GameScene;
import pd.messages.InlineText;

public class Rotberry extends Plant {
	//SPSEXPD: inline Chinese text (generated from messages/plants/zh)
	static {
		InlineText.of(Rotberry.class)
			.t("name", "腐莓")
			.t("desc", "未成熟的腐莓丛结出的莓果尝起来更像是甜蜜的死亡。经过成年累月的生长，这株腐莓丛终会成熟为另一棵腐莓核心。被踩踏后，这株未成熟的腐莓会释放少量毒气。")
			.t("warden_desc", "遭到践踏时腐莓丛通常只会喷出一小股毒气，但_守望者_却能联结其中的魔力，在短时间内提升力量！")
			.t("discover_hint", "你可在某个任务中使用其种子种植该植物。")
			.t("$seed.name", "腐莓之种")
			.t("$seed.discover_hint", "你可在某个任务中找到该物品。")
			.t("$exrotberry.name", "腐莓果丛")
			.t("$exrotberry.desc", "生长腐莓种子和金币的果丛。");
	}




	{
		image = 7;
		seedClass = Seed.class;
	}

	@Override
	public void activate( Char ch ) {
		if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN){
			Buff.affect(ch, AdrenalineSurge.class).reset(1, AdrenalineSurge.DURATION);
		} else {
			GameScene.add( Blob.seed( pos, 100, ToxicGas.class ) );
		}
	}
	
	@Override
	public void wither() {
		GroundItems.uproot( Dungeon.level,  pos );
		
		if (Dungeon.level.heroFOV[pos]) {
			CellEmitter.get( pos ).burst( LeafParticle.GENERAL, 6 );
		}

		//seed always drops, no lotus benefit
		Dungeon.level.drop( new Seed(), pos ).sprite.drop();
	}

	public static class Seed extends Plant.Seed {
		{
			image = ConsumPotionSeedSeedDict.SEED_ROT_BERRY;

			plantClass = Rotberry.class;
			explantClass = ExRotberry.class;

			unique = true;
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

	public static class ExRotberry extends SpsFruitBush {
		{ image = 7; centerClass = Rotberry.Seed.class; }
	}
}
