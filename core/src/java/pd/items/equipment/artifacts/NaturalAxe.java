/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.artifacts;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.items.Item;
import pd.levels.GroundItems;
import pd.levels.Terrain;
import pd.levels.features.HighGrass;
import pd.messages.Messages;
import pd.plants.Plant;
import pd.plants.SpsFruitBush;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;

import java.util.ArrayList;
import pd.messages.InlineText;

/**
 * SPSEXPD: 原创神器——自然之斧。
 *
 * <p>装备后可以对视野内、神器等级范围内的所有高草与特殊植物一次性「收获」（等同于踩踏效果：
 * 高草照常产种子/露珠，果实丛照常散落果实与蔬菜）。踩踏高草或用它收获都会让斧子成长，
 * 等级越高收获半径越大。</p>
 */
public class NaturalAxe extends Artifact {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(NaturalAxe.class)
			.t("name", "自然之斧")
			.t("ac_harvest", "收获")
			.t("prompt", "选择要收获的中心点")
			.t("harvest_none", "附近没有可以收获的高草或植物。")
			.t("harvest_done", "自然之斧一次收获了一株以上草木（共 %1$d 处）。")
			.t("level_up", "自然之斧的收获范围扩大了！")
			.t("desc", "一柄被草木汁液浸透的短斧，刃口没有一丝锈迹。挥动它就能一次性收拢周围的高草与果实丛，踩过草丛也会让它慢慢长大。\n\n收获半径随神器等级扩大。")
			.t("desc_worn", "装备后可用_收获_一次采集范围内的高草与特殊植物；踩踏高草同样让它成长。");
	}

	public static final String AC_HARVEST = "HARVEST";

	{
		image = EquipmentEquipWeaponBasicWeaponDict.HAND_AXE_0;
		levelCap = 10;
		charge = 0;
		partialCharge = 0;
		chargeCap = 10;
		defaultAction = AC_HARVEST;
	}

	/** 收获半径：神器等级 + 1（0 级也有 1 格）。 */
	public int radius() {
		return level() + 1;
	}

	public int exp() {
		return exp;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && !cursed) actions.add(AC_HARVEST);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (!AC_HARVEST.equals(action)) return;

		if (!isEquipped(hero)) {
			GLog.i(Messages.get(Artifact.class, "need_to_equip"));
			usesTargeting = false;
		} else if (cursed) {
			GLog.w(Messages.get(Artifact.class, "cursed"));
			usesTargeting = false;
		} else {
			curUser = hero;
			usesTargeting = true;
			GameScene.selectCell(harvester);
		}
	}

	/** 收获以 target 为中心、半径 radius() 内的所有高草与特殊植物。 */
	public int harvestArea(Hero hero, int target) {
		if (hero == null || Dungeon.level == null || !Dungeon.level.insideMap(target)) return 0;

		int radius = radius();
		int harvested = 0;
		int width = Dungeon.level.width();
		for (int dy = -radius; dy <= radius; dy++) {
			for (int dx = -radius; dx <= radius; dx++) {
				int cell = target + dx + dy * width;
				if (!Dungeon.level.insideMap(cell)) continue;
				if (Dungeon.level.distance(target, cell) > radius) continue;
				if (harvestCell(cell)) harvested++;
			}
		}

		if (harvested <= 0) return 0;
		grow(harvested);
		return harvested;
	}

	/** 收获一格：高草走踩踏逻辑，特殊植物走其自身收获逻辑后连根移除。 */
	public boolean harvestCell(int cell) {
		if (Dungeon.level == null || !Dungeon.level.insideMap(cell)) return false;
		if (Actor.findChar(cell) != null) return false;
		if (Dungeon.level.heroFOV != null && !Dungeon.level.heroFOV[cell]) return false;

		int terrain = Dungeon.level.map[cell];
		if (terrain == Terrain.HIGH_GRASS || terrain == Terrain.FURROWED_GRASS) {
			HighGrass.trample(Dungeon.level, cell);
			return true;
		}

		Plant plant = Dungeon.level.plants.get(cell);
		if (plant != null) {
			//SPSEXPD: 果丛的 activate 就是收获本身；原生野生植物按「野生档」收获——
			//只散 1 枚对应果实（含幸运加成），不触发燃烧/中毒/传送等植物效果，并由 wither 自行移除。
			if (plant instanceof SpsFruitBush) {
				plant.activate(curUser);
				GroundItems.uproot(Dungeon.level, cell);
			} else {
				plant.trigger(curUser);
			}
			return true;
		}
		return false;
	}

	/** 成长：每收获一处 +1，达到等级门槛就升级。 */
	public void grow(int amount) {
		if (amount <= 0) return;
		exp += amount;
		while (level() < levelCap && exp >= level() + 1) {
			exp = 0;
			upgrade();
			GLog.p(Messages.get(this, "level_up"));
		}
		if (exp > level() + 1) exp = level() + 1;
		updateQuickslot();
	}

	public final CellSelector.Listener harvester = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer target) {
			if (target == null || curUser == null) return;
			int harvested = harvestArea(curUser, target);
			if (harvested <= 0) {
				GLog.i(Messages.get(NaturalAxe.class, "harvest_none"));
				return;
			}
			GLog.i(Messages.get(NaturalAxe.class, "harvest_done", harvested));
			curUser.spendAndNext(1f);
		}

		@Override
		public String prompt() {
			return Messages.get(NaturalAxe.class, "prompt");
		}
	};

	@Override
	public String desc() {
		String desc = super.desc();
		if (isEquipped(Dungeon.hero)) desc += "\n\n" + Messages.get(this, "desc_worn");
		return desc;
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new Growth();
	}

	/** 装备标记：踩踏高草时由 HighGrass 调用 grow()。 */
	public class Growth extends ArtifactBuff {
		@Override
		public boolean act() {
			spend(TICK);
			return true;
		}

		public void grow(int amount) {
			if (isCursed()) return;
			NaturalAxe.this.grow(amount);
		}
	}

	/** 踩踏高草时的成长入口（供 HighGrass 调用）。 */
	public static Growth growthOf(Char ch) {
		return ch == null ? null : ch.buff(Growth.class);
	}
}
