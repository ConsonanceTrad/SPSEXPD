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

package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

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
