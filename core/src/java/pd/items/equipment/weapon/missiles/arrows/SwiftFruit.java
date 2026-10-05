/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.items.*;
import pd.items.consum.food.fusion.Nut;
import pd.items.consum.potions.PotionOfTransmute;
import pd.messages.Messages;
import render.utils.math.Random;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.plants.Swiftthistle;

public class SwiftFruit extends SpsFruit {
	static {
		InlineText.of(SwiftFruit.class)
			.t("name", "速行果实")
			.t("desc", "人工种植的迅捷草结出的果实。命中会让目标陷入时间气泡，食用则让自己时间加速。");
	}

	public SwiftFruit() { this(1); }
	public SwiftFruit(int number) { super(pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_SWIFTTHISTLE, 10, 10); quantity(number); }



	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, Swiftthistle.TimeBubble.class).reset(2);
		return super.proc(attacker, defender, 0);
	}

	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Swiftthistle.TimeBubble.class).reset(4);
	}
}
