package pd.items.consum.food.vegetable;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.*;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.Stylus;
import pd.items.UpgradeBlobRed;
import pd.items.UpgradeBlobViolet;
import pd.items.UpgradeBlobYellow;
import pd.items.consum.potions.Potion;
import pd.items.consum.scrolls.Scroll;
import pd.items.equipment.bags.Bag;
import pd.messages.Messages;
import pd.plants.Seedpod;
import pd.scenes.GameScene;
import pd.windows.WndBag;
import pd.messages.InlineText;
import pd.atlas.items.ConsumPotionSeedSeedDict;
import render.utils.math.Random;

import java.util.ArrayList;

/**
 * SPSEXPD: 吞星花蔬菜——主要用途是“吞噬物品并提炼精华”，直接食用则会灼伤自己。
 */
public class StarEaterFlower extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(StarEaterFlower.class)
			.t("name", "吞星花")
			.t("desc", "吞星花的一部分。它真正的用途是吞噬物品并提炼其中的精华；若直接食用，其中的消化液会灼伤你（损失 40% 最大生命）。")
			.t("ac_consume", "吞噬")
			.t("consume_prompt", "选择一件未装备的物品供吞星花吞噬提炼")
			.t("ac_bite", "啃咬")
			.t("bite_prompt", "选择要啃咬的附近目标")
			.t("bite_far", "目标太远了，深渊巨口只够得着身边的东西。")
			.t("bite_kill", "你铭记了这个物种，生命上限提高 5 点（已铭记 %d 种）。")
			.t("bite_known", "你已经铭记过这个物种了。");
	}

	private static final String AC_CONSUME = "CONSUME";
	//SPSXPD: 深渊巨口解锁 —— 啃咬一个附近目标
	private static final String AC_BITE = "BITE";

	{ image = ConsumPotionSeedSeedDict.STAREATER_FLOWER; defaultAction = AC_CONSUME; }

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_CONSUME);
		return actions;
	}

	@Override public void execute(Hero hero, String action) {
		if (AC_BITE.equals(action)) {
			curUser = hero;
			GameScene.selectCell(biteSelector);
			return;
		}
		if (action.equals(AC_CONSUME)) {
			curUser = hero;
			GameScene.selectItem(consumeSelector);
		} else {
			super.execute(hero, action);
		}
	}

	private final WndBag.ItemSelector consumeSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(StarEaterFlower.class, "consume_prompt"); }
		@Override public Class<? extends Bag> preferredBag() { return Belongings.Backpack.class; }
		@Override public boolean itemSelectable(Item item) {
			return item != StarEaterFlower.this && consumable(item);
		}
		@Override public void onSelect(Item item) {
			if (item == null) return;
			Item result = essenceFrom(item);
			item.detach(curUser.belongings.backpack);
			StarEaterFlower.this.detach(curUser.belongings.backpack);
			if (!result.collect()) Dungeon.level.drop(result, curUser.pos).sprite.drop();
			curUser.sprite.operate(curUser.pos);
			curUser.spendAndNext(Actor.TICK);
		}
	};

	/** SPSEXPD: 吞星花蔬菜的“可吞噬”判定（原「吞星花果实」UpgradeEatBall 的逻辑）。 */
	public static boolean consumable(Item item) {
		return item != null && !item.isEquipped(Dungeon.hero)
				&& (item.isUpgradable() || item instanceof Scroll || item instanceof Potion || item instanceof Stylus);
	}

	/** SPSEXPD: 吞噬提炼逻辑（原「吞星花果实」UpgradeEatBall 的逻辑）。 */
	public static Item essenceFrom(Item item) {
		if (item.isUpgradable()) {
			int upgrades = Math.max(0, item.visiblyUpgraded());
			if (Random.Float() < upgrades / 10f) return new UpgradeBlobViolet();
			if (Random.Float() < upgrades / 5f) return new UpgradeBlobRed();
			if (Random.Float() < upgrades / 3f) return new UpgradeBlobYellow();
		} else if (Random.Float() < 0.1f) {
			return new UpgradeBlobYellow();
		}
		return new Seedpod.Seed();
	}

	/** SPSXPD: 啃咬 —— 点选一个附近目标 */
	private final pd.scenes.CellSelector.Listener biteSelector = new pd.scenes.CellSelector.Listener() {
		@Override public void onSelect(Integer cell) {
			if (cell != null) bite(cell);
		}
		@Override public String prompt() {
			return Messages.get(StarEaterFlower.class, "bite_prompt");
		}
	};

	/** 啃咬一个目标：当前攻击力 40% 的纯粹伤害；击杀则铭记该物种（生命上限 +5，每种一次） */
	private void bite(int cell) {
		Hero hero = curUser;
		if (hero == null || Dungeon.level == null) return;
		pd.actors.Char ch = Actor.findChar(cell);
		if (!(ch instanceof pd.actors.mobs.Mob) || !ch.isAlive()) return;
		if (!Dungeon.level.adjacent(hero.pos, cell)) {
			pd.utils.GLog.w(Messages.get(StarEaterFlower.class, "bite_far"));
			return;
		}
		int dmg = Math.max(1, Math.round(hero.damageRoll() * 0.4f));
		ch.HP -= dmg; //SPSXPD: 纯粹伤害 —— 绕过一切防御与减伤
		if (ch.sprite != null) ch.sprite.showStatus(pd.sprites.CharSprite.NEGATIVE, Integer.toString(dmg));
		if (!ch.isAlive()) {
			pd.actors.hero.perks.AbyssalMaw maw =
					hero.heroPerk.get(pd.actors.hero.perks.AbyssalMaw.class);
			if (maw != null && maw.markDevoured(ch.getClass().getName())) {
				hero.HTBoost += 5;
				hero.updateHT(true);
				pd.utils.GLog.p(Messages.get(StarEaterFlower.class, "bite_kill", maw.devouredCount()));
			} else {
				pd.utils.GLog.i(Messages.get(StarEaterFlower.class, "bite_known"));
			}
			ch.die(StarEaterFlower.this);
		}
		if (hero.sprite != null) hero.sprite.operate(hero.pos);
		StarEaterFlower.this.detach(hero.belongings.backpack);
		hero.spendAndNext(Actor.TICK);
	}

	@Override protected void onEat(Hero hero) {
		hero.damage(Math.max(1, Math.round(hero.HT * 0.4f)), this);
	}
}
