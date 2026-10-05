package pd.items.consum.potions;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.messages.InlineText;

/** SPSEXPD: 由无味果果实酿造，饮用后解除饥饿、恢复大量饱食度。 */
public class PotionOfSatiety extends SpsPotion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfSatiety.class)
			.t("name", "充饥药水")
			.t("desc", "以无味果果实酿成的液态食物。饮用后能迅速解除饥饿，恢复大量饱食度。");
	}

	{ image = SpecificPlaceHolderDict.POTION_HOLDER_0; }

	@Override public void apply(Hero hero) {
		Buff.affect(hero, Hunger.class).satisfy(500f);
		hero.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 4);
	}

	@Override public int value() { return 20 * quantity; }
}
