/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Pet collection adapted from Special Surprise Pixel Dungeon.
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.items.quest;

import pd.atlas.items.EquipmentJewelleryArtifactDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.FusionPet;
import pd.items.Item;
import pd.items.equipment.bags.Bag;
import pd.items.consum.food.Food;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndBag;
import pd.windows.WndOptions;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import pd.messages.InlineText;
import pd.atlas.items.ConsumPotionSeedSeedDict;

public class PetCompendium extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PetCompendium.class)
			.t("name", "伙伴图鉴")
			.t("ac_call", "召回伙伴")
			.t("ac_choose", "选择伙伴")
			.t("ac_feed", "喂食")
			.t("choose_family", "选择一组伙伴谱系。更换选择不会立即替换当前仍在战斗的伙伴。")
			.t("family_0", "林地伙伴")
			.t("family_1", "城镇伙伴")
			.t("family_2", "异域伙伴")
			.t("family_3", "高阶伙伴")
			.t("choose_type", "选择下一次召唤的伙伴。")
			.t("selected", "已经选择%s作为同行伙伴。")
			.t("no_space", "你身边没有足够空间让伙伴出现。")
			.t("spent", "本层的召唤力量已经耗尽；进入另一层后才能再次召唤阵亡的伙伴。")
			.t("recalled", "%s回到了你身边。")
			.t("called", "%s响应图鉴，来到你身边。")
			.t("feed_prompt", "选择一份普通食物喂给当前伙伴")
			.t("full", "伙伴现在不需要进食。")
			.t("fed", "%1$s恢复了%2$d点生命。")
			.t("obtained", "年兽留下了一本伙伴图鉴！")
			.t("desc", "记录特别惊喜像素地牢伙伴谱系的图鉴。当前选择：_%s_。\n\n每层可召唤一次伙伴；仍存活的伙伴可以不限次数召回。伙伴不会制造战利品，喂食会消耗一份正常食物。");
	}




	public static final String AC_CALL = "CALL";
	public static final String AC_CHOOSE = "CHOOSE";
	public static final String AC_FEED = "FEED";

	private int selectedType;
	private int petID = -1;
	private int lastSummonDepth = -1;
	private int lastSummonBranch = -1;

	{
		image = ConsumPotionSeedSeedDict.ICE_MINT;
		defaultAction = AC_CALL;
		unique = true;
		keptThoughLostInvent = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = new ArrayList<>();
		actions.add(AC_CALL);
		actions.add(AC_CHOOSE);
		if (findPet() != null) actions.add(AC_FEED);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		curUser = hero;
		curItem = this;
		if (AC_CALL.equals(action)) callPet(hero);
		else if (AC_CHOOSE.equals(action)) chooseFamily();
		else if (AC_FEED.equals(action)) GameScene.selectItem(foodSelector);
	}

	private void chooseFamily() {
		GameScene.show(new WndOptions(Messages.titleCase(name()), Messages.get(this, "choose_family"),
				Messages.get(this, "family_0"), Messages.get(this, "family_1"),
				Messages.get(this, "family_2"), Messages.get(this, "family_3")) {
			@Override
			protected void onSelect(int index) {
				chooseType(index * 4);
			}
		});
	}

	private void chooseType(final int first) {
		String[] options = new String[4];
		for (int i = 0; i < options.length; i++) {
			options[i] = Messages.get(FusionPet.class, "name_" + (first + i));
		}
		GameScene.show(new WndOptions(Messages.titleCase(name()), Messages.get(this, "choose_type"), options) {
			@Override
			protected void onSelect(int index) {
				selectedType = first + index;
				GLog.p(Messages.get(PetCompendium.class, "selected",
						Messages.get(FusionPet.class, "name_" + selectedType)));
			}
		});
	}

	private void callPet(Hero hero) {
		FusionPet pet = findPet();
		int cell = adjacentCell(hero);
		if (cell < 0) {
			GLog.w(Messages.get(this, "no_space"));
			return;
		}
		if (pet != null) {
			ScrollOfTeleportation.appear(pet, cell);
			pet.followHero();
			GLog.i(Messages.get(this, "recalled", pet.name()));
			return;
		}
		if (lastSummonDepth == Dungeon.depth && lastSummonBranch == Dungeon.branch) {
			GLog.w(Messages.get(this, "spent"));
			return;
		}

		pet = new FusionPet().configure(selectedType);
		pet.pos = cell;
		petID = pet.id();
		lastSummonDepth = Dungeon.depth;
		lastSummonBranch = Dungeon.branch;
		GameScene.add(pet, 1f);
		Dungeon.level.occupyCell(pet);
		hero.spendAndNext(1f);
		GLog.p(Messages.get(this, "called", pet.name()));
	}

	private int adjacentCell(Hero hero) {
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = hero.pos + offset;
			if (cell >= 0 && cell < Dungeon.level.length() && Dungeon.level.passable[cell]
					&& Actor.findChar(cell) == null) return cell;
		}
		return -1;
	}

	private FusionPet findPet() {
		if (petID >= 0 && Actor.findById(petID) instanceof FusionPet) {
			return (FusionPet)Actor.findById(petID);
		}
		if (Dungeon.level != null) {
			for (Mob mob : Dungeon.level.mobs()) {
				if (mob instanceof FusionPet && mob.isAlive()) {
					petID = mob.id();
					return (FusionPet)mob;
				}
			}
		}
		petID = -1;
		return null;
	}

	private final WndBag.ItemSelector foodSelector = new WndBag.ItemSelector() {
		@Override
		public String textPrompt() {
			return Messages.get(PetCompendium.class, "feed_prompt");
		}

		@Override
		public Class<? extends Bag> preferredBag() {
			return null;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof Food;
		}

		@Override
		public void onSelect(Item item) {
			FusionPet pet = findPet();
			if (item instanceof Food && pet != null) {
				int healed = pet.feed();
				if (healed <= 0) {
					GLog.i(Messages.get(PetCompendium.class, "full"));
					return;
				}
				item.detach(Dungeon.hero.belongings.backpack);
				Dungeon.hero.spendAndNext(1f);
				GLog.p(Messages.get(PetCompendium.class, "fed", pet.name(), healed));
			}
		}
	};

	public static PetCompendium ensureFor(Hero hero) {
		PetCompendium item = hero.belongings.getItem(PetCompendium.class);
		if (item != null) return item;
		item = new PetCompendium();
		if (!item.collect(hero.belongings.backpack)) {
			hero.belongings.backpack.items.add(item);
			Collections.sort(hero.belongings.backpack.items, Item.itemComparator);
			updateQuickslot();
		}
		GLog.p(Messages.get(PetCompendium.class, "obtained"));
		return item;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", Messages.get(FusionPet.class, "name_" + selectedType));
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public int value() {
		return 0;
	}

	private static final String SELECTED = "selected_type";
	private static final String PET_ID = "pet_id";
	private static final String SUMMON_DEPTH = "summon_depth";
	private static final String SUMMON_BRANCH = "summon_branch";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SELECTED, selectedType);
		bundle.put(PET_ID, petID);
		bundle.put(SUMMON_DEPTH, lastSummonDepth);
		bundle.put(SUMMON_BRANCH, lastSummonBranch);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		selectedType = Math.max(0, Math.min(FusionPet.TYPE_COUNT - 1, bundle.getInt(SELECTED)));
		petID = bundle.contains(PET_ID) ? bundle.getInt(PET_ID) : -1;
		lastSummonDepth = bundle.contains(SUMMON_DEPTH) ? bundle.getInt(SUMMON_DEPTH) : -1;
		lastSummonBranch = bundle.contains(SUMMON_BRANCH) ? bundle.getInt(SUMMON_BRANCH) : -1;
	}
}
