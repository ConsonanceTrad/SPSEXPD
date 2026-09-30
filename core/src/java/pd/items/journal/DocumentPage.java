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

package pd.items.journal;

import pd.Assets;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.journal.Document;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.windows.WndJournal;
import render.noosa.audio.Sample;
import render.utils.serialize.Bundle;

public abstract class DocumentPage extends Item {
	
	{
		image = ItemSpriteSheet.MASTERY;
	}

	public abstract Document document();
	
	private String page;
	
	public void page( String page ){
		this.page = page;
	}
	
	public String page(){
		return page;
	}
	
	@Override
	public final boolean doPickUp(Hero hero, int pos) {
		GameScene.pickUpJournal(this, pos);
		GameScene.flashForDocument(document(), page());
		if (document() == Document.ADVENTURERS_GUIDE){
			WndJournal.last_index = 1;
		} else if (document() == Document.ALCHEMY_GUIDE) {
			WndJournal.last_index = 2;
			WndJournal.AlchemyTab.currentPageIdx = document().pageIdx(page());
		} else if (document().isLoreDoc()){
			WndJournal.last_index = 3;
			WndJournal.CatalogTab.currentItemIdx = 3;
		}
		document().findPage(page);
		Sample.INSTANCE.play( Assets.Sounds.ITEM );
		hero.spendAndNext( pickupDelay() );
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}
	
	private static final String PAGE = "page";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( PAGE, page() );
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		page = bundle.getString( PAGE );
	}
}
