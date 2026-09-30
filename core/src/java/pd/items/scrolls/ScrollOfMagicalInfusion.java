/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.scrolls;

import pd.Dungeon;
import pd.actors.hero.Belongings;
import pd.effects.Speck;
import pd.items.Item;
import pd.items.armor.Armor;
import pd.items.weapon.Weapon;
import pd.messages.Messages;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;

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
