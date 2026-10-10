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
			.t("desc", "强光显著扩大你的视野：日间 9 格、夜间 7 格。\n\n剩余回合：%s。");
	}



	public static final float DURATION = 500f;
	public static final int DISTANCE = 10;   //SPSEXPD: 仅作记录——英雄实际视距由 Dungeon.refreshHeroViewDistance() 决定（日间 9 / 夜间 7）
	{ type = buffType.NEUTRAL; announced = true; }

	@Override public boolean attachTo(Char target) {
		if (!super.attachTo(target)) return false;
		if (Dungeon.level != null) {
			//SPSEXPD: 英雄视野统一由 Dungeon 计算（强光：日间 9 / 夜间 7）；
			//其它角色维持原行为，避免改动它们的感知范围
			if (target == Dungeon.hero) {
				Dungeon.observe();
			} else {
				target.viewDistance = Math.max(Dungeon.level.viewDistance, DISTANCE);
			}
		}
		return true;
	}

	@Override public void detach() {
		//SPSEXPD: 先移除 buff 再观察/还原，否则重算时仍会看到 HighLight 挂着
		Char owner = target;
		super.detach();
		if (Dungeon.level != null && owner != null) {
			if (owner == Dungeon.hero) {
				Dungeon.observe();
			} else {
				owner.viewDistance = Dungeon.level.viewDistance;
			}
		}
	}

	@Override public int icon() { return BuffIndicator.LIGHT; }
	@Override public void fx(boolean on) {
		if (target.sprite == null) return;
		if (on) target.sprite.add(CharSprite.State.ILLUMINATED);
		else target.sprite.remove(CharSprite.State.ILLUMINATED);
	}
}
