package pd.items.medicine;

import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Dewcharge;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;

public class GreenSpore extends Pill {
	{ image = ItemSpriteSheet.MUSHROOM_GREEN_SPORE; }
	@Override protected void onUse(Hero hero) {
		if (!Dungeon.dewWater && !Dungeon.dewDraw) {
			GLog.w(Messages.get(this, "not_time"));
			return;
		}
		Buff.affect(hero, Dewcharge.class, 100f);
	}
	@Override public int value() { return 20 * quantity; }
}
