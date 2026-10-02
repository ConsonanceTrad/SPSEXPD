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

package pd.items.consum.scrolls.exotic;

import pd.Assets;
import pd.Dungeon;
import pd.actors.hero.HeroClass;
import pd.actors.hero.Talent;
import pd.effects.Speck;
import pd.effects.Transmuting;
import pd.items.consum.scrolls.InventoryScroll;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import pd.sprites.ItemIconSheet;
import pd.ui.RenderedTextBlock;
import pd.ui.TalentButton;
import pd.ui.TalentsPane;
import pd.ui.Window;
import pd.windows.IconTitle;
import pd.windows.WndOptions;
import render.noosa.audio.Sample;
import render.utils.math.Random;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;
import pd.messages.InlineText;

public class ScrollOfMetamorphosis extends ExoticScroll {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ScrollOfMetamorphosis.class)
			.t("name", "蜕变秘卷")
			.t("choose_desc", "选择一个天赋以进行蜕变")
			.t("replace_desc", "选择你希望蜕变出的天赋")
			.t("cancel_warn", "取消该行动仍然会消耗你的蜕变秘卷，你确定吗？")
			.t("metamorphose_talent", "蜕变天赋")
			.t("desc", "这张秘卷充满了嬗变的魔力，不过与一般的嬗变卷轴不同。这股魔力将作用于释放者本身而不是一个物品。秘卷的魔力将允许你蜕变一个自身的天赋，使其转化为来自其他英雄的五个同层天赋之一！\n\n这个效果只适用于英雄自身的天赋，对专精天赋与护甲天赋无效。那些你无法使用的天赋将不会出现在蜕变选项里。");
	}

	
	{
		icon = ItemIconSheet.SCROLL_METAMORPH;

		talentFactor = 2f;
	}

	protected static boolean identifiedByUse = false;
	
	@Override
	public void doRead() {
		if (!isKnown()) {
			identify();
			curItem = detach(curUser.belongings.backpack);
			identifiedByUse = true;
		} else {
			identifiedByUse = false;
		}
		GameScene.show(new WndMetamorphChoose());
	}

	public static void onMetamorph( Talent oldTalent, Talent newTalent ){
		if (curItem instanceof ScrollOfMetamorphosis) {
			((ScrollOfMetamorphosis) curItem).readAnimation();
			Sample.INSTANCE.play(Assets.Sounds.READ);
		}
		curUser.sprite.emitter().start(Speck.factory(Speck.CHANGE), 0.2f, 10);
		Transmuting.show(curUser, oldTalent, newTalent);

		if (Dungeon.hero.hasTalent(newTalent)) {
			Talent.onTalentUpgraded(Dungeon.hero, newTalent);
		}
	}

	private void confirmCancelation( Window chooseWindow, boolean byID ) {
		GameScene.show( new WndOptions(new ItemSprite(this),
				Messages.titleCase(name()),
				byID ? Messages.get(InventoryScroll.class, "warning") : Messages.get(ScrollOfMetamorphosis.class, "cancel_warn"),
				Messages.get(InventoryScroll.class, "yes"),
				Messages.get(InventoryScroll.class, "no") ) {
			@Override
			protected void onSelect( int index ) {
				switch (index) {
					case 0:
						curUser.spendAndNext( TIME_TO_READ );
						identifiedByUse = false;
						chooseWindow.hide();
						break;
					case 1:
						//do nothing
						break;
				}
			}
			public void onBackPressed() {}
		} );
	}

	public static class WndMetamorphChoose extends Window {

		public static WndMetamorphChoose INSTANCE;

		TalentsPane pane;

		public WndMetamorphChoose(){
			super();

			INSTANCE = this;

			float top = 0;

			IconTitle title = new IconTitle( curItem );
			title.color( TITLE_COLOR );
			title.setRect(0, 0, 120, 0);
			add(title);

			top = title.bottom() + 2;

			RenderedTextBlock text = PixelScene.renderTextBlock(Messages.get(ScrollOfMetamorphosis.class, "choose_desc"), 6);
			text.maxWidth(120);
			text.setPos(0, top);
			add(text);

			top = text.bottom() + 2;

			ArrayList<LinkedHashMap<Talent, Integer>> talents = new ArrayList<>();
			Talent.initClassTalents(Dungeon.hero.heroClass, talents, Dungeon.hero.metamorphedTalents);

			for (LinkedHashMap<Talent, Integer> tier : talents){
				for (Talent talent : tier.keySet()){
					tier.put(talent, Dungeon.hero.pointsInTalent(talent));
				}
			}

			pane = new TalentsPane(TalentButton.Mode.METAMORPH_CHOOSE, talents);
			add(pane);
			pane.setPos(0, top);
			pane.setSize(120, pane.content().height());
			resize((int)pane.width(), (int)pane.bottom());
			pane.setPos(0, top);
		}

		@Override
		public void hide() {
			super.hide();
			INSTANCE = null;
		}

		@Override
		public void onBackPressed() {

			if (identifiedByUse){
				((ScrollOfMetamorphosis)curItem).confirmCancelation(this, true);
			} else {
				super.onBackPressed();
			}
		}

		@Override
		public void offset(int xOffset, int yOffset) {
			super.offset(xOffset, yOffset);
			pane.setPos(pane.left(), pane.top()); //triggers layout
		}
	}

	public static class WndMetamorphReplace extends Window {

		public static WndMetamorphReplace INSTANCE;

		public Talent replacing;
		public int tier;
		LinkedHashMap<Talent, Integer> replaceOptions;

		//for window restoring
		public WndMetamorphReplace(){
			super();

			if (INSTANCE != null){
				replacing = INSTANCE.replacing;
				tier = INSTANCE.tier;
				replaceOptions = INSTANCE.replaceOptions;
				INSTANCE = this;
				setup(replacing, tier, replaceOptions);
			} else {
				hide();
			}
		}

		public WndMetamorphReplace(Talent replacing, int tier){
			super();

			if (!identifiedByUse && curItem instanceof ScrollOfMetamorphosis) {
				curItem.detach(curUser.belongings.backpack);
			}
			identifiedByUse = false;

			INSTANCE = this;

			this.replacing = replacing;
			this.tier = tier;

			LinkedHashMap<Talent, Integer> options = new LinkedHashMap<>();
			Set<Talent> curTalentsAtTier = Dungeon.hero.talents.get(tier-1).keySet();

			for (HeroClass cls : HeroClass.playableClasses()){

				ArrayList<LinkedHashMap<Talent, Integer>> clsTalents = new ArrayList<>();
				Talent.initClassTalents(cls, clsTalents);

				Set<Talent> clsTalentsAtTier = clsTalents.get(tier-1).keySet();
				boolean replacingIsInSet = false;
				for (Talent talent : clsTalentsAtTier.toArray(new Talent[0])){
					if (talent == replacing){
						replacingIsInSet = true;
						break;
					} else {
						if (curTalentsAtTier.contains(talent)){
							clsTalentsAtTier.remove(talent);
						}
					}
				}
				if (!replacingIsInSet && !clsTalentsAtTier.isEmpty()) {
					options.put(Random.element(clsTalentsAtTier), Dungeon.hero.pointsInTalent(replacing));
				}
			}

			replaceOptions = options;
			setup(replacing, tier, options);
		}

		private void setup(Talent replacing, int tier, LinkedHashMap<Talent, Integer> replaceOptions){
			float top = 0;

			IconTitle title = new IconTitle( curItem );
			title.color( TITLE_COLOR );
			title.setRect(0, 0, 120, 0);
			add(title);

			top = title.bottom() + 2;

			RenderedTextBlock text = PixelScene.renderTextBlock(Messages.get(ScrollOfMetamorphosis.class, "replace_desc"), 6);
			text.maxWidth(120);
			text.setPos(0, top);
			add(text);

			top = text.bottom() + 2;

			TalentsPane.TalentTierPane optionsPane = new TalentsPane.TalentTierPane(replaceOptions, tier, TalentButton.Mode.METAMORPH_REPLACE);
			add(optionsPane);
			optionsPane.title.text(" ");
			optionsPane.setPos(0, top);
			optionsPane.setSize(120, optionsPane.height());
			resize((int)optionsPane.width(), (int)optionsPane.bottom());

			resize(120, (int)optionsPane.bottom());
		}

		@Override
		public void hide() {
			super.hide();
			if (INSTANCE == this) {
				INSTANCE = null;
			}
		}

		@Override
		public void onBackPressed() {
			if (curItem instanceof ScrollOfMetamorphosis) {
				((ScrollOfMetamorphosis) curItem).confirmCancelation(this, false);
			} else {
				super.onBackPressed();
			}
		}
	}
}
