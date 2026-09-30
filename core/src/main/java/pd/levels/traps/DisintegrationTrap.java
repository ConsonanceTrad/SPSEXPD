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
import pd.ShatteredPixelDungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.effects.Beam;
import pd.items.Heap;
import pd.items.Item;
import pd.items.bags.Bag;
import pd.messages.Messages;
import pd.tiles.DungeonTilemap;
import pd.utils.GLog;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.Random;

public class DisintegrationTrap extends Trap {

	{
		color = RED;
		shape = LARGE_DOT;
	}

	@Override
	public void activate() {
		if (Dungeon.level.heroFOV[pos] && Game.instance != null && ShatteredPixelDungeon.scene() != null) {
			int width = Dungeon.level.width();
			ShatteredPixelDungeon.scene().add(new Beam.DeathRay(
					DungeonTilemap.tileCenterToWorld(pos - 1), DungeonTilemap.tileCenterToWorld(pos + 1)));
			ShatteredPixelDungeon.scene().add(new Beam.DeathRay(
					DungeonTilemap.tileCenterToWorld(pos - width), DungeonTilemap.tileCenterToWorld(pos + width)));
			Sample.INSTANCE.play(Assets.Sounds.RAY);
		}

		Heap heap = Dungeon.level.heaps.get(pos);
		if (heap != null) heap.explode();

		Char target = Actor.findChar(pos);
		if (target == null) return;
		int damage = Math.max(target.HT / 5, Random.Int(target.HP / 2, 2 * target.HP / 3));
		target.damage(damage, this);
		if (target != Dungeon.hero) return;

		Hero hero = (Hero)target;
		if (!hero.isAlive()) {
			Dungeon.fail(this);
			if (hero.sprite != null) GLog.n(Messages.get(this, "ondeath"));
			return;
		}

		Item item = hero.belongings.randomUnequipped();
		Bag bag = hero.belongings.backpack;
		if (item instanceof Bag) {
			bag = (Bag)item;
			item = Random.element(bag.items);
		}
		if (item == null || item.level() > 0 || item.unique) return;
		if (!item.stackable) {
			item.detachAll(bag);
			if (hero.sprite != null) GLog.w(Messages.get(this, "one", item.name()));
		} else {
			int count = Random.NormalIntRange(1, (item.quantity() + 1) / 2);
			for (int i = 0; i < count; i++) item.detach(bag);
			if (hero.sprite != null) GLog.w(Messages.get(this, "some", item.name()));
		}
	}
}
