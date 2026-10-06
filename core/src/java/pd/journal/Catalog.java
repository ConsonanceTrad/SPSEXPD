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

package pd.journal;

import pd.Badges;
import pd.Dungeon;
import pd.items.Amulet;
import pd.items.Ankh;
import pd.items.ArcaneResin;
import pd.items.BrokenSeal;
import pd.items.Dewdrop;
import pd.items.EnergyCrystal;
import pd.items.Generator;
import pd.items.Gold;
import pd.items.Honeypot;
import pd.items.KingsCrown;
import pd.items.LiquidMetal;
import pd.items.Stylus;
import pd.items.TengusMask;
import pd.items.Torch;
import pd.items.Waterskin;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.bags.MagicalHolster;
import pd.items.equipment.bags.PotionBandolier;
import pd.items.equipment.bags.ScrollHolder;
import pd.items.equipment.bags.VelvetPouch;
import pd.items.equipment.bombs.ArcaneBomb;
import pd.items.equipment.bombs.Bomb;
import pd.items.equipment.bombs.Firebomb;
import pd.items.equipment.bombs.FlashBangBomb;
import pd.items.equipment.bombs.FrostBomb;
import pd.items.equipment.bombs.HolyBomb;
import pd.items.equipment.bombs.Noisemaker;
import pd.items.equipment.bombs.RegrowthBomb;
import pd.items.equipment.bombs.ShrapnelBomb;
import pd.items.equipment.bombs.SmokeBomb;
import pd.items.equipment.bombs.WoollyBomb;
import pd.items.consum.food.Berry;
import pd.items.consum.food.Blandfruit;
import pd.items.consum.food.ChargrilledMeat;
import pd.items.consum.food.Food;
import pd.items.consum.food.FrozenCarpaccio;
import pd.items.consum.food.MeatPie;
import pd.items.consum.food.MysteryMeat;
import pd.items.consum.food.Pasty;
import pd.items.consum.food.PhantomMeat;
import pd.items.consum.food.SmallRation;
import pd.items.consum.food.StewedMeat;
import pd.items.consum.food.SupplyRation;
import pd.items.specific.keys.CrystalKey;
import pd.items.specific.keys.GoldenKey;
import pd.items.specific.keys.IronKey;
import pd.items.specific.keys.WornKey;
import pd.items.consum.potions.brews.AquaBrew;
import pd.items.consum.potions.brews.BlizzardBrew;
import pd.items.consum.potions.brews.CausticBrew;
import pd.items.consum.potions.brews.InfernalBrew;
import pd.items.consum.potions.brews.ShockingBrew;
import pd.items.consum.potions.brews.UnstableBrew;
import pd.items.consum.potions.elixirs.ElixirOfAquaticRejuvenation;
import pd.items.consum.potions.elixirs.ElixirOfArcaneArmor;
import pd.items.consum.potions.elixirs.ElixirOfDragonsBlood;
import pd.items.consum.potions.elixirs.ElixirOfFeatherFall;
import pd.items.consum.potions.elixirs.ElixirOfHoneyedHealing;
import pd.items.consum.potions.elixirs.ElixirOfIcyTouch;
import pd.items.consum.potions.elixirs.ElixirOfMight;
import pd.items.consum.potions.elixirs.ElixirOfToxicEssence;
import pd.items.consum.potions.elixirs.WishPotion;
import pd.items.consum.potions.exotic.ExoticPotion;
import pd.items.quest.CeremonialCandle;
import pd.items.quest.CorpseDust;
import pd.items.quest.DarkGold;
import pd.items.quest.DwarfToken;
import pd.items.quest.Embers;
import pd.items.quest.EscapeCrystal;
import pd.items.quest.GooBlob;
import pd.items.quest.ImpStatue;
import pd.items.quest.MetalShard;
import pd.items.quest.VaultBeacon;
import pd.items.ground.remains.BowFragment;
import pd.items.ground.remains.BrokenHilt;
import pd.items.ground.remains.BrokenStaff;
import pd.items.ground.remains.CloakScrap;
import pd.items.ground.remains.SealShard;
import pd.items.ground.remains.TornPage;
import pd.items.consum.scrolls.exotic.ExoticScroll;
import pd.items.consum.spells.Alchemize;
import pd.items.consum.spells.BeaconOfReturning;
import pd.items.consum.spells.CurseInfusion;
import pd.items.consum.spells.MagicalInfusion;
import pd.items.consum.spells.PhaseShift;
import pd.items.consum.spells.ReclaimTrap;
import pd.items.consum.spells.Recycle;
import pd.items.consum.spells.SummonElemental;
import pd.items.consum.spells.TelekineticGrab;
import pd.items.consum.spells.UnstableSpell;
import pd.items.consum.spells.WildEnergy;
import pd.items.equipment.trinkets.TrinketCatalyst;
import pd.items.equipment.weapon.SpiritBow;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.missiles.darts.TippedDart;
import pd.messages.Messages;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import pd.messages.InlineText;

