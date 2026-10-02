/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.buffs.BoxStar;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FireImbue;
import pd.actors.buffs.FrostImbue;
import pd.actors.hero.Hero;
import pd.items.Ankh;
import pd.items.Item;
import pd.items.quest.Mushroom;
import pd.items.equipment.weapon.melee.WarHammer;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

public class MKbox extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MKbox.class)
			.t("name", "蘑菇王国问号箱")
			.t("ac_use", "顶一下")
			.t("need_gold", "你需要100金币才能使用它。")
			.t("desc", "一个装着各种奖励的问号箱，每次使用消耗100金币。");
	}

	public static final String AC_USE = "USE";
	{ image=SpecificPlaceHolderDict.SOMETHING_0; defaultAction=AC_USE; unique=true; }
	@Override public ArrayList<String> actions(Hero hero) { ArrayList<String>a=super.actions(hero); a.add(AC_USE); return a; }
	@Override public void execute(Hero hero, String action) {
		if (AC_USE.equals(action)) { if (!use(hero)) GLog.i(Messages.get(this, "need_gold")); }
		else super.execute(hero, action);
	}
	public boolean use(Hero hero) {
		if (hero == null || Dungeon.level == null || Dungeon.gold < 100) return false;
		Dungeon.gold -= 100; hero.spendAndNext(1f);
		if (Random.Int(50) == 0) Buff.affect(hero, BoxStar.class, BoxStar.DURATION);
		else if (Random.Int(49) == 0) Dungeon.gold += 500;
		else if (Random.Int(48) < 3) Dungeon.level.drop(new Ankh(), hero.pos).sprite.drop(hero.pos);
		else if (Random.Int(45) < 20) Dungeon.level.drop(new WarHammer(), hero.pos).sprite.drop(hero.pos);
		else if (Random.Int(25) < 20) Dungeon.level.drop(new Mushroom(), hero.pos).sprite.drop(hero.pos);
		else if (Random.Int(2) == 1) Buff.affect(hero, FireImbue.class).set(FireImbue.DURATION);
		else Buff.affect(hero, FrostImbue.class, FrostImbue.DURATION);
		return true;
	}
	@Override public boolean isUpgradable(){return false;}
	@Override public boolean isIdentified(){return true;}
	@Override public int value(){return 30*quantity;}
}
