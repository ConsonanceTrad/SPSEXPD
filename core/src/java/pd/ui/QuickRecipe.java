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

package pd.ui;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.atlas.items.SpecificPlaceHoldeFruitDict;

import pd.Dungeon;
import pd.ShatteredPixelDungeon;
import pd.items.ArcaneResin;
import pd.items.Garbage;
import pd.items.Generator;
import pd.items.IronMakerRecipes;
import pd.items.Item;
import pd.items.StoneOre;
import pd.items.LiquidMetal;
import pd.items.Recipe;
import pd.items.SpsAlchemyRecipes;
import pd.items.equipment.bombs.Bomb;
import pd.items.consum.brewed.Brewed;
import pd.items.consum.food.Blandfruit;
import pd.items.consum.food.Food;
import pd.items.consum.food.MeatPie;
import pd.items.consum.food.MysteryMeat;
import pd.items.consum.food.Pasty;
import pd.items.consum.food.StewedMeat;
import pd.items.consum.potions.Potion;
import pd.items.consum.potions.PotionOfConfusion;
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
import pd.items.consum.scrolls.Scroll;
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
import pd.items.consum.stones.Runestone;
import pd.items.consum.food.WaterItem;
import pd.items.consum.food.processed.AetherLiquid;
import pd.items.consum.food.processed.CrystalShard;
import pd.items.consum.food.processed.HighEnergySpore;
import pd.items.consum.food.processed.WishPetal;
import pd.items.consum.medicine.Timepill2;
import pd.items.equipment.weapon.spammo.FireAmmo;
import pd.items.equipment.weapon.spammo.HeavyAmmo;
import pd.plants.Firebloom;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.items.equipment.weapon.missiles.arrows.*;
import pd.messages.Messages;
import pd.plants.Plant;
import pd.scenes.AlchemyScene;
import pd.scenes.PixelScene;
import pd.windows.WndBag;
import pd.windows.WndInfoItem;
import render.noosa.BitmapText;
import render.noosa.Group;
import render.noosa.Image;
import render.noosa.PointerArea;
import render.noosa.ui.Component;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.Arrays;

public class QuickRecipe extends Component {
	
	private ArrayList<Item> ingredients;
	
	private ArrayList<ItemSlot> inputs;
	private QuickRecipe.arrow arrow;
	private ItemSlot output;
	
	public QuickRecipe(Recipe.SimpleRecipe r){
		this(r, r.getIngredients(), r.sampleOutput(null));
	}
	
	public QuickRecipe(Recipe r, ArrayList<Item> inputs, final Item output) {
		
		ingredients = inputs;
		int cost = r.cost(inputs);
		boolean hasInputs = true;
		this.inputs = new ArrayList<>();
		for (final Item in : inputs) {
			anonymize(in);
			ItemSlot curr;
			curr = new ItemSlot(in) {
				{
					hotArea.blockLevel = PointerArea.NEVER_BLOCK;
				}

				@Override
				protected void onClick() {
					ShatteredPixelDungeon.scene().addToFront(new WndInfoItem(in));
				}
			};

			int quantity = 0;
			if (Dungeon.hero != null) {
				ArrayList<Item> similar = Dungeon.hero.belongings.getAllSimilar(in);
				for (Item sim : similar) {
					//if we are looking for a specific item, it must be IDed
					if (sim.getClass() != in.getClass() || sim.isIdentified())
						quantity += sim.quantity();
				}
				if (quantity < in.quantity()) {
					curr.sprite.alpha(0.3f);
					hasInputs = false;
				}
			} else {
				hasInputs = false;
			}

			curr.showExtraInfo(false);
			add(curr);
			this.inputs.add(curr);
		}
		
		if (cost > 0) {
			arrow = new arrow(Icons.get(Icons.ARROW), cost);
			arrow.hardlightText(0x44CCFF);
		} else {
			arrow = new arrow(Icons.get(Icons.ARROW));
		}
		if (hasInputs) {
			arrow.icon.tint(1, 1, 0, 1);
			if (!(ShatteredPixelDungeon.scene() instanceof AlchemyScene)) {
				arrow.enable(false);
			}
		} else {
			arrow.icon.color(0, 0, 0);
			arrow.enable(false);
		}
		add(arrow);
		
		anonymize(output);
		this.output = new ItemSlot(output){
			@Override
			protected void onClick() {
				ShatteredPixelDungeon.scene().addToFront(new WndInfoItem(output));
			}
		};
		if (Dungeon.hero != null && !hasInputs){
			this.output.sprite.alpha(0.3f);
		}
		this.output.showExtraInfo(false);
		add(this.output);
		
		layout();
	}
	