//For items, but includes a few item-like effects, such as enchantments
public enum Catalog {

	//EQUIPMENT
	MELEE_WEAPONS,
	ARMOR,
	ENCHANTMENTS,
	GLYPHS,
	THROWN_WEAPONS,
	WANDS,
	RINGS,
	ARTIFACTS,
	TRINKETS,
	MISC_EQUIPMENT,

	//CONSUMABLES
	POTIONS,
	SEEDS,
	SCROLLS,
	STONES,
	FOOD,
	EXOTIC_POTIONS,
	EXOTIC_SCROLLS,
	BOMBS,
	TIPPED_DARTS,
	BREWS_ELIXIRS,
	SPELLS,
	MISC_CONSUMABLES;
	//SPSEXPD: inline Chinese text (generated from messages/journal/zh)
	static {
		InlineText.of(Catalog.class)
			.t("melee_weapons.title", "近战武器")
			.t("armor.title", "护甲")
			.t("enchantments.title", "附魔与诅咒")
			.t("glyphs.title", "刻印与诅咒")
			.t("thrown_weapons.title", "投掷武器")
			.t("wands.title", "法杖")
			.t("rings.title", "戒指")
			.t("artifacts.title", "神器")
			.t("trinkets.title", "饰物")
			.t("misc_equipment.title", "杂项装备")
			.t("potions.title", "药剂")
			.t("scrolls.title", "卷轴")
			.t("seeds.title", "种子")
			.t("stones.title", "符石")
			.t("food.title", "食物")
			.t("exotic_potions.title", "合剂")
			.t("exotic_scrolls.title", "秘卷")
			.t("bombs.title", "炸弹")
			.t("tipped_darts.title", "涂药飞镖")
			.t("brews_elixirs.title", "魔药与秘药")
			.t("spells.title", "法术结晶")
			.t("misc_consumables.title", "杂项消耗品");
	}




	//tracks whether an item has been collected while identified
	private final LinkedHashMap<Class<?>, Boolean> seen = new LinkedHashMap<>();
	//tracks upgrades spent for equipment, uses for consumables
	private final LinkedHashMap<Class<?>, Integer> useCount = new LinkedHashMap<>();
	
	public Collection<Class<?>> items(){
		return seen.keySet();
	}

	//should only be used when initializing
	private void addItems( Class<?>... items){
		for (Class<?> item : items){
			seen.put(item, false);
			useCount.put(item, 0);
		}
	}

	public String title(){
		return Messages.get(this, name() + ".title");
	}

	public int totalItems(){
		return seen.size();
	}

	public int totalSeen(){
		int seenTotal = 0;
		for (boolean itemSeen : seen.values()){
			if (itemSeen) seenTotal++;
		}
		return seenTotal;
	}

