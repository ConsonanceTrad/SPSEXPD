package pd.items.medicine;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Muscle;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.sprites.ItemSpriteSheet;

public class Powerpill extends Pill {
	{ image = ItemSpriteSheet.GREAT_PILL; }
	@Override protected void onUse(Hero hero) {
		Buff.affect(hero, Muscle.class, 1440f);
		if (hero.sprite != null) hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.4f, 4);
	}
	@Override public int value() { return 50 * quantity; }
}
