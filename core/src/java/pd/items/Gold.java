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

import pd.atlas.IconEntry;
import pd.atlas.items.SpecificCurrencyDict;

import pd.Assets;
import pd.Badges;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.hero.Hero;
import pd.effects.FloatingText;
import pd.journal.Catalog;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import render.noosa.audio.Sample;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

public class Gold extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Gold.class)
			.t("name", "金币")
			.t("desc", "一堆金币。收集金币以在随后的商店中消费。")
			.t("ac_makebag", "装袋");
	}



	public static final String AC_MAKEBAG = "MAKEBAG";

	//SPSEXPD: 地面金币堆小于该值时改用单枚金币图标（currency 图集 C3_R1）
	public static final int SMALL_PILE_AMOUNT = 75;

	{
		image = SpecificCurrencyDict.GOLD_0;
		stackable = true;
	}

	//SPSEXPD: 地面金币堆的图标——小额用单枚金币，其余用金币堆。仅由 ItemSprite 在地面堆
	// 渲染时调用（背包/商店等 UI 仍用金币堆图标，不受堆金额影响）。
	public static IconEntry pileImage( int amount ) {
		return amount < SMALL_PILE_AMOUNT
				? SpecificCurrencyDict.CURRENCY_C3_R1
				: SpecificCurrencyDict.GOLD_0;
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