	static {

		MELEE_WEAPONS.addItems(Generator.Category.WEP_T1.classes);
		MELEE_WEAPONS.addItems(Generator.Category.WEP_T2.classes);
		MELEE_WEAPONS.addItems(Generator.Category.WEP_T3.classes);
		MELEE_WEAPONS.addItems(Generator.Category.WEP_T4.classes);
		MELEE_WEAPONS.addItems(Generator.Category.WEP_T5.classes);

		ARMOR.addItems(Generator.Category.ARMOR.classes);

		THROWN_WEAPONS.addItems(Generator.Category.MIS_T1.classes);
		THROWN_WEAPONS.addItems(Generator.Category.MIS_T2.classes);
		THROWN_WEAPONS.addItems(Generator.Category.MIS_T3.classes);
		THROWN_WEAPONS.addItems(Generator.Category.MIS_T4.classes);
		THROWN_WEAPONS.addItems(Generator.Category.MIS_T5.classes);

		// Shattered-only enchantments and glyphs remain implemented but hidden
		// until their SPS counterparts and catalog rules have been ported.

		WANDS.addItems(Generator.Category.WAND.classes);

		RINGS.addItems(Generator.Category.RING.classes);

		ARTIFACTS.addItems(Generator.Category.ARTIFACT.classes);

		// Shattered trinkets and miscellaneous equipment are intentionally not catalogued.



		POTIONS.addItems(Generator.Category.POTION.classes);

		SCROLLS.addItems(Generator.Category.SCROLL.classes);

		SEEDS.addItems(Generator.Category.SEED.classes);

		STONES.addItems(Generator.Category.STONE.classes);

		FOOD.addItems( Food.class, Pasty.class, MysteryMeat.class, ChargrilledMeat.class,
				StewedMeat.class, FrozenCarpaccio.class, SmallRation.class, Berry.class,
				SupplyRation.class, Blandfruit.class, PhantomMeat.class, MeatPie.class );

		EXOTIC_POTIONS.addItems(ExoticPotion.exoToReg.keySet().toArray(new Class[0]));

		EXOTIC_SCROLLS.addItems(ExoticScroll.exoToReg.keySet().toArray(new Class[0]));

		BOMBS.addItems( Bomb.class, FrostBomb.class, Firebomb.class, SmokeBomb.class, RegrowthBomb.class,
				WoollyBomb.class, Noisemaker.class, FlashBangBomb.class, HolyBomb.class, ArcaneBomb.class, ShrapnelBomb.class);

		TIPPED_DARTS.addItems(TippedDart.types.values().toArray(new Class[0]));

		BREWS_ELIXIRS.addItems( UnstableBrew.class, InfernalBrew.class, BlizzardBrew.class,
				ShockingBrew.class, CausticBrew.class, AquaBrew.class, ElixirOfHoneyedHealing.class,
				ElixirOfAquaticRejuvenation.class, ElixirOfArcaneArmor.class, ElixirOfDragonsBlood.class,
				ElixirOfIcyTouch.class, ElixirOfToxicEssence.class, ElixirOfMight.class, ElixirOfFeatherFall.class,
				WishPotion.class);

		SPELLS.addItems( UnstableSpell.class, WildEnergy.class, TelekineticGrab.class, PhaseShift.class,
				Alchemize.class, CurseInfusion.class, MagicalInfusion.class, Recycle.class,
				ReclaimTrap.class, SummonElemental.class, BeaconOfReturning.class);

		MISC_CONSUMABLES.addItems( Gold.class, EnergyCrystal.class, Dewdrop.class,
				IronKey.class, GoldenKey.class, CrystalKey.class, WornKey.class,
				Stylus.class, Torch.class, Honeypot.class, Ankh.class,
				CorpseDust.class, Embers.class, CeremonialCandle.class, DarkGold.class, EscapeCrystal.class, VaultBeacon.class, DwarfToken.class, ImpStatue.class,
				GooBlob.class, TengusMask.class, MetalShard.class, KingsCrown.class,
				LiquidMetal.class, ArcaneResin.class,
				SealShard.class, BrokenStaff.class, CloakScrap.class, BowFragment.class, BrokenHilt.class, TornPage.class);

	}

	public static ArrayList<Catalog> equipmentCatalogs = new ArrayList<>();
	static {
		equipmentCatalogs.add(MELEE_WEAPONS);
		equipmentCatalogs.add(ARMOR);
		equipmentCatalogs.add(THROWN_WEAPONS);
		equipmentCatalogs.add(WANDS);
		equipmentCatalogs.add(RINGS);
		equipmentCatalogs.add(ARTIFACTS);
	}

