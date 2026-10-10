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

package pd.actors.hero;

import pd.atlas.items.EquipmentBagsDict;

import pd.Badges;
import pd.Dungeon;
import pd.GamesInProgress;
import pd.actors.buffs.LostInventory;
import pd.items.Badge;
import pd.items.EquipableItem;
import pd.items.Item;
import pd.items.KindOfWeapon;
import pd.items.KindofMisc;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.armor.ClassArmor;
import pd.items.equipment.artifacts.Artifact;
import pd.items.equipment.bags.Bag;
import pd.items.equipment.rings.Ring;
import pd.items.consum.scrolls.ScrollOfRemoveCurse;
import pd.items.equipment.trinkets.ShardOfOblivion;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.guns.GunWeapon;
import pd.items.equipment.weapon.missiles.ShootGun;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.Iterator;

public class Belongings implements Iterable<Item> {

	private Hero owner;

	public static class Backpack extends Bag {
		{
			image = EquipmentBagsDict.BACKPACK_0;
		}
		public int capacity(){
			//SPS: 主背包 35 格（5 列 x 7 行）。净格恒为 35——包裹袋本体不占格，也不再额外多给一格
			//SPSEXPD: 恒为 BACKPACK_CAPACITY，不给任何挑战/模式放大——背包窗口就是 35 格，
			//且露珠瓶恒占最后一格（普通物品最多 34 件）。曾有过 TEST_TIME 放大到 64 的特例，已按用户要求移除
			return BACKPACK_CAPACITY;
		}

		//SPSEXPD: 最后一格恒留给露珠瓶，其它物品最多 capacity-1 件（露珠瓶自身不受预留限制）
		@Override
		protected int reservedSlotsFor( Item item ){
			return item instanceof pd.items.Waterskin ? 0 : 1;
		}
	}

	//SPSEXPD: 旧存档的露珠瓶可能被药剂挎带等包裹收录，读档后迁回主背包（只被主背包收录）
	private void migrateWaterskin() {
		for (Bag bag : getBags()) {
			if (bag == null || bag == backpack) continue;
			for (Item item : bag.items.toArray(new Item[0])) {
				if (item instanceof pd.items.Waterskin) {
					item.detachAll(bag);
					item.collect(backpack);
				}
			}
		}
	}

	//SPS: 主背包净格数（= 5 列 x 7 行；包裹袋打开时本体占其中一格见 WndBag）
	public static final int BACKPACK_CAPACITY = 35;

	public Backpack backpack;
	
	public Belongings( Hero owner ) {
		this.owner = owner;
		
		backpack = new Backpack();
		backpack.owner = owner;
	}

	/** Adds one round to every SPS firearm, matching the old relord() support effect. */
	public int reloadGuns() {
		int count = 0;
		for (Item item : this) {
			if (item instanceof GunWeapon && ((GunWeapon)item).addRound()) count++;
			if (item instanceof ShootGun) {
				ShootGun gun = (ShootGun)item;
				int before = gun.charge();
				gun.charge(before + 1);
				if (gun.charge() > before) count++;
			}
		}
		return count;
	}

	public KindOfWeapon weapon = null;
	public Armor armor = null;
	//SPS: 神器和戒指已合并为「饰品」——5 个完全通用饰品槽（1-3 沿用旧字段名以兼容既有引用）
	public KindofMisc artifact = null;    //饰品槽 1
	public KindofMisc misc = null;        //饰品槽 2
	public KindofMisc ring = null;        //饰品槽 3
	public KindofMisc accessory4 = null;  //饰品槽 4
	public KindofMisc accessory5 = null;  //饰品槽 5
	//SPS: 徽章槽（特殊穿戴物），与饰品槽互不通用
	public Badge badge = null;

	//used when thrown weapons temporary become the current weapon
	public KindOfWeapon thrownWeapon = null;

	//used to ensure that the duelist always uses the weapon she's using the ability of
	public KindOfWeapon abilityWeapon = null;

	//used by the champion subclass
	public KindOfWeapon secondWep = null;
	public Armor secondArmor = null;

