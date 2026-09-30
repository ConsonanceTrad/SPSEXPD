/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.bags;

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
import pd.items.keys.Key;
import pd.items.quest.AdventureJournal;
import pd.items.quest.ChallengeJournal;
import pd.items.rings.Ring;
import pd.sprites.ItemSpriteSheet;

/** SPS-PD's thirty-slot key ring and route-item container. */
public class KeyRing extends Bag {

	{
		image = ItemSpriteSheet.SPS_KEY_RING;
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
