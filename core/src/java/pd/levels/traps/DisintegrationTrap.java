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
import pd.items.equipment.bags.Bag;
import pd.messages.Messages;
import pd.tiles.DungeonTilemap;
import pd.utils.GLog;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import pd.messages.InlineText;

public class DisintegrationTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(DisintegrationTrap.class)
			.t("name", "解离陷阱")
			.t("one", "陷阱解离了你的%s！")
			.t("some", "陷阱解离了你一部分的%s！")
			.t("ondeath", "你被解离陷阱击杀...")
			.t("desc", "被触发时，这个陷阱将会用解离射线袭击离它最近的目标，造成显著伤害的同时破坏物品。\n\n幸运的是，触发机关并没有被隐藏起来。");
	}


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
