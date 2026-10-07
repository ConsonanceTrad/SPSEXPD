/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

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

import pd.items.TransmutationBall;
import pd.messages.InlineText;

public class TransmuteFruit extends SpsFruit {
	static {
		InlineText.of(TransmuteFruit.class)
			.t("name", "转换果实")
			.t("desc", "人工种植的转换笼结出的果实。命中时会把普通怪物嬗变为本层允许的另一种，食用则短暂提升法术强度。");
	}

	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_TRANSMUTE_CAGE; }//SPSEXPD: 贴图待指认

	public TransmuteFruit() { this(1); }
	public TransmuteFruit(int number) { super(pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_TRANSMUTE_CAGE, 10, 10); quantity(number); }


	@Override public int proc(Char attacker, Char defender, int damage) {
		//SPSEXPD: 嬗变为单一目标效果，不随果实大小放大
		if (defender != attacker) PotionOfTransmute.transmute(defender);
		return super.proc(attacker, defender, 0);
	}

	@Override protected void onEat(Hero hero) {
		//SPSEXPD: 大型果实时长 ×3
		Buff.affect(hero, SuperArcane.class, scaled(20f)).level(1);
	}
}
