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

package pd.levels.traps;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.utils.GLog;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.PathFinder;
import render.utils.Random;

public class RockfallTrap extends Trap {

	{
		color = YELLOW;
		shape = LARGE_DOT;
	}

	@Override
	public void activate() {
		boolean seen = false;
		for (int offset : PathFinder.NEIGHBOURS9) {
			int cell = pos + offset;
			if (cell < 0 || cell >= Dungeon.level.length() || Dungeon.level.solid[cell]) continue;

			if (Dungeon.level.heroFOV[cell] && Game.instance != null && Game.scene() != null) {
				int source = cell - Dungeon.level.width();
				if (source >= 0) CellEmitter.get(source).start(Speck.factory(Speck.ROCK), 0.07f, 10);
				seen = true;
			}

			Char target = Actor.findChar(cell);
			if (target == null || !target.isAlive()) continue;
			int legacyDepth = Dungeon.legacyDepth();
			int damage = Random.NormalIntRange(legacyDepth, legacyDepth * 2);
			damage -= Random.IntRange(0, target.drRoll());
			target.damage(Math.max(damage, 0), this);
			Buff.prolong(target, Paralysis.class, Paralysis.DURATION / 2f);

			if (!target.isAlive() && target == Dungeon.hero) {
				Dungeon.fail(this);
				if (target.sprite != null) GLog.n(Messages.get(this, "ondeath"));
			}
		}

		if (seen) {
			PixelScene.shake(3, 0.7f);
			Sample.INSTANCE.play(Assets.Sounds.ROCKS);
		}
	}
}
