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
			.t("desc", "这张卷轴能提高一件神器 1 点等级（不会超过其等级上限）。")
			.t("inv_title", "选择要灌注的神器")
			.t("infuse", "你的%s被灌注了魔力。")
			.t("maxed", "这件神器的等级已经达到上限了。");
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
		//SPSXPD: 改为只对神器生效
		return item instanceof pd.items.equipment.artifacts.Artifact;
	}

	@Override
	protected void onItemSelected(Item item) {
		pd.items.equipment.artifacts.Artifact artifact =
				(pd.items.equipment.artifacts.Artifact) item;
		ScrollOfRemoveCurse.uncurse(Dungeon.hero, item);
		item.identify();
		//SPSXPD: 提高 1 点神器等级，但不超过其等级上限
		if (artifact.level() >= artifact.levelCap()) {
			GLog.w(Messages.get(this, "maxed"));
			return;
		}
		artifact.upgrade();
		GLog.p(Messages.get(this, "infuse", item.name()));
		curUser.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);
	}

	@Override
	public int value() {
		return isKnown() ? 100 * quantity : super.value();
	}
}
