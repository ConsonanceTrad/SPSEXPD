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
 */

package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Badges;
import pd.Challenges;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Water;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Haste;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.Levitation;
import pd.actors.buffs.MindVision;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.STRDown;
import pd.actors.buffs.Tar;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.HeroSubClass;
import pd.actors.hero.Talent;
import pd.effects.Speck;
import pd.effects.SpellSprite;
import pd.items.equipment.bags.Bag;
import pd.items.consum.food.WaterItem;
import pd.items.equipment.trinkets.VialOfBlood;
import pd.journal.Catalog;
import pd.levels.GroundItems;
import pd.levels.Terrain;
import pd.messages.Messages;
import pd.plants.Plant;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import pd.windows.WndBag;
import pd.windows.WndUseItem;
import render.noosa.audio.Sample;
import render.utils.math.GameMath;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentNonEquipDict;

public class Waterskin extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Waterskin.class)
			.t("name", "露珠瓶")
			.t("ac_drink", "饮用")
			.t("ac_light", "照明")
			.t("ac_detect", "侦测")
			.t("ac_cleanse", "清洗")
			.t("ac_haste", "加速")
			.t("collected", "你将一滴露珠收集到了水袋里。")
			.t("full", "你的水袋装满了！")
			.t("empty", "你的水袋一滴也不剩了！")
			.t("not_enough", "水袋中的露珠不足以施展这项能力。")
			.t("lit", "露珠化作稳定的微光，照亮了你的周围。")
			.t("detected", "露珠短暂揭示了本层所有生物的位置。")
			.t("cleansed", "露珠洗去了有害效果，并为你提供了片刻净化保护。")
			.t("hastened", "露珠令你的脚步短暂加快。")
			.t("desc", "牛皮缝制的液体容器，被软木塞牢牢密封着。在激烈的搏斗中也不会漏出一滴内容。")
			.t("desc_water", "你的水袋里只有普普通通的饮用水，地牢中肯定会有更值得装的东西。")
			.t("desc_heal", "水袋里现在装着有治愈魔力的露水。每滴露珠恢复最大生命值的2.5%%，每次只会喝掉你需要的量。")
			.t("desc_full", "装满了的水袋散发着一股能量，也许能够用来祝福其他的生存道具？")
			.t("desc_utility", "露珠瓶可以恢复生命、侦测生物并持续照明，后续还可解锁种植、强化、清洗、加速和提纯功能。")
			.t("discover_hint", "某位英雄初始携带该物品。")
			.t("mode_random", "露珠研究者已将水袋调整为_祝福强化_模式。")
			.t("mode_accurate", "露珠研究者已将水袋调整为_精确强化_模式。")
			.t("ac_water", "种植")
			.t("ac_splash", "加速")
			.t("ac_bless", "强化")
			.t("ac_pour", "清洗")
			.t("ac_peek", "侦测")
			.t("ac_refine", "提纯")
			.t("peeked", "露珠短暂揭示了本层的所有生物。")
			.t("watered", "植物在你周围生长。")
			.t("blessed", "神秘的能量强化了你的装备。")
			.t("select", "选择一件要强化的物品")
			.t("upgraded", "你的%1$s获得了%2$d级强化。")
			.t("fly", "你漂浮到了空中！")
			.t("no_charge", "你的露珠瓶空了！")
			.t("fast", "你的移动速度大幅提升了！")
			.t("poured", "你用露水清洗了身躯，驱散了多种负面效果。")
			.t("refined", "露珠被提纯成了洁净的水。")
			.t("desc_ex", "露珠瓶的无限溢出池中额外储存了_%d点露珠_。这些露珠可用于侦测、种植、强化和提纯。")
			.t("desc_v1", "露珠瓶v1提供强化和种植功能。")
			.t("desc_v2", "露珠瓶v2提供清洗和加速功能。")
			.t("desc_v3", "露珠瓶v3将基础容量提升至200，并使加速附带漂浮。")
			.t("$dewlight.name", "露珠微光")
			.t("$dewlight.desc", "每20回合将1点普通露珠转化为光亮，保护你免受黑暗侵袭。");
	}




	private static final int BASE_MAX_VOLUME = 100;
	private static final int WING_MAX_VOLUME = 200;

	private static final String AC_DRINK = "DRINK";
	private static final String AC_WATER = "WATER";
	private static final String AC_SPLASH = "SPLASH";
	private static final String AC_BLESS = "BLESS";
	private static final String AC_LIGHT = "LIGHT";
	private static final String AC_POUR = "POUR";
	private static final String AC_PEEK = "PEEK";
	private static final String AC_REFINE = "REFINE";
	private static final String AC_CHOOSE = "CHOOSE";

	private static final int PEEK_COST = 5;
	private static final int SPLASH_COST = 15;
	private static final int POUR_COST = 20;
	private static final int WATER_COST = 25;
	private static final int BLESS_COST = 70;
	private static final int REFINE_COST = 100;

	private static final float TIME_TO_LIGHT = 1f;
	private static final float TIME_TO_DRINK = 2f;
	private static final float TIME_TO_WATER = 3f;

	private static final String TXT_STATUS = "%d";
	private static final String TXT_STATUS2 = "%d/%d";

	{
		image = EquipmentNonEquipDict.WATERSKIN;
		defaultAction = AC_CHOOSE;
		unique = true;
	}

	private int volume;
	private int overflow;
	private UpgradeMode upgradeMode = UpgradeMode.NONE;

	private static final String VOLUME = "volume";
	private static final String LEGACY_VOLUME = "dewpoint";
	private static final String EX_VOLUME = "dewpointex";
	private static final String UPGRADE_MODE = "sps_upgrade_mode";

	public enum UpgradeMode {
		NONE,
		RANDOM_BLESS,
		ACCURATE
	}

	public Waterskin() {
		super();
	}

	public Waterskin(int volume, int overflow) {
		this.volume = Math.max(0, volume);
		this.overflow = Math.max(0, overflow);
	}

	public int checkVol() {
		return volume;
	}

	public int checkVolEx() {
		return overflow;
	}

	public int totalDew() {
		return volume + overflow;
	}

	public void setVol(int volume, int overflow) {
		this.volume = Math.max(0, Math.min(volume, maxVolume()));
		this.overflow = Math.max(0, overflow + Math.max(0, volume - maxVolume()));
		updateQuickslot();
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(VOLUME, volume);
		bundle.put(LEGACY_VOLUME, volume);
		bundle.put(EX_VOLUME, overflow);
		bundle.put(UPGRADE_MODE, upgradeMode);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		volume = bundle.contains(VOLUME) ? bundle.getInt(VOLUME) : bundle.getInt(LEGACY_VOLUME);
		overflow = bundle.getInt(EX_VOLUME);
		upgradeMode = bundle.contains(UPGRADE_MODE)
				? bundle.getEnum(UPGRADE_MODE, UpgradeMode.class)
				: UpgradeMode.NONE;
		volume = Math.max(0, volume);
		overflow = Math.max(0, overflow);
		if (volume > maxVolume()) {
			overflow += volume - maxVolume();
			volume = maxVolume();
		}
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);

		if (volume > 1) {
			actions.add(AC_DRINK);
			actions.add(AC_LIGHT);
		}
		if (Dungeon.dewNorn && volume > 29 && volume >= dewCost(SPLASH_COST)) {
			actions.add(AC_SPLASH);
			if (volume >= dewCost(POUR_COST)) actions.add(AC_POUR);
		}
		if (totalDew() > 29 && totalDew() >= dewCost(PEEK_COST)) actions.add(AC_PEEK);
		if (hasFirstUpgrade() && totalDew() > 39 && totalDew() >= dewCost(WATER_COST)) actions.add(AC_WATER);
		if (hasFirstUpgrade() && totalDew() > 99) {
			if (totalDew() >= dewCost(BLESS_COST)) actions.add(AC_BLESS);
			if (totalDew() >= dewCost(REFINE_COST)) actions.add(AC_REFINE);
		}
		return actions;
	}

	@Override
	public void execute(final Hero hero, String action) {
		super.execute(hero, action);

		if (action.equals(AC_CHOOSE)) {
			if (hero.buff(DewLight.class) == null) GameScene.show(new WndUseItem(null, this));
			else Buff.detach(hero, DewLight.class);
		} else if (action.equals(AC_DRINK)) {
			drink(hero);
		} else if (action.equals(AC_LIGHT)) {
			if (hero.buff(DewLight.class) == null) Buff.affect(hero, DewLight.class);
			else Buff.detach(hero, DewLight.class);
		} else if (action.equals(AC_PEEK) && consumeCombined(dewCost(PEEK_COST))) {
			Buff.prolong(hero, MindVision.class, 2f);
			SpellSprite.show(hero, SpellSprite.VISION, 1f, 0.77f, 0.9f);
			Dungeon.observe();
			operate(hero, TIME_TO_LIGHT);
			GLog.i(Messages.get(this, "peeked"));
		} else if (action.equals(AC_WATER) && consumeCombined(dewCost(WATER_COST))) {
			waterArea(hero);
			operate(hero, TIME_TO_WATER);
			GLog.i(Messages.get(this, "watered"));
		} else if (action.equals(AC_SPLASH) && consumeOrdinary(dewCost(SPLASH_COST))) {
			Buff.prolong(hero, Haste.class, Haste.DURATION);
			if (Dungeon.wings && Dungeon.legacyDepth() < 51) {
				Buff.prolong(hero, Levitation.class, Levitation.DURATION);
				GLog.i(Messages.get(this, "fly"));
			}
			GLog.i(Messages.get(this, "fast"));
		} else if (action.equals(AC_POUR) && consumeOrdinary(dewCost(POUR_COST))) {
			cleanse(hero);
			Buff.prolong(hero, Invisibility.class, Invisibility.DURATION);
			Buff.prolong(hero, Bless.class, Bless.DURATION);
			operate(hero, TIME_TO_WATER);
			GLog.i(Messages.get(this, "poured"));
		} else if (action.equals(AC_BLESS) && randomBlessMode()
				&& consumeCombined(dewCost(BLESS_COST))) {
			randomBless(hero);
			updateQuickslot();
		} else if (action.equals(AC_BLESS) && accurateMode()) {
			curUser = hero;
			GameScene.selectItem(itemSelector);
		} else if (action.equals(AC_REFINE) && consumeCombined(dewCost(REFINE_COST))) {
			refine(hero);
		}
	}

	private boolean hasFirstUpgrade() {
		return Dungeon.dewWater || Dungeon.dewDraw || upgradeMode != UpgradeMode.NONE;
	}

	private boolean randomBlessMode() {
		return Dungeon.dewWater || upgradeMode == UpgradeMode.RANDOM_BLESS;
	}

	private boolean accurateMode() {
		return Dungeon.dewDraw || upgradeMode == UpgradeMode.ACCURATE;
	}

	private void drink(Hero hero) {
		if (!consumeDrink(hero)) return;
		operate(hero, TIME_TO_DRINK);
		Sample.INSTANCE.play(Assets.Sounds.DRINK);
	}

	boolean consumeDrink(Hero hero) {
		if (volume <= 0) {
			GLog.w(Messages.get(this, "empty"));
			return false;
		}

		float dropHealPercent = dropHealPercent(hero);
		float missingHealthPercent = 1f - hero.HP / (float) hero.HT;
		float dropsNeeded = missingHealthPercent / dropHealPercent;
		if (dropsNeeded > 1.01f && VialOfBlood.delayBurstHealing()) {
			dropsNeeded /= VialOfBlood.totalHealMultiplier();
		}
		pd.actors.buffs.Barrier barrier =
				hero.buff(pd.actors.buffs.Barrier.class);
		int curShield = barrier == null ? 0 : barrier.shielding();
		int maxShield = Math.round(hero.HT * 0.2f * hero.pointsInTalent(Talent.SHIELDING_DEW));
		if (hero.hasTalent(Talent.SHIELDING_DEW) && maxShield > 0) {
			float missingShieldPercent = 1f - curShield / (float) maxShield;
			missingShieldPercent *= 0.2f * hero.pointsInTalent(Talent.SHIELDING_DEW);
			if (missingShieldPercent > 0) dropsNeeded += missingShieldPercent / dropHealPercent;
		}

		int dropsToConsume = (int) Math.ceil(dropsNeeded - 0.01f);
		dropsToConsume = (int) GameMath.gate(1, dropsToConsume, volume);
		if (Dewdrop.consumeDew(dropsToConsume, hero, true, dropHealPercent)) {
			volume -= dropsToConsume;
			fillCrystalVial(hero);
			Catalog.countUses(Dewdrop.class, dropsToConsume);
			updateQuickslot();
			return true;
		}
		return false;
	}

	static float dropHealPercent(Hero hero) {
		return hero.subClass == HeroSubClass.WARDEN ? 0.04f : 0.025f;
	}

	static int dewCost(int baseCost) {
		return baseCost + (Dungeon.isChallenged(Challenges.DEW_REJECTION) ? 10 : 0);
	}

	void waterArea(Hero hero) {
		int cx = hero.pos % Dungeon.level.width();
		int cy = hero.pos / Dungeon.level.width();
		for (int y = Math.max(0, cy - 1); y <= Math.min(Dungeon.level.height() - 1, cy + 1); y++) {
			for (int x = Math.max(0, cx - 1); x <= Math.min(Dungeon.level.width() - 1, cx + 1); x++) {
				int cell = x + y * Dungeon.level.width();
				if (Dungeon.level.heroFOV[cell]) {
					int terrain = Dungeon.level.map[cell];
					GameScene.add(Blob.seed(cell, 40, Water.class));
					if (terrain == Terrain.FLOWER_POT) {
						GroundItems.plant( Dungeon.level, (Plant.Seed) Generator.random(Generator.Category.SEED4), cell);
					}
				}
			}
		}
	}

	static void cleanse(Hero hero) {
		Buff.detach(hero, Burning.class);
		Buff.detach(hero, Ooze.class);
		Buff.detach(hero, Tar.class);
		Buff.detach(hero, STRDown.class);
		Buff.detach(hero, Vertigo.class);
	}

	private void randomBless(Hero hero) {
		boolean upgraded = blessItems(hero, hero.belongings.backpack.items.toArray(new Item[0]));
		Item[] equipped = {
				hero.belongings.weapon, hero.belongings.armor, hero.belongings.artifact,
				hero.belongings.misc, hero.belongings.ring, hero.belongings.secondWep,
				hero.belongings.secondArmor
		};
		upgraded |= blessItems(hero, equipped);
		upgraded |= blessItems(hero, equipped);
		if (upgraded) GLog.i(Messages.get(this, "blessed"));
	}

	private boolean blessItems(Hero hero, Item... items) {
		int levelLimit = Math.max(3, 3 + Math.round((Statistics.deepestFloor - 2) / 2f));
		if (hero.heroClass == HeroClass.MAGE) levelLimit++;
		float chance = hero.heroClass == HeroClass.MAGE ? 0.5f : 0.33f;
		boolean upgraded = false;

		for (Item item : items) {
			if (item == null) continue;
			if (item.isUpgradable()) {
				if (Random.Float() < chance && item.level() < levelLimit) {
					item.upgrade();
					upgraded = true;
					hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);
					Badges.validateItemLevelAquired(item);
				} else {
					overflow++;
				}
			}
			if (item instanceof Bag) {
				upgraded |= blessItems(hero, ((Bag) item).items.toArray(new Item[0]));
			}
		}
		return upgraded;
	}

	private final WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {
		@Override
		public String textPrompt() {
			return Messages.get(Waterskin.class, "select");
		}

		@Override
		public Class<? extends Bag> preferredBag() {
			return Belongings.Backpack.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item != null && item.isUpgradable();
		}

		@Override
		public void onSelect(Item item) {
			int cost = dewCost(BLESS_COST);
			if (item == null || totalDew() < cost) return;
			int min = Math.min(1, Statistics.deepestFloor / 24);
			int max = Math.max(2, Statistics.deepestFloor / 6);
			int upgrades = 1 + Random.Int(min, max);
			for (int i = 0; i < upgrades; i++) item.upgrade();
			if (item.level() > 14) item.identify();
			consumeCombined(cost);
			fillCrystalVial(curUser);
			Badges.validateItemLevelAquired(item);
			curUser.sprite.operate(curUser.pos);
			curUser.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);
			curUser.spendAndNext(Actor.TICK);
			GLog.i(Messages.get(Waterskin.class, "upgraded", item.name(), upgrades));
			updateQuickslot();
		}
	};

	private void refine(Hero hero) {
		operate(hero, TIME_TO_DRINK);
		WaterItem water = new WaterItem(10);
		if (water.doPickUp(hero)) {
			GLog.i(Messages.get(hero, "you_now_have", water.name()));
		} else {
			Dungeon.level.drop(water, hero.pos).sprite.drop();
		}
		GLog.i(Messages.get(this, "refined"));
	}

	private static void fillCrystalVial(Hero hero) {
		CrystalVial vial = hero == null ? null : hero.belongings.getItem(CrystalVial.class);
		if (vial != null) vial.fill();
	}

	private void operate(Hero hero, float time) {
		hero.spend(time);
		hero.busy();
		hero.sprite.operate(hero.pos);
		updateQuickslot();
	}

	private boolean consumeOrdinary(int amount) {
		if (volume < amount) {
			GLog.w(Messages.get(this, "not_enough"));
			return false;
		}
		volume -= amount;
		Catalog.countUses(Dewdrop.class, amount);
		updateQuickslot();
		return true;
	}

	private boolean consumeCombined(int amount) {
		if (totalDew() < amount) {
			GLog.w(Messages.get(this, "not_enough"));
			return false;
		}
		int fromOverflow = Math.min(overflow, amount);
		overflow -= fromOverflow;
		volume -= amount - fromOverflow;
		Catalog.countUses(Dewdrop.class, amount);
		updateQuickslot();
		return true;
	}

	public void empty() {
		volume = Math.max(0, volume - 10);
		updateQuickslot();
	}

	public void sip() {
		consumeOrdinary(1);
	}

	public void upbook(int amount) {
		overflow = Math.max(0, overflow - amount);
		updateQuickslot();
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	public boolean isFullBless() {
		return overflow >= 100;
	}

	public boolean isFull() {
		return volume >= maxVolume();
	}

	private int maxVolume() {
		return Dungeon.wings ? WING_MAX_VOLUME : BASE_MAX_VOLUME;
	}

	public void collectDew(Dewdrop dew) {
		GLog.i(Messages.get(this, "collected"));
		int collected = dew.dewValue();
		int room = Math.max(0, maxVolume() - volume);
		int stored = Math.min(room, collected);
		volume += stored;
		overflow += collected - stored;
		if (volume >= maxVolume()) GLog.p(Messages.get(this, "full"));
		updateQuickslot();
	}

	public void fill() {
		overflow += volume;
		volume = maxVolume();
		updateQuickslot();
	}

	public void applySpsUpgrade(UpgradeMode mode) {
		upgradeMode = mode;
		fill();
	}

	public UpgradeMode upgradeMode() {
		return upgradeMode;
	}

	@Override
	public String status() {
		return Messages.format(TXT_STATUS, volume);
	}

	public String status2() {
		return Messages.format(TXT_STATUS2, volume, overflow);
	}

	@Override
	public String toString() {
		return super.toString() + " (" + status2() + ")";
	}

	@Override
	public String info() {
		String info = super.info();
		if (overflow > 0) info += "\n\n" + Messages.get(this, "desc_ex", overflow);
		if (hasFirstUpgrade()) info += "\n\n" + Messages.get(this, "desc_v1");
		if (Dungeon.dewNorn) info += "\n\n" + Messages.get(this, "desc_v2");
		if (Dungeon.wings) info += "\n\n" + Messages.get(this, "desc_v3");
		return info;
	}

	public static class DewLight extends Buff {

		private int left;

		{
			type = buffType.NEUTRAL;
		}

		@Override
		public boolean attachTo(Char target) {
			if (!super.attachTo(target)) return false;
			if (Dungeon.level != null) {
				target.viewDistance = Math.max(Dungeon.level.viewDistance, 6);
				Dungeon.observe();
			}
			return true;
		}

		@Override
		public void detach() {
			if (Dungeon.level != null) {
				target.viewDistance = Dungeon.level.viewDistance;
				Dungeon.observe();
			}
			super.detach();
		}

		@Override
		public boolean act() {
			left--;
			if (left <= 0) {
				Waterskin waterskin = target instanceof Hero
						? ((Hero) target).belongings.getItem(Waterskin.class) : null;
				if (waterskin == null || !waterskin.consumeOrdinary(1)) {
					detach();
					GLog.w(Messages.get(Waterskin.class, "no_charge"));
					if (target instanceof Hero) ((Hero) target).interrupt();
				} else {
					left = 20;
				}
			}
			spend(TICK);
			return true;
		}

		@Override
		public int icon() {
			return BuffIndicator.LIGHT;
		}

		@Override
		public void fx(boolean on) {
			if (on) target.sprite.add(CharSprite.State.ILLUMINATED);
			else target.sprite.remove(CharSprite.State.ILLUMINATED);
		}

		@Override
		public String toString() {
			return Messages.get(this, "name");
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc");
		}

		private static final String LEFT = "left";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(LEFT, left);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			left = bundle.getInt(LEFT);
		}
	}
}
