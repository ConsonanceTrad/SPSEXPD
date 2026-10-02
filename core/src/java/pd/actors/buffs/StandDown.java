/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.Char;
import pd.sprites.CharSprite;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** SPS-PD's stacking-slow stun. Taking actual damage ends it early. */
public class StandDown extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(StandDown.class)
			.t("name", "僵直")
			.t("desc", "僵直效果类似于麻痹，使目标不能行动。与麻痹不同的是，僵直会在目标受到伤害时立即消失。\n\n剩余效果时长：%s回合");
	}




	public static final float DURATION = 5f;

	{
		type = buffType.NEGATIVE;
		immunities.add(SpeedSlow.class);
	}

	@Override
	public boolean attachTo(Char target) {
		if (!super.attachTo(target)) return false;
		target.paralysed++;
		Buff.detach(target, SpeedSlow.class);
		return true;
	}

	@Override
	public void detach() {
		super.detach();
		if (target.paralysed > 0) target.paralysed--;
		Buff.prolong(target, SpeedSlow.class, 3f);
	}

	@Override
	public int icon() {
		return BuffIndicator.PARALYSIS;
	}

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add(CharSprite.State.FROZEN);
		else target.sprite.remove(CharSprite.State.FROZEN);
	}

	public static float duration(Char ch) {
		return DURATION;
	}
}