	@Override
	protected void layout() {
		
		height = 16;
		width = 0;

		int padding = inputs.size() == 1 ? 8 : 0;

		for (ItemSlot item : inputs){
			item.setRect(x + width + padding, y, 16, 16);
			width += 16 + padding;
		}
		
		arrow.setRect(x + width, y, 14, 16);
		width += 14;
		
		output.setRect(x + width, y, 16, 16);
		width += 16;

		width += padding;
	}
	
	//used to ensure that un-IDed items are not spoiled
	private void anonymize(Item item){
		if (item instanceof Potion){
			((Potion) item).anonymize();
		} else if (item instanceof Scroll){
			((Scroll) item).anonymize();
		}
	}
	
	public class arrow extends IconButton {
		
		BitmapText text;
		
		public arrow(){
			super();
		}
		
		public arrow( Image icon ){
			super( icon );
		}
		
		public arrow( Image icon, int count ){
			super( icon );
			hotArea.blockLevel = PointerArea.NEVER_BLOCK;

			text = new BitmapText( Integer.toString(count), PixelScene.pixelFont);
			text.measure();
			add(text);
		}
		
		@Override
		protected void layout() {
			super.layout();
			
			if (text != null){
				text.x = x;
				text.y = y;
				PixelScene.align(text);
			}
		}
		
		@Override
		protected void onPointerUp() {
			icon.brightness(1f);
		}

		@Override
		protected void onClick() {
			super.onClick();
			
			//find the window this is inside of and close it
			Group parent = this.parent;
			while (parent != null){
				if (parent instanceof Window){
					((Window) parent).hide();
					break;
				} else {
					parent = parent.parent;
				}
			}
			
			((AlchemyScene)ShatteredPixelDungeon.scene()).populate(ingredients, Dungeon.hero.belongings);
		}
		
		public void hardlightText(int color ){
			if (text != null) text.hardlight(color);
		}
	}
	
