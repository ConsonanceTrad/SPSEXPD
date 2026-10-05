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

public class StarFruit extends SpsFruit {
	static {
		InlineText.of(StarFruit.class)
			.t("name", "星陨果实")
			.t("desc", "人工种植的星陨花结出的果实。落地会降下持续 4 回合的圣光审判之场，命中或食用则会赋予祝福。");
	}

	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_STARFLOWER; }//SPSEXPD: 贴图待指认

	public StarFruit() { this(1); }
	public StarFruit(int number) { super(pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_STARFLOWER, 10, 10); quantity(number); }


	@Override protected void onThrow(int cell) {
		//SPSEXPD: 投掷位置降下圣光审判之场（4 回合）
		if (landsAt(cell)) seedArea(cell, 3, pd.actors.blobs.effectblobs.HolyLight.class);
		else super.onThrow(cell);
	}

	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.prolong(defender, Bless.class, 10f);
		return super.proc(attacker, defender, 0);
	}

	@Override protected void onEat(Hero hero) {
		Buff.prolong(hero, Bless.class, 20f);
	}
}
