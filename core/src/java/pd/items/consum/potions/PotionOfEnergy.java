package pd.items.consum.potions;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Dewcharge;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.messages.InlineText;

/** SPSEXPD: 由集露草果实酿造，饮用后获得短暂的露珠爆破状态。 */
public class PotionOfEnergy extends SpsPotion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfEnergy.class)
			.t("name", "高能药剂")
			.t("desc", "以集露草果实酿成的能量药剂。饮用后获得短暂的露珠爆破状态。");
	}

	{ image = SpecificPlaceHolderDict.POTION_HOLDER_0; }

	@Override public void apply(Hero hero) {
		Dewcharge.charge(hero, 240f);
		hero.sprite.emitter().start(Speck.factory(Speck.LIGHT), 0.4f, 4);
	}

	@Override public int value() { return 40 * quantity; }
}
