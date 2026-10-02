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

package pd.items.consum.scrolls;

import pd.atlas.IconEntry;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Challenges;
import pd.Dungeon;
import pd.ShatteredPixelDungeon;
import pd.Statistics;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.MagicImmune;
import pd.actors.buffs.Silent;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.items.Generator;
import pd.items.Item;
import pd.items.ItemStatusHandler;
import pd.items.Recipe;
import pd.items.equipment.artifacts.UnstableSpellbook;
import pd.items.consum.scrolls.exotic.ExoticScroll;
import pd.items.consum.scrolls.exotic.ScrollOfAntiMagic;
import pd.items.consum.stones.Runestone;
import pd.items.consum.stones.StoneOfAggression;
import pd.items.consum.stones.StoneOfAugmentation;
import pd.items.consum.stones.StoneOfBlast;
import pd.items.consum.stones.StoneOfBlink;
import pd.items.consum.stones.StoneOfClairvoyance;
import pd.items.consum.stones.StoneOfDeepSleep;
import pd.items.consum.stones.StoneOfDetectMagic;
import pd.items.consum.stones.StoneOfEnchantment;
import pd.items.consum.stones.StoneOfFear;
import pd.items.consum.stones.StoneOfFlock;
import pd.items.consum.stones.StoneOfIntuition;
import pd.items.consum.stones.StoneOfShock;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.scenes.AlchemyScene;
import pd.sprites.HeroSprite;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import pd.messages.InlineText;

