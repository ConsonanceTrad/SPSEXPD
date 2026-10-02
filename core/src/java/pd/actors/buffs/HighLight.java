/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.Char;
import pd.sprites.CharSprite;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class HighLight extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(HighLight.class)
			.t("name", "强光")
			.t("desc", "明亮光线将视野扩大到10格。\n\n剩余回合：%s。");
	}



	public static final float DURATION = 500f;
	public static final int DISTANCE = 10;
	{ type = buffType.NEUTRAL; announced = true; }

	@Override public boolean attachTo(Char target) {
		if (!super.attachTo(target)) return false;
		if (Dungeon.level != null) {
			target.viewDistance = Math.max(Dungeon.level.viewDistance, DISTANCE);
			Dungeon.observe();
		}
		return true;
	}

	@Override public void detach() {
		if (Dungeon.level != null) {
			target.viewDistance = Dungeon.level.viewDistance;
			Dungeon.observe();
		}
		super.detach();
	}

	@Override public int icon() { return BuffIndicator.LIGHT; }
	@Override public void fx(boolean on) {
		if (target.sprite == null) return;
		if (on) target.sprite.add(CharSprite.State.ILLUMINATED);
		else target.sprite.remove(CharSprite.State.ILLUMINATED);
	}
}
