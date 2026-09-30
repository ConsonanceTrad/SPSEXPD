/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.windows;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.ui.RedButton;

/** Trade window for the hidden shop's permanent-health purchases. */
public class WndLifeTradeItem extends WndInfoItem {

	private static final float GAP = 2;
	private static final int BTN_HEIGHT = 18;

	public WndLifeTradeItem(final Heap heap) {
		super(heap);

		int cost = price();
		RedButton buy = new RedButton(Messages.get(this, "buy", cost)) {
			@Override
			protected void onClick() {
				if (purchase(heap)) hide();
			}
		};
		buy.setRect(0, height + GAP, width, BTN_HEIGHT);
		buy.enable(canBuy());
		add(buy);
		resize(width, (int)buy.bottom());
	}

	public static int price() {
		return Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.FOLLOWER ? 8 : 10;
	}

	public static boolean canBuy() {
		return Dungeon.hero != null && Dungeon.hero.permanentHT() > price();
	}

	public static boolean purchase(Heap heap) {
		if (Dungeon.hero == null || Dungeon.level == null || heap == null
				|| heap.type != Heap.Type.FOR_LIFE || heap.size() != 1 || !canBuy()) {
			return false;
		}

		Hero hero = Dungeon.hero;
		int shopCell = heap.pos;
		Item item = heap.pickUp();
		if (item == null || !hero.spendPermanentHT(price())) return false;

		boolean collected = item.doPickUp(hero);
		Heap replacement = Dungeon.level.drop(Generator.random(), shopCell);
		replacement.type = Heap.Type.FOR_SALE;

		if (!collected) {
			int dropCell = adjacentDropCell(shopCell);
			Dungeon.level.drop(item, dropCell).sprite.drop();
		}
		return true;
	}

	private static int adjacentDropCell(int origin) {
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = origin + offset;
			if (cell >= 0 && cell < Dungeon.level.length()
					&& Dungeon.level.passable[cell]
					&& Dungeon.level.heaps.get(cell) == null
					&& Actor.findChar(cell) == null) {
				return cell;
			}
		}
		return Dungeon.hero.pos;
	}
}
