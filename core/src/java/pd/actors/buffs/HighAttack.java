/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.levels.Terrain;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

/** Accumulates an attack multiplier while the target remains next to a wall. */
public class HighAttack extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(HighAttack.class)
			.t("name", "攀墙伏击")
			.t("desc", "在障碍物旁蓄力，获得少量闪避提升和下次攻击伤害加成；离开障碍物会失去效果。\n\n下次伤害加成倍率：%s。");
	}


	public static final float DURATION = 30f;
	private static final String LEVEL = "level";

	private int level;
	private boolean grantsInvisibility;

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	@Override
	public boolean attachTo(Char target) {
		if (!super.attachTo(target)) return false;
		if (target instanceof Hero && ((Hero) target).lvl > 55) {
			target.invisible++;
			grantsInvisibility = true;
		}
		return true;
	}

	@Override
	public boolean act() {
		if (target == null || !target.isAlive() || !nextToWall(target.pos)) {
			detach();
		} else {
			level++;
			spend(TICK);
		}
		return true;
	}

	private static boolean nextToWall(int cell) {
		if (Dungeon.level == null || cell < 0 || cell >= Dungeon.level.length()) return false;
		int width = Dungeon.level.width();
		int height = Dungeon.level.height();
		int cx = cell % width;
		int cy = cell / width;
		for (int y = Math.max(0, cy - 1); y <= Math.min(height - 1, cy + 1); y++) {
			for (int x = Math.max(0, cx - 1); x <= Math.min(width - 1, cx + 1); x++) {
				if (x == cx && y == cy) continue;
				int terrain = Dungeon.level.map[x + y * width];
				if ((Terrain.flags[terrain] & Terrain.SOLID) != 0 || terrain == Terrain.OPEN_DOOR) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void detach() {
		if (grantsInvisibility && target != null && target.invisible > 0) {
			target.invisible--;
		}
		grantsInvisibility = false;
		super.detach();
	}

	public int level() {
		return level;
	}

	public void level(int value) {
		level = Math.max(level, value);
	}

	@Override
	public int icon() {
		return BuffIndicator.PREPARATION;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", level);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEVEL, level);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		level = Math.max(0, bundle.getInt(LEVEL));
	}
}
