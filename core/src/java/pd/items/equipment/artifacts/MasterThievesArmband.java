/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Special Surprise Pixel Dungeon behavior restored from SPS-PD 0.9.8.
 */

package pd.items.equipment.artifacts;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.items.Heap;
import pd.items.Item;
import pd.items.StoneOre;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.items.equipment.wands.DamageWand;
import pd.items.equipment.wands.Wand;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.noosa.audio.Sample;

import java.util.ArrayList;
import pd.messages.InlineText;

/**
 * SPSEXPD: 魔术之手法杖——由「神偷袖章」神器改造而成，现在是一根普通法杖（不再是神器）：
 * 在背包中像其它法杖一样以「释放」施法，命中敌人或 NPC 时造成伤害并顺手偷走一件东西；
 * 另外保留原有的「魔术之手」隔空取物。
 *
 * <p>类名与包名保持不变以尽量减少旧存档加载失败面，但基类已从 Artifact 改为 DamageWand，
 * 因此旧档中仍装在神器槽上的那一件无法按原类型还原（用户已知悉并接受）。</p>
 */
public class MasterThievesArmband extends DamageWand {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MasterThievesArmband.class)
			.t("name", "魔术之手法杖")
			.t("staff_name", "魔术之手魔杖")
			.t("ac_magic_hand", "魔术之手")
			.t("magic_prompt", "选择要隔空取来的目标点")
			.t("magic_none", "那里没有可以取来的东西。")
			.t("magic_shop", "商店的物品无法这样取用。")
			.t("magic_range", "太远了，魔术之手够不到。")
			.t("magic_done", "魔术之手取来了%1$s。")
			.t("stolen", "魔术之手顺手从%1$s身上摸走了%2$s。")
			.t("stolen_stone", "魔术之手没从%1$s身上摸到什么，只抓到一块石头。")
			.t("desc", "一根缠着紫色天鹅绒的细杖，杖顶嵌着一只小小的银手。它的魔力既能伤人，也能把别人的东西悄悄挪进你的背包。")
			.t("stats_desc", "释放时造成_%1$d~%2$d点伤害_，并顺手从命中的敌人或 NPC 身上偷走一件东西。")
			.t("bmage_desc", "当_战斗法师_以魔术之手魔杖近战攻击目标时，这根魔杖同样会恢复充能。")
			.t("discover_hint", "可在法杖池中找到。");
	}

	public static final String AC_MAGIC_HAND = "MAGIC_HAND";
	/** 魔术之手可以够到的最大距离。 */
	public static final int MAGIC_HAND_RANGE = 8;

	{
		//SPSEXPD: 沿用原有占位法杖图标；默认动作与瞄准由 Wand 基类设定（释放）
		image = EquipmentEquipWeaponBasicWeaponDict.OLD_STAFF;
		collisionProperties = Ballistica.MAGIC_BOLT;
	}

	@Override public int min(int lvl) { return 2 + lvl; }
	@Override public int max(int lvl) { return 5 + 3 * lvl; }

	@Override public int initialCharges() { return 3; }

	@Override
	public void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target == null) return;

		wandProc(target, chargesPerCast());
		target.damage(damageRoll(), this);
		//SPSEXPD: 命中敌人或 NPC 时顺手偷窃
		if (target instanceof Mob) stealFrom((Mob) target, target);
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		//与其它 SPS 法杖一致：战斗法师近战联动为空
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (curCharges > 0 || !curChargeKnown) actions.add(AC_MAGIC_HAND);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_MAGIC_HAND.equals(action)) {
			curUser = hero;
			if (curCharges < 1) {
				GLog.w(Messages.get(Wand.class, "fizzles"));
				usesTargeting = false;
			} else if (cursed) {
				GLog.w(Messages.get(Wand.class, "cursed"));
				usesTargeting = false;
			} else {
				usesTargeting = true;
				GameScene.selectCell(magicHand);
			}
			return;
		}
		super.execute(hero, action);
	}

	/** SPSEXPD: 魔术之手——把视野内的掉落物或金币隔空取来，消耗 1 点充能。 */
	public final CellSelector.Listener magicHand = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer target) {
			if (target == null || curUser == null || Dungeon.level == null) return;
			if (!Dungeon.level.insideMap(target) || !Dungeon.level.heroFOV[target]
					|| Dungeon.level.distance(curUser.pos, target) > MAGIC_HAND_RANGE) {
				GLog.w(Messages.get(MasterThievesArmband.class, "magic_range"));
				return;
			}

			Heap heap = Dungeon.level.heaps.get(target);
			if (heap == null || heap.isEmpty()) {
				GLog.w(Messages.get(MasterThievesArmband.class, "magic_none"));
				return;
			}
			if (heap.type == Heap.Type.FOR_SALE) {
				GLog.w(Messages.get(MasterThievesArmband.class, "magic_shop"));
				return;
			}

			Item seized = heap.pickUp();
			if (seized == null) {
				GLog.w(Messages.get(MasterThievesArmband.class, "magic_none"));
				return;
			}

			curCharges--;
			seized.doPickUp(curUser);
			GLog.i(Messages.get(MasterThievesArmband.class, "magic_done", seized.name()));
			Sample.INSTANCE.play(Assets.Sounds.EVOKE);
			if (curUser.sprite != null) curUser.sprite.operate(curUser.pos);
			updateQuickslot();
			curUser.spendAndNext(1f);
		}

		@Override
		public String prompt() {
			return Messages.get(MasterThievesArmband.class, "magic_prompt");
		}
	};

	/** SPSEXPD: 从被命中的目标身上摸走一件东西（沿用旧版袖章的掉落取用规则）。 */
	protected void stealFrom(Mob mob, Char target) {
		Hero owner = curUser instanceof Hero ? (Hero) curUser : Dungeon.hero;
		if (owner == null) return;

		Item loot = takeLegacyLoot(mob);
		if (loot == null) return;

		if (loot instanceof StoneOre) {
			GLog.i(Messages.get(this, "stolen_stone", Messages.get(target, "name")));
		} else {
			GLog.i(Messages.get(this, "stolen", Messages.get(target, "name"), loot.name()));
		}
		if (!loot.doPickUp(owner) && Dungeon.level != null) {
			Dungeon.level.drop(loot, owner.pos);
		}
	}

	/** 旧版规则：目标身上第一件掉落物用 SupercreateLoot 取，取不到就给一块石头。 */
	protected Item takeLegacyLoot(Mob mob) {
		if (mob.firstItem) {
			mob.firstItem = false;
			Item loot = mob.SupercreateLoot();
			return loot == null ? new StoneOre() : loot;
		}
		return new StoneOre();
	}
}