	//*** these accessor methods are so that worn items can be affected by various effects/debuffs
	// we still want to access the raw equipped items in cases where effects should be ignored though,
	// such as when equipping something, showing an interface, or dealing with items from a dead hero

	//normally the primary equipped weapon, but can also be a thrown weapon or an ability's weapon
	public KindOfWeapon attackingWeapon(){
		if (thrownWeapon != null) return thrownWeapon;
		if (abilityWeapon != null) return abilityWeapon;
		return weapon();
	}

	//we cache whether belongings are lost to avoid lots of calls to hero.buff(LostInventory.class)
	private boolean lostInvent;
	public void lostInventory( boolean val ){
		lostInvent = val;
	}

	public boolean lostInventory(){
		return lostInvent;
	}

	public KindOfWeapon weapon(){
		if (!lostInventory() || (weapon != null && weapon.keptThroughLostInventory())){
			return weapon;
		} else {
			return null;
		}
	}

	public Armor armor(){
		if (!lostInventory() || (armor != null && armor.keptThroughLostInventory())){
			return armor;
		} else {
			return null;
		}
	}

	//SPS: 神器和戒指已合并为饰品——getter 统一返回 KindofMisc（具体类型由调用方按需判断）
	public KindofMisc artifact(){
		if (!lostInventory() || (artifact != null && artifact.keptThroughLostInventory())){
			return artifact;
		} else {
			return null;
		}
	}

	public KindofMisc misc(){
		if (!lostInventory() || (misc != null && misc.keptThroughLostInventory())){
			return misc;
		} else {
			return null;
		}
	}

	public KindofMisc ring(){
		if (!lostInventory() || (ring != null && ring.keptThroughLostInventory())){
			return ring;
		} else {
			return null;
		}
	}

	public KindOfWeapon secondWep(){
		if (!lostInventory() || (secondWep != null && secondWep.keptThroughLostInventory())){
			return secondWep;
		} else {
			return null;
		}
	}

	public Armor secondArmor(){
		if (!lostInventory() || (secondArmor != null && secondArmor.keptThroughLostInventory())){
			return secondArmor;
		} else {
			return null;
		}
	}

	// ***
	
	private static final String WEAPON		= "weapon";
	private static final String ARMOR		= "armor";
	private static final String ARTIFACT   = "artifact";
	private static final String MISC       = "misc";
	private static final String RING       = "ring";
	//SPS: 饰品槽 4/5 与徽章槽（新增键；旧档没有这些键 → 读取为 null，天然兼容）
	private static final String ACCESSORY4 = "accessory4";
	private static final String ACCESSORY5 = "accessory5";
	private static final String BADGE      = "badge";

	private static final String SECOND_WEP = "second_wep";
	private static final String SECOND_ARMOR = "second_armor";

	public void storeInBundle( Bundle bundle ) {
		
		backpack.storeInBundle( bundle );
		
		bundle.put( WEAPON, weapon );
		bundle.put( ARMOR, armor );
		bundle.put( ARTIFACT, artifact );
		bundle.put( MISC, misc );
		bundle.put( RING, ring );
		bundle.put( ACCESSORY4, accessory4 );
		bundle.put( ACCESSORY5, accessory5 );
		bundle.put( BADGE, badge );
		bundle.put( SECOND_WEP, secondWep );
		bundle.put( SECOND_ARMOR, secondArmor );
	}

	public static boolean bundleRestoring = false;
	
	public void restoreFromBundle( Bundle bundle ) {
		bundleRestoring = true;
		backpack.clear();
		backpack.restoreFromBundle( bundle );
		
		weapon = (KindOfWeapon) bundle.get(WEAPON);
		if (weapon() != null)       weapon().activate(owner);
		
		armor = (Armor)bundle.get( ARMOR );
		if (armor() != null)        armor().activate( owner );

		artifact = (KindofMisc) bundle.get(ARTIFACT);
		if (artifact() != null)     artifact().activate(owner);

		misc = (KindofMisc) bundle.get(MISC);
		if (misc() != null)         misc().activate( owner );

		ring = (KindofMisc) bundle.get(RING);
		if (ring() != null)         ring().activate( owner );

		accessory4 = (KindofMisc) bundle.get(ACCESSORY4);
		if (accessory4 != null)     accessory4.activate( owner );

		accessory5 = (KindofMisc) bundle.get(ACCESSORY5);
		if (accessory5 != null)     accessory5.activate( owner );

		badge = (Badge) bundle.get(BADGE);
		if (badge != null)          badge.activate( owner );

		secondWep = (KindOfWeapon) bundle.get(SECOND_WEP);
		if (secondWep() != null)    secondWep().activate(owner);

		secondArmor = (Armor) bundle.get(SECOND_ARMOR);

		bundleRestoring = false;

		//SPSEXPD: 旧存档里露珠瓶若被药剂挎带等包裹收录，迁回主背包
		migrateWaterskin();
	}

