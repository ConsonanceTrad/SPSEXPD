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

import pd.Dungeon;
import pd.SPDAction;
import pd.ShatteredPixelDungeon;
import pd.Statistics;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.sprites.HeroSprite;
import pd.ui.BuffIcon;
import pd.ui.BuffIndicator;
import pd.ui.IconButton;
import pd.ui.Icons;
import pd.ui.RenderedTextBlock;
import pd.ui.ScrollPane;
import pd.ui.StatusPane;
import pd.ui.TalentButton;
import pd.ui.TalentsPane;
import pd.ui.PerkSlot;
import pd.ui.Window;
import pd.utils.DungeonSeed;
import render.input.KeyBindings;
import render.input.KeyEvent;
import render.noosa.Gizmo;
import render.noosa.Group;
import render.noosa.Image;
import render.noosa.ui.Component;

import java.util.ArrayList;
import java.util.Locale;
import pd.messages.InlineText;

public class WndHero extends WndTabbed {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndHero.class)
			.t("stats", "属性")
			.t("talents", "特质")
			.t("buffs", "状态")
			.t("$statstab.title", "%1$d级%2$s")
			.t("$statstab.exp", "经验")
			.t("$statstab.satiation", "饱食度")
			.t("$statstab.accuracy", "命中")
			.t("$statstab.evasion", "闪避")
			.t("$statstab.spell_power", "法术强度")
			.t("$statstab.magic_resist", "魔法抗性")
			.t("$statstab.critical_chance", "暴击几率")
			.t("$statstab.perks", "特质")
			.t("$statstab.str", "力量")
			.t("$statstab.health", "生命")
			.t("$statstab.gold", "金币收集数")
			.t("$statstab.depth", "最高层数")
			.t("$statstab.dungeon_seed", "地牢种子")
			.t("$statstab.custom_seed", "_自定义种子_")
			.t("$statstab.daily_for", "_日常挑战于_")
			.t("$statstab.replay_for", "_重玩于_");
	}



	
	private static final int WIDTH		= 120;
	//SPSXPD: 属性页行数增加（饱食度/命中/闪避/法术强度/魔法抗性/暴击/特质），加高窗口
	private static final int HEIGHT		= 210;
	
	private StatsTab stats;
	private TalentsTab talents;
	private BuffsTab buffs;

	public static int lastIdx = 0;

	public WndHero() {
		
		super();
		
		resize( WIDTH, HEIGHT );
		
		stats = new StatsTab();
		add( stats );

		talents = new TalentsTab();
		add(talents);
		talents.setRect(0, 0, WIDTH, HEIGHT);

		buffs = new BuffsTab();
		add( buffs );
		buffs.setRect(0, 0, WIDTH, HEIGHT);
		buffs.setupList();
		
		add( new IconTab( Icons.get(Icons.RANKINGS) ) {
			protected void select( boolean value ) {
				super.select( value );
				if (selected) {
					lastIdx = 0;
					if (!stats.visible) {
						stats.initialize();
					}
				}
				stats.visible = stats.active = selected;
			}
		} );
		add( new IconTab( new render.noosa.Image( pd.Assets.Interfaces.SPECIFIC_POINT ) ) {
			protected void select( boolean value ) {
				super.select( value );
				if (selected) lastIdx = 1;
				if (selected) StatusPane.talentBlink = 0;
				talents.visible = talents.active = selected;
			}
		} );
		add( new IconTab( Icons.get(Icons.BUFFS) ) {
			protected void select( boolean value ) {
				super.select( value );
				if (selected) lastIdx = 2;
				buffs.visible = buffs.active = selected;
			}
		} );

		layoutTabs();

		talents.setRect(0, 0, WIDTH, HEIGHT);
		talents.pane.scrollTo(0, talents.pane.content().height() - talents.pane.height());
		talents.layout();

		select( lastIdx );
	}

	@Override
	public boolean onSignal(KeyEvent event) {
		if (event.pressed && KeyBindings.getActionForKey( event ) == SPDAction.HERO_INFO) {
			onBackPressed();
			return true;
		} else {
			return super.onSignal(event);
		}
	}

	@Override
	public void offset(int xOffset, int yOffset) {
		super.offset(xOffset, yOffset);
		talents.layout();
		buffs.layout();
	}

	private class StatsTab extends Group {
		
		private static final int GAP = 5;
		
		private float pos;
		
		public StatsTab() {
			initialize();
		}

		public void initialize(){

			for (Gizmo g : members){
				if (g != null) g.destroy();
			}
			clear();
			
			Hero hero = Dungeon.hero;

			IconTitle title = new IconTitle();
			title.icon( HeroSprite.avatar(hero) );
			if (hero.name().equals(hero.className()))
				title.label( Messages.get(this, "title", hero.lvl, hero.className() ).toUpperCase( Locale.ENGLISH ) );
			else
				title.label((hero.name() + "\n" + Messages.get(this, "title", hero.lvl, hero.className())).toUpperCase(Locale.ENGLISH));
			title.color(Window.TITLE_COLOR);
			title.setRect( 0, 0, WIDTH-16, 0 );
			add(title);

			IconButton infoButton = new IconButton(Icons.get(Icons.INFO)){
				@Override
				protected void onClick() {
					super.onClick();
					if (ShatteredPixelDungeon.scene() instanceof GameScene){
						GameScene.show(new WndHeroInfo(hero.heroClass));
					} else {
						ShatteredPixelDungeon.scene().addToFront(new WndHeroInfo(hero.heroClass));
					}
				}

				@Override
				protected String hoverText() {
					return Messages.titleCase(Messages.get(WndKeyBindings.class, "hero_info"));
				}

			};
			infoButton.setRect(title.right(), 0, 16, 16);
			add(infoButton);

			pos = title.bottom() + 2*GAP;

			int strBonus = hero.STR() - hero.STR;
			if (strBonus > 0)           statSlot( Messages.get(this, "str"), hero.STR + " + " + strBonus );
			else if (strBonus < 0)      statSlot( Messages.get(this, "str"), hero.STR + " - " + -strBonus );
			else                        statSlot( Messages.get(this, "str"), hero.STR() );
			if (hero.shielding() > 0)   statSlot( Messages.get(this, "health"), hero.HP + "+" + hero.shielding() + "/" + hero.HT );
			else                        statSlot( Messages.get(this, "health"), (hero.HP) + "/" + hero.HT );
			statSlot( Messages.get(this, "exp"), hero.exp + "/" + hero.maxExp() );

			//SPSXPD: 饱食度（当前饥饿值 / 上限；上限受「坚忍肠胃」特质影响）
			pd.actors.buffs.Hunger hungerBuff = hero.buff(pd.actors.buffs.Hunger.class);
			int hunger = hungerBuff == null ? 0 : hungerBuff.hunger();
			int hungerCap = (int)(pd.actors.buffs.Hunger.STARVING
					+ pd.actors.hero.perks.HardenedStomach.capBonus(hero));
			statSlot( Messages.get(this, "satiation"), hunger + "/" + hungerCap );

			//SPSXPD: 命中 / 闪避 / 法术强度 / 魔法抗性
			statSlot( Messages.get(this, "accuracy"), hero.attackSkill(hero) );
			statSlot( Messages.get(this, "evasion"), hero.defenseSkill(hero) );
			statSlot( Messages.get(this, "spell_power"), hero.magicSkill() );
			statSlot( Messages.get(this, "magic_resist"),
					Math.round(hero.magicalResistance() * 100) + "%" );

			//SPSXPD: 特质体系在属性页的显示 —— 暴击几率与特质情况
			statSlot( Messages.get(this, "critical_chance"),
					Math.round(pd.actors.hero.Critical.chance(hero) * 100) + "%" );
			String perkText = String.valueOf(hero.heroPerk == null ? 0 : hero.heroPerk.getPerks().size());
			if (hero.reservedPerks > 0) {
				perkText += " +" + hero.reservedPerks;
			}
			statSlot( Messages.get(this, "perks"), perkText );

			pos += GAP;

			statSlot( Messages.get(this, "gold"), Statistics.goldCollected );
			if (Dungeon.daily){
				if (!Dungeon.dailyReplay) {
					statSlot(Messages.get(this, "daily_for"), "_" + Dungeon.customSeedText + "_");
				} else {
					statSlot(Messages.get(this, "replay_for"), "_" + Dungeon.customSeedText + "_");
				}
			} else if (!Dungeon.customSeedText.isEmpty()){
				statSlot( Messages.get(this, "custom_seed"), "_" + Dungeon.customSeedText + "_" );
			} else {
				statSlot( Messages.get(this, "dungeon_seed"), DungeonSeed.convertToCode(Dungeon.seed) );
			}

			pos += GAP;
		}

		private void statSlot( String label, String value ) {

			int size = 8;
			RenderedTextBlock txt;
			do {
				txt = PixelScene.renderTextBlock( label, size );
				size--;
			} while (txt.width() >= WIDTH * 0.55f);
			txt.setPos(0, pos + (6 - txt.height())/2);
			PixelScene.align(txt);
			add( txt );

			size = 8;
			do {
				txt = PixelScene.renderTextBlock( value, size );
				size--;
			} while (txt.width() >= WIDTH * 0.45f);
			txt.setPos(WIDTH * 0.55f, pos + (6 - txt.height())/2);
			PixelScene.align(txt);
			add( txt );
			
			pos += GAP + txt.height();
		}
		
		private void statSlot( String label, int value ) {
			statSlot( label, Integer.toString( value ) );
		}
		
		public float height() {
			return pos;
		}
	}

	public class TalentsTab extends Component {

		private static final int GAP = 2;
		private static final int COLS = 5;

		private ScrollPane pane;
		private ArrayList<PerkSlot> slots;

		@Override
		protected void createChildren() {
			super.createChildren();
			//SPSXPD: 特质页 —— 展示已获得的特质（取代原天赋页）
			//注意：createChildren() 会在构造期被 Component 调用，
			//所以字段必须在这里初始化，不能在声明处初始化（否则 NPE）
			slots = new ArrayList<>();
			pane = new ScrollPane(new Component());
			add(pane);

			if (pd.Dungeon.hero != null && pd.Dungeon.hero.heroPerk != null) {
				for (pd.actors.hero.perks.Perk perk : pd.Dungeon.hero.heroPerk.getPerks()) {
					PerkSlot slot = new PerkSlot(perk);
					slots.add(slot);
					pane.content().add(slot);
				}
			}
		}

		@Override
		protected void layout() {
			super.layout();
			if (pane == null || slots == null) return;
			pane.setRect(x, y, width, height);

			for (int i = 0; i < slots.size(); i++) {
				int r = i / COLS;
				int c = i % COLS;
				slots.get(i).setRect(
						GAP + c * (PerkSlot.BTN + GAP),
						GAP + r * (PerkSlot.BTN + GAP),
						PerkSlot.BTN, PerkSlot.BTN);
			}
			int rows = Math.max(1, (slots.size() + COLS - 1) / COLS);
			pane.content().setSize(width, Math.max(height, GAP + rows * (PerkSlot.BTN + GAP)));
		}

	}
	
	private class BuffsTab extends Component {
		
		private static final int GAP = 2;
		
		private float pos;
		private ScrollPane buffList;
		private ArrayList<BuffSlot> slots = new ArrayList<>();

		@Override
		protected void createChildren() {

			super.createChildren();

			buffList = new ScrollPane( new Component() ){
				@Override
				public void onClick( float x, float y ) {
					int size = slots.size();
					for (int i=0; i < size; i++) {
						if (slots.get( i ).onClick( x, y )) {
							break;
						}
					}
				}
			};
			add(buffList);
		}
		
		@Override
		protected void layout() {
			super.layout();
			buffList.setRect(0, 0, width, height);
		}
		
		private void setupList() {
			Component content = buffList.content();
			for (Buff buff : Dungeon.hero.buffs()) {
				if (buff.icon() != BuffIndicator.NONE) {
					BuffSlot slot = new BuffSlot(buff);
					slot.setRect(0, pos, WIDTH, slot.icon.height());
					content.add(slot);
					slots.add(slot);
					pos += GAP + slot.height();
				}
			}
			content.setSize(buffList.width(), pos);
			buffList.setSize(buffList.width(), buffList.height());
		}

		private class BuffSlot extends Component {

			private Buff buff;

			Image icon;
			RenderedTextBlock txt;

			public BuffSlot( Buff buff ){
				super();
				this.buff = buff;

				icon = new BuffIcon(buff, true);
				icon.y = this.y;
				add( icon );

				txt = PixelScene.renderTextBlock( Messages.titleCase(buff.name()), 8 );
				txt.setPos(
						icon.width + GAP,
						this.y + (icon.height - txt.height()) / 2
				);
				PixelScene.align(txt);
				add( txt );

			}

			@Override
			protected void layout() {
				super.layout();
				icon.y = this.y;
				txt.maxWidth((int)(width - icon.width()));
				txt.setPos(
						icon.width + GAP,
						this.y + (icon.height - txt.height()) / 2
				);
				PixelScene.align(txt);
			}
			
			protected boolean onClick ( float x, float y ) {
				if (inside( x, y )) {
					GameScene.show(new WndInfoBuff(buff));
					return true;
				} else {
					return false;
				}
			}
		}
	}
}
