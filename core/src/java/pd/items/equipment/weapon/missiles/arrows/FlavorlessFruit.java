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

import pd.actors.blobs.Water;
import pd.messages.InlineText;

public class FlavorlessFruit extends SpsFruit {
	static {
		InlineText.of(FlavorlessFruit.class)
			.t("name", "无味果实")
			.t("desc", "人工种植的无味果结出的果实。落地会碎成小块无味果，食用饱腹感与干粮相当。");
	}

	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_BLANDFRUIT; }//SPSEXPD: 贴图待指认

	public FlavorlessFruit() { this(1); }
	public FlavorlessFruit(int number) { super(pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_BLANDFRUIT, 10, 10); quantity(number); }


	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) {
			Heap heap = Dungeon.level.drop(new FlavorlessFruitPiece(), cell);
			if (heap.sprite != null) heap.sprite.drop(cell);
		} else super.onThrow(cell);
	}

	@Override protected float eatEnergy() {
		return Hunger.HUNGRY;
	}
}
