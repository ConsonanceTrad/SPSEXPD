/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.Heap;
import pd.items.Item;
import pd.items.weapon.melee.start.LinkSword;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.math.Random;

import java.util.ArrayList;

public class RangeBag extends MiscEquippable {

	public static final String AC_BUY = "BUY";
	public static final int PRICE = 500;

	{ image = SpecificPlaceHolderDict.SOMETHING_0; unique = true; defaultAction = AC_BUY; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_BUY);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_BUY.equals(action)) {
			if (!buy(hero)) {
				GLog.p(Messages.get(this, "need_gold"));
			}
		} else {
			super.execute(hero, action);
		}
	}

	public boolean buy(Hero hero) {
		if (hero == null || Dungeon.level == null || Dungeon.gold < PRICE) return false;
		Dungeon.gold -= PRICE;
		drop(LinkSword.randomLinkDrop(), hero.pos);
		if (hero.sprite != null) hero.sprite.operate(hero.pos);
		hero.spendAndNext(1f);
		return true;
	}

	public boolean shouldDrop(Hero hero) {
		return isEquipped(hero) && Random.Int(6) == 0;
	}

	public Item createDrop() { return LinkSword.randomLinkDrop(); }

	public void drop(Item item, int cell) {
		if (item == null || Dungeon.level == null || !Dungeon.level.insideMap(cell)) return;
		Heap heap = Dungeon.level.drop(item, cell);
		if (heap.sprite != null) heap.sprite.drop();
	}

	@Override protected MiscBuff createBuff() { return new RangeBagBless(); }
	public class RangeBagBless extends MiscBuff { }
}