	public void clear(){
		backpack.clear();
		weapon = secondWep = null;
		armor = secondArmor = null;
		artifact = null;
		misc = null;
		ring = null;
		accessory4 = null;
		accessory5 = null;
		badge = null;
	}
	
	public static void preview( GamesInProgress.Info info, Bundle bundle ) {
		if (bundle.contains( ARMOR )){
			Armor armor = ((Armor)bundle.get( ARMOR ));
			if (armor instanceof ClassArmor){
				info.armorTier = 6;
			} else {
				info.armorTier = armor.tier;
			}
		} else {
			info.armorTier = 0;
		}
	}

	//ignores lost inventory debuff
	public ArrayList<Bag> getBags(){
		ArrayList<Bag> result = new ArrayList<>();

		result.add(backpack);

		ArrayList<Bag> others = new ArrayList<>();
		for (Item i : this){
			if (i instanceof Bag){
				others.add((Bag)i);
			}
		}

		//SPSEXPD: 标签页固定顺序——绒布包-卷轴筒-药水箱-购物车-竹背篓-魔法套筒-暗器袋-草靶子-钥匙串
		//（Bag.bagOrder()，相等的保持原有次序；主背包固定在下标 0）
		others.sort(java.util.Comparator.comparingInt(Bag::bagOrder));
		result.addAll(others);

		return result;
	}
	
	@SuppressWarnings("unchecked")
	public<T extends Item> T getItem( Class<T> itemClass ) {

		boolean lostInvent = lostInventory();

		for (Item item : this) {
			if (itemClass.isInstance( item )) {
				if (!lostInvent || item.keptThroughLostInventory()) {
					return (T) item;
				}
			}
		}
		
		return null;
	}

	public<T extends Item> ArrayList<T> getAllItems( Class<T> itemClass ) {
		ArrayList<T> result = new ArrayList<>();

		boolean lostInvent = lostInventory();

		for (Item item : this) {
			if (itemClass.isInstance( item )) {
				if (!lostInvent || item.keptThroughLostInventory()) {
					result.add((T) item);
				}
			}
		}

		return result;
	}
	
