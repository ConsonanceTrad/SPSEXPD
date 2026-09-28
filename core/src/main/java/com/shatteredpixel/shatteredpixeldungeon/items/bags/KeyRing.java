/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.bags;

import com.shatteredpixel.shatteredpixeldungeon.items.AncientCoin;
import com.shatteredpixel.shatteredpixeldungeon.items.Bone;
import com.shatteredpixel.shatteredpixeldungeon.items.ConchShell;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.PotKey;
import com.shatteredpixel.shatteredpixeldungeon.items.ShadowEaterKey;
import com.shatteredpixel.shatteredpixeldungeon.items.TenguKey;
import com.shatteredpixel.shatteredpixeldungeon.items.TreasureMap;
import com.shatteredpixel.shatteredpixeldungeon.items.TriforceOfCourage;
import com.shatteredpixel.shatteredpixeldungeon.items.TriforceOfPower;
import com.shatteredpixel.shatteredpixeldungeon.items.TriforceOfWisdom;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.AdventureJournal;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.ChallengeJournal;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

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
