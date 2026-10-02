/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificTaskDict;

import pd.items.quest.AdventureJournal;
import pd.messages.Messages;

/** Otiluke's Dolya slate, the physical journal sold after the sewer chapter. */
public class DolyaSlate extends AdventureJournal {

	{
		image = SpecificTaskDict.DOLYA_SLATE;
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
