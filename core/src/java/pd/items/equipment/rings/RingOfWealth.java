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

package pd.items.equipment.rings;

import pd.Challenges;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.CounterBuff;
import pd.actors.hero.Hero;
import pd.effects.Flare;
import pd.items.Generator;
import pd.items.Gold;
import pd.items.Honeypot;
import pd.items.Item;
import pd.items.misc.LuckyBadge;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.bombs.Bomb;
import pd.items.consum.potions.PotionOfExperience;
import pd.items.consum.potions.brews.UnstableBrew;
import pd.items.consum.potions.exotic.ExoticPotion;
import pd.items.consum.potions.exotic.PotionOfDivineInspiration;
import pd.items.consum.scrolls.ScrollOfTransmutation;
import pd.items.consum.scrolls.exotic.ExoticScroll;
import pd.items.consum.scrolls.exotic.ScrollOfMetamorphosis;
import pd.items.consum.spells.UnstableSpell;
import pd.items.consum.stones.StoneOfEnchantment;
import pd.items.equipment.trinkets.ExoticCrystals;
import pd.items.equipment.weapon.Weapon;
import pd.messages.Messages;
import pd.sprites.ItemIconSheet;
import render.noosa.Visual;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import pd.messages.InlineText;

public class RingOfWealth extends Ring {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RingOfWealth.class)
			.t("name", "财富之戒")
			.t("stats", "佩戴这枚戒指时，你的整体幸运程度会提升_%s%%_。")
			.t("typical_stats", "佩戴这枚戒指时，你的整体幸运程度通常会提升_%s%%_。")
			.t("combined_stats", "你已装备的戒指正联结它们的力量，一共增加了你_%s%%_的幸运。")
			.t("upgrade_stat_name_1", "幸运加成")
			.t("desc", "这枚戒指有如磁石般的招财功效，佩戴者有更大的机率从敌人身上或各类宝箱中取得较有价值的道具。戒指每提升 1 级都会为你带来 1 点幸运。被诅咒的戒指反而会降低发现宝物的可能性。");
	}




	{
		icon = ItemIconSheet.RING_WEALTH;
		buffClass = Wealth.class;
	}

	private float triesToDrop = Float.MIN_VALUE;
	private int dropsToRare = Integer.MIN_VALUE;
	
	public String statsInfo() {
		if (isIdentified()){
			String info = Messages.get(this, "stats",
					Messages.decimalFormat("#.##", 100f * (Math.pow(1.20f, soloBuffedBonus()) - 1f)));
			if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)){
				info += "\n\n" + Messages.get(this, "combined_stats",
						Messages.decimalFormat("#.##", 100f * (Math.pow(1.20f, combinedBuffedBonus(Dungeon.hero)) - 1f)));
			}
			return info;
		} else {
			return Messages.get(this, "typical_stats", Messages.decimalFormat("#.##", 20f));
		}
	}

	public String upgradeStat1(int level){
		if (cursed && cursedKnown) level = Math.min(-1, level-3);
		return Messages.decimalFormat("#.##", 100f * (Math.pow(1.2f, level+1)-1f)) + "%";
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
		triesToDrop = bundle.getFloat(TRIES_TO_DROP);
		dropsToRare = bundle.getInt(DROPS_TO_RARE);
	}

	@Override
	protected RingBuff buff( ) {
		return new Wealth();
	}
	
	/**
	 * SPSEXPD: 财富机制的等效等级。
	 *
	 * <p>戒指自身的 buffedLvl 保持原版全额效果；其余来源的幸运值按「每 2 点幸运 = 1 级」折算后叠加。
	 * 幸运值里已经包含戒指等级（见 LuckyBadge.luckBonus），这里把戒指部分扣掉以免重复计入。
	 */
	public static int wealthBonus( Char target ){
		if (target == null) return 0;
		int ring = getBuffedBonus(target, Wealth.class);
		int luck = target instanceof Hero ? LuckyBadge.luckBonus((Hero) target) : 0;
		return ring + Math.max(0, luck - ring) / 2;
	}

	public static float dropChanceMultiplier( Char target ){
		return (float)Math.pow(1.20, wealthBonus(target));
	}
	
	public static ArrayList<Item> tryForBonusDrop(Char target, int tries ){
		int bonus = wealthBonus(target);

		if (bonus <= 0) return null;

		CounterBuff triesToDrop = target.buff(TriesToDropTracker.class);
		if (triesToDrop == null){
			triesToDrop = Buff.affect(target, TriesToDropTracker.class);
			triesToDrop.countUp( Random.NormalIntRange(0, 20) );
		}

		CounterBuff dropsToEquip = target.buff(DropsToEquipTracker.class);
		if (dropsToEquip == null){
			dropsToEquip = Buff.affect(target, DropsToEquipTracker.class);
			dropsToEquip.countUp( Random.NormalIntRange(5, 10) );
		}

		//now handle reward logic
		ArrayList<Item> drops = new ArrayList<>();

		triesToDrop.countDown(tries);
		while ( triesToDrop.count() <= 0 ){
			if (dropsToEquip.count() <= 0){
				int equipBonus = 0;

				//A second ring of wealth can be at most +1 when calculating wealth bonus for equips
				//This is to prevent using an upgraded wealth to farm another upgraded wealth and
				//using the two to get substantially more upgrade value than intended
				for (Wealth w : target.buffs(Wealth.class)){
					if (w.buffedLvl() > equipBonus){
						equipBonus = w.buffedLvl() + Math.min(equipBonus, 2);
					} else {
						equipBonus += Math.min(w.buffedLvl(), 2);
					}
				}

				Item i;
				do {
					i = genEquipmentDrop(equipBonus - 1);
				} while (Challenges.isItemBlocked(i));
				drops.add(i);
				dropsToEquip.countUp(Random.NormalIntRange(5, 10));
			} else {
				Item i;
				do {
					i = genConsumableDrop(bonus - 1);
				} while (Challenges.isItemBlocked(i));
				drops.add(i);
				dropsToEquip.countDown(1);
			}
			triesToDrop.countUp( Random.NormalIntRange(0, 20) );
		}
		
		return drops;
	}

	//used for visuals
	// 1/2/3 used for low/mid/high tier consumables
	// 3 used for +0-1 equips, 4 used for +2 or higher equips
	private static int latestDropTier = 0;

	public static void showFlareForBonusDrop( Visual vis ){
		if (vis == null || vis.parent == null) return;
		switch (latestDropTier){
			default:
				break; //do nothing
			case 1:
				new Flare(6, 20).color(0x00FF00, true).show(vis, 3f);
				break;
			case 2:
				new Flare(6, 24).color(0x00AAFF, true).show(vis, 3.33f);
				break;
			case 3:
				new Flare(6, 28).color(0xAA00FF, true).show(vis, 3.67f);
				break;
			case 4:
				new Flare(6, 32).color(0xFFAA00, true).show(vis, 4f);
				break;
		}
		latestDropTier = 0;
	}
	
	public static Item genConsumableDrop(int level) {
		float roll = Random.Float();
		//60% chance - 4% per level. Starting from +15: 0%
		if (roll < (0.6f - 0.04f * level)) {
			latestDropTier = 1;
			return genLowValueConsumable();
		//30% chance + 2% per level. Starting from +15: 60%-2%*(lvl-15)
		} else if (roll < (0.9f - 0.02f * level)) {
			latestDropTier = 2;
			return genMidValueConsumable();
		//10% chance + 2% per level. Starting from +15: 40%+2%*(lvl-15)
		} else {
			latestDropTier = 3;
			return genHighValueConsumable();
		}
	}

	private static Item genLowValueConsumable(){
		switch (Random.Int(4)){
			case 0: default:
				Item i = new Gold().random();
				return i.quantity(i.quantity()/2);
			case 1:
				return Generator.randomUsingDefaults(Generator.Category.STONE);
			case 2:
				return Generator.randomUsingDefaults(Generator.Category.POTION);
			case 3:
				return Generator.randomUsingDefaults(Generator.Category.SCROLL);
		}
	}

	private static Item genMidValueConsumable(){
		switch (Random.Int(6)){
			case 0: default:
				Item i = genLowValueConsumable();
				return i.quantity(i.quantity()*2);
			case 1:
				i = Generator.randomUsingDefaults(Generator.Category.POTION);
				if (!(i instanceof ExoticPotion)) {
					return Reflection.newInstance(ExoticPotion.regToExo.get(i.getClass()));
				} else {
					return Reflection.newInstance(i.getClass());
				}
			case 2:
				i = Generator.randomUsingDefaults(Generator.Category.SCROLL);
				if (!(i instanceof ExoticScroll)){
					return Reflection.newInstance(ExoticScroll.regToExo.get(i.getClass()));
				} else {
					return Reflection.newInstance(i.getClass());
				}
			case 3:
				return Random.Int(2) == 0 ? new UnstableBrew() : new UnstableSpell();
			case 4:
				return new Bomb();
			case 5:
				return new Honeypot();
		}
	}

	private static Item genHighValueConsumable(){
		switch (Random.Int(4)){
			case 0: default:
				Item i = genMidValueConsumable();
				if (i instanceof Bomb){
					return new Bomb.DoubleBomb();
				} else {
					return i.quantity(i.quantity()*2);
				}
			case 1:
				return new StoneOfEnchantment();
			case 2:
				return Random.Float() < ExoticCrystals.consumableExoticChance() ? new PotionOfDivineInspiration() : new PotionOfExperience();
			case 3:
				return Random.Float() < ExoticCrystals.consumableExoticChance() ? new ScrollOfMetamorphosis() : new ScrollOfTransmutation();
		}
	}

	private static Item genEquipmentDrop( int level ){
		Item result;
		//each upgrade increases depth used for calculating drops by 1
		//SPSEXPD: 每章 8 层——档位改为“章节 + 每 5 级加成”
		int floorset = Dungeon.chapterIndex(Dungeon.depth) + level/5;
		switch (Random.Int(5)){
			default: case 0: case 1:
				Weapon w = Generator.randomWeapon(floorset, true);
				if (!w.hasGoodEnchant() && Random.Int(10) < level)      w.enchant();
				else if (w.hasCurseEnchant())                           w.enchant(null);
				result = w;
				break;
			case 2:
				Armor a = Generator.randomArmor(floorset);
				if (!a.hasGoodGlyph() && Random.Int(10) < level)        a.inscribe();
				else if (a.hasCurseGlyph())                             a.inscribe(null);
				result = a;
				break;
			case 3:
				result = Generator.randomUsingDefaults(Generator.Category.RING);
				break;
			case 4:
				result = Generator.random(Generator.Category.ARTIFACT);
				break;
		}
		//minimum level is 1/2/3/4/5/6 when ring level is 1/3/5/7/9/11
		if (result.isUpgradable()){
			int minLevel = (level+1)/2;
			if (result.level() < minLevel){
				result.level(minLevel);
			}
		}
		result.cursed = false;
		result.cursedKnown = true;
		if (result.level() >= 2) {
			latestDropTier = 4;
		} else {
			latestDropTier = 3;
		}
		return result;
	}

	public class Wealth extends RingBuff {
	}

	public static class TriesToDropTracker extends CounterBuff {
		{
			revivePersists = true;
		}
	}

	public static class DropsToEquipTracker extends CounterBuff {
		{
			revivePersists = true;
		}
	}
}
