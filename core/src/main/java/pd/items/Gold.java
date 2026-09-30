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

import pd.Assets;
import pd.Badges;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.hero.Hero;
import pd.effects.FloatingText;
import pd.journal.Catalog;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.sprites.ItemSpriteSheet;
import render.noosa.audio.Sample;
import render.utils.Random;

import java.util.ArrayList;

public class Gold extends Item {
	public static final String AC_MAKEBAG = "MAKEBAG";

	{
		image = ItemSpriteSheet.GOLD;
		stackable = true;
	}
	
	public Gold() {
		this( 1 );
	}
	
	public Gold( int value ) {
		this.quantity = value;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = new ArrayList<>();
		if (Dungeon.gold > 10000) actions.add(AC_MAKEBAG);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_MAKEBAG.equals(action)) {
			super.execute(hero, action);
			return;
		}
		makeBag(hero);
	}

	public boolean makeBag(Hero hero) {
		if (hero == null || Dungeon.level == null || Dungeon.gold <= 10000) return false;
		Dungeon.gold -= 10000;
		Heap heap = Dungeon.level.drop(new GoldBag(), hero.pos);
		if (heap.sprite != null) heap.sprite.drop();
		if (hero.sprite != null) hero.sprite.operate(hero.pos);
		hero.spendAndNext(1f);
		return true;
	}
	
	@Override
	public boolean doPickUp(Hero hero, int pos) {

		Catalog.setSeen(getClass());
		Statistics.itemTypesDiscovered.add(getClass());

		Dungeon.gold += quantity;
		Statistics.goldCollected += quantity;
		Badges.validateGoldCollected();

		GameScene.pickUp( this, pos );
		hero.sprite.showStatusWithIcon( CharSprite.NEUTRAL, Integer.toString(quantity), FloatingText.GOLD );
		hero.spendAndNext( pickupDelay() );
		
		Sample.INSTANCE.play( Assets.Sounds.GOLD, 1, 1, Random.Float( 0.9f, 1.1f ) );
		updateQuickslot();
		
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
	
	@Override
	public Item random() {
		int legacyDepth = Dungeon.legacyDepth();
		quantity = Random.Int(30 + legacyDepth * 10, 60 + legacyDepth * 20);
		return this;
	}

}
