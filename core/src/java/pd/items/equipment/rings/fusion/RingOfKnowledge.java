/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.rings.fusion;

import pd.Dungeon;
import pd.actors.Char;
import pd.effects.Flare;
import pd.effects.Speck;
import pd.items.Generator;
import pd.items.Gold;
import pd.items.GreatRune;
import pd.items.Item;
import pd.items.Stylus;
import pd.items.Weightstone;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.rings.Ring;
import pd.items.equipment.weapon.Weapon;
import pd.messages.Messages;
import pd.sprites.ItemIconSheet;
import render.noosa.Visual;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.HashSet;
import pd.messages.InlineText;

public class RingOfKnowledge extends Ring {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RingOfKnowledge.class)
			.t("name", "学识戒指")
			.t("stats", "佩戴这枚戒指时，你施法时有25%%的几率造成_%1$s_倍伤害，有_%2$s%%_的几率在击败敌人后获得额外掉落。")
			.t("upgrade_stat_name_1", "法术暴击伤害")
			.t("upgrade_stat_name_2", "额外掉落进度")
			.t("desc", "这枚戒指提升了配戴者的意识，增加了他造成法术暴击的几率，并允许他在击败敌人时获得额外的战利品。在30级时这枚戒指效果达到上限。");
	}




	{
		icon = ItemIconSheet.RING_WEALTH;
		buffClass = RingKnowledge.class;
	}

	private float triesToDrop = Float.MIN_VALUE;
	private int dropsToRare = Integer.MIN_VALUE;

	private static boolean latestDropWasRare;

	@Override
	public String statsInfo() {
		if (!isIdentified()) return "???";
		return Messages.get(this, "stats",
				Messages.decimalFormat("#.##", Math.min(3f, 1.2f + level() * 0.06f)),
				Messages.decimalFormat("#.##", Math.min(30f, level())));
	}

	@Override
	public String upgradeStat1(int level) {
		return Messages.decimalFormat("#.##", Math.min(3f, 1.2f + level * 0.06f));
	}

	@Override
	public String upgradeStat2(int level) {
		return Messages.decimalFormat("#.##", Math.min(30f, level)) + "%";
	}

	private static final String TRIES_TO_DROP = "tries_to_drop";
	private static final String DROPS_TO_RARE = "drops_to_rare";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TRIES_TO_DROP, triesToDrop);
		bundle.put(DROPS_TO_RARE, dropsToRare);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		triesToDrop = bundle.contains(TRIES_TO_DROP) ? bundle.getFloat(TRIES_TO_DROP) : Float.MIN_VALUE;
		dropsToRare = bundle.contains(DROPS_TO_RARE) ? bundle.getInt(DROPS_TO_RARE) : Integer.MIN_VALUE;
	}

	@Override
	protected RingBuff buff() {
		return new RingKnowledge();
	}

	public static int knowledgeBonus(Char target) {
		if (target == null) return 0;
		int bonus = 0;
		for (RingKnowledge buff : target.buffs(RingKnowledge.class)) {
			bonus += Math.min(buff.level(), 30);
		}
		return bonus;
	}

	public static int applyCriticalBonus(Char target, int damage, int roll) {
		if (target == null) return damage;
		int bonus = knowledgeBonus(target);
		if (bonus > 0 && roll < 5) {
			damage = (int)Math.floor(damage * (1.2 + 0.06 * bonus) + 1e-9);
			if (target.sprite != null) target.sprite.emitter().burst(Speck.factory(Speck.STAR), 8);
		}
		return damage;
	}

	public static ArrayList<Item> tryForBonusDrop(Char target, int tries) {
		if (knowledgeBonus(target) <= 0) return null;

		HashSet<RingKnowledge> buffs = target.buffs(RingKnowledge.class);
		float triesToDrop = Float.MIN_VALUE;
		int dropsToRare = Integer.MIN_VALUE;
		for (RingKnowledge buff : buffs) {
			if (buff.triesToDrop() > triesToDrop) {
				triesToDrop = buff.triesToDrop();
				dropsToRare = buff.dropsToRare();
			}
		}

		if (triesToDrop == Float.MIN_VALUE) {
			triesToDrop = Random.NormalIntRange(0, 40);
			dropsToRare = Random.NormalIntRange(0, 10);
		}

		ArrayList<Item> drops = new ArrayList<>();
		triesToDrop -= Math.max(1, dropProgression(target, tries));
		int safety = 0;
		while (triesToDrop <= 0 && safety++ < 100) {
			if (dropsToRare <= 0) {
				drops.add(genRareDrop());
				latestDropWasRare = true;
				dropsToRare = Random.NormalIntRange(0, 10);
			} else {
				drops.add(genStandardDrop());
				dropsToRare--;
			}
			triesToDrop += Random.NormalIntRange(0, 40);
		}
		if (triesToDrop <= 0) triesToDrop = 1;

		for (RingKnowledge buff : buffs) {
			buff.triesToDrop(triesToDrop);
			buff.dropsToRare(dropsToRare);
		}
		return drops;
	}

	public static Item genStandardDrop() {
		float roll = Random.Float();
		if (roll < 0.3f) {
			Item result = new Gold().random();
			return result.quantity(Math.round(result.quantity() * Random.Float(0.33f, 1f)));
		} else if (roll < 0.7f) {
			return genBasicConsumable();
		} else if (roll < 0.9f) {
			return genExoticConsumable();
		} else if (Random.Int(3) != 0) {
			Weapon weapon = Generator.randomWeapon();
			weapon.enchant(null);
			weapon.cursed = false;
			weapon.cursedKnown = true;
			weapon.upgrade(0);
			return weapon;
		} else {
			Armor armor = Generator.randomArmor();
			armor.inscribe(null);
			armor.cursed = false;
			armor.cursedKnown = true;
			armor.upgrade(0);
			return armor;
		}
	}

	private static Item genBasicConsumable() {
		float roll = Random.Float();
		if (roll < 0.4f) return Generator.random(Generator.Category.SEED);
		if (roll < 0.7f) return Generator.random(Generator.Category.POTION);
		return Generator.random(Generator.Category.SCROLL);
	}

	private static Item genExoticConsumable() {
		float roll = Random.Float();
		if (roll < 0.3f) return Generator.random(Generator.Category.POTION);
		return Generator.random(Generator.Category.SCROLL);
	}

	public static Item genRareDrop() {
		float roll = Random.Float();
		if (roll < 0.3f) {
			Item result = new Gold().random();
			return result.quantity(Math.round(result.quantity() * Random.Float(3f, 6f)));
		} else if (roll < 0.7f) {
			return genHighValueConsumable();
		} else if (roll < 0.9f) {
			Item result = Random.Int(2) == 0
					? Generator.random(Generator.Category.ARTIFACT)
					: Generator.random(Generator.Category.RING);
			if (result == null) result = Generator.randomUsingDefaults(Generator.Category.RING);
			result.cursed = false;
			result.cursedKnown = true;
			return result;
		} else if (Random.Int(3) != 0) {
			Weapon weapon = Generator.randomWeapon(rareEquipmentTier());
			weapon.upgrade(1);
			weapon.enchant(Weapon.Enchantment.random());
			weapon.cursed = false;
			weapon.cursedKnown = true;
			return weapon;
		} else {
			Armor armor = Generator.randomArmor(rareEquipmentTier());
			armor.upgrade();
			armor.inscribe(Armor.Glyph.random());
			armor.cursed = false;
			armor.cursedKnown = true;
			return armor;
		}
	}

	public static int rareEquipmentTier() {
		return (Dungeon.legacyDepth() / 5) + 1;
	}

	private static Item genHighValueConsumable() {
		switch (Random.Int(4)) {
			case 0: default: return new GreatRune();
			case 1: return new GreatRune().quantity(2);
			case 2: return new Weightstone();
			case 3: return new Stylus();
		}
	}

	private static float dropProgression(Char target, int tries) {
		return tries * Math.min(3f, 0.1f * knowledgeBonus(target));
	}

	public static void showFlareForBonusDrop(Visual visual) {
		if (visual != null && visual.parent != null) {
			if (latestDropWasRare) new Flare(8, 48).color(0xAA00FF, true).show(visual, 3f);
			else new Flare(8, 24).color(0xFFFFFF, true).show(visual, 3f);
		}
		latestDropWasRare = false;
	}

	public class RingKnowledge extends RingBuff {
		@Override public int level() { return RingOfKnowledge.this.level(); }
		@Override public int buffedLvl() { return level(); }
		private void triesToDrop(float value) { triesToDrop = value; }
		private float triesToDrop() { return triesToDrop; }
		private void dropsToRare(int value) { dropsToRare = value; }
		private int dropsToRare() { return dropsToRare; }
	}
}
