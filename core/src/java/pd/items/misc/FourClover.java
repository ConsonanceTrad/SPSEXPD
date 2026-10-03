/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.actors.buffs.Buff;
import pd.atlas.items.EquipmentJewelleryArtifactDict;
import pd.items.Badge;
import pd.messages.InlineText;

/** The old three-slot luck charm, worn in the dedicated badge slot. */
public class FourClover extends Badge {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FourClover.class)
			.t("name", "四叶薄荷项链")
			.t("desc", "这个四叶草形状的项链能提升佩戴者升级时的增益，并强化附魔装备的效果。");
	}



	{
		image = EquipmentJewelleryArtifactDict.CLOVER_BADGE;
	}
	@Override protected Buff buff() { return new FourCloverBless(); }
	public class FourCloverBless extends Buff { }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 500 * quantity; }
}
