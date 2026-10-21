/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.materials;

import pd.atlas.items.ConsumGoodsMaterialsMaterialsExtraDict;
import pd.items.Item;
import pd.messages.InlineText;

/**
 * SPSEXPD: 炼金能量瓶（docs/alchemy-rework.md §6.5）。
 *
 * <p>4 点炼金能量 + 1 份水炼成。**唯一的生效形式是作为辅材放置**：
 * 放进掷点公式的辅材格参与酿造，该次成功率 +15%，随后被消耗。
 * 它不占辅材方案的槽位（可与其它辅材同时放入），对不失败的公式无效。
 */
public class EnergyFlask extends Item {
	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(EnergyFlask.class)
			.t("name", "炼金能量瓶")
			.t("desc", "把炼金能量封进水里得到的瓶子。把它当作辅材一起投进炼金釜，"
					+ "这一炉的火候会稳当不少（成功率 +15%），代价是瓶子本身也被烧掉。");
	}

	{
		image = ConsumGoodsMaterialsMaterialsExtraDict.DRY_TWIG;
		stackable = true;
		bones = false;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 20 * quantity; }
}
