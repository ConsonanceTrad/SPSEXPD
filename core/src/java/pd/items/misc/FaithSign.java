/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.faithbuff.BalanceFaith;
import pd.actors.buffs.faithbuff.DemonFaith;
import pd.actors.buffs.faithbuff.FaithBuff;
import pd.actors.buffs.faithbuff.HumanFaith;
import pd.actors.buffs.faithbuff.LifeFaith;
import pd.actors.buffs.faithbuff.MechFaith;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.scenes.GameScene;
import pd.windows.WndUseItem;

import java.util.ArrayList;

public class FaithSign extends Item {

	public static final String AC_CHOOSE = "CHOOSE";
	public static final String AC_DEMON = "DEMON";
	public static final String AC_HUMAN = "HUMAN";
	public static final String AC_MECH = "MECH";
	public static final String AC_LIFE = "LIFE";
	public static final String AC_BALANCE = "BALANCE";

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		unique = true;
		defaultAction = AC_CHOOSE;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		actions.add(AC_DEMON);
		actions.add(AC_HUMAN);
		actions.add(AC_MECH);
		actions.add(AC_LIFE);
		actions.add(AC_BALANCE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_CHOOSE.equals(action)) {
			GameScene.show(new WndUseItem(null, this));
			return;
		}
		Class<? extends FaithBuff> faith = null;
		if (AC_DEMON.equals(action)) faith = DemonFaith.class;
		else if (AC_HUMAN.equals(action)) faith = HumanFaith.class;
		else if (AC_MECH.equals(action)) faith = MechFaith.class;
		else if (AC_LIFE.equals(action)) faith = LifeFaith.class;
		else if (AC_BALANCE.equals(action)) faith = BalanceFaith.class;
		if (faith == null) {
			super.execute(hero, action);
			return;
		}
		Buff.detach(hero, DemonFaith.class);
		Buff.detach(hero, HumanFaith.class);
		Buff.detach(hero, MechFaith.class);
		Buff.detach(hero, LifeFaith.class);
		Buff.detach(hero, BalanceFaith.class);
		Buff.affect(hero, faith);
		hero.spendAndNext(1f);
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
}
