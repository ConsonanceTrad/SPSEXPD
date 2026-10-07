/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.items.Heap;

import pd.actors.Char;
import pd.actors.hero.Hero;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.items.*;
import pd.items.consum.food.fusion.Nut;
import pd.items.consum.potions.PotionOfTransmute;
import pd.messages.Messages;
import render.utils.math.Random;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.items.Generator;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.InlineText;

import java.util.ArrayList;

public class SeedFruit extends SpsFruit {
	static {
		InlineText.of(SeedFruit.class)
			.t("name", "种子果实")
			.t("desc", "人工种植的种子荚结出的果实。落地会炸出随机种子，食用则可缓慢治愈自己。");
	}

	public SeedFruit() { this(1); }
	public SeedFruit(int number) { super(pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_POD, 10, 10); quantity(number); }


	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) {
			Heap heap = Dungeon.level.drop(Generator.random(Generator.Category.SEED), cell);
			if (heap.sprite != null) heap.sprite.drop(cell);
		} else super.onThrow(cell);
	}

	@Override protected void onEat(Hero hero) {
		//SPSEXPD: 缓慢治愈（与治疗药剂同机制），大型果实为小型果实的 3 倍
		Buff.affect(hero, Healing.class).setHeal(Math.max(1, Math.round(scaled(hero.HT * 0.07f))), 0.25f, 0, true);
	}
}
