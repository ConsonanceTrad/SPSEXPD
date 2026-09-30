/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.Char;
import pd.sprites.CharSprite;
import pd.ui.BuffIndicator;

/** A paralysis effect which is not shortened by incoming damage. */
public class HolyStun extends FlavourBuff {
	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	@Override
	public boolean attachTo(Char target) {
		if (!super.attachTo(target)) return false;
		target.paralysed++;
		return true;
	}

	@Override
	public void detach() {
		super.detach();
		if (target.paralysed > 0) target.paralysed--;
	}

	@Override public int icon() { return BuffIndicator.PARALYSIS; }

	@Override
	public void fx(boolean on) {
		if (target.sprite == null) return;
		if (on) target.sprite.add(CharSprite.State.PARALYSED);
		else if (target.paralysed <= 1) target.sprite.remove(CharSprite.State.PARALYSED);
	}
}
