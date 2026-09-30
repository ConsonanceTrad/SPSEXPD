/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.EquipableItem;
import pd.items.Item;
import pd.items.reward.BoundReward;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.windows.WndBag;
import pd.windows.WndUseItem;
import render.utils.math.Random;

import java.util.ArrayList;

/** Tester mission pad for spending experiment points on permanent item upgrades. */
public class RewardPaper extends Item {

	public static final String AC_CHOOSE = "CHOOSE";
	public static final String AC_DOSP = "DOSP";
	public static final String AC_DOUP = "DOUP";
	public static final String AC_DORE = "DORE";
	public static final String AC_NEED = "NEED";
	public static final String AC_RANKUP = "RANKUP";
	public static final int SPECIAL_COST = 50;
	public static final int ITEM_COST = 100;
	public static final int GOLD_COST = 1000;

	{ image = ItemSpriteSheet.SPS_REWARD_PAPER; unique = true; defaultAction = AC_CHOOSE; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		if (hero.spp >= ITEM_COST) {
			actions.add(AC_DOUP);
			actions.add(AC_DORE);
		}
		if (hero.spp >= SPECIAL_COST) {
			actions.add(AC_DOSP);
			actions.add(AC_RANKUP);
		}
		if (Dungeon.gold >= GOLD_COST) actions.add(AC_NEED);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_CHOOSE.equals(action)) {
			GameScene.show(new WndUseItem(null, this));
		} else if (AC_DOSP.equals(action)) {
			GameScene.selectItem(selector(hero, AC_DOSP));
		} else if (AC_DOUP.equals(action)) {
			GameScene.selectItem(selector(hero, AC_DOUP));
		} else if (AC_DORE.equals(action)) {
			GameScene.selectItem(selector(hero, AC_DORE));
		} else if (AC_NEED.equals(action)) {
			buyReward(hero);
		} else if (AC_RANKUP.equals(action)) {
			rankUp(hero);
		} else {
			super.execute(hero, action);
		}
	}

	private WndBag.ItemSelector selector(final Hero hero, final String action) {
		return new WndBag.ItemSelector() {
			@Override public String textPrompt() { return Messages.get(RewardPaper.this, "prompt"); }
			@Override public boolean itemSelectable(Item item) {
				if (AC_DOSP.equals(action)) return item instanceof EquipableItem && !item.unique;
				if (AC_DOUP.equals(action)) return item.isUpgradable() && !item.reinforced;
				return item.isUpgradable();
			}
			@Override public void onSelect(Item item) {
				if (item != null && improveItem(hero, item, action)) hero.spendAndNext(1f);
			}
		};
	}

	public boolean improveItem(Hero hero, Item item, String action) {
		if (hero == null || item == null) return false;
		if (AC_DOSP.equals(action) && hero.spp >= SPECIAL_COST && item instanceof EquipableItem && !item.unique) {
			hero.spp -= SPECIAL_COST;
			item.unique = true;
			return true;
		}
		if (AC_DOUP.equals(action) && hero.spp >= ITEM_COST && item.isUpgradable() && !item.reinforced) {
			hero.spp -= ITEM_COST;
			item.reinforce();
			return true;
		}
		if (AC_DORE.equals(action) && hero.spp >= ITEM_COST && item.isUpgradable()) {
			hero.spp -= ITEM_COST;
			item.upgrade(10);
			return true;
		}
		return false;
	}

	public boolean buyReward(Hero hero) {
		if (hero == null || Dungeon.level == null || Dungeon.gold < GOLD_COST) return false;
		Dungeon.gold -= GOLD_COST;
		Dungeon.level.drop(new BoundReward(), hero.pos);
		hero.spendAndNext(1f);
		return true;
	}

	public boolean rankUp(Hero hero) {
		return rankUp(hero, Random.Int(50));
	}

	public boolean rankUp(Hero hero, int roll) {
		if (hero == null || hero.spp < SPECIAL_COST || roll < 0 || roll >= 50) return false;
		hero.spp -= SPECIAL_COST;
		if (roll == 0 || roll == 45) {
			hero.HTBoost++;
			hero.updateHT(true);
		} else if (roll == 46) {
			hero.improveAttackSkill(1);
		} else if (roll == 47) {
			hero.improveDefenseSkill(1);
		} else if (roll == 48) {
			hero.improveMagicSkill(1);
		} else if (roll == 49) {
			hero.HTBoost++;
			hero.updateHT(true);
			hero.improveAttackSkill(1);
			hero.improveDefenseSkill(1);
			hero.improveMagicSkill(1);
		}
		hero.spendAndNext(1f);
		return true;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
}
