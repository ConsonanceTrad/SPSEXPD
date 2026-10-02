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

package pd.items;

import com.badlogic.gdx.Gdx;
import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Fire;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Frost;
import pd.actors.buffs.Roots;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mimic;
import pd.actors.mobs.MonsterBox;
import pd.actors.mobs.RedWraith;
import pd.actors.mobs.Spinner;
import pd.actors.mobs.Wraith;
import pd.actors.mobs.npcs.Shopkeeper;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.effects.particles.ElmoParticle;
import pd.effects.particles.FlameParticle;
import pd.effects.particles.ShadowParticle;
import pd.items.equipment.artifacts.Artifact;
import pd.items.equipment.bombs.Bomb;
import pd.items.consum.eggs.Egg;
import pd.items.consum.food.ChargrilledMeat;
import pd.items.consum.food.FrozenCarpaccio;
import pd.items.consum.food.MysteryMeat;
import pd.items.consum.food.meatfood.DarkMeat;
import pd.items.consum.food.meatfood.EarthMeat;
import pd.items.consum.food.meatfood.FireMeat;
import pd.items.consum.food.meatfood.IceMeat;
import pd.items.consum.food.meatfood.LightMeat;
import pd.items.consum.food.meatfood.Meat;
import pd.items.consum.food.meatfood.ShockMeat;
import pd.items.specific.journal.DocumentPage;
import pd.items.specific.journal.Guidebook;
import pd.items.consum.medicine.Pill;
import pd.items.nornstone.NornStone;
import pd.items.consum.potions.Potion;
import pd.items.equipment.rings.RingOfWealth;
import pd.items.consum.scrolls.Scroll;
import pd.items.consum.scrolls.ScrollOfMagicalInfusion;
import pd.items.consum.scrolls.ScrollOfUpgrade;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.melee.relic.AresSword;
import pd.items.equipment.weapon.melee.relic.CromCruachAxe;
import pd.items.equipment.weapon.melee.relic.JupitersWraith;
import pd.items.equipment.weapon.melee.relic.LokisFlail;
import pd.items.equipment.weapon.melee.relic.NeptunusTrident;
import pd.items.equipment.weapon.missiles.darts.Dart;
import pd.items.equipment.weapon.missiles.darts.TippedDart;
import pd.journal.Document;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.plants.Plant;
import pd.plants.Rotberry;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Bundlable;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import pd.messages.InlineText;

