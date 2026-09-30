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

import pd.Dungeon;
import pd.scenes.PixelScene;
import render.noosa.BitmapText;
import render.noosa.Game;
import render.noosa.ui.Component;

public class CurrencyIndicator extends Component {

	private static final float TIME	= 2f;

	//SPS: 金币兑 S金比例（用户裁决 2026-09-30）：2333 金币兑换 1 S金，整除后余数留在金币。
	public static final int SC_EXCHANGE_RATE = 2333;

	private int lastGold = 0;
	private int lastEnergy = 0;
	
	private BitmapText gold;
	private BitmapText energy;

	
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


	}


	/** 按 2333:1 计算可兑换的 S金数量（整除，余数留在金币）。 */
	public static int sCoinForGold( int gold ) {
		return gold < SC_EXCHANGE_RATE ? 0 : gold / SC_EXCHANGE_RATE;
	}
}
