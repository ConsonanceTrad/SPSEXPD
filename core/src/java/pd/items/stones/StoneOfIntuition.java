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

package pd.items.stones;

import pd.Assets;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.hero.Talent;
import pd.effects.Identification;
import pd.items.Item;
import pd.items.potions.Potion;
import pd.items.potions.exotic.ExoticPotion;
import pd.items.rings.Ring;
import pd.items.scrolls.Scroll;
import pd.items.scrolls.exotic.ExoticScroll;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import pd.sprites.ItemSpriteSheet;
import pd.ui.IconButton;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;
import pd.utils.GLog;
import pd.windows.IconTitle;
import render.noosa.Image;
import render.utils.serialize.Reflection;

import java.util.ArrayList;

public class StoneOfIntuition extends InventoryStone {
	
	{
		image = ItemSpriteSheet.STONE_INTUITION;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		if (item instanceof Ring){
			return !((Ring) item).isKnown();
		} else if (item instanceof Potion){
			return !((Potion) item).isKnown();
		} else if (item instanceof Scroll){
			return !((Scroll) item).isKnown();
		}
		return false;
	}
	
	@Override
	protected void onItemSelected(Item item) {

		GameScene.show( new WndGuess(item));
		
	}

	@Override
	public String desc() {
		String text = super.desc();
		if (Dungeon.hero != null){
			if (Dungeon.hero.buff(IntuitionUseTracker.class) == null){
				text += "\n\n" + Messages.get(this, "break_info");
			} else {
				text += "\n\n" + Messages.get(this, "break_warn");
			}
		}
		return text;
	}

	public static class IntuitionUseTracker extends Buff {{ revivePersists = true; }};
	
	private static Class curGuess = null;

	public class WndGuess extends Window {
		
		private static final int WIDTH = 120;
		private static final int BTN_SIZE = 20;
		
		public WndGuess(final Item item){
			
			IconTitle titlebar = new IconTitle();
			titlebar.icon( new ItemSprite(item) );
			titlebar.label( Messages.titleCase(item.name()) );
			titlebar.setRect( 0, 0, WIDTH, 0 );
			add( titlebar );
			
			RenderedTextBlock text = PixelScene.renderTextBlock(6);
			text.text( Messages.get(this, "text") );
			text.setPos(0, titlebar.bottom()+2);
			text.maxWidth( WIDTH );
			add(text);
			
			final RedButton guess = new RedButton(""){
				@Override
				protected void onClick() {
					super.onClick();
					useAnimation();
					if (item.getClass() == curGuess){
						if (item instanceof Ring){
							((Ring) item).setKnown();
							Item.updateQuickslot();
						} else {
							item.identify();
						}
						GLog.p( Messages.get(WndGuess.class, "correct") );
						curUser.sprite.parent.add( new Identification( curUser.sprite.center().offset( 0, -16 ) ) );
					} else {
						GLog.w( Messages.get(WndGuess.class, "incorrect") );
					}
					if (!anonymous) {
						Catalog.countUse(StoneOfIntuition.class);
						if (curUser.buff(IntuitionUseTracker.class) == null) {
							Buff.affect(curUser, IntuitionUseTracker.class);
						} else {
							curItem.detach(curUser.belongings.backpack);
							curUser.buff(IntuitionUseTracker.class).detach();
						}
						Talent.onRunestoneUsed(curUser, curUser.pos, StoneOfIntuition.class);
					}
					curGuess = null;
					hide();
				}
			};
			guess.visible = false;
			guess.icon( new ItemSprite(item) );
			guess.enable(false);
			guess.setRect(0, 80, WIDTH, 20);
			add(guess);
			
			float left;
			float top = text.bottom() + 5;
			int rows;
			int placed = 0;
			
			final ArrayList<Class<?extends Item>> unIDed = new ArrayList<>();
			if (item.isIdentified()){
				hide();
				return;
			} else if (item instanceof Potion){
				if (item instanceof ExoticPotion) {
					for (Class<?extends Item> i : Potion.getUnknown()){
						unIDed.add(ExoticPotion.regToExo.get(i));
					}
				} else {
					unIDed.addAll(Potion.getUnknown());
				}
			} else if (item instanceof Scroll){
				if (item instanceof ExoticScroll) {
					for (Class<?extends Item> i : Scroll.getUnknown()){
						unIDed.add(ExoticScroll.regToExo.get(i));
					}
				} else {
					unIDed.addAll(Scroll.getUnknown());
				}
			} else if (item instanceof Ring) {
				unIDed.addAll(Ring.getUnknown());
			} else {
				hide();
				return;
			}
			
			if (unIDed.size() <= 5){
				rows = 1;
				top += BTN_SIZE/2f;
				left = (WIDTH - BTN_SIZE*unIDed.size())/2f;
			} else {
				rows = 2;
				left = (WIDTH - BTN_SIZE*((unIDed.size()+1)/2))/2f;
			}
			
			for (final Class<?extends Item> i : unIDed){

				IconButton btn = new IconButton(){
					@Override
					protected void onClick() {
						curGuess = i;
						guess.visible = true;
						guess.text( Messages.titleCase(Messages.get(curGuess, "name")) );
						guess.enable(true);
						super.onClick();
					}
				};
				Image im = new Image(Assets.Sprites.ITEM_ICONS);
				im.frame(ItemSpriteSheet.Icons.film.get(Reflection.newInstance(i).icon));
				im.scale.set(2f);
				btn.icon(im);
				btn.setRect(left + placed*BTN_SIZE, top, BTN_SIZE, BTN_SIZE);
				add(btn);
				
				placed++;
				if (rows == 2 && placed == ((unIDed.size()+1)/2)){
					placed = 0;
					if (unIDed.size() % 2 == 1){
						left += BTN_SIZE/2f;
					}
					top += BTN_SIZE;
				}
			}
			
			resize(WIDTH, 100);
			
		}

	}
}
