package pd.items.medicine;

import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;

public class Hardpill extends Pill {
	{ image = ItemSpriteSheet.GREAT_PILL; }
	@Override protected void onUse(Hero hero) {
		Buff.affect(hero, DefenceUp.class, 800f).level(50);
	}
	@Override public int value() { return 50 * quantity; }
}
