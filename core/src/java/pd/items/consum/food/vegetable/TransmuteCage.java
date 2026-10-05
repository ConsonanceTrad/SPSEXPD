package pd.items.consum.food.vegetable;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.*;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.effects.Transmuting;
import pd.items.Item;
import pd.items.TransmutationBall;
import pd.items.equipment.bags.Bag;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.items.equipment.weapon.missiles.darts.TippedDart;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.windows.WndBag;
import pd.messages.InlineText;
import pd.atlas.items.ConsumPotionSeedSeedDict;

import java.util.ArrayList;

/**
 * SPSEXPD: 转换笼蔬菜——主要用途是“把一件物品转换成另一件”，直接食用则会受伤并短暂强化法术。
 */
public class TransmuteCage extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TransmuteCage.class)
			.t("name", "转换笼")
			.t("desc", "转换笼的一部分。它真正的用途是转换一件未装备的物品；若直接食用，笼中魔力会灼伤你（损失 10% 最大生命），并在 40 回合内为你提供 5 点法术强度。")
			.t("ac_transmute", "转换")
			.t("transmute_prompt", "选择一件未装备的物品进行转换");
	}

	private static final String AC_TRANSMUTE = "TRANSMUTE";

	{ image = ConsumPotionSeedSeedDict.TRANSMUTE_CAGE; defaultAction = AC_TRANSMUTE; }

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_TRANSMUTE);
		return actions;
	}

	@Override public void execute(Hero hero, String action) {
		if (action.equals(AC_TRANSMUTE)) {
			curUser = hero;
			GameScene.selectItem(transmuteSelector);
		} else {
			super.execute(hero, action);
		}
	}

	private final WndBag.ItemSelector transmuteSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(TransmuteCage.class, "transmute_prompt"); }
		@Override public Class<? extends Bag> preferredBag() { return Belongings.Backpack.class; }
		@Override public boolean itemSelectable(Item item) {
			return item != TransmuteCage.this && TransmutationBall.selectable(item);
		}
		@Override public void onSelect(Item item) {
			if (item == null) return;
			Item result = TransmutationBall.changeItem(item);
			if (result == null) return;
			int slot = Dungeon.quickslot.getSlot(item);
			if (item instanceof MissileWeapon && !(item instanceof TippedDart)) {
				item.detachAll(curUser.belongings.backpack);
			} else {
				item.detach(curUser.belongings.backpack);
			}
			TransmuteCage.this.detach(curUser.belongings.backpack);
			if (!result.collect()) Dungeon.level.drop(result, curUser.pos).sprite.drop();
			if (slot != -1 && result.defaultAction() != null && Dungeon.hero.belongings.contains(result)) {
				Dungeon.quickslot.setSlot(slot, result);
			}
			Transmuting.show(curUser, item, result);
			curUser.sprite.emitter().start(Speck.factory(Speck.CHANGE), 0.2f, 10);
			curUser.spendAndNext(Actor.TICK);
		}
	};

	@Override protected void onEat(Hero hero) {
		hero.damage(Math.max(1, Math.round(hero.HT * 0.1f)), this);
		Buff.affect(hero, SuperArcane.class, 40f).level(5);
	}
}