	//gets recipes for a particular alchemy guide page
	//a null entry indicates a break in section
	public static ArrayList<QuickRecipe> getRecipes( int pageIdx ){
		ArrayList<QuickRecipe> result = new ArrayList<>();
		switch (pageIdx){
			case 0: default: {
				//SPSEXPD: 药剂酿造已由种子改为果实（展示使用果实/大型果实占位图标）
				WndBag.Placeholder brewed = new WndBag.Placeholder(SpecificPlaceHolderDict.POTION_HOLDER_0) {
					@Override
					public String name() {
						return Messages.get(Potion.FruitToPotion.class, "name");
					}

					@Override
					public String info() {
						return "";
					}
				};
				Item fruitHolder = new Item() {
					{
						image = SpecificPlaceHoldeFruitDict.FRUIT_HOLDER_0;
					}
					@Override
					public String name() { return ""; }
					@Override
					public String info() { return ""; }
				};
				Item largeFruitHolder = new Item() {
					{
						image = SpecificPlaceHoldeFruitDict.LARGE_FRUIT_HOLDER_0;
					}
					@Override
					public String name() { return ""; }
					@Override
					public String info() { return ""; }
				};
				result.add(new QuickRecipe( new Potion.FruitToPotion(),
						new ArrayList<>(Arrays.asList(fruitHolder, fruitHolder, fruitHolder, fruitHolder)),
						brewed));
				result.add(null);
				result.add(new QuickRecipe( new Potion.FruitToPotion(),
						new ArrayList<>(Arrays.asList(largeFruitHolder, fruitHolder)),
						brewed));
				return result;
			}
			case 1:
				Recipe r = new Scroll.ScrollToStone();
				for (Class<?> cls : Generator.Category.SCROLL.classes){
					Scroll scroll = (Scroll) Reflection.newInstance(cls);
					if (!scroll.isKnown()) scroll.anonymize();
					ArrayList<Item> in = new ArrayList<Item>(Arrays.asList(scroll));
					result.add(new QuickRecipe( r, in, r.sampleOutput(in)));
				}
				return result;
			case 2:
				result.add(new QuickRecipe( new StewedMeat.oneMeat() ));
				result.add(new QuickRecipe( new StewedMeat.twoMeat() ));
				result.add(new QuickRecipe( new StewedMeat.threeMeat() ));
				result.add(null);
				result.add(new QuickRecipe( new MeatPie.Recipe(),
						new ArrayList<Item>(Arrays.asList(new Pasty(), new Food(), new MysteryMeat.PlaceHolder())),
						new MeatPie()));
				return result;
			case 3:
				r = new ExoticPotion.PotionToExotic();
				for (Class<?> cls : Generator.Category.POTION.classes){
					Potion pot = (Potion) Reflection.newInstance(cls);
					ArrayList<Item> in = new ArrayList<>(Arrays.asList(pot));
					Item exoticPotion = r.sampleOutput(in);
					//SPSXPD: 没有合剂对应的药剂不显示（避免预览为空导致空条目/崩溃）
					if (exoticPotion == null) continue;
					result.add(new QuickRecipe( r, in, exoticPotion));
				}
				return result;
			case 4:
				r = new ExoticScroll.ScrollToExotic();
				for (Class<?> cls : Generator.Category.SCROLL.classes){
					Scroll scroll = (Scroll) Reflection.newInstance(cls);
					ArrayList<Item> in = new ArrayList<>(Arrays.asList(scroll));
					Item exoticScroll = r.sampleOutput(in);
					//SPSXPD: 没有合剂对应的卷轴（如测试卷轴）不显示，避免空条目/崩溃
					if (exoticScroll == null) continue;
					result.add(new QuickRecipe( r, in, exoticScroll));
				}
				return result;
			case 5:
				r = new Bomb.EnhanceBomb();
				int i = 0;
				for (Class<?> cls : Bomb.EnhanceBomb.validIngredients.keySet()){
					if (i == 2){
						result.add(null);
						i = 0;
					}
					Item item = (Item) Reflection.newInstance(cls);
					ArrayList<Item> in = new ArrayList<>(Arrays.asList(new Bomb(), item));
					result.add(new QuickRecipe( r, in, r.sampleOutput(in)));
					i++;
				}
				return result;
			case 6:
				result.add(new QuickRecipe( new LiquidMetal.Recipe(),
						new ArrayList<Item>(Arrays.asList(new MissileWeapon.PlaceHolder())),
						new LiquidMetal()));
				result.add(new QuickRecipe( new ArcaneResin.Recipe(),
						new ArrayList<Item>(Arrays.asList(new Wand.PlaceHolder())),
						new ArcaneResin()));
				result.add(null);
				//SPSEXPD: 原铁砧的锻造公式已整体并入炼金釜
				IronMakerRecipes forge = new IronMakerRecipes();
				result.add(new QuickRecipe( forge,
						new ArrayList<>(Arrays.asList(new StoneOre(), new StoneOre())),
						new HeavyAmmo()));
				result.add(new QuickRecipe( forge,
						new ArrayList<>(Arrays.asList(new StoneOre(), new Firebloom.Seed())),
						new FireAmmo()));
				result.add(new QuickRecipe( forge,
						new ArrayList<>(Arrays.asList(new StoneOre(), new StoneOre(), new StoneOre(), new StoneOre(), new WaterItem())),
						new Timepill2()));
				result.add(new QuickRecipe( forge,
						new ArrayList<>(Arrays.asList(new Garbage(), new Garbage(), new Garbage(), new Garbage(), new Garbage())),
						new Garbage(3)));
				return result;
			case 7:
				result.add(new QuickRecipe(new UnstableBrew.Recipe(), new ArrayList<>(Arrays.asList(new Potion.PlaceHolder(), new  Plant.Seed.PlaceHolder())), new UnstableBrew()));
				result.add(new QuickRecipe(new CausticBrew.Recipe()));
				result.add(new QuickRecipe(new BlizzardBrew.Recipe()));
				result.add(new QuickRecipe(new ShockingBrew.Recipe()));
				result.add(new QuickRecipe(new InfernalBrew.Recipe()));
				result.add(new QuickRecipe(new AquaBrew.Recipe()));
				result.add(null);
				result.add(null);
				result.add(new QuickRecipe(new ElixirOfHoneyedHealing.Recipe()));
				result.add(new QuickRecipe(new ElixirOfAquaticRejuvenation.Recipe()));
				result.add(new QuickRecipe(new ElixirOfArcaneArmor.Recipe()));
				result.add(new QuickRecipe(new ElixirOfIcyTouch.Recipe()));
				result.add(new QuickRecipe(new ElixirOfToxicEssence.Recipe()));
				result.add(new QuickRecipe(new ElixirOfDragonsBlood.Recipe()));
				result.add(new QuickRecipe(new ElixirOfFeatherFall.Recipe()));
				result.add(new QuickRecipe(new ElixirOfMight.Recipe()));
				result.add(null);
				//SPSEXPD: 3 个同种大型果实 → 对应秘药
				Item largeFruit = new Item() {
					{
						image = SpecificPlaceHoldeFruitDict.LARGE_FRUIT_HOLDER_0;
					}
					@Override
					public String name() { return ""; }
					@Override
					public String info() { return ""; }
				};
				WndBag.Placeholder elixirHolder = new WndBag.Placeholder(SpecificPlaceHolderDict.ELIXIR_HOLDER_0) {
					@Override
					public String info() { return ""; }
				};
				result.add(new QuickRecipe(new pd.items.LargeFruitToElixir(),
						new ArrayList<>(Arrays.asList(largeFruit, largeFruit, largeFruit)),
						elixirHolder));
				result.add(null);
				//SPSEXPD: 许愿魔药——混乱药剂 + 许愿花瓣 + 水晶碎片 + 韵魔原液 + 高能孢子
				result.add(new QuickRecipe(SpsAlchemyRecipes.WISH_POTION,
						new ArrayList<>(Arrays.asList(new PotionOfConfusion(), new WishPetal(),
								new CrystalShard(), new AetherLiquid(), new HighEnergySpore())),
						new WishPotion()));
				return result;
			case 8:
				result.add(new QuickRecipe(new UnstableSpell.Recipe(), new ArrayList<>(Arrays.asList(new Scroll.PlaceHolder(), new  Runestone.PlaceHolder())), new UnstableSpell()));
				result.add(new QuickRecipe(new WildEnergy.Recipe()));
				result.add(new QuickRecipe(new TelekineticGrab.Recipe()));
				result.add(new QuickRecipe(new PhaseShift.Recipe()));
				if (!PixelScene.landscape()) result.add(null);
				result.add(null);
				result.add(new QuickRecipe(new Alchemize.Recipe(), new ArrayList<>(Arrays.asList(new Plant.Seed.PlaceHolder(), new Runestone.PlaceHolder())), new Alchemize().quantity(8)));
				result.add(new QuickRecipe(new CurseInfusion.Recipe()));
				result.add(new QuickRecipe(new MagicalInfusion.Recipe()));
				result.add(new QuickRecipe(new Recycle.Recipe()));
				if (!PixelScene.landscape()) result.add(null);
				result.add(null);
				result.add(new QuickRecipe(new ReclaimTrap.Recipe()));
				result.add(new QuickRecipe(new SummonElemental.Recipe()));
				result.add(new QuickRecipe(new BeaconOfReturning.Recipe()));
				return result;
		}
	}
	
}
