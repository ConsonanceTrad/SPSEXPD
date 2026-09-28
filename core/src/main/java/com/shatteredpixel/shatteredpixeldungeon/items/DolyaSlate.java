/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.items.quest.AdventureJournal;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

/** Otiluke's Dolya slate, the physical journal sold after the sewer chapter. */
public class DolyaSlate extends AdventureJournal {

	{
		image = ItemSpriteSheet.DOLYA_SLATE;
		stackable = true;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc") + "\n\n"
				+ Messages.get(this, "charge", charge(), FULL_CHARGE);
	}

	@Override
	public int value() {
		return 300 * quantity;
	}
}
