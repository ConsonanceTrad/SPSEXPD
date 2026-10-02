/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.meleethrow;

import pd.atlas.items.SpecificPlaceHolderDict;

import com.badlogic.gdx.Gdx;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HolyStun;
import pd.items.KindOfWeapon;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.ConsumThrowsDict;

public class Brick extends MeleeThrowWeapon {
	{
		image = ConsumThrowsDict.BRICK;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Brick.class)
			.t("name", "砖头")
			.t("desc", "一块由泥土烧成的普通砖头。劳动节辛苦了。\n高级钝器，飞掷，易碎-报酬");
	}



	public Brick() { super(1, 8, 8, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 40) Buff.prolong(defender, HolyStun.class, 2f);
		if (Random.Int(80) == 1) {
			destroy(attacker);
			dropRandomItems(defender.pos, 3);
			if (Gdx.app != null) GLog.n(Messages.get(KindOfWeapon.class, "destory"));
		}
		return super.proc(attacker, defender, damage);
	}
}
