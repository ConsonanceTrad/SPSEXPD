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
 */

package pd.levels.features;

import pd.Dungeon;
import pd.ShatteredPixelDungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.effects.CellEmitter;
import pd.effects.particles.LeafParticle;
import pd.levels.Level;
import pd.scenes.GameScene;
import com.watabou.noosa.Game;

/** The persistent grass tile used by SPS maps; trampling it never removes the tile. */
public final class OldHighGrass {

	public static void trample(Level level, int pos, Char ch) {
		if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN) {
			Buff.affect(ch, Invisibility.class, 4f);
		}

		if (Game.instance != null && ShatteredPixelDungeon.scene() instanceof GameScene) {
			CellEmitter.get(pos).burst(LeafParticle.LEVEL_SPECIFIC, 4);
			Dungeon.observe();
		}
	}

	private OldHighGrass() {
	}
}
