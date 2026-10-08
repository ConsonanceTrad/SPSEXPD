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

import pd.Assets;
import pd.Dungeon;
import pd.actors.hero.HeroClass;
import pd.actors.hero.Talent;
import pd.items.Item;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.rings.Ring;
import pd.items.consum.scrolls.ScrollOfUpgrade;
import pd.items.consum.spells.MagicalInfusion;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.melee.Crossbow;
import pd.items.equipment.weapon.melee.Greatshield;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.items.equipment.weapon.melee.RoundShield;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.items.equipment.weapon.missiles.Tomahawk;
import pd.messages.Languages;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.sprites.CharSprite;
import pd.sprites.ItemSprite;
import pd.ui.Icons;
import pd.ui.ItemSlot;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;
import render.noosa.BitmapText;
import render.noosa.ColorBlock;
import render.noosa.audio.Sample;
import render.utils.serialize.Reflection;
import pd.messages.InlineText;

public class WndUpgrade extends Window {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndUpgrade.class)
			.t("title", "升级一件物品")
			.t("desc", "升级这件物品会永久提升其如下属性：")
			.t("remaining", "你还剩有_%d个_升级用物品。")
			.t("unided", "你并不知道这件物品的属性，此处显示的是其未被升级的状态。")
			.t("unknown_ring", "你并不知道这枚戒指的效果，因此无从得知升级对其有何作用。")
			.t("cursed", "升级这件物品还有33%的概率净化其诅咒。")
			.t("cursed_weaken", "升级这件物品不仅会弱化其诅咒，还有33%的概率完全净化其诅咒。")
			.t("curse_infusion", "如果诅咒被净化，这件物品将会不再带有诅咒菱晶的升级效果！")
			.t("enchant", "升级这件物品还有%d%%的概率消除其附魔！")
			.t("glyph", "升级这件物品还有%d%%的概率消除其刻印！")
			.t("harden", "升级这件物品还有%d%%的概率消除其硬化！")
			.t("resin", "这根法杖已被奥术树脂强化，一般的升级效果将会覆盖树脂的升级效果！")
			.t("thrown_dust", "这组武器不在你背包的部分将化为尘土。")
			.t("damage", "伤害")
			.t("dart_damage", "飞镖伤害")
			.t("bleeding", "流血")
			.t("blocking", "防御")
			.t("weight", "重量")
			.t("durability", "耐久度")
			.t("quantity", "数量")
			.t("zap_damage", "魔法伤害")
			.t("corrosion_damage", "酸蚀伤害")
			.t("ward_damage", "哨卫伤害")
			.t("charges", "充能上限")
			.t("ring_boost", "戒指加成")
			.t("upgrade", "升级")
			.t("upgrade_all", "连续升级")
			.t("back", "返回");
	}




	private static final int WIDTH = 120;

	private static final float COL_1 = WIDTH/4f;
	private static final float COL_2 = 5*WIDTH/8f;
	private static final float COL_3 = 7*WIDTH/8f;

	private static final int GAP	= 2;
	private static final int ITEMSLOT_SIZE = 18;

	private Item upgrader;
	private boolean force;

	private RedButton btnUpgrade;
	private RedButton btnUpgradeAll;
	private RedButton btnCancel;

	/** SPSEXPD: 连续升级的防御性上限，避免异常情况下死循环。 */
	private static final int MAX_BULK_UPGRADES = 100;

	public WndUpgrade( Item upgrader, Item toUpgrade, boolean force){

		this.upgrader = upgrader;
		this.force = force;

		IconTitle title = new IconTitle( new ItemSprite(upgrader), Messages.get(this, "title") );

		title.setRect(0, 0, WIDTH, 0);
		add(title);

		int quantity = upgrader.quantity();
		Item moreUpgradeItem = Dungeon.hero.belongings.getItem(upgrader.getClass());

		if (moreUpgradeItem != null && moreUpgradeItem != upgrader){
			quantity += moreUpgradeItem.quantity();
		}

		String mainText = Messages.get(this, "desc");
		if (quantity > 1){
			mainText += "\n" + Messages.get(this, "remaining", quantity);
		}

		RenderedTextBlock message = PixelScene.renderTextBlock( 6 );
		message.text( mainText, WIDTH);
		message.setPos(0, title.bottom()+GAP);
		add(message);

		// *** Computing current and next level to display ***

		int levelFrom = toUpgrade.isIdentified() ? toUpgrade.level() : 0;
		int levelTo = levelFrom + 1;

		if (toUpgrade instanceof Wand && ((Wand) toUpgrade).resinBonus > 0){
			levelTo--;
		}

		boolean curseInfused = (toUpgrade instanceof Weapon && ((Weapon) toUpgrade).curseInfusionBonus)
				|| (toUpgrade instanceof Armor && ((Armor) toUpgrade).curseInfusionBonus)
				|| (toUpgrade instanceof Wand && ((Wand) toUpgrade).curseInfusionBonus);

		if (curseInfused){
			if (toUpgrade.trueLevel()/6 < (toUpgrade.trueLevel()+1)/6){
				//new level bracket for curse infusion bonus
				levelTo++;
			}
		}

		// *** Sprites, showing item at current level and with +1 ***

		ColorBlock bg1 = new ColorBlock(ITEMSLOT_SIZE, ITEMSLOT_SIZE, 0x9953564D);
		bg1.x = COL_2 - ITEMSLOT_SIZE/2f;
		bg1.y = message.bottom() + 2*GAP;
		add(bg1);

		ColorBlock bg2 = new ColorBlock(ITEMSLOT_SIZE, ITEMSLOT_SIZE, 0x9953564D);
		bg2.x = COL_3 - ITEMSLOT_SIZE/2f;
		bg2.y = message.bottom() + 2*GAP;
		add(bg2);

		if (!toUpgrade.isIdentified()){
			if (!toUpgrade.cursed && toUpgrade.cursedKnown){
				bg1.hardlight(1f, 1, 2f);
				bg2.hardlight(1f, 1, 2f);
			} else {
				bg1.hardlight(2f, 1, 2f);
				bg2.hardlight(2f, 1, 2f);
			}
		} else if (toUpgrade.cursed && toUpgrade.cursedKnown){
			bg1.hardlight(2f, 0.5f, 1f);
			bg2.hardlight(2f, 0.5f, 1f);
		}

		ItemSprite i1 = new ItemSprite();
		add(i1);
		i1.view(toUpgrade);
		i1.x = COL_2 - i1.width()/2f;
		i1.y = bg1.y + (ITEMSLOT_SIZE-i1.height())/2f;
		PixelScene.align(i1);
		add(i1);

		ItemSprite i2 = new ItemSprite();
		add(i2);
		i2.view(toUpgrade);
		i2.x = COL_3 - i2.width()/2f;
		i2.y = i1.y;
		PixelScene.align(i2);
		add(i2);

		BitmapText t1 = new BitmapText(PixelScene.pixelFont);
		BitmapText t2 = new BitmapText(PixelScene.pixelFont);
		if (toUpgrade.isIdentified()){
			if (levelFrom > 0){
				t1.text("+" + levelFrom);
			} else {
				t1.text("");
			}
			t1.hardlight(ItemSlot.UPGRADED);
			t2.text("+" + levelTo);
			t2.hardlight(ItemSlot.UPGRADED);

			if (curseInfused){
				t1.hardlight(ItemSlot.CURSE_INFUSED);
				t2.hardlight(ItemSlot.CURSE_INFUSED);
			}

		} else {
			t1.text("?");
			t2.text("+1?");
			t2.hardlight(ItemSlot.UPGRADED);
		}
		t1.measure();
		t1.x = COL_2 + ITEMSLOT_SIZE/2f - t1.width();
		t1.y = bg1.y + ITEMSLOT_SIZE - t1.baseLine() - 1;
		add(t1);

		t2.measure();
		t2.x = COL_3 + ITEMSLOT_SIZE/2f - t2.width();
		t2.y = bg2.y + ITEMSLOT_SIZE - t2.baseLine() - 1;
		add(t2);

		float bottom = i1.y + ITEMSLOT_SIZE;

		// *** Various lines for stats, highlighting differences between current level and +1 ***

		//physical damage
		if (toUpgrade instanceof Weapon){
			Weapon.Augment aug = ((Weapon) toUpgrade).augment;
			bottom = fillFields(Messages.get(this, "damage"),
					aug.damageFactor(((Weapon) toUpgrade).min(levelFrom)) + "-" + aug.damageFactor(((Weapon) toUpgrade).max(levelFrom)),
					aug.damageFactor(((Weapon) toUpgrade).min(levelTo)) + "-" + aug.damageFactor(((Weapon) toUpgrade).max(levelTo)),
					bottom);
		}

		if (toUpgrade instanceof Crossbow){
			bottom = fillFields(Messages.get(this, "dart_damage"),
					((Crossbow) toUpgrade).dartMin(levelFrom) + "-" + ((Crossbow) toUpgrade).dartMax(levelFrom),
					((Crossbow) toUpgrade).dartMin(levelTo) + "-" + ((Crossbow) toUpgrade).dartMax(levelTo),
					bottom);
		}

		//bleeding (tomahawk)
		if (toUpgrade instanceof Tomahawk){
			bottom = fillFields(Messages.get(this, "bleeding"),
					Math.round(((Tomahawk) toUpgrade).minBleed(levelFrom)) + "-" + Math.round(((Tomahawk) toUpgrade).maxBleed(levelFrom)),
					Math.round(((Tomahawk) toUpgrade).minBleed(levelTo)) + "-" + Math.round(((Tomahawk) toUpgrade).maxBleed(levelTo)),
					bottom);
		}

		if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.DUELIST
				&& toUpgrade instanceof MeleeWeapon && ((MeleeWeapon) toUpgrade).upgradeAbilityStat(levelFrom) != null){
			bottom = fillFields(Messages.get(toUpgrade, "upgrade_ability_stat_name"),
					((MeleeWeapon) toUpgrade).upgradeAbilityStat(levelFrom),
					((MeleeWeapon) toUpgrade).upgradeAbilityStat(levelTo),
					bottom);
		}

		//blocking (armor and shields)
		if (toUpgrade instanceof Armor){
			Armor.Augment aug = ((Armor) toUpgrade).augment;
			bottom = fillFields(Messages.get(this, "blocking"),
					((Armor) toUpgrade).DRMin(levelFrom) + "-" + (((Armor) toUpgrade).DRMax(levelFrom)),
					((Armor) toUpgrade).DRMin(levelTo) + "-" +  (((Armor) toUpgrade).DRMax(levelTo)),
					bottom);
		} else if (toUpgrade instanceof RoundShield){
			bottom = fillFields(Messages.get(this, "blocking"),
					0 + "-" + ((RoundShield) toUpgrade).DRMax(levelFrom),
					0 + "-" + ((RoundShield) toUpgrade).DRMax(levelTo),
					bottom);
		} else if (toUpgrade instanceof Greatshield){
			bottom = fillFields(Messages.get(this, "blocking"),
					0 + "-" + ((Greatshield) toUpgrade).DRMax(levelFrom),
					0 + "-" + ((Greatshield) toUpgrade).DRMax(levelTo),
					bottom);
		}

		//weight (i.e. strength requirement)
		if (toUpgrade instanceof Weapon){
			bottom = fillFields(Messages.get(this, "weight"),
					Integer.toString((((Weapon) toUpgrade).STRReq(levelFrom))),
					Integer.toString((((Weapon) toUpgrade).STRReq(levelTo))),
					bottom);
		} else if (toUpgrade instanceof Armor) {
			bottom = fillFields(Messages.get(this, "weight"),
					Integer.toString((((Armor) toUpgrade).STRReq(levelFrom))),
					Integer.toString((((Armor) toUpgrade).STRReq(levelTo))),
					bottom);
		}

		//durability
		if (toUpgrade instanceof MissileWeapon){
			int uses1, uses2;
			uses1 = (int) Math.ceil(100f / ((MissileWeapon) toUpgrade).durabilityPerUse(levelFrom));
			uses2 = (int) Math.ceil(100f / ((MissileWeapon) toUpgrade).durabilityPerUse(levelTo));
			bottom = fillFields(Messages.get(this, "durability"),
					uses1 > 100 ? "∞" : Integer.toString(uses1),
					uses2 > 100 ? "∞" : Integer.toString(uses2),
					bottom);

			bottom = fillFields(Messages.get(this, "quantity"),
					Integer.toString(toUpgrade.quantity()),
					Integer.toString(((MissileWeapon) toUpgrade).defaultQuantity()),
					bottom);

		}

		//we use a separate reference for wand properties so that mage's staff can include them
		Item wand = toUpgrade;
		if (toUpgrade instanceof MagesStaff && ((MagesStaff) toUpgrade).wandClass() != null){
			wand = Reflection.newInstance(((MagesStaff) toUpgrade).wandClass());
		}

		//Various wand stats (varies by wand)
		if (wand instanceof Wand){
			if (((Wand) wand).upgradeStat1(levelFrom) != null){
				bottom = fillFields(Messages.get(wand, "upgrade_stat_name_1"),
						((Wand) wand).upgradeStat1(levelFrom),
						((Wand) wand).upgradeStat1(levelTo),
						bottom);
			}
			if (((Wand) wand).upgradeStat2(levelFrom) != null){
				bottom = fillFields(Messages.get(wand, "upgrade_stat_name_2"),
						((Wand) wand).upgradeStat2(levelFrom),
						((Wand) wand).upgradeStat2(levelTo),
						bottom);
			}
			if (((Wand) wand).upgradeStat3(levelFrom) != null){
				bottom = fillFields(Messages.get(wand, "upgrade_stat_name_3"),
						((Wand) wand).upgradeStat3(levelFrom),
						((Wand) wand).upgradeStat3(levelTo),
						bottom);
			}
		}

		//max charges
		if (wand instanceof Wand){
			//SPSEXPD: 预览改用 Wand 的充能公式（含每级充能量、各自上限与能量流失挑战），
			//不再写死「每级+1、上限10」
			int staffBonus = toUpgrade instanceof MagesStaff ? 1 : 0;
			bottom = fillFields(Messages.get(this, "charges"),
					Integer.toString(((Wand) wand).maxChargesAtLevel(levelFrom, staffBonus)),
					Integer.toString(((Wand) wand).maxChargesAtLevel(levelTo, staffBonus)),
					bottom);
		}

		//Various ring stats (varies by ring)
		if (toUpgrade instanceof Ring){
			if (((Ring) toUpgrade).isKnown()) {
				if (((Ring) toUpgrade).upgradeStat1(levelFrom) != null) {
					bottom = fillFields(Messages.get(toUpgrade, "upgrade_stat_name_1"),
							((Ring) toUpgrade).upgradeStat1(levelFrom),
							((Ring) toUpgrade).upgradeStat1(levelTo),
							bottom);
				}
				if (((Ring) toUpgrade).upgradeStat2(levelFrom) != null) {
					bottom = fillFields(Messages.get(toUpgrade, "upgrade_stat_name_2"),
							((Ring) toUpgrade).upgradeStat2(levelFrom),
							((Ring) toUpgrade).upgradeStat2(levelTo),
							bottom);
				}
				if (((Ring) toUpgrade).upgradeStat3(levelFrom) != null) {
					bottom = fillFields(Messages.get(toUpgrade, "upgrade_stat_name_3"),
							((Ring) toUpgrade).upgradeStat3(levelFrom),
							((Ring) toUpgrade).upgradeStat3(levelTo),
							bottom);
				}
			}
		}

		//visual separators for each column
		ColorBlock sep = new ColorBlock(1, 1, 0xFF222222);
		sep.size(1, bottom - message.bottom());
		sep.x = WIDTH/2f;
		sep.y = message.bottom() + GAP;
		add(sep);

		sep = new ColorBlock(1, 1, 0xFF222222);
		sep.size(1, bottom - message.bottom());
		sep.x = 3*WIDTH/4f;
		sep.y = message.bottom() + GAP;
		add(sep);

		// *** Various extra info texts that can appear underneath stats ***

		//warning relating to identification
		if (!toUpgrade.isIdentified()){
			if (toUpgrade instanceof Ring && !((Ring) toUpgrade).isKnown()){
				bottom = addMessage(Messages.get(this, "unknown_ring"), CharSprite.WARNING, bottom);
			} else {
				bottom = addMessage(Messages.get(this, "unided"), CharSprite.WARNING, bottom);
			}
		}

		// various messages relating to enchantments and curses
		if (!(upgrader instanceof MagicalInfusion)) {

			if ((toUpgrade instanceof Weapon && ((Weapon) toUpgrade).hasGoodEnchant())
					|| (toUpgrade instanceof Armor && ((Armor) toUpgrade).hasGoodGlyph())) {
				int lossChance;
				if ((toUpgrade instanceof Weapon && ((Weapon) toUpgrade).enchantHardened)
						|| (toUpgrade instanceof Armor && ((Armor) toUpgrade).glyphHardened)) {
					lossChance = Math.min(100, 10 * (int) Math.pow(2, levelFrom - 6));
				} else {
					lossChance = Math.min(100, 10 * (int) Math.pow(2, levelFrom - 4));
					if (Dungeon.hero != null && Dungeon.hero.heroClass != HeroClass.WARRIOR && Dungeon.hero.hasTalent(Talent.RUNIC_TRANSFERENCE)){
						if (levelFrom < 5+Dungeon.hero.pointsInTalent(Talent.RUNIC_TRANSFERENCE)){
							lossChance = 0;
						}
					}
				}

				if (lossChance >= 10) {
					String warn;
					if (toUpgrade instanceof Weapon) {
						if (((Weapon) toUpgrade).enchantHardened) {
							warn = Messages.get(this, "harden", lossChance);
						} else {
							warn = Messages.get(this, "enchant", lossChance);
						}
					} else {
						if (((Armor) toUpgrade).glyphHardened) {
							warn = Messages.get(this, "harden", lossChance);
						} else {
							warn = Messages.get(this, "glyph", lossChance);
						}
					}
					bottom = addMessage(warn, CharSprite.WARNING, bottom);
				}
			}

			if ((toUpgrade.cursed
					|| (toUpgrade instanceof Weapon && ((Weapon) toUpgrade).hasCurseEnchant())
					|| (toUpgrade instanceof Armor && ((Armor) toUpgrade).hasCurseGlyph()))
					&& toUpgrade.cursedKnown) {

				if (toUpgrade.cursed && (toUpgrade instanceof MeleeWeapon && ((Weapon) toUpgrade).hasCurseEnchant())
						|| (toUpgrade instanceof Armor && ((Armor) toUpgrade).hasCurseGlyph())){
					bottom = addMessage(Messages.get(this, "cursed_weaken"), CharSprite.POSITIVE, bottom);
				} else {
					bottom = addMessage(Messages.get(this, "cursed"), CharSprite.POSITIVE, bottom);
				}

				if (curseInfused) {
					bottom = addMessage(Messages.get(this, "curse_infusion"), CharSprite.WARNING, bottom);
				}
			}
		}

		//warning relating to arcane resin
		if (toUpgrade instanceof Wand && ((Wand) toUpgrade).resinBonus > 0){
			bottom = addMessage(Messages.get(this, "resin"), CharSprite.WARNING, bottom);
		}

		if (toUpgrade instanceof MissileWeapon && ((MissileWeapon) toUpgrade).extraThrownLeft){
			bottom = addMessage(Messages.get(this, "thrown_dust"), CharSprite.WARNING, bottom);
		}

		// *** Buttons for confirming/cancelling ***

		//SPSEXPD: 还剩多于一个升级用物品时，额外提供「连续升级」——一次用光全部同类
		boolean bulkAvailable = quantity > 1;

		btnUpgrade = new RedButton(Messages.get(this, "upgrade")){
			@Override
			protected void onClick() {
				super.onClick();

				Item upgraded = upgradeOnce(upgrader, toUpgrade);

				if (!force) upgrader.detach(Dungeon.hero.belongings.backpack);
				Item moreUpgradeItem = Dungeon.hero.belongings.getItem(upgrader.getClass());

				hide();

				if (moreUpgradeItem != null && toUpgrade.isUpgradable()){
					GameScene.show(new WndUpgrade(moreUpgradeItem, upgraded, false));
				}
			}
		};
		btnUpgrade.setRect(0, bottom+2*GAP, WIDTH/2f, 16);
		btnUpgrade.icon(new ItemSprite(upgrader));
		btnUpgrade.enable(Dungeon.hero.ready);
		add(btnUpgrade);

		btnCancel = new RedButton(Messages.get(this, "back")){
			@Override
			protected void onClick() {
				super.onClick();
				hide();
				if (upgrader instanceof ScrollOfUpgrade) {
					((ScrollOfUpgrade) upgrader).reShowSelector(force);
				} else if (upgrader instanceof MagicalInfusion){
					((MagicalInfusion)upgrader).reShowSelector();
				}
			}

		};
		btnCancel.icon(Icons.EXIT.get());
		add(btnCancel);

		if (bulkAvailable){
			btnUpgradeAll = new RedButton(Messages.get(this, "upgrade_all")){
				@Override
				protected void onClick() {
					super.onClick();

					hide();

					Item target = toUpgrade;
					Item scroll = upgrader;
					boolean first = true;
					int performed = 0;

					while (scroll != null && target != null
							&& target.isUpgradable()
							&& performed++ < MAX_BULK_UPGRADES){

						target = upgradeOnce(scroll, target);

						//SPSEXPD: 未鉴定使用时第一张已在 doRead() 里被消耗，不能再 detach 一次
						if (!first || !force){
							scroll.detach(Dungeon.hero.belongings.backpack);
						}
						first = false;

						scroll = Dungeon.hero.belongings.getItem(upgrader.getClass());
					}
				}
			};
			btnUpgradeAll.setRect(btnUpgrade.right()+1, bottom+2*GAP, WIDTH/2f, 16);
			btnUpgradeAll.icon(new ItemSprite(upgrader));
			btnUpgradeAll.enable(Dungeon.hero.ready);
			add(btnUpgradeAll);

			//连续升级占了右上位置，「返回」另起一行
			btnCancel.setRect(0, btnUpgrade.bottom()+1, WIDTH, 16);
		} else {
			btnCancel.setRect(btnUpgrade.right()+1, bottom+2*GAP, WIDTH/2f, 16);
		}

		bottom = (int)btnCancel.bottom();

		resize(WIDTH, (int)bottom);

	}

	/** SPSEXPD: 用一张升级用物品升级一次（不含消耗与窗口切换），「升级」与「连续升级」共用。 */
	private Item upgradeOnce(Item scroll, Item target) {
		ScrollOfUpgrade.upgrade(Dungeon.hero);

		Item upgraded = target;
		if (scroll instanceof ScrollOfUpgrade){
			((ScrollOfUpgrade) scroll).readAnimation();
			upgraded = ((ScrollOfUpgrade) scroll).upgradeItem(target);
			Sample.INSTANCE.play( Assets.Sounds.READ );
		} else if (scroll instanceof MagicalInfusion){
			((MagicalInfusion) scroll).useAnimation();
			upgraded = ((MagicalInfusion) scroll).upgradeItem(target);
		}
		return upgraded;
	}

	@Override
	public synchronized void update() {
		super.update();
		if (Dungeon.hero.ready){
			if (!btnUpgrade.active){
				btnUpgrade.enable(true);
			}
			if (btnUpgradeAll != null && !btnUpgradeAll.active){
				btnUpgradeAll.enable(true);
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (upgrader instanceof ScrollOfUpgrade) {
			((ScrollOfUpgrade) upgrader).reShowSelector(force);
		} else if (upgrader instanceof MagicalInfusion){
			((MagicalInfusion)upgrader).reShowSelector();
		}
	}

	public WndBag.ItemSelector getItemSelector(){
		if (upgrader instanceof ScrollOfUpgrade) {
			return ((ScrollOfUpgrade) upgrader).getSelector(force);
		} else if (upgrader instanceof MagicalInfusion){
			return ((MagicalInfusion)upgrader).getSelector();
		}
		return null;
	}

	private float fillFields(String title, String msg1, String msg2, float bottom){

		//the ~ symbol is more commonly used in Chinese
		if (Messages.lang() == Languages.CHI_SMPL || Messages.lang() == Languages.CHI_TRAD){
			msg1 = msg1.replace('-', '~');
			msg2 = msg2.replace('-', '~');
		}

		RenderedTextBlock ttl = PixelScene.renderTextBlock(6);
		ttl.align(RenderedTextBlock.CENTER_ALIGN);
		ttl.text(title, WIDTH/2);
		ttl.setPos(COL_1 - ttl.width() / 2f, bottom + GAP);
		PixelScene.align(ttl);
		add(ttl);

		RenderedTextBlock m1 = PixelScene.renderTextBlock(msg1, 6);
		m1.setPos(COL_2 - m1.width() / 2f, ttl.top());
		PixelScene.align(m1);
		add(m1);

		RenderedTextBlock m2 = PixelScene.renderTextBlock(msg2, 6);
		m2.setPos(COL_3 - m2.width() / 2f, ttl.top());
		PixelScene.align(m2);
		add(m2);

		return ttl.bottom() + GAP;

	}

	private float addMessage(String text, int color, float bottom){
		RenderedTextBlock message = PixelScene.renderTextBlock(6);
		message.text(text, WIDTH);
		message.setPos(0, bottom + GAP);
		message.hardlight(color);
		add(message);

		return message.bottom();
	}

}
