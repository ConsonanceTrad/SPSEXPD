package pd.items.medicine;

import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HasteBuff;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.items.Heap;
import pd.items.artifacts.TimekeepersHourglass;
import pd.sprites.ItemSpriteSheet;

/** The alchemy-made time block from SPS-PD 0.9.8. */
public class TimePill extends Pill {

	{
		image = ItemSpriteSheet.SANDBAG;
	}

	@Override
	protected void onUse(Hero hero) {
		Buff.affect(hero, HasteBuff.class, 400f);
		if (hero.sprite != null) hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.4f, 4);
		if (Dungeon.level != null) {
			Heap heap = Dungeon.level.drop(new TimekeepersHourglass.sandBag(), hero.pos);
			if (heap.sprite != null) heap.sprite.drop();
		}
	}

	@Override public int value() { return 50 * quantity; }
}
