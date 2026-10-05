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

package pd.items.consum.potions;

import pd.atlas.IconEntry;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.atlas.items.ConsumPotionSeedBasicPotionDict;

import pd.Assets;
import pd.Challenges;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Fire;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Ooze;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.effects.Splash;
import pd.items.Generator;
import pd.items.Item;
import pd.items.ItemStatusHandler;
import pd.items.Recipe;
import pd.items.consum.potions.brews.AquaBrew;
import pd.items.consum.potions.brews.Brew;
import pd.items.consum.potions.elixirs.ElixirOfHoneyedHealing;
import pd.items.consum.potions.elixirs.ElixirOfMight;
import pd.items.consum.potions.exotic.ExoticPotion;
import pd.items.consum.potions.exotic.PotionOfCleansing;
import pd.items.consum.potions.exotic.PotionOfCorrosiveGas;
import pd.items.consum.potions.exotic.PotionOfShroudingFog;
import pd.items.consum.potions.exotic.PotionOfSnapFreeze;
import pd.items.consum.potions.exotic.PotionOfStormClouds;
import pd.items.equipment.weapon.missiles.arrows.*;
import pd.journal.Catalog;
import pd.levels.Terrain;
import pd.messages.Messages;
import pd.plants.Blindweed;
import pd.plants.Earthroot;
import pd.plants.Fadeleaf;
import pd.plants.Firebloom;
import pd.plants.Icecap;
import pd.plants.Dreamfoil;
import pd.plants.Mageroyal;
import pd.plants.NutPlant;
import pd.plants.Plant;
import pd.plants.ReNepenth;
import pd.plants.Rotberry;
import pd.plants.Seedpod;
import pd.plants.SiOtwoFlower;
import pd.plants.Sorrowmoss;
import pd.plants.StarEater;
import pd.plants.Starflower;
import pd.plants.Stormvine;
import pd.plants.Sungrass;
import pd.plants.Swiftthistle;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import pd.utils.GLog;
import pd.windows.WndBag;
import pd.windows.WndOptions;
import pd.windows.WndUseItem;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import pd.messages.InlineText;