public class Heap implements Bundlable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Heap.class)
			.t("for_sale", "%2$s：%1$d金币")
			.t("for_life", "%2$s：%1$d点永久生命")
			.t("mimic", "这是一个宝箱怪！")
			.t("chest", "宝箱")
			.t("chest_desc", "打开前你是看不见里面有什么的！")
			.t("locked_chest", "上锁的宝箱")
			.t("locked_chest_desc", "打开前你是看不见里面有什么的！你需要一枚金钥匙才能打开它。")
			.t("crystal_chest", "水晶宝箱")
			.t("crystal_chest_desc", "你看得见里面的_%s_，但你需要一枚水晶钥匙才能打开它。")
			.t("artifact", "一件神器")
			.t("wand", "一根法杖")
			.t("ring", "一枚戒指")
			.t("tomb", "坟墓")
			.t("tomb_desc", "这个坟墓里或许埋葬着一些有用的东西，但墓主肯定是不会让你拿走的。")
			.t("skeleton", "遗骸")
			.t("skeleton_desc", "某个不幸的冒险家存在过的唯一证明。或许可以找找里面有什么值钱的东西。")
			.t("remains", "英雄遗骸")
			.t("remains_desc", "你的某个先辈存在过的唯一证明。或许能找到点什么值钱的东西。");
	}

	
	public enum Type {
		HEAP,
		FOR_SALE,
		FOR_LIFE,
		CHEST,
		LOCKED_CHEST,
		CRYSTAL_CHEST,
		TOMB,
		SKELETON,
		REMAINS,
		E_DUST,
		M_WEB,
		// Appended so existing Shattered/SPS save enum names and ordinals remain stable.
		G_MIMIC,
		MIMIC
	}
	public Type type = Type.HEAP;
	
	public int pos = 0;
	
	public ItemSprite sprite;
	public boolean seen = false;
	public boolean haunted = false;
	public boolean autoExplored = false; //used to determine if this heap should count for exploration bonus
	public boolean hidden = false; //sets alpha to 15%
	
	public LinkedList<Item> items = new LinkedList<>();
	
	public void open( Hero hero ) {
		boolean spsContainer = type == Type.E_DUST || type == Type.M_WEB || type == Type.G_MIMIC;
		boolean spsRemains = type == Type.SKELETON || type == Type.REMAINS;
		switch (type) {
		case MIMIC:
			if (Mimic.spawnAt(pos, new ArrayList<>(items)) != null) {
				if (Gdx.app != null) GLog.n(Messages.get(this, "mimic"));
				destroy();
			} else {
				type = Type.CHEST;
			}
			return;
		case G_MIMIC:
			if (MonsterBox.spawnAt(pos) == null) return;
			GLog.n(Messages.get(this, "mimic"));
			break;
		case TOMB:
			Wraith.spawnAround( hero.pos );
			break;
		case REMAINS:
		case SKELETON:
			if (sprite != null) CellEmitter.center(pos).start(Speck.factory(Speck.RATTLE), 0.1f, 3);
			if (RedWraith.spawnAt(pos) == null) {
				if (hero.sprite != null) hero.sprite.emitter().burst(ShadowParticle.CURSE, 6);
				hero.damage(hero.HP / 2, this);
			}
			if (sprite != null) Sample.INSTANCE.play(Assets.Sounds.CURSED);
			break;
		case M_WEB:
			CellEmitter.center(pos).start(Speck.factory(Speck.WOOL), 0.1f, 3);
			if (Random.Int(10) == 0) spawnSpinner(hero.pos);
			Buff.affect(hero, Roots.class, 5f);
			Sample.INSTANCE.play(Assets.Sounds.SHATTER);
			break;
		default:
		}
		
		if (haunted && !spsRemains){
			if (Wraith.spawnAt( pos ) == null) {
				hero.sprite.emitter().burst( ShadowParticle.CURSE, 6 );
				hero.damage( hero.HP / 2, this );
				if (!hero.isAlive()){
					Dungeon.fail(Wraith.class);
					GLog.n( Messages.capitalize(Messages.get(Char.class, "kill", Messages.get(Wraith.class, "name"))));
				}
			}
			Sample.INSTANCE.play( Assets.Sounds.CURSED );
		}

		type = Type.HEAP;
		ArrayList<Item> bonus = spsContainer ? null : RingOfWealth.tryForBonusDrop(hero, 1);
		if (bonus != null && !bonus.isEmpty()) {
			items.addAll(0, bonus);
			if (sprite != null) RingOfWealth.showFlareForBonusDrop(sprite);
		}
		if (sprite != null) {
			sprite.link();
			sprite.drop();
		}
	}

	private static void spawnSpinner(int center) {
		ArrayList<Integer> cells = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = center + offset;
			if (cell >= 0 && cell < Dungeon.level.length()
					&& Dungeon.level.passable[cell] && Actor.findChar(cell) == null) {
				cells.add(cell);
			}
		}
		if (!cells.isEmpty()) {
			Spinner spinner = new Spinner();
			spinner.pos = Random.element(cells);
			GameScene.add(spinner);
		}
	}
	
	public Heap setHauntedIfCursed(){
		for (Item item : items) {
			if (item.cursed) {
				haunted = true;
				item.cursedKnown = true;
				break;
			}
		}
		return this;
	}
	
	public int size() {
		return items.size();
	}
	
	public Item pickUp() {
		
		if (items.isEmpty()){
			destroy();
			return null;
		}
		Item item = items.removeFirst();
		if (items.isEmpty()) {
			destroy();
		} else if (sprite != null) {
			sprite.view(this).place( pos );
		}
		
		return item;
	}
	
	public Item peek() {
		return items.peek();
	}
	
	public void drop( Item item ) {
		hidden = false;
		
		if (item.stackable && type != Type.FOR_SALE && type != Type.FOR_LIFE) {
			
			for (Item i : items) {
				if (i.isSimilar( item )) {
					item = i.merge( item );
					break;
				}
			}
			items.remove( item );
			
		}

		//certain items always go on the bottom of a heap if possible
		if ((item.dropsDownHeap && type != Type.FOR_SALE && type != Type.FOR_LIFE)) {
			items.add( item );
		//lost backpack must always be on top of a heap
		} else if (peek() instanceof LostBackpack){
			items.add(1, item);
		} else {
			items.addFirst( item );
		}
		
		if (sprite != null) {
			sprite.view(this).place( pos );
		}

		if (TippedDart.lostDarts > 0){
			Dart d = new Dart();
			d.quantity(TippedDart.lostDarts);
			TippedDart.lostDarts = 0;
			drop(d);
		}
	}
	
	public void replace( Item a, Item b ) {
		hidden = false;
		int index = items.indexOf( a );
		if (index != -1) {
			items.remove( index );
			for (Item i : items) {
				if (i.isSimilar( b )) {
					i.merge( b );
					return;
				}
			}
			items.add( index, b );
		}
	}
	
	public void remove( Item a ){
		hidden = false;
		items.remove(a);
		if (items.isEmpty()){
			destroy();
		} else if (sprite != null) {
			sprite.view(this).place( pos );
		}
	}
	
	public void burn() {
		hidden = false;

		if (type == Type.M_WEB) {
			GameScene.add(Blob.seed(pos, 2, Fire.class));
			type = Type.HEAP;
			if (sprite != null) {
				sprite.link();
				sprite.drop();
			}
			return;
		}

		if (type != Type.HEAP) {
			return;
		}
		
		boolean burnt = false;
		boolean evaporated = false;
		
		for (Item item : items.toArray( new Item[0] )) {
			if (item instanceof Egg) {
				((Egg)item).burns++;
				burnt = true;
			} else if (item instanceof Scroll && !item.unique) {
				items.remove( item );
				burnt = true;
			} else if (item instanceof Dewdrop) {
				items.remove( item );
				evaporated = true;
			} else if (item instanceof MysteryMeat || item instanceof FrozenCarpaccio) {
				replace( item, ChargrilledMeat.cook( item.quantity ) );
				burnt = true;
			} else if (item instanceof Bomb) {
				items.remove( item );
				((Bomb) item).explode( pos );
				if (((Bomb) item).explodesDestructively()) {
					//stop processing the burning, it will be replaced by the explosion.
					return;
				} else {
					burnt = true;
				}
			}
		}
		
		if (burnt || evaporated) {
			
			if (Dungeon.level.heroFOV[pos]) {
				if (burnt) {
					burnFX( pos );
				} else {
					evaporateFX( pos );
				}
			}
			
			if (isEmpty()) {
				destroy();
			} else if (sprite != null) {
				sprite.view(this).place( pos );
			}
			
		}
	}

	/** Item reaction used specifically by SPS-PD's meteorite wand. */
	public void firehit() {
		hidden = false;
		if (type == Type.MIMIC) {
			Mimic mimic = Mimic.spawnAt(pos, new ArrayList<>(items));
			if (mimic != null) {
				Buff.affect(mimic, Burning.class).reignite(mimic, 3f);
				if (mimic.sprite != null) mimic.sprite.emitter().burst(FlameParticle.FACTORY, 5);
				destroy();
			}
			return;
		}
		if (type == Type.M_WEB) {
			GameScene.add(Blob.seed(pos, 2, Fire.class));
			type = Type.HEAP;
			if (sprite != null) {
				sprite.link();
				sprite.drop();
			}
			return;
		}
		if (type != Type.HEAP) return;

		boolean changed = false;
		for (Item item : items.toArray(new Item[0])) {
			if (item instanceof Scroll && !(item instanceof ScrollOfUpgrade)
					&& !(item instanceof ScrollOfMagicalInfusion)) {
				items.remove(item);
				changed = true;
			} else if (item instanceof Egg) {
				((Egg) item).burns++;
				changed = true;
			} else if (item instanceof MysteryMeat || item instanceof Meat) {
				replace(item, FireMeat.cook(item.quantity()));
				changed = true;
			}
		}
		if (changed) {
			if (Dungeon.level != null && Dungeon.level.heroFOV[pos]) burnFX(pos);
			if (isEmpty()) destroy();
			else if (sprite != null) sprite.view(this).place(pos);
		}
	}

	//Note: should not be called to initiate an explosion, but rather by an explosion that is happening.
	public void explode() {
		hidden = false;

		//breaks open most standard containers, mimics die.
		if (type == Type.MIMIC || type == Type.CHEST || type == Type.SKELETON) {
			type = Type.HEAP;
			if (sprite != null) {
				sprite.link();
				sprite.drop();
			}
			return;
		}

		if (type != Type.HEAP) {

			return;

		} else {

			for (Item item : items.toArray( new Item[0] )) {

				//unique items and equipment aren't affect by explosions
				if (item.unique || item.isUpgradable() || item instanceof EquipableItem){
					continue;
				}

				if (item instanceof Potion) {
					items.remove(item);
					((Potion) item).shatter(pos);

				} else if (item instanceof Honeypot.ShatteredPot) {
					items.remove(item);
					((Honeypot.ShatteredPot) item).destroyPot(pos);

				} else if (item instanceof Bomb) {
					items.remove( item );
					((Bomb) item).explode(pos);
					if (((Bomb) item).explodesDestructively()) {
						//stop processing current explosion, it will be replaced by the new one.
						return;
					}

				} else {
					items.remove( item );
				}

			}

			if (isEmpty()){
				destroy();
			} else if (sprite != null) {
				sprite.view(this).place( pos );
			}
		}
	}
	
	public void freeze() {

		if (type != Type.HEAP) {
			return;
		}
		
		boolean frozen = false;
		for (Item item : items.toArray( new Item[0] )) {
			if (item instanceof Egg) {
				((Egg)item).freezes++;
				frozen = true;
			} else if (item instanceof MysteryMeat || item instanceof Meat) {
				replace(item, IceMeat.cook(item.quantity()));
				frozen = true;
			} else if (item instanceof Potion && !item.unique) {
				items.remove(item);
				((Potion) item).shatter(pos);
				frozen = true;
			} else if (item instanceof Bomb && ((Bomb) item).fuse != null){
				frozen = frozen || ((Bomb) item).fuse.freeze();
			}
		}
		
		if (frozen) {
			if (isEmpty()) {
				destroy();
			} else if (sprite != null) {
				sprite.view(this).place( pos );
			}
		}
	}

	/** Legacy SPS wand hook; modern heaps already implement the same item-freezing rules here. */
	public void icehit() {
		if (type == Type.MIMIC) {
			Mimic mimic = Mimic.spawnAt(pos, new ArrayList<>(items));
			if (mimic != null) {
				Buff.prolong(mimic, Frost.class, Frost.DURATION * Random.Float(1f, 1.5f));
				destroy();
			}
			return;
		}
		freeze();
	}

	public Weapon consecrate() {
		int count = 0;
		int type = 0;
		for (Item item : items) {
			if (!(item instanceof NornStone)) return null;
			count += item.quantity();
			if (type == 0 || Random.Int(3) < item.quantity()) type = ((NornStone) item).type;
		}
		if (count < 2) return null;

		Weapon weapon;
		switch (type) {
			case 1: weapon = new LokisFlail(); break;
			case 2: weapon = new NeptunusTrident(); break;
			case 3: weapon = new CromCruachAxe(); break;
			case 4: weapon = new AresSword(); break;
			case 5: weapon = new JupitersWraith(); break;
			default: weapon = new AresSword(); break;
		}
		destroy();
		weapon.cursed = false;
		weapon.identify();
		weapon.upgrade(6);
		return weapon;
	}

	public void shockhit() {
		reactToEnergy(EggEnergy.LIGHTNING);
	}

	public void darkhit() {
		reactToEnergy(EggEnergy.DARK);
	}

	public void earthhit() {
		reactToEnergy(EggEnergy.EARTH);
	}

	public void lighthit() {
		reactToEnergy(EggEnergy.LIGHT);
	}

	private enum EggEnergy { LIGHTNING, DARK, EARTH, LIGHT }

	private void reactToEnergy(EggEnergy energy) {
		if (type != Type.HEAP) return;
		for (Item item : items.toArray(new Item[0])) {
			if (item instanceof MysteryMeat || item instanceof Meat) {
				switch (energy) {
					case LIGHTNING: replace(item, ShockMeat.cook(item.quantity())); break;
					case DARK: replace(item, DarkMeat.cook(item.quantity())); break;
					case EARTH: replace(item, EarthMeat.cook(item.quantity())); break;
					case LIGHT: replace(item, LightMeat.cook(item.quantity())); break;
				}
			} else if (item instanceof Egg) {
				Egg egg = (Egg)item;
				switch (energy) {
					case LIGHTNING: egg.lits++; break;
					case DARK: egg.darks++; break;
					case EARTH: egg.poisons++; break;
					case LIGHT: egg.lights++; break;
				}
			} else if (energy == EggEnergy.LIGHTNING && item instanceof Pill) {
				items.remove(item);
			} else if (energy == EggEnergy.DARK && item instanceof Bomb) {
				items.remove(item);
			} else if (energy == EggEnergy.EARTH && item instanceof StoneOre) {
				items.remove(item);
			} else if (energy == EggEnergy.LIGHT && item instanceof Plant.Seed
					&& !(item instanceof Rotberry.Seed)) {
				items.remove(item);
			}
		}
		if (isEmpty()) {
			if (Dungeon.level != null) destroy();
		} else if (sprite != null) {
			sprite.view(this).place(pos);
		}
	}
	
	public static void burnFX( int pos ) {
		CellEmitter.get( pos ).burst( ElmoParticle.FACTORY, 6 );
		Sample.INSTANCE.play( Assets.Sounds.BURNING );
	}
	
	public static void evaporateFX( int pos ) {
		CellEmitter.get( pos ).burst( Speck.factory( Speck.STEAM ), 5 );
	}
	
	public boolean isEmpty() {
		return items == null || items.size() == 0;
	}
	
	public void destroy() {
		Dungeon.level.heaps.remove( this.pos );
		if (sprite != null) {
			sprite.kill();
		}
		items.clear();
	}

	public String title(){
		switch(type){
			case FOR_SALE:
				Item i = peek();
				if (size() == 1) {
					return Messages.get(this, "for_sale", Shopkeeper.sellPrice(i), i.title());
				} else {
					return i.title();
				}
			case FOR_LIFE:
				Item lifeItem = peek();
				if (size() == 1) {
					return Messages.get(this, "for_life",
							pd.windows.WndLifeTradeItem.price(),
							lifeItem.title());
				} else {
					return lifeItem.title();
				}
			case CHEST:
			case MIMIC:
				return Messages.get(this, "chest");
			case LOCKED_CHEST:
			case G_MIMIC:
				return Messages.get(this, "locked_chest");
			case CRYSTAL_CHEST:
				return Messages.get(this, "crystal_chest");
			case TOMB:
				return Messages.get(this, "tomb");
			case SKELETON:
				return Messages.get(this, "skeleton");
			case REMAINS:
				return Messages.get(this, "remains");
			default:
				return peek().title();
		}
	}

	public String info(){
		switch(type){
			case CHEST:
			case MIMIC:
				return Messages.get(this, "chest_desc");
			case LOCKED_CHEST:
			case G_MIMIC:
				return Messages.get(this, "locked_chest_desc");
			case CRYSTAL_CHEST:
				if (peek() instanceof Artifact)
					return Messages.get(this, "crystal_chest_desc", Messages.get(this, "artifact") );
				else if (peek() instanceof Wand)
					return Messages.get(this, "crystal_chest_desc", Messages.get(this, "wand") );
				else
					return Messages.get(this, "crystal_chest_desc", Messages.get(this, "ring") );
			case TOMB:
				return Messages.get(this, "tomb_desc");
			case SKELETON:
				return Messages.get(this, "skeleton_desc");
			case REMAINS:
				return Messages.get(this, "remains_desc");
			default:
				return peek().info();
		}
	}

	private static final String POS		= "pos";
	private static final String SEEN	= "seen";
	private static final String TYPE	= "type";
	private static final String ITEMS	= "items";
	private static final String HAUNTED	= "haunted";
	private static final String AUTO_EXPLORED	= "auto_explored";
	private static final String HIDDEN	= "hidden";
	
	@SuppressWarnings("unchecked")
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		pos = bundle.getInt( POS );
		seen = bundle.getBoolean( SEEN );
		type = Type.valueOf( bundle.getString( TYPE ) );
		
		items = new LinkedList<>((Collection<Item>) ((Collection<?>) bundle.getCollection(ITEMS)));
		items.removeAll(Collections.singleton(null));
		
		//remove any document pages that either don't exist anymore or that the player already has
		for (Item item : items.toArray(new Item[0])){
			if (item instanceof DocumentPage
					&& ( !((DocumentPage) item).document().pageNames().contains(((DocumentPage) item).page())
					||    ((DocumentPage) item).document().isPageFound(((DocumentPage) item).page()))){
				items.remove(item);
			}
			if (item instanceof Guidebook && Document.ADVENTURERS_GUIDE.isPageRead(0)){
				items.remove(item);
			}
		}
		
		haunted = bundle.getBoolean( HAUNTED );
		autoExplored = bundle.getBoolean( AUTO_EXPLORED );
		hidden = bundle.getBoolean( HIDDEN );
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( POS, pos );
		bundle.put( SEEN, seen );
		bundle.put( TYPE, type );
		bundle.put( ITEMS, items );
		bundle.put( HAUNTED, haunted );
		bundle.put( AUTO_EXPLORED, autoExplored );
		bundle.put( HIDDEN, hidden );
	}
	
}
