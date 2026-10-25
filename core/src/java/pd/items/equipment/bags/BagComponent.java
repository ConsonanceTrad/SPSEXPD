package pd.items.equipment.bags;

import java.util.ArrayList;

import pd.Dungeon;
import pd.actors.hero.Belongings;
import pd.items.Item;

/**
 * SPSEXPD: 包裹组件的标记接口。拓展组件（BagExpand）本身就是一个小背包，强化组件（BagUpgrade）给包裹提供功能。
 * 规则：拾取自动归位到 targetBag()；同容器排序 普通物品 -> 强化(10/20/30) -> 拓展(100 最后)；同类只留一个，重复转 5 暗金矿。
 */
public interface BagComponent {

	int SLOT_BONUS = 15;
	int COMPONENT_ORDER_EXPAND = 100;

	Class<? extends Bag> targetBag();

	int componentOrder();

	default boolean active() {
		Bag target = Components.findTargetBag(this);
		return target != null && Components.locate(this, target) >= 0;
	}

	final class Components {
		private Components() {}

		public static BagComponent of(Item item) {
			return item instanceof BagComponent ? (BagComponent) item : null;
		}

		public static Bag findTargetBag(BagComponent component) {
			if (Dungeon.hero == null || Dungeon.hero.belongings == null) return null;
			Belongings b = Dungeon.hero.belongings;
			if (component.targetBag().isInstance(b.backpack)) return b.backpack;
			for (Bag bag : b.getBags()) {
				if (component.targetBag().isInstance(bag)) return bag;
			}
			return null;
		}

		public static int locate(Object component, Bag container) {
			for (int i = 0; i < container.items.size(); i++) {
				if (container.items.get(i) == component) return i;
			}
			return -1;
		}

		public static void insertInto(Item item, Bag target) {
			BagComponent comp = of(item);
			if (comp == null) { target.items.add(item); return; }
			int insertAt = target.items.size();
			if (comp.componentOrder() < COMPONENT_ORDER_EXPAND) {
				for (int i = target.items.size() - 1; i >= 0; i--) {
					BagComponent other = of(target.items.get(i));
					if (other == null) break;
					if (other.componentOrder() <= comp.componentOrder()) break;
					insertAt = i;
				}
			}
			target.items.add(insertAt, item);
		}

		public static void migrateAll(Belongings belongings) {
			if (belongings == null) return;
			ArrayList<Item> pending = new ArrayList<>();
			for (Item i : belongings.backpack.items) {
				if (i instanceof BagComponent) pending.add(i);
			}
			for (Item item : pending) {
				BagComponent comp = (BagComponent) item;
				Bag target = findTargetBag(comp);
				if (target == null || target == belongings.backpack) continue;
				belongings.backpack.items.remove(item);
				insertInto(item, target);
			}
		}

		/** 组件拾取：归位到目标容器（没有则主背包）；同类已存在则转 5 暗金矿。返回 true 表示已处理。 */
		public static boolean collectComponent(Item item, Bag container) {
			BagComponent comp = of(item);
			if (comp == null) return false;
			if (Dungeon.hero == null || Dungeon.hero.belongings == null) return false;
			Bag target = findTargetBag(comp);
			if (target == null) target = Dungeon.hero.belongings.backpack;

			if (hasSameComponent(target, item)) {
				pd.items.quest.DarkGold gold = new pd.items.quest.DarkGold();
				gold.quantity(5);
				if (Dungeon.level != null) {
					Dungeon.level.drop(gold, Dungeon.hero.pos);
				}
				return true;
			}

			insertInto(item, target);
			Item.updateQuickslot();
			return true;
		}

		public static boolean hasSameComponent(Bag target, Item item) {
			for (Item i : target.items) {
				if (i != item && i.getClass() == item.getClass()) return true;
			}
			return false;
		}
	}
}