public class Potion extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Potion.class)
			.t("ac_drink", "饮用")
			.t("turquoise", "青绿药剂")
			.t("crimson", "猩红药剂")
			.t("azure", "湛蓝药剂")
			.t("jade", "碧绿药剂")
			.t("golden", "金黄药剂")
			.t("magenta", "品红药剂")
			.t("charcoal", "煤黑药剂")
			.t("ivory", "乳白药剂")
			.t("amber", "琥珀药剂")
			.t("bistre", "深褐药剂")
			.t("indigo", "靛紫药剂")
			.t("silver", "银灰药剂")
			.t("unknown_desc", "瓶子里装着些不断打旋的彩色液体。谁知道饮用或投掷它时会有什么效果呢？")
			.t("harmful", "有害药剂！")
			.t("beneficial", "有益药剂")
			.t("yes", "是的，我知道我在做什么")
			.t("no", "不，我改变主意了")
			.t("sure_drink", "你确定要饮用它吗？大多数情况下你该把它扔向敌人的。")
			.t("sure_throw", "你确定要投掷它吗？大多数情况下把它喝下去会更有用。")
			.t("shatter", "药瓶碎裂开，溅出了无害的液体。")
			.t("discover_hint", "你可在地牢中概率找到该物品，或通过炼金合成该物品。")
			.t("$placeholder.name", "药剂")
			.t("$seedtopotion.name", "随机药剂");
	}




	public static final String AC_DRINK = "DRINK";
	
	//used internally for potions that can be drunk or thrown
	public static final String AC_CHOOSE = "CHOOSE";

	private static final float TIME_TO_DRINK = 1f;

	private static final LinkedHashMap<String, IconEntry> colors = new LinkedHashMap<String, IconEntry>() {
		{
			put("crimson",ConsumPotionSeedBasicPotionDict.POTION_CRIMSON_0);
			put("amber",ConsumPotionSeedBasicPotionDict.POTION_HEALING_0);
			put("golden",ConsumPotionSeedBasicPotionDict.POTION_MINDVIS_0);
			put("jade",ConsumPotionSeedBasicPotionDict.POTION_JADE_0);
			put("turquoise",ConsumPotionSeedBasicPotionDict.POTION_TURQUOISE_0);
			put("azure",ConsumPotionSeedBasicPotionDict.POTION_AZURE_0);
			put("indigo",ConsumPotionSeedBasicPotionDict.POTION_INDIGO_0);
			put("magenta",ConsumPotionSeedBasicPotionDict.POTION_INVIS_0);
			put("bistre",ConsumPotionSeedBasicPotionDict.POTION_BISTRE_0);
			put("charcoal",ConsumPotionSeedBasicPotionDict.POTION_CHARCOAL_0);
			put("silver",ConsumPotionSeedBasicPotionDict.POTION_SILVER_0);
			put("ivory",ConsumPotionSeedBasicPotionDict.POTION_EXP_0);
		}
	};

	protected static final HashSet<Class<?extends Potion>> mustThrowPots = new HashSet<>();
	static{
		mustThrowPots.add(PotionOfToxicGas.class);
		mustThrowPots.add(PotionOfLiquidFlame.class);
		mustThrowPots.add(PotionOfParalyticGas.class);
		mustThrowPots.add(PotionOfFrost.class);
		
		//exotic
		mustThrowPots.add(PotionOfCorrosiveGas.class);
		mustThrowPots.add(PotionOfSnapFreeze.class);
		mustThrowPots.add(PotionOfShroudingFog.class);
		mustThrowPots.add(PotionOfStormClouds.class);
		
		//also all brews except unstable, hardcoded
	}
	
	protected static final HashSet<Class<?extends Potion>> canThrowPots = new HashSet<>();
	static{
		canThrowPots.add(PotionOfPurity.class);
		canThrowPots.add(PotionOfLevitation.class);
		
		//exotic
		canThrowPots.add(PotionOfCleansing.class);
		
		//elixirs
		canThrowPots.add(ElixirOfHoneyedHealing.class);
	}
	
	protected static ItemStatusHandler<Potion> handler;
	
	protected String color;

	//affects how strongly on-potion talents trigger from this potion
	protected float talentFactor = 1;
	//the chance (0-1) of whether on-potion talents trigger from this potion
	protected float talentChance = 1;
	
	{
		stackable = true;
		defaultAction = AC_DRINK;
	}
	
	@SuppressWarnings("unchecked")
	public static void initColors() {
		handler = new ItemStatusHandler<>( (Class<? extends Potion>[])Generator.Category.POTION.classes, colors );
	}

	public static void clearColors() {
		handler = null;
	}
	
	public static void save( Bundle bundle ) {
		handler.save( bundle );
	}

	public static void saveSelectively( Bundle bundle, ArrayList<Item> items ) {
		ArrayList<Class<?extends Item>> classes = new ArrayList<>();
		for (Item i : items){
			if (i instanceof ExoticPotion){
				if (!classes.contains(ExoticPotion.exoToReg.get(i.getClass()))){
					classes.add(ExoticPotion.exoToReg.get(i.getClass()));
				}
			} else if (i instanceof Potion){
				if (!classes.contains(i.getClass())){
					classes.add(i.getClass());
				}
			}
		}
		handler.saveClassesSelectively( bundle, classes );
	}
	
	@SuppressWarnings("unchecked")
	public static void restore( Bundle bundle ) {
		handler = new ItemStatusHandler<>( (Class<? extends Potion>[])Generator.Category.POTION.classes, colors, bundle );
	}
	
	public Potion() {
		super();
		reset();
	}
	
	//anonymous potions are always IDed, do not affect ID status,
	//and their sprite is replaced by a placeholder if they are not known,
	//useful for items that appear in UIs, or which are only spawned for their effects
	protected boolean anonymous = false;
	public void anonymize(){
		if (!isKnown()) image = SpecificPlaceHolderDict.POTION_HOLDER_0;
		anonymous = true;
	}

	@Override
	public void reset(){
		super.reset();
		if (handler != null && handler.contains(this)) {
			image = handler.image(this);
			color = handler.label(this);
		} else {
			image = ConsumPotionSeedBasicPotionDict.POTION_CRIMSON_0;
			color = "crimson";
		}
	}

	@Override
	public String defaultAction() {
		if (isKnown() && mustThrowPots.contains(this.getClass())) {
			return AC_THROW;
		} else if (isKnown() &&canThrowPots.contains(this.getClass())){
			return AC_CHOOSE;
		} else {
			return AC_DRINK;
		}
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_DRINK );
		return actions;
	}
	
	@Override
	public void execute( final Hero hero, String action ) {

		super.execute( hero, action );
		
		if (action.equals( AC_CHOOSE )){
			
			GameScene.show(new WndUseItem(null, this) );
			
		} else if (action.equals( AC_DRINK )) {
			
			if (isKnown() && mustThrowPots.contains(getClass())) {
				
					GameScene.show(
						new WndOptions(new ItemSprite(this),
								Messages.get(Potion.class, "harmful"),
								Messages.get(Potion.class, "sure_drink"),
								Messages.get(Potion.class, "yes"), Messages.get(Potion.class, "no") ) {
							@Override
							protected void onSelect(int index) {
								if (index == 0) {
									drink( hero );
								}
							}
						}
					);
					
				} else {
					drink( hero );
				}
			
		}
	}
	
	@Override
	public void doThrow( final Hero hero ) {

		if (isKnown()
				&& !mustThrowPots.contains(this.getClass())
				&& !canThrowPots.contains(this.getClass())) {
		
			GameScene.show(
				new WndOptions(new ItemSprite(this),
						Messages.get(Potion.class, "beneficial"),
						Messages.get(Potion.class, "sure_throw"),
						Messages.get(Potion.class, "yes"), Messages.get(Potion.class, "no") ) {
					@Override
					protected void onSelect(int index) {
						if (index == 0) {
							Potion.super.doThrow( hero );
						}
					}
				}
			);
			
		} else {
			super.doThrow( hero );
		}
	}
	
	protected void drink( Hero hero ) {
		
		detach( hero.belongings.backpack );
		
		hero.spend( timeToDrink() );
		hero.busy();
		apply( hero );
		
		Sample.INSTANCE.play( Assets.Sounds.DRINK );
		
		hero.sprite.operate( hero.pos );

		if (!anonymous) {
			Catalog.countUse(getClass());
			if (Random.Float() < talentChance) {
				Talent.onPotionUsed(curUser, curUser.pos, talentFactor);
			}
		}
	}

	protected float timeToDrink() {
		return Dungeon.isChallenged(Challenges.ITEM_PHOBIA) ? 5f : TIME_TO_DRINK;
	}
	
	@Override
	protected void onThrow( int cell ) {
		if (Dungeon.level.map[cell] == Terrain.WELL || Dungeon.level.pit[cell]) {
			
			super.onThrow( cell );
			
		} else  {

			//aqua brew and storm clouds specifically don't press cells, so they can disarm traps
			if (!(this instanceof AquaBrew) && !(this instanceof PotionOfStormClouds)){
				Dungeon.level.pressCell( cell );
			}
			shatter( cell );

			if (!anonymous && (mustThrowPots.contains(getClass()) || canThrowPots.contains(getClass()) || this instanceof Brew)) {
				Catalog.countUse(getClass());
				if (Random.Float() < talentChance) {
					Talent.onPotionUsed(curUser, cell, talentFactor);
				}
			}
			
		}
	}
	
	public void apply( Hero hero ) {
		shatter( hero.pos );
	}
	
	public void shatter( int cell ) {
		splash( cell );
		if (Dungeon.level.heroFOV[cell]) {
			GLog.i( Messages.get(Potion.class, "shatter") );
			Sample.INSTANCE.play( Assets.Sounds.SHATTER );
		}
	}

	@Override
	public void cast( final Hero user, int dst ) {
			super.cast(user, dst);
	}
	
	public boolean isKnown() {
		return anonymous || (handler != null && handler.isKnown( this ));
	}
	
	public void setKnown() {
		if (!anonymous) {
			if (!isKnown()) {
				handler.know(this);
				updateQuickslot();
			}
			
			if (Dungeon.hero.isAlive()) {
				Catalog.setSeen(getClass());
				Statistics.itemTypesDiscovered.add(getClass());
			}
		}
	}
	
	@Override
	public Item identify( boolean byHero ) {
		super.identify(byHero);

		if (!isKnown()) {
			setKnown();
		}
		return this;
	}
	
	@Override
	public String name() {
		return isKnown() ? super.name() : Messages.get(this, color);
	}

	@Override
	public String info() {
		//skip custom notes if anonymized and un-Ided
		return (anonymous && (handler == null || !handler.isKnown( this ))) ? desc() : super.info();
	}

	@Override
	public String desc() {
		return isKnown() ? super.desc() : Messages.get(this, "unknown_desc");
	}
	
	@Override
	public boolean isIdentified() {
		return isKnown();
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	public static HashSet<Class<? extends Potion>> getKnown() {
		return handler.known();
	}
	
	public static HashSet<Class<? extends Potion>> getUnknown() {
		return handler.unknown();
	}
	
	public static boolean allKnown() {
		return handler != null && handler.known().size() == Generator.Category.POTION.classes.length;
	}
	
	protected int splashColor(){
		return anonymous ? 0x00AAFF : ItemSprite.pick( image, 5, 9 );
	}
	
	protected void splash( int cell ) {
		Fire fire = (Fire)Dungeon.level.blobs.get( Fire.class );
		if (fire != null) {
			fire.clear(cell);
		}

		Char ch = Actor.findChar(cell);
		if (ch != null && ch.alignment == Char.Alignment.ALLY) {
			Buff.detach(ch, Burning.class);
			Buff.detach(ch, Ooze.class);
		}

		if (Dungeon.level.heroFOV[cell]) {
			if (ch != null) {
				Splash.at(ch.sprite.center(), splashColor(), 5);
			} else {
				Splash.at(cell, splashColor(), 5);
			}
		}
	}
	
	@Override
	public int value() {
		return 30 * quantity;
	}

	@Override
	public int energyVal() {
		return 6 * quantity;
	}

	public static class PlaceHolder extends Potion {
		
		{
			image = SpecificPlaceHolderDict.POTION_HOLDER_0;
		}
		
		@Override
		public boolean isSimilar(Item item) {
			return ExoticPotion.regToExo.containsKey(item.getClass())
					|| ExoticPotion.regToExo.containsValue(item.getClass());
		}
		
		@Override
		public String info() {
			return "";
		}
	}
	
	public static class SeedToPotion extends Recipe {
		
		public static HashMap<Class<?extends Plant.Seed>, Class<?extends Potion>> types = new HashMap<>();
		static {
			types.put(Blindweed.Seed.class,     PotionOfInvisibility.class);
			types.put(Dreamfoil.Seed.class,     PotionOfPurity.class);
			types.put(Mageroyal.Seed.class,     PotionOfPurity.class);//兼容旧存档里的魔皇草之种
			types.put(Earthroot.Seed.class,     PotionOfParalyticGas.class);
			types.put(Fadeleaf.Seed.class,      PotionOfMindVision.class);
			types.put(Firebloom.Seed.class,     PotionOfLiquidFlame.class);
			types.put(Icecap.Seed.class,        PotionOfFrost.class);
			types.put(Rotberry.Seed.class,      PotionOfStrength.class);
			types.put(Sorrowmoss.Seed.class,    PotionOfToxicGas.class);
			types.put(Starflower.Seed.class,    PotionOfExperience.class);
			types.put(Stormvine.Seed.class,     PotionOfLevitation.class);
			types.put(Sungrass.Seed.class,      PotionOfHealing.class);
			types.put(Swiftthistle.Seed.class,  PotionOfHaste.class);
			types.put(NutPlant.Seed.class,      PotionOfHealing.class);//原恢复药水，已并入治疗药剂
			types.put(ReNepenth.Seed.class,     ElixirOfMight.class);//原根骨药水，已并入根骨秘药
			types.put(Seedpod.Seed.class,       PotionOfMixing.class);
			types.put(SiOtwoFlower.Seed.class,  PotionOfShield.class);
			types.put(StarEater.Seed.class,     PotionOfHealing.class);//原生命药水，已并入治疗药剂
		}
		
		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			if (ingredients.size() != 3) {
				return false;
			}
			
			for (Item ingredient : ingredients){
				if (!(ingredient instanceof Plant.Seed
						&& ingredient.quantity() >= 1
						&& types.containsKey(ingredient.getClass()))){
					return false;
				}
			}
			return true;
		}
		
		@Override
		public int cost(ArrayList<Item> ingredients) {
			return 0;
		}
		
		@Override
		public Item brew(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;
			
			for (Item ingredient : ingredients){
				ingredient.quantity(ingredient.quantity() - 1);
			}
			
			ArrayList<Class<?extends Plant.Seed>> seeds = new ArrayList<>();
			for (Item i : ingredients) {
				if (!seeds.contains(i.getClass())) {
					seeds.add((Class<? extends Plant.Seed>) i.getClass());
				}
			}
			
			Potion result;
			
			if ( (seeds.size() == 2 && Random.Int(4) == 0)
					|| (seeds.size() == 3 && Random.Int(2) == 0)) {
				
				result = (Potion) Generator.randomUsingDefaults( Generator.Category.POTION );
				
			} else {
				result = Reflection.newInstance(types.get(Random.element(ingredients).getClass()));
				
			}
			
			if (seeds.size() == 1){
				result.identify();
			}

			while (result instanceof PotionOfHealing
					&& Random.Int(10) < Dungeon.LimitedDrops.COOKING_HP.count) {

				result = (Potion) Generator.randomUsingDefaults(Generator.Category.POTION);
			}
			
			if (result instanceof PotionOfHealing) {
				Dungeon.LimitedDrops.COOKING_HP.count++;
			}
			
			return result;
		}
		
		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			return new WndBag.Placeholder(SpecificPlaceHolderDict.POTION_HOLDER_0){

				@Override
				public String name() {
					return Messages.get(Potion.SeedToPotion.class, "name");
				}
				
				@Override
				public String info() {
					return "";
				}
			};
		}
	}

	/**
	 * SPSEXPD: 果实酿造配方（取代原先的三种子酿造）。
	 * 4 个果实（可混搭）或 1 个大型果实 + 1 个普通果实均可酿出一瓶药剂。
	 * 出货概率沿用种子酿造的机制：投入的果实种类越多，越容易开出随机药剂；
	 * 只投入单一品种时，直接得到对应的药剂（并自动鉴定）。
	 */
	public static class FruitToPotion extends Recipe {

		public static final int COUNT = 4;

		public static final LinkedHashMap<Class<? extends Item>, Class<? extends Potion>> types = new LinkedHashMap<>();

		static {
			//SPSEXPD: inline Chinese text (generated from messages/items/zh)
			InlineText.of(FruitToPotion.class)
				.t("name", "果实酿造");

			types.put(NutFruit.class,        PotionOfShield.class);
			types.put(StarEaterFruit.class,  PotionOfAcid.class);
			types.put(TransmuteFruit.class,  PotionOfTransmute.class);
			types.put(GlassFruit.class,      PotionOfGlass.class);
			types.put(DewFruit.class,        PotionOfEnergy.class);
			types.put(SeedFruit.class,       PotionOfConfusion.class);
			types.put(FlavorlessFruit.class, PotionOfSatiety.class);
			types.put(FreshFruit.class,      PotionOfMixing.class);
			types.put(HealFruit.class,       PotionOfHealing.class);
			types.put(FireFruit.class,       PotionOfLiquidFlame.class);
			types.put(IceFruit.class,        PotionOfFrost.class);
			types.put(ToxicFruit.class,      PotionOfToxicGas.class);
			types.put(BlindFruit.class,      PotionOfInvisibility.class);
			types.put(RootFruit.class,       PotionOfParalyticGas.class);
			types.put(StarFruit.class,       PotionOfExperience.class);
			types.put(SwiftFruit.class,      PotionOfHaste.class);
			types.put(RotFruit.class,        PotionOfStrength.class);
			types.put(ShockFruit.class,      PotionOfLevitation.class);
			types.put(SmokeFruit.class,      PotionOfMindVision.class);
			types.put(CharmFruit.class,      PotionOfPurity.class);
		}

		/** 沿继承链查找果实对应的药剂（大型果实是普通果实的子类）。 */
		public static Class<? extends Potion> potionFor(Item fruit) {
			for (Class<?> type = fruit.getClass(); type != null; type = type.getSuperclass()) {
				Class<? extends Potion> potion = types.get(type);
				if (potion != null) return potion;
			}
			return null;
		}

		/** 自身不在映射表中、却能沿父类匹配到药剂，即为大型果实。 */
		public static boolean isLarge(Item fruit) {
			return potionFor(fruit) != null && !types.containsKey(fruit.getClass());
		}

		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			if (ingredients.size() == COUNT) {
				for (Item ingredient : ingredients) if (potionFor(ingredient) == null) return false;
				return true;
			}
			if (ingredients.size() == 2) {
				int large = 0;
				for (Item ingredient : ingredients) {
					if (potionFor(ingredient) == null) return false;
					if (isLarge(ingredient)) large++;
				}
				return large == 1;
			}
			return false;
		}

		@Override
		public int cost(ArrayList<Item> ingredients) {
			return 0;
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;

			for (Item ingredient : ingredients) {
				ingredient.quantity(ingredient.quantity() - 1);
			}

			ArrayList<Class<? extends Potion>> kinds = new ArrayList<>();
			for (Item ingredient : ingredients) {
				Class<? extends Potion> potion = potionFor(ingredient);
				if (!kinds.contains(potion)) kinds.add(potion);
			}

			Potion result;
			if ((kinds.size() == 2 && Random.Int(4) == 0)
					|| (kinds.size() >= 3 && Random.Int(2) == 0)) {
				result = (Potion) Generator.randomUsingDefaults(Generator.Category.POTION);
			} else {
				result = Reflection.newInstance(potionFor(Random.element(ingredients)));
			}

			if (kinds.size() == 1 && Dungeon.hero != null) {
				result.identify();
			}

			while (result instanceof PotionOfHealing
					&& Random.Int(10) < Dungeon.LimitedDrops.COOKING_HP.count) {
				result = (Potion) Generator.randomUsingDefaults(Generator.Category.POTION);
			}

			if (result instanceof PotionOfHealing) {
				Dungeon.LimitedDrops.COOKING_HP.count++;
			}

			return result;
		}

		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			return new WndBag.Placeholder(SpecificPlaceHolderDict.POTION_HOLDER_0) {

				@Override
				public String name() {
					return Messages.get(Potion.FruitToPotion.class, "name");
				}

				@Override
				public String info() {
					return "";
				}
			};
		}
	}
}
