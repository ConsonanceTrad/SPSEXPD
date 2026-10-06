/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.atlas.items.EquipmentWaterBagDict;
import pd.items.Badge;
import pd.items.Dewdrop;
import pd.items.Heap;
import pd.items.Item;
import pd.items.Waterskin;
import pd.journal.Catalog;
import pd.messages.InlineText;

import java.util.ArrayList;

/**
 * SPSEXPD: 集露徽章——占用徽章槽。佩戴后，英雄每迈出一步，
 * 就把落脚点周围 3x3 内的地面露珠（含彩色露珠）收进露珠瓶，且不消耗任何回合。
 */
public class DewBadge extends Badge {
	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(DewBadge.class)
			.t("name", "魔法集露袋")
			.t("desc", "装备后，你每移动一步都会自动把落脚点周围九格内的地面露珠收入露珠瓶，且不消耗任何回合。需要随身携带露珠瓶（露珠瓶）。");
	}

	{
		image = EquipmentWaterBagDict.WATER_BAG;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 400 * quantity; }

	/**
	 * SPSEXPD: 由 {@code Hero.move()} 在移动成功那一刻调用——收取落脚点周围 3x3 内的所有地面露珠。
	 * 纯收集，不消耗回合。
	 */
	public static void collectAround(Hero hero) {
		if (hero == null || Dungeon.level == null) return;

		Waterskin flask = hero.belongings.getItem(Waterskin.class);
		if (flask == null) return;

		int width = Dungeon.level.width();
		int pos = hero.pos;
		int posX = pos % width;

		for (int dy = -1; dy <= 1; dy++) {
			for (int dx = -1; dx <= 1; dx++) {
				int cell = pos + dx + dy * width;
				if (!Dungeon.level.insideMap(cell)) continue;
				//同一行相邻才会真正邻近，避免绕行时取到相邻行的错位格
				if (Math.abs((cell % width) - posX) > 1) continue;

				Heap heap = Dungeon.level.heaps.get(cell);
				if (heap == null) continue;

				ArrayList<Item> dews = new ArrayList<>();
				for (Item item : heap.items) {
					if (item instanceof Dewdrop) dews.add(item);
				}
				if (dews.isEmpty()) continue;

				for (Item item : dews) {
					Dewdrop dew = (Dewdrop) item;
					Catalog.setSeen(Dewdrop.class);
					flask.collectDew(dew);
					heap.items.remove(dew);
				}

				if (heap.isEmpty()) {
					Dungeon.level.heaps.remove(cell);
					if (heap.sprite != null) {
						heap.sprite.killAndErase();
						heap.sprite = null;
					}
				}
			}
		}
	}
}
