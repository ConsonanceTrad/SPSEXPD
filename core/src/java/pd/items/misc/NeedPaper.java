/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.AttackDown;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.ForeverShadow;
import pd.actors.buffs.Poison;
import pd.actors.buffs.STRDown;
import pd.actors.hero.Hero;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndUseItem;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

public class NeedPaper extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(NeedPaper.class)
			.t("name", "通缉令")
			.t("ac_choose", "选择")
			.t("ac_shop", "黑市")
			.t("ac_help", "黑帮")
			.t("need_charge", "点数不足。")
			.t("desc", "越知名，越危险。消耗500点数可以治疗并隐蔽自身；消耗3000点数可以获得随机装备。");
	}




	public static final String AC_CHOOSE = "CHOOSE";
	public static final String AC_HELP = "HELP";
	public static final String AC_SHOP = "SHOP";
	public static final int HELP_COST = 500;
	public static final int SHOP_COST = 3000;

	{ image = SpecificPlaceHolderDict.SOMETHING_0; unique = true; defaultAction = AC_CHOOSE; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		actions.add(AC_HELP);
		actions.add(AC_SHOP);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_CHOOSE.equals(action)) GameScene.show(new WndUseItem(null, this));
		else if (AC_HELP.equals(action)) { if (!hide(hero)) GLog.p(Messages.get(this, "need_charge")); }
		else if (AC_SHOP.equals(action)) { if (!shop(hero)) GLog.p(Messages.get(this, "need_charge")); }
		else super.execute(hero, action);
	}

	public boolean hide(Hero hero) {
		if (hero == null || hero.spp < HELP_COST) return false;
		hero.spp -= HELP_COST;
		hero.HP = hero.HT;
		Buff.prolong(hero, ForeverShadow.class, 15f);
		Buff.detach(hero, Poison.class);
		Buff.detach(hero, Cripple.class);
		Buff.detach(hero, STRDown.class);
		Buff.detach(hero, Bleeding.class);
		Buff.detach(hero, AttackDown.class);
		Buff.detach(hero, ArmorBreak.class);
		hero.spendAndNext(1f);
		return true;
	}

	public boolean shop(Hero hero) {
		if (hero == null || Dungeon.level == null || hero.spp < SHOP_COST) return false;
		Item item = Generator.random(Random.oneOf(Generator.Category.WAND, Generator.Category.RING,
				Generator.Category.ARTIFACT, Generator.Category.MELEEWEAPON, Generator.Category.ARMOR));
		if (item == null) return false;
		hero.spp -= SHOP_COST;
		Heap heap = Dungeon.level.drop(item, hero.pos);
		if (heap.sprite != null) heap.sprite.drop();
		hero.spendAndNext(1f);
		return true;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
}
