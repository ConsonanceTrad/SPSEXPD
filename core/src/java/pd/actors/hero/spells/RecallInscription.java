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

package pd.actors.hero.spells;

import pd.Dungeon;
import pd.ShatteredPixelDungeon;
import pd.actors.buffs.FlavourBuff;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.effects.Enchanting;
import pd.items.Item;
import pd.items.equipment.artifacts.HolyTome;
import pd.items.consum.scrolls.Scroll;
import pd.items.consum.scrolls.ScrollOfTransmutation;
import pd.items.consum.scrolls.exotic.ExoticScroll;
import pd.items.consum.scrolls.exotic.ScrollOfEnchantment;
import pd.items.consum.scrolls.exotic.ScrollOfMetamorphosis;
import pd.items.consum.stones.InventoryStone;
import pd.items.consum.stones.Runestone;
import pd.items.consum.stones.StoneOfAugmentation;
import pd.items.consum.stones.StoneOfEnchantment;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.ui.HeroIcon;
import render.utils.data.Callback;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;
import pd.messages.InlineText;

public class RecallInscription extends ClericSpell {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(RecallInscription.class)
			.t("name", "卷藏咒言")
			.t("short_desc", "重复最近使用的符石或卷轴效果。")
			.t("desc", "牧师使用神圣魔法复制最近使用的符文以再次触发%s回合前使用的卷轴或符石的魔法效果。\n\n卷藏咒言不能复制升级卷轴，充能消耗根据最近一次使用的物品变化而变化：符石2点充能、卷轴3点充能、秘卷4点充能。复制嬗变卷轴或嬗变/升级卷轴的符石/秘卷时，充能消耗还会翻倍。")
			.t("useditemtracker.name", "近期已使用符文")
			.t("useditemtracker.desc", "牧师近期已使用一个兼容卷藏咒言法术效果的物品，可以通过施法再次触发物品效果。\n\n已使用物品：%1$s\n\n剩余回合数：%2$s");
	}


	public static RecallInscription INSTANCE = new RecallInscription();

	@Override
	public int icon() {
		return HeroIcon.RECALL_GLYPH;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", Dungeon.hero.pointsInTalent(Talent.RECALL_INSCRIPTION) == 2 ? 300 : 10) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
	}

	@Override
	public void onCast(HolyTome tome, Hero hero) {

		if (hero.buff(UsedItemTracker.class) == null){
			return;
		}

		Item item = Reflection.newInstance(hero.buff(UsedItemTracker.class).item);

		item.setCurrent(hero);

		hero.sprite.operate(hero.pos);
		Enchanting.show(hero, item);

		if (item instanceof Scroll){
			((Scroll) item).anonymize();
			((Scroll) item).talentChance = 0; //does not trigger on-scroll effects
			((Scroll) item).doRead();
		} else if (item instanceof Runestone){
			((Runestone) item).anonymize();
			if (item instanceof InventoryStone){
				((InventoryStone) item).directActivate();
			} else {
				//we're already on the render thread, but we want to delay this
				//as things like time freeze cancel can stop stone throwing from working
				ShatteredPixelDungeon.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						item.doThrow(hero);
					}
				});
			}
		}

		onSpellCast(tome, hero);
		if (hero.buff(UsedItemTracker.class) != null){
			hero.buff(UsedItemTracker.class).detach();
		}

	}

	@Override
	public float chargeUse(Hero hero) {
		if (hero.buff(UsedItemTracker.class) != null){
			Class<? extends Item> item = hero.buff(UsedItemTracker.class).item;
			if (ExoticScroll.class.isAssignableFrom(item)){
				if (item == ScrollOfMetamorphosis.class || item == ScrollOfEnchantment.class){
					return 8;
				} else {
					return 4;
				}
			} else if (Scroll.class.isAssignableFrom(item)){
				if (item == ScrollOfTransmutation.class){
					return 6;
				} else {
					return 3;
				}
			} else if (Runestone.class.isAssignableFrom(item)){
				if (item == StoneOfAugmentation.class || item == StoneOfEnchantment.class){
					return 4;
				} else {
					return 2;
				}
			}
		}
		return 0;
	}

	@Override
	public boolean canCast(Hero hero) {
		return super.canCast(hero)
				&& hero.hasTalent(Talent.RECALL_INSCRIPTION)
				&& hero.buff(UsedItemTracker.class) != null;
	}

	public static class UsedItemTracker extends FlavourBuff {

		{
			type = buffType.POSITIVE;
		}

		public Class<?extends Item> item;

		@Override
		public int icon() {
			return BuffIndicator.GLYPH_RECALL;
		}

		@Override
		public float iconFadePercent() {
			float duration = Dungeon.hero.pointsInTalent(Talent.RECALL_INSCRIPTION) == 2 ? 300 : 10;
			return Math.max(0, (duration - visualcooldown()) / duration);
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", Messages.titleCase(Reflection.newInstance(item).name()), dispTurns());
		}

		private static String ITEM = "item";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(ITEM, item);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			item = bundle.getClass(ITEM);
		}
	}

}
