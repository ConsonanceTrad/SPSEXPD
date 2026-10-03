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

package pd.items;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.Talent;
import pd.items.equipment.artifacts.Artifact;
import pd.items.equipment.rings.Ring;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import pd.utils.GLog;
import pd.windows.WndOptions;
import render.utils.math.Random;
import pd.messages.InlineText;


public abstract class KindofMisc extends EquipableItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(KindofMisc.class)
			.t("unequip_title", "卸下一件装备")
			.t("unequip_message", "你必须先取下一件装备。请选择要替换的物品。");
	}




	@Override
	public boolean doEquip(final Hero hero) {

		//SPS: 徽章走独立徽章槽（不参与 5 个饰品槽的分配）
		if (this instanceof Badge) {
			Badge current = hero.belongings.badge;
			if (current == this) {
				return false;
			}
			if (current != null && !current.doUnequip(hero, true, false)) {
				return false;
			}
			hero.belongings.badge = (Badge) this;
			detach( hero.belongings.backpack );
			Talent.onItemEquipped(hero, this);
			activate( hero );
			cursedKnown = true;
			if (cursed) {
				equipCursed( hero );
				GLog.n( Messages.get(this, "equip_cursed", this) );
			}
			hero.spendAndNext( timeToEquip(hero) );
			return true;
		}

		//SPS: 5 个饰品槽分两区——槽 0-2 归非戒指饰品，槽 3-4 归戒指
		final KindofMisc[] miscs = new KindofMisc[5];
		miscs[0] = hero.belongings.artifact;
		miscs[1] = hero.belongings.misc;
		miscs[2] = hero.belongings.ring;
		miscs[3] = hero.belongings.accessory4;
		miscs[4] = hero.belongings.accessory5;

		//SPS: 戒指只能用槽 3-4，其余饰品只能用槽 0-2
		final boolean isRing = this instanceof Ring;
		final int slotFrom = isRing ? 3 : 0;
		final int slotTo   = isRing ? 5 : 3;

		int emptySlot = -1;
		int sameSlot = -1;
		for (int i = slotFrom; i < slotTo; i++) {
			if (miscs[i] == null) {
				if (emptySlot < 0) emptySlot = i;
			} else if (miscs[i].getClass() == getClass()) {
				sameSlot = i;
			}
		}

		//同类已装备：先换下旧的（回到背包），再占用该槽
		if (sameSlot >= 0) {
			if (!miscs[sameSlot].doUnequip(hero, true, false)) {
				return false;
			}
			emptySlot = sameSlot;
		}

		if (emptySlot < 0) {

			//SPS: 只允许替换本区内的饰品槽（区外按钮禁用且显示 ---）
			final boolean[] enabled = new boolean[5];
			for (int i = 0; i < 5; i++) enabled[i] = i >= slotFrom && i < slotTo && miscs[i] != null;

			GameScene.show(
					new WndOptions(new ItemSprite(this),
							Messages.get(KindofMisc.class, "unequip_title"),
							Messages.get(KindofMisc.class, "unequip_message"),
							slotLabel(0, miscs, slotFrom, slotTo),
							slotLabel(1, miscs, slotFrom, slotTo),
							slotLabel(2, miscs, slotFrom, slotTo),
							slotLabel(3, miscs, slotFrom, slotTo),
							slotLabel(4, miscs, slotFrom, slotTo)) {

						@Override
						protected void onSelect(int index) {

							KindofMisc equipped = miscs[index];
							//we directly remove the item because we want to have inventory capacity
							// to unequip the equipped one, but don't want to trigger any other
							// item detaching logic
							int slot = Dungeon.quickslot.getSlot(KindofMisc.this);
							slotOfUnequipped = -1;
							Dungeon.hero.belongings.backpack.items.remove(KindofMisc.this);
							if (equipped != null && equipped.doUnequip(hero, true, false)) {
								Dungeon.hero.belongings.backpack.items.add(KindofMisc.this);
								doEquip(hero);
							} else {
								Dungeon.hero.belongings.backpack.items.add(KindofMisc.this);
							}
							if (slot != -1) {
								Dungeon.quickslot.setSlot(slot, KindofMisc.this);
							} else if (slotOfUnequipped != -1 && defaultAction() != null){
								Dungeon.quickslot.setSlot(slotOfUnequipped, KindofMisc.this);
							}
							updateQuickslot();
						}

						@Override
						protected boolean enabled(int index) {
							return enabled[index];
						}
					});

			return false;

		} else {

			// 15/25% chance
			if (hero.heroClass != HeroClass.CLERIC && hero.hasTalent(Talent.HOLY_INTUITION)
					&& cursed && !cursedKnown
					&& Random.Int(20) < 1 + 2*hero.pointsInTalent(Talent.HOLY_INTUITION)){
				cursedKnown = true;
				GLog.p(Messages.get(this, "curse_detected"));
				return false;
			}

			//SPS: 放进扫描出的空饰品槽
			switch (emptySlot) {
				case 0:  hero.belongings.artifact = this;    break;
				case 1:  hero.belongings.misc = this;        break;
				case 2:  hero.belongings.ring = this;        break;
				case 3:  hero.belongings.accessory4 = this;  break;
				default: hero.belongings.accessory5 = this;  break;
			}

			detach( hero.belongings.backpack );

			Talent.onItemEquipped(hero, this);
			activate( hero );

			cursedKnown = true;
			if (cursed) {
				equipCursed( hero );
				GLog.n( Messages.get(this, "equip_cursed", this) );
			}

			hero.spendAndNext( timeToEquip(hero) );
			return true;

		}

	}

	//SPS: 饰品替换窗口的槽标签——区外的格子不适用当前物品，显示 ---
	private static String slotLabel( int index, KindofMisc[] slots, int from, int to ) {
		if (index < from || index >= to) return "---";
		return slots[index] == null ? "---" : Messages.titleCase(slots[index].title());
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if (super.doUnequip(hero, collect, single)){

			//SPS: 5 个通用饰品槽 + 徽章槽
			if (hero.belongings.artifact == this) {
				hero.belongings.artifact = null;
			} else if (hero.belongings.misc == this) {
				hero.belongings.misc = null;
			} else if (hero.belongings.ring == this){
				hero.belongings.ring = null;
			} else if (hero.belongings.accessory4 == this){
				hero.belongings.accessory4 = null;
			} else if (hero.belongings.accessory5 == this){
				hero.belongings.accessory5 = null;
			} else if (hero.belongings.badge == this){
				hero.belongings.badge = null;
			}

			return true;

		} else {

			return false;

		}
	}

	@Override
	public boolean isEquipped( Hero hero ) {
		return hero != null && (hero.belongings.artifact() == this
				|| hero.belongings.misc() == this
				|| hero.belongings.ring() == this
				|| hero.belongings.accessory4 == this
				|| hero.belongings.accessory5 == this
				|| hero.belongings.badge == this);
	}

}
