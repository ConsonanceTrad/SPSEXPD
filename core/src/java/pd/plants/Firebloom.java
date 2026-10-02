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
import pd.actors.blobs.Blob;
import pd.actors.blobs.Fire;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FireImbue;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.mobs.Mob;
import pd.effects.CellEmitter;
import pd.effects.particles.FlameParticle;
import pd.items.equipment.weapon.missiles.arrows.FireFruit;
import pd.levels.traps.Trap;
import pd.scenes.GameScene;
import pd.messages.InlineText;

public class Firebloom extends Plant {
	//SPSEXPD: inline Chinese text (generated from messages/plants/zh)
	static {
		InlineText.of(Firebloom.class)
			.t("name", "烈焰花")
			.t("desc", "烈焰花被任何物品触碰到时，都会化为一团火焰。")
			.t("warden_desc", "_守望者_能将有害的火焰转化为短时的烈焰之力。")
			.t("$seed.name", "烈焰花之种")
			.t("$exfirebloom.name", "烈焰花果丛")
			.t("$exfirebloom.desc", "生长火焰果的果丛。");
	}



	
	{
		image = 0;
		seedClass = Seed.class;
	}
	
	@Override
	public void activate( Char ch ) {
		
		if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN){
			Buff.affect(ch, FireImbue.class).set( FireImbue.DURATION*0.3f );
		}

		if (ch instanceof Mob){
			Buff.prolong(ch, Trap.HazardAssistTracker.class, Trap.HazardAssistTracker.DURATION);
		}
		
		GameScene.add( Blob.seed( pos, 2, Fire.class ) );
		
		if (Dungeon.level.heroFOV[pos]) {
			CellEmitter.get( pos ).burst( FlameParticle.FACTORY, 5 );
		}
	}
	
	public static class Seed extends Plant.Seed {
		{
			image = ConsumPotionSeedSeedDict.SEED_FIREBLOOM;

			plantClass = Firebloom.class;
			explantClass = ExFirebloom.class;
		}
	}

	public static class ExFirebloom extends SpsFruitBush {
		{ image = 0; harvestCount = 3; harvestClass = FireFruit.class; }
	}
}
