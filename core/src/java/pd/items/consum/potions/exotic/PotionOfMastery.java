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

package pd.items.consum.potions.exotic;

import pd.Assets;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.items.Item;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.weapon.SpiritBow;
import pd.items.equipment.weapon.Weapon;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import pd.sprites.ItemIconSheet;
import pd.utils.GLog;
import pd.windows.WndBag;
import pd.windows.WndOptions;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import pd.messages.InlineText;

public class PotionOfMastery extends ExoticPotion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfMastery.class)
			.t("name", "肌肉记忆合剂")
			.t("prompt", "精通一件物品")
			.t("weapon_easier", "你的武器变得前所未有的顺手！")
			.t("armor_easier", "你的护甲变得前所未有的合身！")
			.t("desc", "这个合剂不会直接增强你的力量，但却会为你建立使用特定物品的肌肉记忆。使你对其的运用更加得心应手，仿佛已经练习了千百遍。\n\n所选武器或护甲所需要的力量减少2点。此合剂在每个物品上只能使用一次。");
	}

	
	{
		icon = ItemIconSheet.POTION_MASTERY;

		unique = true;

		talentFactor = 2f;
	}

	protected static boolean identifiedByUse = false;

	@Override
	//need to override drink so that time isn't spent right away
	protected void drink(final Hero hero) {

		if (!isKnown()) {
			identify();
			curItem = detach( hero.belongings.backpack );
			identifiedByUse = true;
		} else {
			identifiedByUse = false;
		}

		GameScene.selectItem(itemSelector);
	}

	protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(PotionOfMastery.class, "prompt");
		}

		@Override
		public boolean itemSelectable(Item item) {
			return
					(item instanceof Weapon && !(item instanceof SpiritBow) && !((Weapon) item).masteryPotionBonus)
					|| (item instanceof Armor && !((Armor) item).masteryPotionBonus);
		}

		@Override
		public void onSelect(Item item) {

			if (item == null && identifiedByUse){
				GameScene.show( new WndOptions(new ItemSprite(PotionOfMastery.this),
						Messages.titleCase(name()),
						Messages.get(ExoticPotion.class, "warning"),
						Messages.get(ExoticPotion.class, "yes"),
						Messages.get(ExoticPotion.class, "no") ) {
					@Override
					protected void onSelect( int index ) {
						switch (index) {
							case 0:
								curUser.spendAndNext(1f);
								identifiedByUse = false;
								break;
							case 1:
								GameScene.selectItem(itemSelector);
								break;
						}
					}
					public void onBackPressed() {}
				} );
			} else if (item != null) {

				if (item instanceof Weapon) {
					((Weapon) item).masteryPotionBonus = true;
					GLog.p( Messages.get(PotionOfMastery.class, "weapon_easier") );
				} else if (item instanceof Armor) {
					((Armor) item).masteryPotionBonus = true;
					GLog.p( Messages.get(PotionOfMastery.class, "armor_easier") );
				}
				updateQuickslot();

				Sample.INSTANCE.play( Assets.Sounds.DRINK );
				curUser.sprite.operate(curUser.pos);

				if (!identifiedByUse) {
					curItem.detach(curUser.belongings.backpack);
				}
				identifiedByUse = false;

				if (!anonymous) {
					Catalog.countUse(PotionOfMastery.class);
					if (Random.Float() < talentChance) {
						Talent.onPotionUsed(curUser, curUser.pos, talentFactor);
					}
				}
			}

		}
	};
	
}
