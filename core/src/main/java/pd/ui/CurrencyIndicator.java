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

package pd.ui;

import pd.Assets;
import pd.Dungeon;
import pd.SPDSettings;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.utils.GLog;
import pd.windows.WndMessage;
import pd.windows.WndOptions;
import watabou.input.PointerEvent;
import watabou.noosa.BitmapText;
import watabou.noosa.Game;
import watabou.noosa.PointerArea;
import watabou.noosa.audio.Sample;
import watabou.noosa.ui.Component;
import watabou.utils.Random;

public class CurrencyIndicator extends Component {

	private static final float TIME	= 2f;

	//SPS: 金币兑 S金比例（用户裁决 2026-09-30）：2333 金币兑换 1 S金，整除后余数留在金币。
	public static final int SC_EXCHANGE_RATE = 2333;

	private int lastGold = 0;
	private int lastEnergy = 0;
	
	private BitmapText gold;
	private BitmapText energy;

	private PointerArea goldTouch;
	
	private float goldTime;
	private float energyTime;

	public static boolean showGold = false;
	
	@Override
	protected void createChildren() {
		gold = new BitmapText( PixelScene.pixelFont);
		gold.text( Integer.toString(lastGold) );
		gold.measure();
		gold.hardlight( 0xFFFF00 );
		add( gold );

		energy = new BitmapText( PixelScene.pixelFont);
		energy.text( Integer.toString(lastEnergy) );
		energy.measure();
		energy.hardlight( 0x44CCFF );
		add( energy );

		//SPS: 金币行点击兑换 S金（2333:1）。可见时拦截点击，避免穿透到背包按钮。
		goldTouch = new PointerArea( 0, 0, 0, 0 ) {
			@Override
			protected void onClick( PointerEvent event ) {
				onGoldClick();
			}
		};
		add( goldTouch );
		
		gold.visible = energy.visible = false;
	}

	@Override
	protected void layout() {
		gold.x = x+1;
		gold.y = top() + 1;

		energy.x = x+1;
		if (gold.visible) {
			energy.y = top() + energy.height() - 1;
		} else {
			energy.y = top() + 1;
		}

		//点击区覆盖金币数字所在行；不可见时由 update 关闭拦截
		goldTouch.x = x;
		goldTouch.y = top();
		goldTouch.width = width;
		goldTouch.height = 12;
	}
	
	@Override
	public void update() {
		super.update();
		
		if (gold.visible) {
			
			goldTime -= Game.elapsed;
			if (goldTime > 0) {
				gold.alpha( goldTime > TIME / 2 ? 1f : goldTime * 2 / TIME );
			} else {
				gold.visible = false;
			}
			
		}

		if (energy.visible) {

			energyTime -= Game.elapsed;
			if (energyTime > 0) {
				energy.alpha( energyTime > TIME / 2 ? 1f : energyTime * 2 / TIME );
			} else {
				energy.visible = false;
			}

		}

		if (Dungeon.gold != lastGold) {
			
			lastGold = Dungeon.gold;
			
			gold.text( Integer.toString(lastGold) );
			gold.measure();
			
			gold.visible = true;
			goldTime = TIME;
			
			layout();
		}

		if (Dungeon.energy != lastEnergy) {
			lastEnergy = Dungeon.energy;

			energy.text( Integer.toString(lastEnergy) );
			energy.measure();

			energy.visible = true;
			energyTime = TIME;

			layout();
		}

		//SPS: 金币可兑换 S金时保持显示（可发现性），其余维持 2 秒淡出
		if (showGold || Dungeon.gold >= SC_EXCHANGE_RATE){
			if (!gold.visible){
				gold.visible = true;
				layout();
			}
			goldTime = TIME/2;
		}

		//点击拦截跟随金币显示状态：隐藏时点击穿透到背包按钮
		goldTouch.active = gold.visible;

	}

	//SPS: 点击金币 → 确认后按 2333:1 把金币兑换为全局 S金
	private void onGoldClick() {
		if (Dungeon.hero == null || !Dungeon.hero.isAlive()) return;

		int sCoin = sCoinForGold( Dungeon.gold );
		if (sCoin <= 0) {
			GameScene.show( new WndMessage( Messages.get( this, "not_enough", SC_EXCHANGE_RATE ) ) );
			return;
		}
		final int spend = sCoin * SC_EXCHANGE_RATE;
		GameScene.show( new WndOptions(
				Messages.get( this, "exchange_title" ),
				Messages.get( this, "exchange_body", spend, sCoin ),
				Messages.get( this, "exchange_confirm" ),
				Messages.get( this, "cancel" ) ) {
			@Override
			protected void onSelect( int index ) {
				if (index != 0) return;
				if (Dungeon.gold < spend) return;
				Dungeon.gold -= spend;
				SPDSettings.sCoinAdd( sCoin );
				GLog.p( Messages.get( CurrencyIndicator.class, "exchange_ok", spend, sCoin ) );
				Sample.INSTANCE.play( Assets.Sounds.GOLD, 1, 1, Random.Float( 0.9f, 1.1f ) );
			}
		} );
	}

	/** 按 2333:1 计算可兑换的 S金数量（整除，余数留在金币）。 */
	public static int sCoinForGold( int gold ) {
		return gold < SC_EXCHANGE_RATE ? 0 : gold / SC_EXCHANGE_RATE;
	}
}
