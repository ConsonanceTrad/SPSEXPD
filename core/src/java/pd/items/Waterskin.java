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
import pd.actors.blobs.Blob;
import pd.actors.blobs.Water;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.HighLight;
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
			.t("collected", "你将一滴露珠收集到了露珠瓶里。")
			.t("empty", "你的露珠瓶一滴也不剩了！")
			.t("not_enough", "露珠瓶中的露珠不足以施展这项能力。")
			.t("lit", "露珠化作稳定的微光，照亮了你的周围。")
			.t("detected", "露珠短暂揭示了本层所有生物的位置。")
			.t("cleansed", "露珠洗去了有害效果，并为你提供了片刻净化保护。")
			.t("hastened", "露珠令你的脚步短暂加快。")
			.t("desc", "一瓶收集来的露珠。露珠是地牢里凝结的净化之水，可以用来照明、浇灌、提纯，或在积攒足够后强化自身。")
			.t("desc_water", "你的露珠瓶里只有普普通通的饮用水，地牢中肯定会有更值得装的东西。")
			.t("desc_heal", "露珠瓶里现在装着有治愈魔力的露水。每滴露珠恢复最大生命值的2.5%%，每次只会喝掉你需要的量。")
			.t("desc_full", "装满了的露珠瓶散发着一股能量，也许能够用来祝福其他的生存道具？")
			.t("desc_utility", "露珠瓶可以恢复生命、侦测生物并一次性消耗露珠照明，后续还可解锁种植、强化、清洗、加速和提纯功能。")
			.t("discover_hint", "某位英雄初始携带该物品。")
			.t("mode_random", "露珠研究者已将露珠瓶调整为_祝福强化_模式。")
			.t("mode_accurate", "露珠研究者已将露珠瓶调整为_精确强化_模式。")
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
			.t("bless_gate", "这件装备已经达到或超过了露珠强化的门槛（当前门槛 %d 级），无法再被强化。")
			.t("fly", "你漂浮到了空中！")
			.t("fast", "你的移动速度大幅提升了！")
			.t("poured", "你用露水清洗了身躯，驱散了多种负面效果。")
			.t("refined", "露珠被提纯成了洁净的水。")
			.t("desc_total", "瓶中共储存了_%d点露珠_。露珠可用于饮水、侦测、种植、强化、清洗和提纯。")
			.t("desc_v1", "露珠瓶v1提供强化和种植功能。")
			.t("desc_v2", "露珠瓶v2提供清洗和加速功能。")
			.t("desc_v3", "露珠瓶v3使加速附带漂浮。");
	}




	//SPSEXPD: 露珠瓶改为单一存储池（无上限），不再区分基础容量/翅膀容量

	private static final String AC_DRINK = "DRINK";
	private static final String AC_WATER = "WATER";
	private static final String AC_SPLASH = "SPLASH";
	private static final String AC_BLESS = "BLESS";
	public static final String AC_LIGHT = "LIGHT";   //SPSEXPD: 快捷操作需要引用
	private static final String AC_POUR = "POUR";
	private static final String AC_PEEK = "PEEK";
	private static final String AC_REFINE = "REFINE";
	private static final String AC_CHOOSE = "CHOOSE";

	private static final int PEEK_COST = 5;
	private static final int SPLASH_COST = 15;
	private static final int POUR_COST = 20;
	private static final int WATER_COST = 25;
	private static final int BLESS_COST = 100; //SPSXPD: 提高到 100，方便玩家计算
	private static final int REFINE_COST = 100;
	//SPSEXPD: 照明改为一次性消耗，不再按回合持续扣露珠
	public static final int LIGHT_COST = 50;
	public static final int LIGHT_DURATION = 100;

	private static final float TIME_TO_LIGHT = 1f;
	private static final float TIME_TO_DRINK = 2f;
	private static final float TIME_TO_WATER = 3f;

	//格子状态数字超过 3 位会超框，超过 999 时显示 999+
	private static final int STATUS_CAP = 999;
	private static final String TXT_STATUS = "%d";
	private static final String TXT_STATUS2 = "%d";

	{
		image = EquipmentNonEquipDict.WATERSKIN;
		defaultAction = AC_CHOOSE;
		unique = true;
	}

	private int volume;   //SPSEXPD: 单一露珠存储池（无上限）
	private UpgradeMode upgradeMode = UpgradeMode.NONE;

	private static final String VOLUME = "volume";
	private static final String LEGACY_VOLUME = "dewpoint";
	private static final String LEGACY_EX_VOLUME = "dewpointex";   //旧溢出池，读档时并入 volume
	private static final String UPGRADE_MODE = "sps_upgrade_mode";

	public enum UpgradeMode {
		NONE,
		RANDOM_BLESS,
		ACCURATE
	}

	public Waterskin() {
		super();
	}

	//SPSEXPD: 双参构造保留签名，两个数值一律并入同一个池
	public Waterskin(int volume, int overflow) {
		this.volume = Math.max(0, volume) + Math.max(0, overflow);
	}

	public int checkVol() {
		return volume;
	}

	//SPSEXPD: 溢出池已取消，总量即唯一的池
	public int checkVolEx() {
		return volume;
	}

	public int totalDew() {
		return volume;
	}

	//SPSEXPD: 双参保留签名，两个数值一律并入同一个池
	public void setVol(int volume, int overflow) {
		this.volume = Math.max(0, volume) + Math.max(0, overflow);
		updateQuickslot();
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(VOLUME, volume);
		bundle.put(LEGACY_VOLUME, volume);
		bundle.put(UPGRADE_MODE, upgradeMode);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		volume = bundle.contains(VOLUME) ? bundle.getInt(VOLUME) : bundle.getInt(LEGACY_VOLUME);
		//SPSEXPD: 旧存档的溢出池并入主池
		volume += bundle.getInt(LEGACY_EX_VOLUME);
		upgradeMode = bundle.contains(UPGRADE_MODE)
				? bundle.getEnum(UPGRADE_MODE, UpgradeMode.class)
				: UpgradeMode.NONE;
		volume = Math.max(0, volume);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);

		if (volume > 1) {
			actions.add(AC_DRINK);
		}
		//SPSEXPD: 照明一次性消耗50露珠，与火把的「强光」时间叠加
		if (volume >= LIGHT_COST) {
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
			GameScene.show(new WndUseItem(null, this));
		} else if (action.equals(AC_DRINK)) {
			drink(hero);
		} else if (action.equals(AC_LIGHT) && consumeOrdinary(LIGHT_COST)) {
			//SPSEXPD: 与火把同一个「强光」buff，剩余时间直接叠加；点亮即语义结束，无关闭
			Buff.affect(hero, HighLight.class, LIGHT_DURATION);
			GLog.i(Messages.get(this, "lit"));
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
		} else if (action.equals(AC_BLESS)) {
			//SPSEXPD: 已取消祝福强化分支，强化统一走"选一件装备"的精确强化
			curUser = hero;
			GameScene.selectItem(itemSelector);
		} else if (action.equals(AC_REFINE) && consumeCombined(dewCost(REFINE_COST))) {
			refine(hero);
		}
	}

	private boolean hasFirstUpgrade() {
		return Dungeon.dewWater || Dungeon.dewDraw || upgradeMode != UpgradeMode.NONE;
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
						//SPSEXPD: 露珠瓶浇水催生的植物保持野生形态（踩踏触发原生效果 + 散落 1 枚果实），
						//给野生植物多一条获取途径；果丛形态只保留给入口房/帐篷房与手动把种子种进花盆
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

	//SPSEXPD: 祝福强化分支已取消，原 randomBless()/blessItems() 一并移除；强化统一走 itemSelector。

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
			if (item == null || !item.isUpgradable()) return;

			//SPSEXPD: 门槛 = 玩家等级/4 向上取整（上限 10）。装备等级低于门槛才可强化；
			//单轮提升 1~3 级，若仍未达门槛且露珠足够就继续，直到达门槛或露珠耗尽。
			int threshold = Math.min(10, (curUser.lvl + 3) / 4);
			if (item.level() >= threshold) {
				GLog.w(Messages.get(Waterskin.class, "bless_gate", threshold));
				return;
			}

			int levels = 0;
			while (item.level() < threshold && totalDew() >= cost) {
				int upgrades = 1 + Random.Int(3);
				for (int i = 0; i < upgrades; i++) item.upgrade();
				levels += upgrades;
				consumeCombined(cost);
			}
			if (levels <= 0) return;

			if (item.level() > 14) item.identify();
			fillCrystalVial(curUser);
			Badges.validateItemLevelAquired(item);
			curUser.sprite.operate(curUser.pos);
			curUser.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);
			curUser.spendAndNext(Actor.TICK);
			GLog.i(Messages.get(Waterskin.class, "upgraded", item.name(), levels));
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

	//SPSEXPD: 单池化后所有消耗都走同一实现
	/** SPSXPD: 动作按钮右上角显示的露珠消耗 */
	@Override
	public String actionCost(String action, Hero hero) {
		if (AC_LIGHT.equals(action)) return costText(LIGHT_COST, hero);
		if (AC_PEEK.equals(action)) return costText(dewCost(PEEK_COST), hero);
		if (AC_WATER.equals(action)) return costText(dewCost(WATER_COST), hero);
		if (AC_SPLASH.equals(action)) return costText(dewCost(SPLASH_COST), hero);
		if (AC_POUR.equals(action)) return costText(dewCost(POUR_COST), hero);
		if (AC_BLESS.equals(action)) return costText(dewCost(BLESS_COST), hero);
		if (AC_REFINE.equals(action)) return costText(dewCost(REFINE_COST), hero);
		return null;
	}

	/** SPSXPD: 消耗提示文本；拥有「露珠研究」时显示随机折扣后的区间（如 35~45） */
	private static String costText(int base, Hero hero) {
		pd.actors.hero.perks.DewResearch research =
				hero == null ? null : hero.heroPerk.get(pd.actors.hero.perks.DewResearch.class);
		if (research == null) return Integer.toString(base);
		return research.costMin(base) + "~" + research.costMax(base);
	}

	private boolean consumeDew(int amount) {
		//SPSXPD: 「露珠研究」特质 —— 随机降低本次露珠消耗（1 级 10~30%，2 级 20~40%）
		amount = pd.actors.hero.perks.DewResearch.applyDiscount(Dungeon.hero, amount);
		if (volume < amount) {
			GLog.w(Messages.get(this, "not_enough"));
			return false;
		}
		volume -= amount;
		Catalog.countUses(Dewdrop.class, amount);
		updateQuickslot();
		return true;
	}

	private boolean consumeOrdinary(int amount) {
		return consumeDew(amount);
	}

	private boolean consumeCombined(int amount) {
		return consumeDew(amount);
	}

	public void empty() {
		volume = Math.max(0, volume - 10);
		updateQuickslot();
	}

	public void sip() {
		consumeOrdinary(1);
	}

	//SPSEXPD: 单池化，直接扣露珠池
	public void upbook(int amount) {
		volume = Math.max(0, volume - amount);
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

	//SPSEXPD: 单池化后"祝福门槛"看总量
	public boolean isFullBless() {
		return volume >= 100;
	}

	//SPSEXPD: 单池化后"满"指达到基础量100（供十字章等判定）
	public boolean isFull() {
		return volume >= 100;
	}

	//SPSEXPD: 无条件收入，不再有上限，也不再有"已满"提示
	public void collectDew(Dewdrop dew) {
		GLog.i(Messages.get(this, "collected"));
		volume += Math.max(0, dew.dewValue());
		updateQuickslot();
	}

	//SPSEXPD: 强化时补足到基础量，不再有上限概念
	public void fill() {
		volume = Math.max(volume, 100);
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
		//SPSEXPD: 格子上超过 999 显示 999+，真实数量在详情页
		return volume > STATUS_CAP ? (STATUS_CAP + "+") : Messages.format(TXT_STATUS, volume);
	}

	public String status2() {
		return Messages.format(TXT_STATUS2, volume);
	}

	@Override
	public String toString() {
		return super.toString() + " (" + status2() + ")";
	}

	@Override
	public String info() {
		String info = super.info();
		//SPSEXPD: 详情页显示真实数量
		info += "\n\n" + Messages.get(this, "desc_total", volume);
		if (hasFirstUpgrade()) info += "\n\n" + Messages.get(this, "desc_v1");
		if (Dungeon.dewNorn) info += "\n\n" + Messages.get(this, "desc_v2");
		if (Dungeon.wings) info += "\n\n" + Messages.get(this, "desc_v3");
		return info;
	}

}
