/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Legacy tar: keeps burning active and is washed away by water. */
public class Tar extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Tar.class)
			.t("name", "迟缓焦油")
			.t("desc", "粘稠油脂覆盖全身，使火焰难以熄灭；进入水中可以洗掉焦油。")
			.t("heromsg", "你身上覆盖满了粘稠的油脂。");
	}

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	@Override
	public boolean act() {
		if (target.isAlive()) {
			Burning burning = target.buff(Burning.class);
			if (burning != null) burning.reignite(target, 3f);
		}
		if (Dungeon.level != null && Dungeon.level.water[target.pos] && !target.flying) detach();
		else spend(TICK);
		return true;
	}

	@Override public int icon() { return BuffIndicator.OOZE; }
	@Override public String heroMessage() { return Messages.get(this, "heromsg"); }
	@Override public String desc() { return Messages.get(this, "desc"); }
}
