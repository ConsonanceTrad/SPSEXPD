package pd.items.potions;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.items.potions.elixirs.ElixirOfMight;

public class PotionOfMight extends SpsPotion {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public void apply(Hero hero) {
		Buff.affect(hero, Barkskin.class).set(8 + hero.lvl / 2, 360);
		Buff.affect(hero, ElixirOfMight.HTBoost.class).reset();
		hero.updateHT(true);
		hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.4f, 4);
	}
	@Override public int value() { return 200 * quantity; }
}
