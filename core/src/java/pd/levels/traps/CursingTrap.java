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
import pd.actors.hero.Hero;
import pd.effects.CellEmitter;
import pd.effects.particles.ShadowParticle;
import pd.items.EquipableItem;
import pd.items.Heap;
import pd.items.Item;
import pd.messages.Messages;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class CursingTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(CursingTrap.class)
			.t("name", "诅咒陷阱")
			.t("curse", "你身上的装备被诅咒了！")
			.t("desc", "这个陷阱充满了诅咒的力量。触发它会诅咒你身上的部分装备。");
	}


	{
		color = VIOLET;
		shape = LARGE_DOT;
	}

	@Override
	public void activate() {
		if (Dungeon.level.heroFOV[pos]) {
			CellEmitter.get(pos).burst(ShadowParticle.UP, 5);
			Sample.INSTANCE.play(Assets.Sounds.CURSED);
		}

		Heap heap = Dungeon.level.heaps.get(pos);
		if (heap != null) {
			for (Item item : heap.items) {
				if (item.isUpgradable()) curse(item);
			}
		}

		if (Dungeon.hero != null && Dungeon.hero.pos == pos) {
			curse(Dungeon.hero);
		}
	}

	public static void curse(Hero hero) {
		curse(hero.belongings.weapon);
		curse(hero.belongings.armor);
		curse(hero.belongings.artifact);
		curse(hero.belongings.misc);
		curse(hero.belongings.ring);
		curse(hero.belongings.accessory4);
		curse(hero.belongings.accessory5);
		curse(hero.belongings.badge);
		curse(hero.belongings.secondWep);
		curse(hero.belongings.secondArmor);

		if (hero.sprite != null) {
			EquipableItem.equipCursed(hero);
			GLog.n(Messages.get(CursingTrap.class, "curse"));
		}
		Item.updateQuickslot();
	}

	private static void curse(Item item) {
		if (item != null) item.cursed = item.cursedKnown = true;
	}
}
