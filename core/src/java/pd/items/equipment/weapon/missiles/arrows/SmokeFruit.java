/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.actors.hero.Hero;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.items.*;
import pd.items.consum.food.fusion.Nut;
import pd.items.consum.potions.PotionOfTransmute;
import pd.messages.Messages;
import render.utils.math.Random;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.Char;
import pd.actors.blobs.DarkGas;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.messages.InlineText;
import pd.atlas.items.SpecificPlaceHolderDict;

public class SmokeFruit extends SpsFruit {
	{
		image = pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_FADELEAF;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SmokeFruit.class)
			.t("name", "消逝果实")
			.t("desc", "人工种植的消逝草结出的果实。落地会散出黑暗，命中则致盲目标。");
	}



	public SmokeFruit() { this(1); }
	public SmokeFruit(int number) { super(pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_FADELEAF, 10, 10); quantity(number); }


	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) seedArea(cell, 6, DarkGas.class);
		else super.onThrow(cell);
	}

	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.prolong(defender, Blindness.class, 2f);
		return super.proc(attacker, defender, 0);
	}

	@Override protected void onEat(Hero hero) {
		Buff.prolong(hero, Blindness.class, 4f);
	}
}
