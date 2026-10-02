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

package pd.items.consum.stones;

import pd.atlas.items.ConsumScrollAmuletAmuletDict;

import pd.actors.hero.Belongings;
import pd.actors.hero.Talent;
import pd.items.Item;
import pd.items.equipment.armor.Armor;
import pd.items.consum.scrolls.ScrollOfUpgrade;
import pd.items.consum.scrolls.exotic.ScrollOfEnchantment;
import pd.items.equipment.weapon.Weapon;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;
import pd.windows.IconTitle;
import pd.messages.InlineText;

public class StoneOfAugmentation extends InventoryStone {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(StoneOfAugmentation.class)
			.t("name", "强化符石")
			.t("inv_title", "强化一件物品")
			.t("desc", "这颗符石内的强力魔法可以用于强化装备的一种属性，代价是会减弱另一种属性。\n\n用于武器可以强化伤害或者速度。投掷武器的耐久度也会随着速度的增减而增减。\n\n用于护甲可以强化防御或者闪避。")
			.t("discover_hint", "你可在商店中中购买该物品，或通过炼金合成该物品。")
			.t("$wndaugment.choice", "强化一项属性也会弱化另一项属性。你想强化哪个属性？")
			.t("$wndaugment.already", "这个物品已经被强化过了，你可以调换被强化的属性，或是直接移除强化。")
			.t("$wndaugment.speed", "攻速上升 伤害下降")
			.t("$wndaugment.damage", "伤害上升 攻速下降")
			.t("$wndaugment.evasion", "闪避上升 防御下降")
			.t("$wndaugment.defense", "防御上升 闪避下降")
			.t("$wndaugment.none", "移除强化")
			.t("$wndaugment.cancel", "算了");
	}



	
	{
		preferredBag = Belongings.Backpack.class;
		image = ConsumScrollAmuletAmuletDict.STONE_AUGMENTATION_0;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return ScrollOfEnchantment.enchantable(item);
	}

	@Override
	protected void onItemSelected(Item item) {
		
		GameScene.show(new WndAugment( item));
		
	}
	
	public void apply( Weapon weapon, Weapon.Augment augment ) {
		
		weapon.augment = augment;
		useAnimation();
		ScrollOfUpgrade.upgrade(curUser);
		if (!anonymous) {
			curItem.detach(curUser.belongings.backpack);
			Catalog.countUse(getClass());
			Talent.onRunestoneUsed(curUser, curUser.pos, getClass());
		}
	}
	
	public void apply( Armor armor, Armor.Augment augment ) {
		
		armor.augment = augment;
		useAnimation();
		ScrollOfUpgrade.upgrade(curUser);
		if (!anonymous) {
			curItem.detach(curUser.belongings.backpack);
			Catalog.countUse(getClass());
			Talent.onRunestoneUsed(curUser, curUser.pos, getClass());
		}
	}
	
	@Override
	public int value() {
		return 30 * quantity;
	}

	@Override
	public int energyVal() {
		return 5 * quantity;
	}
	
	public class WndAugment extends Window {
		
		private static final int WIDTH			= 120;
		private static final int MARGIN 		= 2;
		private static final int BUTTON_WIDTH	= WIDTH - MARGIN * 2;
		private static final int BUTTON_HEIGHT	= 18;
		
		public WndAugment( final Item toAugment ) {
			super();
			
			IconTitle titlebar = new IconTitle( toAugment );
			titlebar.setRect( 0, 0, WIDTH, 0 );
			add( titlebar );

			String msg = Messages.get(this, "choice");

			if (toAugment instanceof Weapon && ((Weapon) toAugment).augment != Weapon.Augment.NONE){
				msg += "\n\n" + Messages.get(this, "already");
			} else if (toAugment instanceof Armor && ((Armor) toAugment).augment != Armor.Augment.NONE){
				msg += "\n\n" + Messages.get(this, "already");
			}

			RenderedTextBlock tfMesage = PixelScene.renderTextBlock( msg, 6 );
			tfMesage.maxWidth(WIDTH - MARGIN * 2);
			tfMesage.setPos(MARGIN, titlebar.bottom() + MARGIN);
			add( tfMesage );
			
			float pos = tfMesage.bottom() + MARGIN;
			
			if (toAugment instanceof Weapon){
				for (final Weapon.Augment aug : Weapon.Augment.values()){
					if (((Weapon) toAugment).augment != aug){
						RedButton btnAug = new RedButton( Messages.get(this, aug.name()) ) {
							@Override
							protected void onClick() {
								hide();
								StoneOfAugmentation.this.apply( (Weapon)toAugment, aug );
							}
						};
						btnAug.setRect( MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT );
						add( btnAug );
						
						pos = btnAug.bottom();
					}
				}
				
			} else if (toAugment instanceof Armor){
				for (final Armor.Augment aug : Armor.Augment.values()){
					if (((Armor) toAugment).augment != aug){
						RedButton btnAug = new RedButton( Messages.get(this, aug.name()) ) {
							@Override
							protected void onClick() {
								hide();
								StoneOfAugmentation.this.apply( (Armor) toAugment, aug );
							}
						};
						btnAug.setRect( MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT );
						add( btnAug );
						
						pos = btnAug.bottom();
					}
				}
			}
			
			RedButton btnCancel = new RedButton( Messages.get(this, "cancel") ) {
				@Override
				protected void onClick() {
					hide();
					activate(curUser.pos);
				}
			};
			btnCancel.setRect( MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT );
			add( btnCancel );
			
			resize( WIDTH, (int)btnCancel.bottom() + MARGIN );
		}
		
		@Override
		public void onBackPressed() {
			super.onBackPressed();
			activate(curUser.pos);
		}
	}
}
