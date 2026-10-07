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

import pd.atlas.items.SpecificPlaceHolderDict;

import com.badlogic.gdx.Gdx;
import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.CounterBuff;
import pd.actors.buffs.GoldTouch;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.effects.particles.ElmoParticle;
import pd.items.Heap;
import pd.items.Item;
import pd.items.StoneOre;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class MasterThievesArmband extends Artifact {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MasterThievesArmband.class)
			.t("name", "魔术之手法杖")
			.t("ac_steal", "偷窃")
			.t("ac_magic_hand", "魔术之手")
			.t("magic_prompt", "选择要隔空取来的目标点")
			.t("magic_none", "那里没有可以取来的东西。")
			.t("magic_shop", "商店的物品无法这样取用。")
			.t("magic_range", "太远了，魔术之手够不到。")
			.t("magic_done", "魔术之手取来了%1$s。")
			.t("ac_goldtouch", "耗竭-点金")
			.t("no_charge", "充能不足")
			.t("cursed", "它被诅咒了，正在吞食你的金币。")
			.t("no_target", "没有找到目标")
			.t("level_up", "神偷袖章升级了")
			.t("prompt", "选择偷窃的目标")
			.t("desc", "一根缠着紫色天鹅绒的细杖，杖顶嵌着一只小小的银手。挥动它，远处的东西就会自己飞进你的背包。")
			.t("desc_worn", "装备后只要还有充能，就能用_魔术之手_隔空取来视野中的物品与金币。");
	}




	{
		//SPSEXPD: 由「神偷袖章」改造为「魔术之手法杖」——占位法杖图标，默认动作改为隔空取物
		image = EquipmentEquipWeaponBasicWeaponDict.OLD_STAFF;
		levelCap = 5;
		charge = 0;
		partialCharge = 0;
		chargeCap = 1 + level();
		defaultAction = AC_MAGIC_HAND;
	}

	public static final String AC_STEAL = "STEAL";
	public static final String AC_GOLDTOUCH = "GOLDTOUCH";
	public static final String AC_MAGIC_HAND = "MAGIC_HAND";
	/** 魔术之手可以够到的最大距离。 */
	public static final int MAGIC_HAND_RANGE = 8;

	@Override
	public String status() {
		return levelKnown ? charge + "/" + chargeCap : null;
	}

	@Override
	public Item upgrade() {
		chargeCap = Math.min(6, chargeCap + 1);
		return super.upgrade();
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && charge > 0 && !cursed) {
			actions.add(AC_MAGIC_HAND);
			actions.add(AC_STEAL);
		}
		if (!isEquipped(hero) && level() > 1 && !cursed) actions.add(AC_GOLDTOUCH);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (AC_MAGIC_HAND.equals(action)) {
			curUser = hero;
			if (!isEquipped(hero)) {
				GLog.i(Messages.get(Artifact.class, "need_to_equip"));
				usesTargeting = false;
			} else if (charge < 1) {
				GLog.i(Messages.get(this, "no_charge"));
				usesTargeting = false;
			} else if (cursed) {
				GLog.w(Messages.get(this, "cursed"));
				usesTargeting = false;
			} else {
				usesTargeting = true;
				GameScene.selectCell(magicHand);
			}
		}
		if (AC_STEAL.equals(action)) {
			curUser = hero;
			if (!isEquipped(hero)) {
				GLog.i(Messages.get(Artifact.class, "need_to_equip"));
				usesTargeting = false;
			} else if (charge < 1) {
				GLog.i(Messages.get(this, "no_charge"));
				usesTargeting = false;
			} else if (cursed) {
				GLog.w(Messages.get(this, "cursed"));
				usesTargeting = false;
			} else {
				usesTargeting = true;
				GameScene.selectCell(targeter);
			}
		}
		if (AC_GOLDTOUCH.equals(action)) {
			applyGoldTouch(hero);
		}
	}

	protected void applyGoldTouch(Hero hero) {
		Buff.affect(hero, GoldTouch.class, level() * 5f);
		if (Gdx.audio != null) Sample.INSTANCE.play(Assets.Sounds.BURNING);
		if (hero.sprite != null) hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
		hero.spend(1f);
		hero.busy();
		if (hero.sprite != null) hero.sprite.operate(hero.pos);
		level(level() - 1);
		updateQuickslot();
	}

	/** SPSEXPD: 魔术之手——把视野内的掉落物或金币隔空取来。 */
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

			charge--;
			seized.doPickUp(curUser);
			GLog.i(Messages.get(MasterThievesArmband.class, "magic_done", seized.name()));
			if (Gdx.audio != null) Sample.INSTANCE.play(Assets.Sounds.EVOKE);
			if (curUser.sprite != null) curUser.sprite.operate(curUser.pos);
			updateQuickslot();
			curUser.spendAndNext(1f);
		}

		@Override
		public String prompt() {
			return Messages.get(MasterThievesArmband.class, "magic_prompt");
		}
	};

	public final CellSelector.Listener targeter = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer target) {
			if (target == null) return;
			if (curUser == null || Dungeon.level == null || target < 0 || target >= Dungeon.level.length()
					|| !Dungeon.level.adjacent(curUser.pos, target)) {
				GLog.w(Messages.get(MasterThievesArmband.class, "no_target"));
				return;
			}

			Char targetChar = Actor.findChar(target);
			if (!(targetChar instanceof Mob)) return;

			final Mob mob = (Mob) targetChar;
			curUser.busy();
			Callback finish = new Callback() {
				@Override
				public void call() {
					if (Gdx.audio != null) Sample.INSTANCE.play(Assets.Sounds.HIT);
					performLegacySteal(mob);
				}
			};
			if (curUser.sprite != null) curUser.sprite.attack(target, finish);
			else finish.call();
		}

		@Override
		public String prompt() {
			return Messages.get(MasterThievesArmband.class, "prompt");
		}
	};

	protected void performLegacySteal(Mob mob) {
		Item loot = takeLegacyLoot(mob);
		if (Dungeon.level != null && curUser != null && loot != null) {
			Heap heap = Dungeon.level.drop(loot, curUser.pos);
			if (heap != null && heap.sprite != null) heap.sprite.drop();
		}

		recordLegacySteal();
		if (curUser != null) curUser.next();
	}

	protected void recordLegacySteal() {
		charge--;
		exp++;
		while (exp >= level() && level() < levelCap) {
			exp = 0;
			GLog.p(Messages.get(MasterThievesArmband.class, "level_up"));
			upgrade();
		}
		updateQuickslot();
	}

	protected Item takeLegacyLoot(Mob mob) {
		if (mob.firstItem) {
			mob.firstItem = false;
			Item loot = mob.SupercreateLoot();
			return loot == null ? new StoneOre() : loot;
		}
		return new StoneOre();
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new Thievery();
	}

	@Override
	public String desc() {
		String desc = super.desc();
		if (isEquipped(Dungeon.hero)) desc += "\n\n" + Messages.get(this, "desc_worn");
		return desc;
	}

	private static final String LEGACY_PARTIAL_CHARGE = "partialCharge";
	private static final String SAVED_CHARGE = "charge";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEGACY_PARTIAL_CHARGE, partialCharge);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		int savedCharge = bundle.getInt(SAVED_CHARGE);
		super.restoreFromBundle(bundle);
		if (level() > levelCap) level(levelCap);
		chargeCap = Math.min(6, 1 + level());
		charge = Math.max(0, Math.min(chargeCap, savedCharge));
		if (bundle.contains(LEGACY_PARTIAL_CHARGE)) {
			partialCharge = bundle.getFloat(LEGACY_PARTIAL_CHARGE);
		}
	}

	public class Thievery extends ArtifactBuff {
		@Override
		public boolean act() {
			if (cursed && Dungeon.gold > 0 && Random.Int(5) == 0) Dungeon.gold--;

			if (charge < chargeCap) {
				partialCharge += 1f;
				if (partialCharge >= 400f) {
					charge++;
					partialCharge = 0;
					if (charge == chargeCap) partialCharge = 0;
				}
			} else {
				partialCharge = 0;
			}

			updateQuickslot();
			spend(TICK);
			return true;
		}

		public void gainCharge() {
			if (cursed) return;
			if (charge < chargeCap) {
				partialCharge += level();
				while (partialCharge > 400f) {
					partialCharge = 0;
					charge++;
					updateQuickslot();
					if (charge == chargeCap) partialCharge = 0;
				}
			} else {
				partialCharge = 0f;
			}
		}

		// Retained only so Shattered's shop code compiles; zero keeps that non-SPS action hidden.
		public boolean steal(Item item) { return false; }
		public float stealChance(Item item) { return 0f; }
		public int chargesToUse(Item item) { return 0; }
	}

	/** Kept to deserialize saves made by earlier SPS-SPD development builds. */
	public static class StolenTracker extends CounterBuff {
		{ revivePersists = true; }
		public void setItemStolen(boolean stolen) { if (stolen) countUp(1); }
		public boolean itemWasStolen() { return count() > 0; }
	}
}
