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
import pd.messages.InlineText;

public class StarEaterFruit extends SpsFruit {
	static {
		InlineText.of(StarEaterFruit.class)
			.t("name", "吞星果实")
			.t("desc", "人工种植的吞星花结出的果实。落地会散出酸蚀气体，食用则会灼伤自己。");
	}

	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_STAREATER; }//SPSEXPD: 贴图待指认

	public StarEaterFruit() { this(1); }
	public StarEaterFruit(int number) { super(pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_STAREATER, 10, 10); quantity(number); }


	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) seedArea(cell, 6, pd.actors.blobs.CorrosiveGas.class);
		else super.onThrow(cell);
	}

	@Override protected void onEat(Hero hero) {
		hero.damage(Math.max(1, Math.round(hero.HT * 0.2f)), this);
	}
}
