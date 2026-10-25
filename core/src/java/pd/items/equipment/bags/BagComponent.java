/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.items.equipment.bags;

import java.util.ArrayList;

import pd.Dungeon;
import pd.items.Item;
import pd.items.quest.DarkGold;
import pd.messages.InlineText;
import pd.messages.Messages;

/**
 * SPSEXPD: 通用包裹组件的基类。
 *
 * <p>组件分两类：
 * <ul>
 *   <li>{@link BagExpand} 拓展组件——给目标容器 +{@link #SLOT_BONUS} 格</li>
 *   <li>{@link BagUpgrade} 强化组件——给目标容器提供功能（产能量 / 转化食物 / 处理种子）</li>
 * </ul>
 *
 * <p>规则：
 * <ul>
 *   <li>拾取时自动归位到 {@link #targetBag()} 指定的容器（没有该容器时留在主背包、**不生效**）</li>
 *   <li>同一容器内排序：普通物品 → 强化组件（{@link #componentOrder()} 10/20/30）→ 拓展组件（100，永远最后）</li>
 *   <li>同一容器最多 1 个同类组件，重复的转换成 5 个暗金矿</li>
 * </ul>
 */
public abstract class BagComponent extends Item {

	/** 每个拓展组件提供的格数。 */
	public static final int SLOT_BONUS = 15;

	static {
		InlineText.of(BagComponent.class)
				.t("duplicate", "你已经有了同款组件，这一件化成了暗金矿。");
	}

	/** 目标容器类（主背包用 {@link pd.actors.hero.Belongings.Backpack}）。 */
	public abstract Class<? extends Bag> targetBag();

	/** 同容器内的排序位：强化组件 10/20/30，拓展组件 100。 */
	public int componentOrder() {
		return COMPONENT_ORDER_EXPAND;
	}

	public static final int COMPONENT_ORDER_EXPAND = 100;

	/** 组件是否已装在目标容器里（= 生效）。 */
	public boolean active() {
		if (Dungeon.hero == null || Dungeon.hero.belongings == null) return false;
		Bag target = findTargetBag();
		return target != null && target.items.contains(this);
	}

	/** 找到目标容器的实例；主背包也算。 */
	protected Bag findTargetBag() {
		if (Dungeon.hero == null || Dungeon.hero.belongings == null) return null;
		Bag backpack = Dungeon.hero.belongings.backpack;
		if (targetBag().isInstance(backpack)) return backpack;
		for (Bag bag : Dungeon.hero.belongings.getBags()) {
			if (targetBag().isInstance(bag)) return bag;
		}
		return null;
	}

	@Override
	public boolean collect(Bag container) {
		//SPSEXPD: 组件不走普通拾取流程——直接归位到目标容器的组件区段
		if (Dungeon.hero == null || Dungeon.hero.belongings == null) return false;
		Bag target = findTargetBag();
		if (target == null) target = Dungeon.hero.belongings.backpack;

		//同类组件已经在该容器里 → 转成 5 个暗金矿
		for (Item i : target.items) {
			if (i.getClass() == this.getClass()) {
				DarkGold gold = new DarkGold();
				gold.quantity(5);
				if (Dungeon.level != null) {
					Dungeon.level.drop(gold, Dungeon.hero.pos);
				} else if (!gold.collect(target)) {
					return true;
				}
				return true;
			}
		}

		insertInto(target);
		Item.updateQuickslot();
		return true;
	}

	/** 按「强化组件在前、拓展组件最后」的规则插进容器的组件区段。 */
	public void insertInto(Bag target) {
		int insertAt = target.items.size();
		if (componentOrder() < COMPONENT_ORDER_EXPAND) {
			//强化组件：从末尾往前找第一个 order 比自己大的强化组件
			for (int i = target.items.size() - 1; i >= 0; i--) {
				Item it = target.items.get(i);
				if (!(it instanceof BagComponent)) break;
				BagComponent other = (BagComponent) it;
				if (other.componentOrder() <= componentOrder()) break;
				insertAt = i;
			}
		} else {
			//拓展组件：永远追加到最后
			insertAt = target.items.size();
		}
		target.items.add(insertAt, this);
	}

	/** SPSEXPD: 未生效时的提示文案（放在主背包里但没有目标容器）。 */
	@Override
	public String desc() {
		String text = super.desc();
		if (!active()) {
			text += "\n\n" + Messages.get(this, "inactive");
		}
		return text;
	}

	{
		stackable = false;
		unique = true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int value() {
		return 0;
	}

	//*** 两个子类层次

	/** 拓展组件：给目标容器 +{@link #SLOT_BONUS} 格。 */
	public abstract static class BagExpand extends BagComponent {

		static {
			InlineText.of(BagExpand.class)
					.t("desc_suffix", "装进%1$s后，内部会多出 %2$d 格空间。")
					.t("inactive", "还没有对应的包裹可以装配，暂时没有效果。");
		}

		@Override
		public int componentOrder() {
			return COMPONENT_ORDER_EXPAND;
		}

		/** 「装进某某包裹后多出 15 格」这句说明。 */
		protected String bonusSuffix() {
			String bagName;
			try {
				Bag target = render.utils.serialize.Reflection.newInstance(targetBag());
				bagName = target == null ? "" : Messages.titleCase(target.name());
			} catch (Exception e) {
				bagName = "";
			}
			return Messages.get(BagExpand.class, "desc_suffix", bagName, SLOT_BONUS);
		}
	}

	/** 强化组件：给目标容器提供功能，彼此有固定顺序（10 / 20 / 30）。 */
	public abstract static class BagUpgrade extends BagComponent {
		//顺序由子类覆写 componentOrder()
	}

	/** SPSEXPD: 供容器查询「本容器是否装有指定类型的组件」。 */
	public static <T extends BagComponent> T find(Bag container, Class<T> type) {
		if (container == null) return null;
		for (Item i : container.items) {
			if (type.isInstance(i)) return type.cast(i);
		}
		return null;
	}

	/** SPSEXPD: 把主背包里的组件迁移到（可能刚获得的）目标容器里。 */
	public static void migrateAll(pd.actors.hero.Belongings belongings) {
		if (belongings == null) return;
		ArrayList<BagComponent> pending = new ArrayList<>();
		for (Item i : belongings.backpack.items) {
			if (i instanceof BagComponent) pending.add((BagComponent) i);
		}
		for (BagComponent comp : pending) {
			Bag target = comp.findTargetBag();
			if (target == null || target == belongings.backpack) continue;
			belongings.backpack.items.remove(comp);
			comp.insertInto(target);
		}
	}
}
