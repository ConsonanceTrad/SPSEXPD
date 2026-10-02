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

package pd.windows;

import pd.atlas.items.ConsumPotionSeedSeedDict;
import pd.atlas.items.SpecificTaskDict;

import pd.ShatteredPixelDungeon;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.scenes.SupporterScene;
import pd.sprites.ItemSprite;
import pd.ui.Icons;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;
import render.noosa.Image;
import pd.messages.InlineText;

public class WndVictoryCongrats extends Window {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndVictoryCongrats.class)
			.t("title", "获胜！")
			.t("start_text", "恭喜您征服了这座地牢！新的游戏选项已经解锁，你可以在选择英雄时查看并设置：")
			.t("challenges", "您现在可以开启_挑战_功能了！挑战是一类通过各种方式为游戏增添难度的设置选项。")
			.t("custom_seeds", "您现在可以使用_自定义种子_进行游戏了！在版本不变的情况下，使用同一个种子将总是生成相同的地牢。")
			.t("dailies", "您现在可以参与_每日挑战_了！每天都有一场新游戏，对每个人都是一样的！")
			.t("thank_you", "万分感谢您能游玩破碎的像素地牢！")
			.t("support_prompt", "_请考虑赞助这款游戏_，如果您还没有的话。忠实玩家的支持能让开发者不断对游戏进行打磨！")
			.t("support", "赞助")
			.t("close", "关闭");
	}




	public WndVictoryCongrats(){
		int width = PixelScene.landscape() ? 180 : 120;
		int height = 0;

		IconTitle title = new IconTitle( new ItemSprite(SpecificTaskDict.AMULET_0), Messages.get(this, "title"));
		title.setRect( 0, 0, width, 0 );
		add(title);

		RenderedTextBlock text = PixelScene.renderTextBlock( Messages.get(this, "start_text"), 6 );
		text.maxWidth( width );
		text.setPos( 0, title.bottom() + 4 );
		add( text );

		height = (int)text.bottom() + 6;

		Image chalImg = Icons.CHALLENGE_COLOR.get();
		chalImg.y = height;
		chalImg.x = (16-chalImg.width())/2f;
		PixelScene.align(chalImg);
		add(chalImg);

		RenderedTextBlock chalTxt = PixelScene.renderTextBlock(Messages.get(this, "challenges"), 6);
		chalTxt.maxWidth(width - 16);
		chalTxt.setPos(16, height);
		add(chalTxt);

		if (chalTxt.height() > chalImg.height()){
			chalImg.y = chalImg.y + (chalTxt.height() - chalImg.height())/2f;
			PixelScene.align(chalImg);
		}

		height += Math.max(chalImg.height(), chalTxt.height()) + 6;

		Image seedImg = new ItemSprite(ConsumPotionSeedSeedDict.SEED_SUNGRASS);
		seedImg.y = height;
		seedImg.x = (16-seedImg.width())/2f;
		PixelScene.align(seedImg);
		add(seedImg);

		RenderedTextBlock seedTxt = PixelScene.renderTextBlock(Messages.get(this, "custom_seeds"), 6);
		seedTxt.maxWidth(width - 16);
		seedTxt.setPos(16, height);
		add(seedTxt);

		if (seedTxt.height() > seedImg.height()){
			seedImg.y = seedImg.y + (seedTxt.height() - seedImg.height())/2f;
			PixelScene.align(seedImg);
		}

		height += Math.max(seedImg.height(), seedTxt.height()) + 6;

		Image dailyImg = Icons.CALENDAR.get();
		dailyImg.hardlight(0.5f, 1f, 2f);
		dailyImg.y = height;
		dailyImg.x = (16-dailyImg.width())/2f;
		PixelScene.align(dailyImg);
		add(dailyImg);

		RenderedTextBlock dailyTxt = PixelScene.renderTextBlock(Messages.get(this, "dailies"), 6);
		dailyTxt.maxWidth(width - 16);
		dailyTxt.setPos(16, height);
		add(dailyTxt);

		if (dailyTxt.height() > dailyImg.height()){
			dailyImg.y = dailyImg.y + (dailyTxt.height() - dailyImg.height())/2f;
			PixelScene.align(dailyImg);
		}

		height += Math.max(dailyImg.height(), dailyTxt.height()) + 6;

		RenderedTextBlock finalTxt = PixelScene.renderTextBlock(Messages.get(this, "thank_you") + " "  + Messages.get(this, "support_prompt"), 6);
		finalTxt.maxWidth(width);
		finalTxt.setPos(0, height);
		add(finalTxt);

		height = (int) finalTxt.bottom() + 4;

		RedButton btnSupport = new RedButton(Messages.get(this, "support")) {
			@Override
			protected void onClick() {
				ShatteredPixelDungeon.switchScene(SupporterScene.class);
			}
		};
		btnSupport.icon(Icons.GOLD.get());
		btnSupport.setRect(0, height, width / 2, 18);
		add(btnSupport);

		RedButton btnClose = new RedButton(Messages.get(this, "close")) {
			@Override
			protected void onClick() {
				hide();
			}
		};
		btnClose.icon(Icons.EXIT.get());
		btnClose.setRect(btnSupport.right() + 1, height, width / 2 - 1, 18);
		add(btnClose);

		resize(width, (int)btnClose.bottom());

	}

	@Override
	public void onBackPressed() {
		//do nothing
	}
}