	public static ArrayList<Catalog> consumableCatalogs = new ArrayList<>();
	static {
		consumableCatalogs.add(POTIONS);
		consumableCatalogs.add(SCROLLS);
		consumableCatalogs.add(SEEDS);
		consumableCatalogs.add(STONES);
		consumableCatalogs.add(FOOD);
		consumableCatalogs.add(EXOTIC_POTIONS);
		consumableCatalogs.add(EXOTIC_SCROLLS);
		consumableCatalogs.add(BOMBS);
		consumableCatalogs.add(TIPPED_DARTS);
		consumableCatalogs.add(BREWS_ELIXIRS);
		consumableCatalogs.add(SPELLS);
		consumableCatalogs.add(MISC_CONSUMABLES);
	}
	
	public static boolean isSeen(Class<?> cls){
		for (Catalog cat : values()) {
			if (cat.seen.containsKey(cls)) {
				return cat.seen.get(cls);
			}
		}
		return false;
	}
	
	public static void setSeen(Class<?> cls){
		for (Catalog cat : values()) {
			if (cat.seen.containsKey(cls) && !cat.seen.get(cls)) {
				cat.seen.put(cls, true);
				Journal.saveNeeded = true;
			}
		}
		Badges.validateCatalogBadges();
	}

	public static int useCount(Class<?> cls){
		for (Catalog cat : values()) {
			if (cat.useCount.containsKey(cls)) {
				return cat.useCount.get(cls);
			}
		}
		return 0;
	}

	public static void countUse(Class<?> cls){
		countUses(cls, 1);
	}

	public static void countUses(Class<?> cls, int uses){
		for (Catalog cat : values()) {
			if (cat.useCount.containsKey(cls) && cat.useCount.get(cls) != Integer.MAX_VALUE) {
				cat.useCount.put(cls, cat.useCount.get(cls)+uses);
				if (cat.useCount.get(cls) < -1_000_000_000){ //to catch cases of overflow
					cat.useCount.put(cls, Integer.MAX_VALUE);
				}
				Journal.saveNeeded = true;
			}
		}
	}

	private static final String CATALOG_CLASSES = "catalog_classes";
	private static final String CATALOG_SEEN    = "catalog_seen";
	private static final String CATALOG_USES    = "catalog_uses";
	
	public static void store( Bundle bundle ){

		ArrayList<Class<?>> classes = new ArrayList<>();
		ArrayList<Boolean> seen = new ArrayList<>();
		ArrayList<Integer> uses = new ArrayList<>();
		
		for (Catalog cat : values()) {
			for (Class<?> item : cat.items()) {
				if (cat.seen.get(item) || cat.useCount.get(item) > 0){
					classes.add(item);
					seen.add(cat.seen.get(item));
					uses.add(cat.useCount.get(item));
				}
			}
		}

		Class<?>[] storeCls = new Class[classes.size()];
		boolean[] storeSeen = new boolean[seen.size()];
		int[] storeUses = new int[uses.size()];

		for (int i = 0; i < storeCls.length; i++){
			storeCls[i] = classes.get(i);
			storeSeen[i] = seen.get(i);
			storeUses[i] = uses.get(i);
		}
		
		bundle.put( CATALOG_CLASSES, storeCls );
		bundle.put( CATALOG_SEEN, storeSeen );
		bundle.put( CATALOG_USES, storeUses );
		
	}
	
	public static void restore( Bundle bundle ){

		if (bundle.contains(CATALOG_CLASSES)){
			Class<?>[] classes = bundle.getClassArray(CATALOG_CLASSES);
			boolean[] seen = bundle.getBooleanArray(CATALOG_SEEN);
			int[] uses = bundle.getIntArray(CATALOG_USES);

			for (int i = 0; i < classes.length; i++){
				for (Catalog cat : values()) {
					if (cat.seen.containsKey(classes[i])) {
						cat.seen.put(classes[i], seen[i]);
						cat.useCount.put(classes[i], uses[i]);
					}
				}

			}
		}

	}
	
}
