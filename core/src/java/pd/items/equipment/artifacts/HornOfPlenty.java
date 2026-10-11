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

package pd.items.equipment.artifacts;

import pd.atlas.items.EquipmentJewelleryArtifactDict;

import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Hunger;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.consum.food.Food;
import pd.items.consum.food.staplefood.NormalRation;
import pd.items.consum.food.staplefood.OverpricedRation;
import pd.items.equipment.bags.Bag;
import pd.items.equipment.bags.ShoppingCart;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndBag;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

/**
 * SPSEXPD: 丰饶之角——干粮制造机。
 *
 * 充能来源：随时间恢复 + 吞噬食物（按食物的号角价值换充能）。
 * 每攒满 {@link #RATION_COST} 点充能自动凝成 1 包干粮（余数保留，充能无上限），
 * 每产出一包干粮神器成长 1 级（等级越高随时间回充能越快）。
 * 快捷行为是「吞噬食物」；状态栏只显示充能数。
 */
public class HornOfPlenty extends Artifact {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HornOfPlenty.class)
			.t("name", "丰饶之角")
			.t("ac_swallow", "吞噬")
			.t("prompt", "选择")
			.t("swallow", "号角吞噬了%1$s，得到%2$d点能量。")
			.t("ration", "号角的能量转化成了一包干粮。")
			.t("ration_many", "号角的能量转化成了%1$d包干粮。")
			.t("auto_feed", "号角喂了你一包干粮。")
			.t("levelup", "号角成长了一级。")
			.t("maxlevel", "号角的成长已经到达极限。")
			.t("desc", "这个号角不能被用来吹奏，不过它会随时间逐渐积蓄或吞噬其他食物的能量，并最终将其转化为易食的干粮。\n\n号角的成长上限为 _10 级_；等级越高，吞噬食物时的能量转化效率越高（从 _75%%_ 提升到 _125%%_）。\n\n在你极度饥饿时，它将伸出一只虚幻的手把干粮送入你的口中。")
			.t("desc_hint", "当前充能：%1$d")
			.t("desc_cursed", "被诅咒的号角把自己绑在了你的身边，它似乎在渴望吸取能量而不是制造食物。");
	}

	private static final float TIME_TO_SWALLOW = 2f;
	private static final float ENERGY_PER_CHARGE = 40f;
	/** 每这么多点充能自动凝成 1 包干粮。SPSEXPD: 等级上限 30 -> 10 后同步 ×3，满级总需求仍是 180 点。 */
	public static final int RATION_COST = 18;
	/**
	 * SPSEXPD: 时间充能整体倍率。1.0 时 0 级号角要 320 回合才攒 1 点充能、
	 * 1920 回合才凝出 1 包干粮（6 点充能）。先提到 8 倍后实测太快，按用户裁决降到 1/4（= 2 倍）。
	 */
	public static final float RECHARGE_MULTIPLIER = 2f;
	private static final String OBSOLETE_STORED_ENERGY = "stored";
	/** SPSEXPD: 号角自己的实数充能存档键。 */
	private static final String HORN_CHARGE = "horn_charge";

	public static final String AC_SWALLOW = "SWALLOW";

	{
		image = EquipmentJewelleryArtifactDict.ARTIFACT_HORN1;
		//SPSEXPD: 上限 30 -> 10（刻度等比变粗：每级所需充能 6 -> 18，总量不变）
		levelCap = 10;
		charge = 0;
		partialCharge = 0;
		//SPSEXPD: 不再设置充能上限——充能攒够 RATION_COST 会自动凝成干粮
		chargeCap = 0;
		defaultAction = AC_SWALLOW;
	}

	/**
	 * SPSEXPD: 号角自己的实数充能。基类 Artifact.charge 是 int 且被所有神器共用（无法改类型），
	 * 用 float 才能让「食物能量 × 转化效率」的小数部分不被截断丢弃。
	 * 设为 protected 以便子类/同包测试直接读写。
	 */
	protected float hornCharge = 0f;

	/** SPSEXPD: 食物转化效率——等级越高越高，0 级 75%、满级 125%（线性）。 */
	public float conversionEfficiency() {
		return 0.75f + 0.5f * (level() / (float) levelCap);
	}

	//SPSEXPD: 号角上限 10，等级直接按内部计（每产出 1 包干粮 +1 级，显示 0~10），
	//不再折算成 0~10 的通用显示尺度。
	@Override
	public int visiblyUpgraded() {
		return level();
	}

	@Override
	public int buffedVisiblyUpgraded() {
		return level();
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero)) actions.add(AC_SWALLOW);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		//SPSEXPD: 必须先走 super 以正确设置 curUser/curItem——否则 itemSelector.onSelect 里的
		//「curItem instanceof HornOfPlenty」守卫会失败，吞噬毫无反应（食物也不减少）
		super.execute(hero, action);
		if (!AC_SWALLOW.equals(action)) return;

		if (!isEquipped(hero)) {
			GLog.i(Messages.get(Artifact.class, "need_to_equip"));
		} else {
			GameScene.selectItem(itemSelector);
		}
	}

	/** SPSEXPD: 吞噬整组食物（一次吞掉该堆叠的全部数量），按整组的总号角价值 × 转化效率换充能，随后按需自动凝成干粮。 */
	public float swallow(Hero hero, Food food) {
		if (hero == null || food == null) return 0f;
		//SPSEXPD: 整组吞噬——单个 hornValue × 堆叠数量 × 转化效率。
		//全程实数计算：hornValue=1 的食物在 75% 效率下是 0.75 点，不再被 int 截断成 0（能量不再凭空丢失）。
		int count = Math.max(1, food.quantity());
		float gained = Math.max(0, food.hornValue) * count * conversionEfficiency();
		hornCharge += gained;
		String label = count > 1 ? food.name() + " ×" + count : food.name();
		GLog.p(Messages.get(this, "swallow", label, (int)gained));
		convertRations(hero);
		updateQuickslot();
		return gained;
	}

	/**
	 * SPSEXPD: 充能每满 RATION_COST 点自动凝成 1 包干粮，每产出一包神器成长 1 级。
	 * 整组吞噬可能一次凝成很多包：文本合并为一条（产出即代表升级，故不再单独提示升级）。
	 */
	private void convertRations(Hero hero) {
		int produced = 0;
		boolean reachedCap = false;
		while (hornCharge >= RATION_COST) {
			hornCharge -= RATION_COST;

			NormalRation ration = new NormalRation();
			boolean collected = hero != null && ration.collect(hero.belongings.backpack);
			if (!collected && Dungeon.level != null && hero != null) {
				Dungeon.level.drop(ration, hero.pos);
			}
			produced++;

			if (level() < levelCap) {
				upgrade(1);
				if (level() >= levelCap) {
					level(levelCap);
					reachedCap = true;
				}
			}
		}
		if (produced <= 0) return;

		updateImage();
		GLog.p(produced == 1
				? Messages.get(this, "ration")
				: Messages.get(this, "ration_many", produced));
		if (reachedCap) GLog.p(Messages.get(this, "maxlevel"));
	}

	//SPSEXPD: 兼容隐藏法术（SpiritForm）——号角吐出一口食物，不耗充能
	public void doEatEffect(Hero hero, int chargesToUse) {
		if (hero == null) return;
		Buff.affect(hero, Hunger.class).satisfy(ENERGY_PER_CHARGE * Math.max(1, chargesToUse));
		GLog.i(Messages.get(this, "ration"));
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new hornRecharge();
	}

	@Override
	public String status() {
		//SPSEXPD: 只显示充能数（不显示上限）；内部是实数，界面一律向下取整
		if (!isIdentified() || cursed) return null;
		return String.valueOf((int)hornCharge);
	}

	@Override
	public String desc() {
		String desc = super.desc();
		if (isEquipped(Dungeon.hero)) {
			desc += "\n\n" + Messages.get(this, "desc_hint", (int)hornCharge);
			if (cursed) desc += "\n\n" + Messages.get(this, "desc_cursed");
		}
		return desc;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(HORN_CHARGE, hornCharge);
		// The obsolete Shattered food-energy field is deliberately not written.
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(HORN_CHARGE)) {
			hornCharge = bundle.getFloat(HORN_CHARGE);
		} else {
			//SPSEXPD: 旧档的充能写在基类 charge 里，且单位是旧的 1/3（旧 RATION_COST = 6）——换算到新单位
			hornCharge = charge * 3f;
			charge = 0;
		}
		if (bundle.contains(OBSOLETE_STORED_ENERGY)) {
			int migratedLevel = level() * 3 + Math.round(bundle.getInt(OBSOLETE_STORED_ENERGY) / 100f);
			level(Math.min(levelCap, migratedLevel));
		}
		//SPSEXPD: 旧档等级上限是 30，收敛到新的 10 级
		if (level() > levelCap) level(levelCap);
		//SPSEXPD: 旧档的充能可能高于 RATION_COST——下次 tick 自动凝成干粮
		updateImage();
	}

	private void updateImage() {
		//SPSEXPD: 阈值随 RATION_COST 同步 ×3（原为 1/3/5）
		if (hornCharge >= 15) image = EquipmentJewelleryArtifactDict.ARTIFACT_HORN4;
		else if (hornCharge >= 9) image = EquipmentJewelleryArtifactDict.ARTIFACT_HORN3;
		else if (hornCharge >= 3) image = EquipmentJewelleryArtifactDict.ARTIFACT_HORN2;
		else image = EquipmentJewelleryArtifactDict.ARTIFACT_HORN1;
	}

	public class hornRecharge extends ArtifactBuff {
		@Override
		public boolean act() {
			if (!cursed) {
				//SPSEXPD: 时间充能速率 = (0.25 + 0.045×等级) × RECHARGE_MULTIPLIER。
				//上限由 30 级变 10 级，斜率同步 ×3，使各等级的时间产出与旧版一一对应
				//（新 0 级 = 旧 0 级、新 10 级 = 旧 30 级）
				partialCharge += (0.25f + 0.045f * level()) * RECHARGE_MULTIPLIER;
				if (partialCharge >= 80f) {
					hornCharge++;
					partialCharge -= 80f;
					//SPSEXPD: 充能满 RATION_COST 点自动凝成干粮（随时间产出）
					convertRations(Dungeon.hero);
					updateImage();
					updateQuickslot();
				}
				autoFeed();
			} else {
				partialCharge = 0;
			}
			spend(TICK);
			return true;
		}

		/**
		 * SPSEXPD: 英雄处于「极度饥饿」（Hunger.isStarving）时，自动用背包里的干粮包喂食，不消耗回合。
		 * 每次 tick 最多喂一包；若修正（无食物挑战 / 诅咒号角）让一包不足以脱离极度饥饿，下一回合会继续喂。
		 */
		private void autoFeed() {
			Hero hero = Dungeon.hero;
			if (hero == null || hero.belongings == null) return;
			Hunger hunger = hero.buff(Hunger.class);
			if (hunger == null || !hunger.isStarving()) return;
			NormalRation ration = hero.belongings.getItem(NormalRation.class);
			if (ration == null) return;
			GLog.p(Messages.get(HornOfPlenty.class, "auto_feed"));
			ration.eatQuietly(hero);
		}
	}

	protected static WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {
		@Override
		public String textPrompt() {
			return Messages.get(HornOfPlenty.class, "prompt");
		}

		@Override
		public Class<? extends Bag> preferredBag() {
			//SPSEXPD: 食物会被自动收进只装食物的「购物车」，所以默认打开购物车；
			//没有购物车时 WndBag / InventoryPane 会自动回退到主背包
			return ShoppingCart.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			//SPSEXPD: 干粮包/干粮小包不能喂——号角自产的就是干粮包，能再喂回去会形成与外部食物
			//脱钩的自喂升级链（干粮小包也能被炼金从干粮包转化，一并堵上）
			return item instanceof Food
					&& !(item instanceof NormalRation)
					&& !(item instanceof OverpricedRation);
		}

		@Override
		public void onSelect(Item item) {
			if (!(item instanceof Food) || !(curItem instanceof HornOfPlenty)) return;
			Hero hero = Dungeon.hero;
			if (hero == null) return;
			if (hero.sprite != null) hero.sprite.operate(hero.pos);
			hero.busy();
			hero.spend(TIME_TO_SWALLOW);
			((HornOfPlenty) curItem).swallow(hero, (Food) item);
			//SPSEXPD: 吞掉整组——Item.detach 只会消耗 1 个，整堆吞食要用 detachAll
			item.detachAll(hero.belongings.backpack);
		}
	};
}
