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

package pd.items.specific.journal;

import pd.atlas.items.ConsumUsefulProcessEnhanceDict;

import pd.Assets;
import pd.SPDAction;
import pd.SPDSettings;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.journal.Document;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.ui.GameLog;
import pd.utils.GLog;
import render.input.ControllerHandler;
import render.input.KeyBindings;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class Guidebook extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Guidebook.class)
			.t("name", "地牢探索指南")
			.t("hint_mobile", "你的指南有新的建议要告诉你！点击屏幕右上角闪烁的日志按钮进行阅读。")
			.t("hint_desktop", "你的指南有新的建议要告诉你！选择屏幕右上角闪烁的日志按钮(%s)进行阅读。")
			.t("hint_status", "指南")
			.t("desc", "一本静静躺在地上的地牢探索指南，不知被谁遗弃在此。看来冒险不是很适合它的旧主人！\n\n这本精致而独特的书附有魔法，会在需要的时候主动提醒冒险家去翻阅它。\n\n除去强大的功能，这本书走红的原因还有一点：它的封面上用友好而亲切的大字写着“不 要 恐 慌 ！”");
	}




	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
	}

	@Override
	public final boolean doPickUp(Hero hero, int pos) {
		Document.ADVENTURERS_GUIDE.findPage(Document.GUIDE_INTRO);
		Document.ADVENTURERS_GUIDE.findPage(Document.GUIDE_EXAMINING);
		Document.ADVENTURERS_GUIDE.findPage(Document.GUIDE_SURPRISE_ATKS);
		Document.ADVENTURERS_GUIDE.findPage(Document.GUIDE_IDING);
		Document.ADVENTURERS_GUIDE.findPage(Document.GUIDE_FOOD);
		Document.ADVENTURERS_GUIDE.findPage(Document.GUIDE_ALCHEMY);
		Document.ADVENTURERS_GUIDE.findPage(Document.GUIDE_DIEING);

		GameScene.pickUpJournal(this, pos);
		//we do this here so the pickup message appears before the tutorial text
		GameLog.wipe();
		GLog.i( Messages.capitalize(Messages.get(Hero.class, "you_now_have", name())) );
		if (SPDSettings.interfaceSize() == 0){
			GLog.p(Messages.get(GameScene.class, "tutorial_guidebook_mobile"));
		} else {
			GLog.p(Messages.get(GameScene.class, "tutorial_guidebook_desktop", KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(SPDAction.JOURNAL, ControllerHandler.isControllerConnected()))));
		}
		GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_INTRO);
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

}