	public boolean contains( Item contains ){

		boolean lostInvent = lostInventory();
		
		for (Item item : this) {
			if (contains == item) {
				if (!lostInvent || item.keptThroughLostInventory()) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public Item getSimilar( Item similar ){

		boolean lostInvent = lostInventory();
		
		for (Item item : this) {
			if (similar != item && similar.isSimilar(item)) {
				if (!lostInvent || item.keptThroughLostInventory()) {
					return item;
				}
			}
		}
		
		return null;
	}
	
	public ArrayList<Item> getAllSimilar( Item similar ){
		ArrayList<Item> result = new ArrayList<>();

		boolean lostInvent = lostInventory();
		
		for (Item item : this) {
			if (item != similar && similar.isSimilar(item)) {
				if (!lostInvent || item.keptThroughLostInventory()) {
					result.add(item);
				}
			}
		}
		
		return result;
	}

	//triggers when a run ends, so ignores lost inventory effects
	public void identify() {
		for (Item item : this) {
			item.identify(false);
		}
	}
	
	public void observe() {
		if (weapon() != null) {
			if (ShardOfOblivion.passiveIDDisabled() && weapon() instanceof Weapon){
				((Weapon) weapon()).setIDReady();
			} else {
				weapon().identify();
				Badges.validateItemLevelAquired(weapon());
			}
		}
		if (secondWep() != null){
			if (ShardOfOblivion.passiveIDDisabled() && secondWep() instanceof Weapon){
				((Weapon) secondWep()).setIDReady();
			} else {
				secondWep().identify();
				Badges.validateItemLevelAquired(secondWep());
			}
		}
		if (armor() != null) {
			if (ShardOfOblivion.passiveIDDisabled()){
				armor().setIDReady();
			} else {
				armor().identify();
				Badges.validateItemLevelAquired(armor());
			}
		}
		if (secondArmor() != null) {
			if (ShardOfOblivion.passiveIDDisabled()){
				secondArmor().setIDReady();
			} else {
				secondArmor().identify();
				Badges.validateItemLevelAquired(secondArmor());
			}
		}
		if (artifact() != null) {
			//oblivion shard does not prevent artifact IDing
			artifact().identify();
			Badges.validateItemLevelAquired(artifact());
		}
		if (misc() != null) {
			if (ShardOfOblivion.passiveIDDisabled() && misc() instanceof Ring){
				((Ring) misc()).setIDReady();
			} else {
				misc().identify();
				Badges.validateItemLevelAquired(misc());
			}
		}
		if (ring() != null) {
			if (ShardOfOblivion.passiveIDDisabled() && ring() instanceof Ring){
				((Ring) ring()).setIDReady();
			} else {
				ring().identify();
				Badges.validateItemLevelAquired(ring());
			}
		}
		if (ShardOfOblivion.passiveIDDisabled()){
			GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready_worn"));
		}
		for (Item item : backpack) {
			if (item instanceof EquipableItem || item instanceof Wand) {
				item.cursedKnown = true;
			}
		}
		Item.updateQuickslot();
	}
	
	public void uncurseEquipped() {
		ScrollOfRemoveCurse.uncurse( owner, armor(), weapon(), artifact(), misc(), ring(),
				accessory4, accessory5, badge, secondWep(), secondArmor());
	}
	
	public Item randomUnequipped() {
		if (owner.buff(LostInventory.class) != null) return null;

		return Random.element( backpack.items );
	}
	
	public int charge( float charge ) {
		
		int count = 0;
		
		for (Wand.Charger charger : owner.buffs(Wand.Charger.class)){
			charger.gainCharge(charge);
			count++;
		}
		
		return count;
	}

	@Override
	public Iterator<Item> iterator() {
		return new ItemIterator();
	}
	
	private class ItemIterator implements Iterator<Item> {

		private int index = 0;
		private int returnedEquipped = -1;
		
		private Iterator<Item> backpackIterator = backpack.iterator();
		
		private Item[] equipped = {weapon, armor, artifact, misc, ring, secondWep, secondArmor, accessory4, accessory5, badge};
		private int backpackIndex = equipped.length;
		
		@Override
		public boolean hasNext() {
			
			for (int i=index; i < backpackIndex; i++) {
				if (equipped[i] != null) {
					return true;
				}
			}
			
			return backpackIterator.hasNext();
		}

		@Override
		public Item next() {
			
			while (index < backpackIndex) {
				int slot = index++;
				Item item = equipped[slot];
				if (item != null) {
					returnedEquipped = slot;
					return item;
				}
			}
			returnedEquipped = -1;
			return backpackIterator.next();
		}

		@Override
		public void remove() {
			switch (returnedEquipped) {
			case 0:
				equipped[0] = weapon = null;
				break;
			case 1:
				equipped[1] = armor = null;
				break;
			case 2:
				equipped[2] = artifact = null;
				break;
			case 3:
				equipped[3] = misc = null;
				break;
			case 4:
				equipped[4] = ring = null;
				break;
			case 5:
				equipped[5] = secondWep = null;
				break;
			case 6:
				equipped[6] = secondArmor = null;
				break;
			case 7:
				equipped[7] = accessory4 = null;
				break;
			case 8:
				equipped[8] = accessory5 = null;
				break;
			case 9:
				equipped[9] = badge = null;
				break;
			default:
				backpackIterator.remove();
			}
			returnedEquipped = -1;
		}
	}
}
