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
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.mobs.Mob;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.items.equipment.weapon.missiles.arrows.SmokeFruit;
import pd.levels.Level;
import pd.levels.Transitions;
import pd.levels.traps.Trap;
import pd.scenes.InterlevelScene;
import render.noosa.Game;
import pd.messages.InlineText;

public class Fadeleaf extends Plant {
	//SPSEXPD: inline Chinese text (generated from messages/plants/zh)
	static {
		InlineText.of(Fadeleaf.class)
			.t("name", "消逝草")
			.t("desc", "任何触碰到消逝草的生物都会被传送到当前层的一个随机地点。")
			.t("warden_desc", "_守望者_能进一步发挥消逝草的空间魔力，直接回到上一层的下楼梯处。")
			.t("$seed.name", "消逝草之种")
			.t("$exfadeleaf.name", "消逝草果丛")
			.t("$exfadeleaf.desc", "生长烟雾果的果丛。");
	}



	
	{
		image = 6;
		seedClass = Seed.class;
	}
	
	@Override
	public void activate( final Char ch ) {
		
		if (ch instanceof Hero) {
			
			((Hero)ch).curAction = null;
			
			if (((Hero) ch).subClass == HeroSubClass.WARDEN && Dungeon.interfloorTeleportAllowed()){

				Transitions.beforeTransition();
				InterlevelScene.mode = InterlevelScene.Mode.RETURN;
				InterlevelScene.returnDepth = Math.max(1, (Dungeon.depth - 1));
				InterlevelScene.returnBranch = 0;
				InterlevelScene.returnPos = -2;
				Game.switchScene( InterlevelScene.class );
				
			} else {
				ScrollOfTeleportation.teleportChar(ch, Fadeleaf.class);
			}
			
		} else if (ch instanceof Mob && !ch.properties().contains(Char.Property.IMMOVABLE)) {

			Buff.prolong(ch, Trap.HazardAssistTracker.class, Trap.HazardAssistTracker.DURATION);
			ScrollOfTeleportation.teleportChar(ch, Fadeleaf.class);

		}
		
		if (Dungeon.level.heroFOV[pos]) {
			CellEmitter.get( pos ).start( Speck.factory( Speck.LIGHT ), 0.2f, 3 );
		}
	}
	
	public static class Seed extends Plant.Seed {
		{
			image = ConsumPotionSeedSeedDict.SEED_FADELEAF_0;

			plantClass = Fadeleaf.class;
			explantClass = ExFadeleaf.class;
		}
	}

	public static class ExFadeleaf extends SpsFruitBush {
		{ image = 6; }
	}
}
