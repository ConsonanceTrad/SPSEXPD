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
import pd.items.equipment.bags.Bag;
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
			.t("ac_swallow", "吞噬食物")
			.t("prompt", "选择一个食物")
			.t("swallow", "号角吞噬了%1$s，充能增加%2$d点。")
			.t("ration", "号角把充能凝成了一包干粮。")
			.t("levelup", "号角凝成干粮后成长了一级。")
			.t("maxlevel", "号角的成长已经到达极限，但它仍会继续产出干粮。")
			.t("desc", "这个号角不能被用来吹奏，不过它会随时间逐渐积蓄食物能量。把食物给它吞噬也能积蓄能量；每积蓄 6 点能量，它就会自动凝成一包干粮，每凝成一包干粮它都会成长一点（成长会加快能量积蓄）。")
			.t("desc_hint", "当前充能：%1$d（每 6 点自动凝成一包干粮）")
			.t("desc_cursed", "被诅咒的号角把自己绑在了你的身边，它似乎在渴望得到食物而不是制造食物。");
	}

	private static final float TIME_TO_SWALLOW = 2f;
	private static final float ENERGY_PER_CHARGE = 40f;
	/** 每这么多点充能自动凝成 1 包干粮。 */
	public static final int RATION_COST = 6;
	private static final String OBSOLETE_STORED_ENERGY = "stored";

	public static final String AC_SWALLOW = "SWALLOW";

	{
		image = EquipmentJewelleryArtifactDict.ARTIFACT_HORN1;
		levelCap = 30;
		charge = 0;
		partialCharge = 0;
		//SPSEXPD: 不再设置充能上限——充能超过 6 点会自动凝成干粮
		chargeCap = 0;
		defaultAction = AC_SWALLOW;
	}

	//SPSEXPD: 号角上限 30，等级直接按内部计（每产出 1 包干粮 +1 级，显示 0~30），
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
		if (!AC_SWALLOW.equals(action)) {
			super.execute(hero, action);
			return;
		}
		if (!isEquipped(hero)) {
			GLog.i(Messages.get(Artifact.class, "need_to_equip"));
		} else {
			GameScene.selectItem(itemSelector);
		}
	}

	/** SPSEXPD: 吞噬一份食物，按号角价值换充能，随后按需自动凝成干粮。 */
	public int swallow(Hero hero, Food food) {
		if (hero == null || food == null) return 0;
		int gained = Math.max(0, food.hornValue);
		charge += gained;
		GLog.p(Messages.get(this, "swallow", food.name(), gained));
		convertRations(hero);
		updateQuickslot();
		return gained;
	}

	/** SPSEXPD: 充能每满 RATION_COST 点自动凝成 1 包干粮，每产出一包神器成长 1 级。 */
	private void convertRations(Hero hero) {
		while (charge >= RATION_COST) {
			charge -= RATION_COST;

			NormalRation ration = new NormalRation();
			boolean collected = hero != null && ration.collect(hero.belongings.backpack);
			if (!collected && Dungeon.level != null && hero != null) {
				Dungeon.level.drop(ration, hero.pos);
			}
			GLog.p(Messages.get(this, "ration"));

			if (level() < levelCap) {
				upgrade(1);
				if (level() >= levelCap) {
					level(levelCap);
					GLog.p(Messages.get(this, "maxlevel"));
				} else {
					GLog.p(Messages.get(this, "levelup"));
				}
			}
			updateImage();
		}
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
		//SPSEXPD: 只显示充能数（不显示上限）
		if (!isIdentified() || cursed) return null;
		return String.valueOf(charge);
	}

	@Override
	public String desc() {
		String desc = super.desc();
		if (isEquipped(Dungeon.hero)) {
			desc += "\n\n" + Messages.get(this, "desc_hint", charge);
			if (cursed) desc += "\n\n" + Messages.get(this, "desc_cursed");
		}
		return desc;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		// The obsolete Shattered food-energy field is deliberately not written.
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(OBSOLETE_STORED_ENERGY)) {
			int migratedLevel = level() * 3 + Math.round(bundle.getInt(OBSOLETE_STORED_ENERGY) / 100f);
			level(Math.min(levelCap, migratedLevel));
		}
		//SPSEXPD: 旧档的充能可能高于 6（旧上限 10）——下次 tick 自动凝成干粮
		updateImage();
	}

	private void updateImage() {
		if (charge >= 5) image = EquipmentJewelleryArtifactDict.ARTIFACT_HORN4;
		else if (charge >= 3) image = EquipmentJewelleryArtifactDict.ARTIFACT_HORN3;
		else if (charge >= 1) image = EquipmentJewelleryArtifactDict.ARTIFACT_HORN2;
		else image = EquipmentJewelleryArtifactDict.ARTIFACT_HORN1;
	}

	public class hornRecharge extends ArtifactBuff {
		@Override
		public boolean act() {
			if (!cursed) {
				partialCharge += 0.25f + 0.015f * level();
				if (partialCharge >= 80f) {
					charge++;
					partialCharge -= 80f;
					//SPSEXPD: 充能满 6 点自动凝成干粮（随时间产出）
					convertRations(Dungeon.hero);
					updateImage();
					updateQuickslot();
				}
			} else {
				partialCharge = 0;
			}
			spend(TICK);
			return true;
		}
	}

	protected static WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {
		@Override
		public String textPrompt() {
			return Messages.get(HornOfPlenty.class, "prompt");
		}

		@Override
		public Class<? extends Bag> preferredBag() {
			return Belongings.Backpack.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof Food;
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
			item.detach(hero.belongings.backpack);
		}
	};
}
