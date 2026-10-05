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

import pd.items.Dewdrop;
import pd.messages.InlineText;

public class DewFruit extends SpsFruit {
	static {
		InlineText.of(DewFruit.class)
			.t("name", "集露果实")
			.t("desc", "人工种植的集露草结出的果实。落地会凝结出一颗露珠，命中则赋予目标露珠爆破。");
	}

	public DewFruit() { this(1); }
	public DewFruit(int number) { super(pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_DEWCATCHER, 10, 10); quantity(number); }




	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) {
			Heap heap = Dungeon.level.drop(randomDewdrop(), cell);
			if (heap.sprite != null) heap.sprite.drop(cell);
		} else super.onThrow(cell);
	}

	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, Dewcharge.class, 15f);
		return super.proc(attacker, defender, 0);
	}

	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Dewcharge.class, 30f);
	}

	private static Item randomDewdrop() {
		switch (Random.Int(4)) {
			case 1: return new YellowDewdrop();
			case 2: return new RedDewdrop();
			case 3: return new VioletDewdrop();
			default: return new Dewdrop();
		}
	}
}