public abstract class Scroll extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Scroll.class)
			.t("ac_read", "阅读")
			.t("kaunan", "KAUNAN卷轴")
			.t("sowilo", "SOWILO卷轴")
			.t("laguz", "LAGUZ卷轴")
			.t("yngvi", "YNGVI卷轴")
			.t("gyfu", "GYFU卷轴")
			.t("raido", "RAIDO卷轴")
			.t("isaz", "ISAZ卷轴")
			.t("mannaz", "MANNAZ卷轴")
			.t("naudiz", "NAUDIZ卷轴")
			.t("berkanan", "BERKANAN卷轴")
			.t("ncosrane", "NCOSRANE卷轴")
			.t("odal", "ODAL卷轴")
			.t("tiwaz", "TIWAZ卷轴")
			.t("nendil", "NENDIL卷轴")
			.t("libra", "LIBRA卷轴")
			.t("unknown_desc", "这张羊皮纸上写满了难以破译的魔法符文。大声念出来会发生什么？")
			.t("blinded", "你不能在失明时阅读卷轴。")
			.t("no_magic", "你不能在魔法免疫时阅读卷轴。")
			.t("cursed", "被诅咒的法典抑制了卷轴中法术的启动！也许祛邪卷轴足够强大还能被使用.....")
			.t("$placeholder.name", "卷轴");
	}



	
	public static final String AC_READ	= "READ";
	
	protected static final float TIME_TO_READ	= 1f;

	private static final LinkedHashMap<String, IconEntry> runes = new LinkedHashMap<String, IconEntry>() {
		{
			put("KAUNAN",SpecificPlaceHolderDict.SOMETHING_0);
			put("SOWILO",SpecificPlaceHolderDict.SOMETHING_0);
			put("LAGUZ",SpecificPlaceHolderDict.SOMETHING_0);
			put("YNGVI",SpecificPlaceHolderDict.SOMETHING_0);
			put("GYFU",SpecificPlaceHolderDict.SOMETHING_0);
			put("RAIDO",SpecificPlaceHolderDict.SOMETHING_0);
			put("ISAZ",SpecificPlaceHolderDict.SOMETHING_0);
			put("MANNAZ",SpecificPlaceHolderDict.SOMETHING_0);
			put("NAUDIZ",SpecificPlaceHolderDict.SOMETHING_0);
			put("BERKANAN",SpecificPlaceHolderDict.SOMETHING_0);
			put("NCOSRANE",SpecificPlaceHolderDict.SOMETHING_0);
			put("TIWAZ",SpecificPlaceHolderDict.SOMETHING_0);
			put("NENDIL",SpecificPlaceHolderDict.SOMETHING_0);
			put("LIBRA",SpecificPlaceHolderDict.SOMETHING_0);
		}
	};
	
	protected static ItemStatusHandler<Scroll> handler;
	
	protected String rune;
	protected int initials;

	//affects how strongly on-scroll talents trigger from this scroll
	public float talentFactor = 1;
	//the chance (0-1) of whether on-scroll talents trigger from this potion
	public float talentChance = 1;
	//SPS-PD spellbook casts must not consume or identify a physical scroll.
	public boolean ownedByBook = false;
	
	{
		stackable = true;
		defaultAction = AC_READ;
	}
	
	@SuppressWarnings("unchecked")
	public static void initLabels() {
		handler = new ItemStatusHandler<>( (Class<? extends Scroll>[])Generator.Category.SCROLL.classes, runes );
	}

	public static void clearLabels(){
		handler = null;
	}
	
	public static void save( Bundle bundle ) {
		handler.save( bundle );
	}

	public static void saveSelectively( Bundle bundle, ArrayList<Item> items ) {
		ArrayList<Class<?extends Item>> classes = new ArrayList<>();
		for (Item i : items){
			if (i instanceof ExoticScroll){
				if (!classes.contains(ExoticScroll.exoToReg.get(i.getClass()))){
					classes.add(ExoticScroll.exoToReg.get(i.getClass()));
				}
			} else if (i instanceof Scroll){
				if (!classes.contains(i.getClass())){
					classes.add(i.getClass());
				}
			}
		}
		handler.saveClassesSelectively( bundle, classes );
	}

	@SuppressWarnings("unchecked")
	public static void restore( Bundle bundle ) {
		handler = new ItemStatusHandler<>( (Class<? extends Scroll>[])Generator.Category.SCROLL.classes, runes, bundle );
	}
	
	public Scroll() {
		super();
		reset();
	}
	
	//anonymous scrolls are always IDed, do not affect ID status,
	//and their sprite is replaced by a placeholder if they are not known,
	//useful for items that appear in UIs, or which are only spawned for their effects
	protected boolean anonymous = false;
	public void anonymize(){
		if (!isKnown()) image = SpecificPlaceHolderDict.SCROLL_HOLDER_0;
		anonymous = true;
	}
	
	
	@Override
	public void reset(){
		super.reset();
		if (handler != null && handler.contains(this)) {
			image = handler.image(this);
			rune = handler.label(this);
		} else {
			image = SpecificPlaceHolderDict.SOMETHING_0;
			rune = "KAUNAN";
		}
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_READ );
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_READ )) {
			
			if (hero.buff(MagicImmune.class) != null){
				GLog.w( Messages.get(this, "no_magic") );
			} else if (hero.buff( Blindness.class ) != null) {
				GLog.w( Messages.get(this, "blinded") );
			} else if (hero.buff(UnstableSpellbook.bookRecharge.class) != null
					&& hero.buff(UnstableSpellbook.bookRecharge.class).isCursed()
					&& !(this instanceof ScrollOfRemoveCurse || this instanceof ScrollOfAntiMagic)){
				GLog.n( Messages.get(this, "cursed") );
			} else {
				doRead();
			}
			
		}
	}
	
	public abstract void doRead();

	//SPS-PD 0.9.8 overrides this in scrolls with a distinct empowered effect.
	public void empoweredRead() {
		doRead();
	}

	public void readAnimation() {
		Invisibility.dispel();
		curUser.spend( TIME_TO_READ );
		curUser.busy();
		((HeroSprite)curUser.sprite).read();
		applySpsChallengePenalty(curUser);

		if (!anonymous && !ownedByBook) {
			Catalog.countUse(getClass());
		}
		if (Random.Float() < talentChance) {
			Talent.onScrollUsed(curUser, curUser.pos, talentFactor, getClass());
		}

	}

	protected void applySpsChallengePenalty(Hero hero) {
		if (Dungeon.isChallenged(Challenges.ITEM_PHOBIA)) {
			Buff.affect(hero, Silent.class, 5f);
			hero.damage(hero.HT / 10, this);
		}
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
			
			if (Dungeon.hero != null && Dungeon.hero.isAlive()) {
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
		return isKnown() ? super.name() : Messages.get(this, rune);
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
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return isKnown();
	}
	
	public static HashSet<Class<? extends Scroll>> getKnown() {
		return handler.known();
	}
	
	public static HashSet<Class<? extends Scroll>> getUnknown() {
		return handler.unknown();
	}
	
	public static boolean allKnown() {
		return handler != null && handler.known().size() == Generator.Category.SCROLL.classes.length;
	}

	public Integer initials() {
		return isKnown() ? initials : null;
	}
	
	@Override
	public int value() {
		return 30 * quantity;
	}

	@Override
	public int energyVal() {
		return 6 * quantity;
	}
	
	public static class PlaceHolder extends Scroll {
		
		{
			image = SpecificPlaceHolderDict.SCROLL_HOLDER_0;
		}
		
		@Override
		public boolean isSimilar(Item item) {
			return ExoticScroll.regToExo.containsKey(item.getClass())
					|| ExoticScroll.regToExo.containsValue(item.getClass());
		}
		
		@Override
		public void doRead() {}
		
		@Override
		public String info() {
			return "";
		}
	}
	
	public static class ScrollToStone extends Recipe {
		
		private static HashMap<Class<?extends Scroll>, Class<?extends Runestone>> stones = new HashMap<>();
		static {
			stones.put(ScrollOfIdentify.class,      StoneOfIntuition.class);
			stones.put(ScrollOfLullaby.class,       StoneOfDeepSleep.class);
			stones.put(ScrollOfMagicMapping.class,  StoneOfClairvoyance.class);
			stones.put(ScrollOfMirrorImage.class,   StoneOfFlock.class);
			stones.put(ScrollOfRetribution.class,   StoneOfBlast.class);
			stones.put(ScrollOfRage.class,          StoneOfAggression.class);
			stones.put(ScrollOfRecharging.class,    StoneOfShock.class);
			stones.put(ScrollOfRemoveCurse.class,   StoneOfDetectMagic.class);
			stones.put(ScrollOfTeleportation.class, StoneOfBlink.class);
			stones.put(ScrollOfTerror.class,        StoneOfFear.class);
			stones.put(ScrollOfTransmutation.class, StoneOfAugmentation.class);
			stones.put(ScrollOfUpgrade.class,       StoneOfEnchantment.class);
		}
		
		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			if (ingredients.size() != 1
					|| !(ingredients.get(0) instanceof Scroll)
					|| !stones.containsKey(ingredients.get(0).getClass())){
				return false;
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
			
			Scroll s = (Scroll) ingredients.get(0);
			
			s.quantity(s.quantity() - 1);
			if (ShatteredPixelDungeon.scene() instanceof AlchemyScene){
				if (!s.isIdentified()){
					((AlchemyScene) ShatteredPixelDungeon.scene()).showIdentify(s);
				}
			} else {
				s.identify();
			}
			
			return Reflection.newInstance(stones.get(s.getClass())).quantity(2);
		}
		
		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;
			
			Scroll s = (Scroll) ingredients.get(0);

			if (!s.isKnown()){
				return new Runestone.PlaceHolder().quantity(2);
			} else {
				return Reflection.newInstance(stones.get(s.getClass())).quantity(2);
			}
		}
	}
}
