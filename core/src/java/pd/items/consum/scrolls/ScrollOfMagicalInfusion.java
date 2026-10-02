/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.scrolls;

import pd.Dungeon;
import pd.actors.hero.Belongings;
import pd.effects.Speck;
import pd.items.Item;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.weapon.Weapon;
import pd.messages.Messages;
import pd.sprites.ItemIconSheet;
import pd.utils.GLog;
import pd.messages.InlineText;

public class ScrollOfMagicalInfusion extends InventoryScroll {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ScrollOfMagicalInfusion.class)
			.t("name", "魔力灌注卷轴")
			.t("desc", "这张卷轴能在保留并强化附魔的同时升级一件武器或护甲。")
			.t("inv_title", "选择要灌注的物品")
			.t("infuse", "你的%s充满了魔力。");
	}



	@Override
	public void empoweredRead() {
		//The SPS-PD 0.9.8 empowered infusion branch intentionally has no effect.
	}
	{
		icon = ItemIconSheet.SCROLL_UPGRADE;
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
