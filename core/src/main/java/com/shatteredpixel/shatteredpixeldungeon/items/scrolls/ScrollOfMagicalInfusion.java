/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class ScrollOfMagicalInfusion extends InventoryScroll {
	@Override
	public void empoweredRead() {
		//The SPS-PD 0.9.8 empowered infusion branch intentionally has no effect.
	}
	{
		icon = ItemSpriteSheet.Icons.SCROLL_UPGRADE;
		preferredBag = Belongings.Backpack.class;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return item instanceof Weapon || item instanceof Armor;
	}

	@Override
	protected void onItemSelected(Item item) {
		ScrollOfRemoveCurse.uncurse(Dungeon.hero, item);
		item.identify();
		if (item instanceof Weapon) ((Weapon) item).upgrade(true);
		else ((Armor) item).upgrade(true);
		GLog.p(Messages.get(this, "infuse", item.name()));
		curUser.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);
	}

	@Override
	public int value() {
		return isKnown() ? 100 * quantity : super.value();
	}
}
