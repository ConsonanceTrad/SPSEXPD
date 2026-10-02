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

package pd.items.consum.scrolls;

import pd.Badges;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.buffs.Degrade;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.effects.particles.ShadowParticle;
import pd.items.Item;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.rings.Ring;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.Weapon;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemIconSheet;
import pd.utils.GLog;
import pd.windows.WndBag;
import pd.windows.WndUpgrade;
import pd.messages.InlineText;

public class ScrollOfUpgrade extends InventoryScroll {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ScrollOfUpgrade.class)
			.t("name", "升级卷轴")
			.t("inv_title", "选择一件要升级的物品")
			.t("weaken_curse", "升级卷轴减弱了物品上的诅咒。")
			.t("remove_curse", "升级卷轴净化了物品上的诅咒！")
			.t("desc", "这张卷轴可以升级一件物品或一组投掷武器。法杖可获得更高的效能与更多的充能，武器与护甲可获得更强的攻防属性，投掷武器组的耐久度可获得重置与提升，而戒指的效能可获得强化。\n\n这张卷轴能弱化诅咒甚至可能将其完全净化，但是效果不如祛邪卷轴稳定。不幸地，升级魔法也可能消除高级装备的附魔或刻印。");
	}


	@Override
	public void empoweredRead() {
		//The SPS-PD 0.9.8 empowered upgrade branch intentionally has no effect.
	}
	
	{
		icon = ItemIconSheet.SCROLL_UPGRADE;
		preferredBag = Belongings.Backpack.class;

		unique = true;

		talentFactor = 2f;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return item.isUpgradable();
	}

	@Override
	protected void onItemSelected( Item item ) {

		GameScene.show(new WndUpgrade(this, item, identifiedByUse));

	}

	public void reShowSelector(boolean force){
		identifiedByUse = force;
		curItem = this;
		GameScene.selectItem(itemSelector);
	}

	public WndBag.ItemSelector getSelector(boolean force){
		identifiedByUse = force;
		curItem = this;
		return itemSelector;
	}

	public Item upgradeItem( Item item ){
		upgrade( curUser );

		Degrade.detach( curUser, Degrade.class );

		//logic for telling the user when item properties change from upgrades
		//...yes this is rather messy
		if (item instanceof Weapon){
			Weapon w = (Weapon) item;
			boolean wasCursed = w.cursed;
			boolean wasHardened = w.enchantHardened;
			boolean hadCursedEnchant = w.hasCurseEnchant();
			boolean hadGoodEnchant = w.hasGoodEnchant();

			item = w.upgrade();

			if (w.cursedKnown && hadCursedEnchant && !w.hasCurseEnchant()){
				removeCurse( Dungeon.hero );
			} else if (w.cursedKnown && wasCursed && !w.cursed){
				weakenCurse( Dungeon.hero );
			}
			if (wasHardened && !w.enchantHardened){
				GLog.w( Messages.get(Weapon.class, "hardening_gone") );
			} else if (hadGoodEnchant && !w.hasGoodEnchant()){
				GLog.w( Messages.get(Weapon.class, "incompatible") );
			}

		} else if (item instanceof Armor){
			Armor a = (Armor) item;
			boolean wasCursed = a.cursed;
			boolean wasHardened = a.glyphHardened;
			boolean hadCursedGlyph = a.hasCurseGlyph();
			boolean hadGoodGlyph = a.hasGoodGlyph();

			item = a.upgrade();

			if (a.cursedKnown && hadCursedGlyph && !a.hasCurseGlyph()){
				removeCurse( Dungeon.hero );
			} else if (a.cursedKnown && wasCursed && !a.cursed){
				weakenCurse( Dungeon.hero );
			}
			if (wasHardened && !a.glyphHardened){
				GLog.w( Messages.get(Armor.class, "hardening_gone") );
			} else if (hadGoodGlyph && !a.hasGoodGlyph()){
				GLog.w( Messages.get(Armor.class, "incompatible") );
			}

		} else if (item instanceof Wand || item instanceof Ring) {
			boolean wasCursed = item.cursed;

			item = item.upgrade();

			if (item.cursedKnown && wasCursed && !item.cursed){
				removeCurse( Dungeon.hero );
			}

		} else {
			item = item.upgrade();
		}

		Badges.validateItemLevelAquired( item );
		Statistics.upgradesUsed++;
		Badges.validateMageUnlock();

		Catalog.countUse(item.getClass());

		return item;
	}
	
	public static void upgrade( Hero hero ) {
		hero.sprite.emitter().start( Speck.factory( Speck.UP ), 0.2f, 3 );
	}

	public static void weakenCurse( Hero hero ){
		GLog.p( Messages.get(ScrollOfUpgrade.class, "weaken_curse") );
		hero.sprite.emitter().start( ShadowParticle.UP, 0.05f, 5 );
	}

	public static void removeCurse( Hero hero ){
		GLog.p( Messages.get(ScrollOfUpgrade.class, "remove_curse") );
		hero.sprite.emitter().start( ShadowParticle.UP, 0.05f, 10 );
		Badges.validateClericUnlock();
	}
	
	@Override
	public int value() {
		return isKnown() ? 50 * quantity : super.value();
	}

	@Override
	public int energyVal() {
		return isKnown() ? 10 * quantity : super.energyVal();
	}
}
