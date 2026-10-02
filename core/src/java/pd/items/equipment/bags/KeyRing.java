/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;

import pd.items.AncientCoin;
import pd.items.Bone;
import pd.items.ConchShell;
import pd.items.Item;
import pd.items.PotKey;
import pd.items.ShadowEaterKey;
import pd.items.TenguKey;
import pd.items.TreasureMap;
import pd.items.TriforceOfCourage;
import pd.items.TriforceOfPower;
import pd.items.TriforceOfWisdom;
import pd.items.specific.keys.Key;
import pd.items.quest.AdventureJournal;
import pd.items.quest.ChallengeJournal;
import pd.items.equipment.rings.Ring;
import pd.messages.InlineText;

/** SPS-PD's thirty-slot key ring and route-item container. */
public class KeyRing extends Bag {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(KeyRing.class)
			.t("name", "钥匙环")
			.t("desc", "这个钥匙环有三十格空间，可以收纳钥匙、戒指、路线日志和其他传送道具。");
	}


	{
		image = EquipmentBagsDict.SPS_KEY_RING;
	}

	@Override
	public boolean canHold(Item item) {
		return (item instanceof Key
				|| item instanceof AncientCoin
				|| item instanceof Bone
				|| item instanceof ConchShell
				|| item instanceof PotKey
				|| item instanceof ShadowEaterKey
				|| item instanceof TenguKey
				|| item instanceof TriforceOfCourage
				|| item instanceof TriforceOfPower
				|| item instanceof TriforceOfWisdom
				|| item instanceof Ring
				|| item instanceof TreasureMap
				|| item instanceof AdventureJournal
				|| item instanceof ChallengeJournal)
				&& super.canHold(item);
	}

	@Override public int capacity() { return 34; }
	@Override public int value() { return 50 * quantity; }
}
