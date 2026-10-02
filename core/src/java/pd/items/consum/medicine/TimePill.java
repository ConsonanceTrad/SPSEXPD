package pd.items.consum.medicine;

import pd.atlas.items.GroundFunctionalFallingDict;

import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HasteBuff;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.items.Heap;
import pd.items.equipment.artifacts.TimekeepersHourglass;
import pd.messages.InlineText;

/** The alchemy-made time block from SPS-PD 0.9.8. */
public class TimePill extends Pill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TimePill.class)
			.t("name", "时之块")
			.t("desc", "提供加速和时之沙。\n使用_1份水，4份原石_炼金");
	}


	{
		image = GroundFunctionalFallingDict.SANDBAG_0;
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
