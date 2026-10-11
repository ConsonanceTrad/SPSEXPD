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
		//SPSEXPD: 命中空地的效果已按用户要求移除——改为保留投掷物（落地可捡回）
		super.onThrow(cell);
	}

	/** SPSEXPD: 食用会灼伤自己——默认动作为「投掷」。 */
	@Override protected boolean harmfulOnEat() { return true; }

	@Override protected void onEat(Hero hero) {
		hero.damage(Math.max(1, Math.round(scaled(hero.HT * 0.2f))), this);
	}
}
