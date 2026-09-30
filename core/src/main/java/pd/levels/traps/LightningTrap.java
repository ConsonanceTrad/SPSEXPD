/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels.traps;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.items.Heap;
import pd.items.Item;
import pd.items.wands.Wand;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.Random;

/** Legacy teal diamond trap: percentage damage plus wand and heap electrification. */
public class LightningTrap extends Trap {

	{
		color = TEAL;
		shape = DIAMOND;
	}

	@Override public void activate() {
		activate(Actor.findChar(pos));
	}

	public void activate(Char target) {
		if (target != null && target.isAlive()) {
			int minimum = target.HP / 3;
			int maximum = Math.max(minimum + 1, 2 * target.HP / 3);
			target.damage(Math.max(1, Random.Int(minimum, maximum)),
					pd.actors.blobs.Electricity.class);
			if (target == Dungeon.hero && !target.isAlive()) {
				Dungeon.fail(this);
				GLog.n(Messages.get(this, "ondeath"));
			}
		}

		if (Dungeon.level == null) return;
		Heap heap = Dungeon.level.heaps.get(pos);
		if (heap == null) return;
		Item item = heap.peek();
		if (item instanceof Wand) {
			Wand wand = (Wand)item;
			wand.curCharges = Math.min(wand.maxCharges,
					wand.curCharges + (int)Math.ceil((wand.maxCharges - wand.curCharges) / 2f));
		}
		heap.shockhit();
	}
}
