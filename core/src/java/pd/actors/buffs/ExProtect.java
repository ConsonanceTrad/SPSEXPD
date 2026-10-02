/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.Char;
import pd.actors.mobs.Mob;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Keeps an SPS exit guard asleep until its first incoming hit is absorbed. */
public class ExProtect extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(ExProtect.class)
			.t("name", "精英保护")
			.t("desc", "免疫受到的第一次伤害。在这层保护被打破前，守卫会保持沉睡。");
	}




	{
		type = buffType.POSITIVE;
		announced = true;
	}

	@Override
	public boolean attachTo(Char target) {
		if (!super.attachTo(target)) return false;
		if (target instanceof Mob) ((Mob)target).state = ((Mob)target).SLEEPING;
		target.paralysed++;
		return true;
	}

	@Override
	public void detach() {
		if (target.paralysed > 0) target.paralysed--;
		super.detach();
	}

	@Override public int icon() { return BuffIndicator.MAGIC_SLEEP; }
	@Override public String desc() { return Messages.get(this, "desc"); }

	@Override
	public void fx(boolean on) {
		if (target.sprite == null) return;
		if (on) target.sprite.add(CharSprite.State.PARALYSED);
		else if (target.paralysed <= 1) target.sprite.remove(CharSprite.State.PARALYSED);
	}
}
