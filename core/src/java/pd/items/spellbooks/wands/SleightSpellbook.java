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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pd.items.spellbooks.wands;

import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.mobs.Mob;
import pd.actors.hero.Hero;
import pd.items.Heap;
import pd.items.Item;
import pd.items.StoneOre;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;

/**
 * SPSEXPD: 妙手书基类（魔术之手法杖的书化）——效果照原杖主干直接重写：
 * 生物=伤害+偷窃（固定概率，不复刻原杖价位曲线）；地面=货架/宝箱怪/开箱/取物。
 */
public abstract class SleightSpellbook extends pd.items.spellbooks.Spellbook {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(SleightSpellbook.class)
			.t("nothing", "那只手扑了个空。")
			.t("stolen_stone", "你从%1$s身上只摸到了一块石头。")
			.t("stolen", "你从%1$s身上顺走了%2$s！")
			.t("steal_fail", "%1$s护得太紧，没能得手。")
			.t("steal_goods_ok", "你从货架上顺走了%1$s。")
			.t("steal_goods_fail", "失手了——商店老板发现了你！")
			.t("grabbed", "那只手取来了%1$s。");
	}

	@Override
	public boolean targetRequired() { return true; }

	protected abstract int castLevel();
	protected abstract int stealChancePercent();
	protected abstract int minDamage();
	protected abstract int maxDamage();

	@Override
	public void castAt( Hero hero, int cell ) {
		Char target = Actor.findChar( cell );
		if (target != null) {
			target.damage( power( hero, render.utils.math.Random.NormalIntRange( minDamage(), maxDamage() ) ), this );
			if (target instanceof Mob) {
				stealFrom( (Mob) target, hero );
			}
			return;
		}

		Heap heap = pd.Dungeon.level != null ? pd.Dungeon.level.heaps.get( cell ) : null;
		if (heap != null) {
			if (heap.type == Heap.Type.FOR_SALE || heap.type == Heap.Type.FOR_LIFE) {
				stealFromShop( heap, hero );
				return;
			}
			if (heap.type == Heap.Type.MIMIC || heap.type == Heap.Type.G_MIMIC) {
				//惊醒宝箱怪（照原杖：open 会把它变成怪）
				heap.open( hero );
				return;
			}
			if (pd.items.equipment.artifacts.MasterThievesArmband.isRemoteOpenable( heap )) {
				heap.open( hero );
				return;
			}
			if (!heap.isEmpty()) {
				grabGroundItem( heap, hero );
				return;
			}
		}
		//其余情况：什么都没发生
		GLog.w( Messages.get( SleightSpellbook.class, "nothing" ) );
	}

	private void stealFrom( Mob mob, Hero owner ) {
		Item loot = mob.firstItem ? mob.SupercreateLoot() : null;
		if (loot == null) {
			GLog.i( Messages.get( SleightSpellbook.class, "stolen_stone", Messages.get( mob, "name" ) ) );
			deliver( new StoneOre(), owner );
			return;
		}
		if (render.utils.math.Random.Float() >= stealChancePercent() / 100f) {
			GLog.w( Messages.get( SleightSpellbook.class, "steal_fail", Messages.get( mob, "name" ) ) );
			return;
		}
		mob.firstItem = false;
		GLog.i( Messages.get( SleightSpellbook.class, "stolen", Messages.get( mob, "name" ), loot.name() ) );
		deliver( loot, owner );
	}

		private void stealFromShop( Heap heap, Hero owner ) {
		Item goods = heap.isEmpty() ? null : heap.pickUp();
		if (goods == null) return;
		if (render.utils.math.Random.Float() < stealChancePercent() / 100f) {
			GLog.i( Messages.get( SleightSpellbook.class, "steal_goods_ok", goods.name() ) );
			deliver( goods, owner );
		} else {
			GLog.w( Messages.get( SleightSpellbook.class, "steal_goods_fail" ) );
			pd.Dungeon.level.drop( goods, heap.pos ).sprite.drop();
			//惊动本层商人（照原杖：先取快照再迭代，老板会召唤守卫）
			if (pd.Dungeon.level.mobs() != null) {
				for (Mob mob : pd.Dungeon.level.mobs().snapshot()) {
					if (mob instanceof pd.actors.mobs.npcs.Shopkeeper) ((pd.actors.mobs.npcs.Shopkeeper) mob).noticeTheft();
				}
			}
		}
	}

	private void grabGroundItem( Heap heap, Hero owner ) {
		Item item = heap.pickUp();
		if (item != null) {
			GLog.i( Messages.get( SleightSpellbook.class, "grabbed", item.name() ) );
			deliver( item, owner );
		}
	}

	private void deliver( Item item, Hero owner ) {
		if (!item.collect( owner.belongings.backpack )) {
			pd.Dungeon.level.drop( item, owner.pos ).sprite.drop();
		}
	}
}
