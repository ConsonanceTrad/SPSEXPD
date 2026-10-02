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
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.utils.GLog;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import pd.messages.InlineText;

public class RockfallTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(RockfallTrap.class)
			.t("name", "落石陷阱")
			.t("ondeath", "你被落石砸扁了...")
			.t("desc", "这个陷阱和头顶上一片松散的岩石相连，触发它会导致石块崩塌砸向整个房间！如果这种陷阱不是在某个房间内，石块会砸向陷阱周围的一定区域。\n\n幸运的是，触发机关并没有被隐藏起来。");
	}




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
