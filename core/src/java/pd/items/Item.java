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

import pd.Assets;
import pd.atlas.IconEntry;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.Badges;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Degrade;
import pd.actors.buffs.EndlessAmmo;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.effects.Speck;
import pd.items.equipment.bags.Bag;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.items.equipment.weapon.missiles.darts.Dart;
import pd.items.equipment.weapon.missiles.darts.TippedDart;
import pd.journal.Catalog;
import pd.journal.Notes;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import pd.sprites.MissileSprite;
import pd.ui.QuickSlotButton;
import render.noosa.audio.Sample;
import render.noosa.particles.Emitter;
import render.utils.data.Callback;
import render.utils.serialize.Bundlable;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import pd.messages.InlineText;

public class Item implements Bundlable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Item.class)
			.t("prompt", "选择丢出的方位")
			.t("ac_drop", "放下")
			.t("ac_throw", "扔出")
			.t("rankings_desc", "死于：%s")
			.t("curse", "诅咒")
			.t("custom_note_type", "你为这类物品写下了备注：“_%s_”")
			.t("custom_note", "你为这件物品写下了备注：“_%s_”")
			.t("discover_hint", "你可在地牢中概率找到该物品。")
			.t("not_here", "你不能在这里使用它。")
			.t("boss_first", "你必须先击败首领。")
			.t("reinforced", "这件物品已用精金破阶，可以突破通常的强化上限。");
	}




	protected static final String TXT_TO_STRING_LVL		= "%s %+d";
	protected static final String TXT_TO_STRING_X		= "%s x%d";
	
	protected static final float TIME_TO_THROW		= 1.0f;
	protected static final float TIME_TO_PICK_UP	= 1.0f;
	protected static final float TIME_TO_DROP		= 1.0f;
	
	public static final String AC_DROP		= "DROP";
	public static final String AC_THROW		= "THROW";
	
	protected String defaultAction;
	public boolean usesTargeting;

	//TODO should these be private and accessed through methods?
	public IconEntry image = SpecificPlaceHolderDict.SOMETHING_0;
	public int icon = -1; //used as an identifier for items with randomized images
	
	public boolean stackable = false;
	protected int quantity = 1;
	public boolean dropsDownHeap = false;

	// Legacy SPS alchemy value. Kept separately from Shattered's energy system.
	public int consumedValue = 0;
	
	private int level = 0;

	public boolean levelKnown = false;
	
	public boolean cursed;
	public boolean cursedKnown;
	public boolean reinforced;
	
	// Unique items persist through revival
	public boolean unique = false;

	// These items are preserved even if the hero's inventory is lost via unblessed ankh
	// this is largely set by the resurrection window, items can override this to always be kept
	public boolean keptThoughLostInvent = false;

	// whether an item can be included in heroes remains
	public boolean bones = false;

	public int customNoteID = -1;
	
	public static final Comparator<Item> itemComparator = new Comparator<Item>() {
		@Override
		public int compare( Item lhs, Item rhs ) {
			return Generator.Category.order( lhs ) - Generator.Category.order( rhs );
		}
	};
	
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = new ArrayList<>();
		actions.add( AC_DROP );
		actions.add( AC_THROW );
		return actions;
	}

	public String actionName(String action, Hero hero){
		return Messages.get(this, "ac_" + action);
	}

	/** SPSXPD: 从快捷栏点击时是否打开详情页（多行为工具返回 true，而不是直接执行默认动作） */
	public boolean quickSlotOpensMenu() {
		return defaultAction() == null;
	}

	/** SPSXPD: 是否允许放入快捷栏。默认要求物品有默认动作；无默认行为的物品（如社会升降器）可覆写放开 */
	public boolean canQuickSlot() {
		return defaultAction() != null;
	}

	/** SPSXPD: 动作按钮列表里该动作是否另起一行（用于排版控制，默认不换行） */
	public boolean actionBreakBefore(String action) {
		return false;
	}

	/** SPSXPD: 动作按钮右上角显示的消耗提示（默认无；露珠瓶用来显示露珠消耗） */
	public String actionCost(String action, Hero hero) {
		return null;
	}

	/** SPSXPD: 该动作当前是否可用（资源是否足够）。默认 true；露珠瓶用它把不足项的角标标红 */
	public boolean actionCostOk(String action, Hero hero) {
		return true;
	}

	public final boolean doPickUp( Hero hero ) {
		return doPickUp( hero, hero.pos );
	}

	public boolean doPickUp(Hero hero, int pos) {
		if (collect( hero.belongings.backpack )) {
			
			GameScene.pickUp( this, pos );
			Sample.INSTANCE.play( Assets.Sounds.ITEM );
			hero.spendAndNext( pickupDelay() );
			return true;
			
		} else {
			return false;
		}
	}
	
	public void doDrop( Hero hero ) {
		hero.spendAndNext(TIME_TO_DROP);
		int pos = hero.pos;
		Dungeon.level.drop(detachAll(hero.belongings.backpack), pos).sprite.drop(pos);
	}

	//resets an item's properties, to ensure consistency between runs
	public void reset(){
		keptThoughLostInvent = false;
	}

	public boolean keptThroughLostInventory(){
		return keptThoughLostInvent;
	}

	public void doThrow( Hero hero ) {
		GameScene.selectCell(thrower);
	}
	
	public void execute( Hero hero, String action ) {

		GameScene.cancel();
		curUser = hero;
		curItem = this;
		
		if (action.equals( AC_DROP )) {
			
			if (hero.belongings.backpack.contains(this) || isEquipped(hero)) {
				doDrop(hero);
			}
			
		} else if (action.equals( AC_THROW )) {
			
			if (hero.belongings.backpack.contains(this) || isEquipped(hero)) {
				doThrow(hero);
			}
			
		}
	}

	//can be overridden if default action is variable
	public String defaultAction(){
		return defaultAction;
	}
	
	public void execute( Hero hero ) {
		String action = defaultAction();
		if (action != null) {
			execute(hero, defaultAction());
		}
	}
	
	protected void onThrow( int cell ) {
		Heap heap = Dungeon.level.drop( this, cell );
		if (!heap.isEmpty()) {
			heap.sprite.drop( cell );
		}
	}
	
	//takes two items and merges them (if possible)
	public Item merge( Item other ){
		if (isSimilar( other )){
			quantity += other.quantity;
			other.quantity = 0;
		}
		return this;
	}
	
	public boolean collect( Bag container ) {

		if (quantity <= 0){
			return true;
		}

		ArrayList<Item> items = container.items;

		if (items.contains( this )) {
			return true;
		}

		for (Item item:items) {
			if (item instanceof Bag && ((Bag)item).canHold( this )) {
				if (collect( (Bag)item )){
					return true;
				}
			}
		}

		if (!container.canHold(this)){
			return false;
		}
		
		if (stackable) {
			for (Item item:items) {
				if (isSimilar( item )) {
					item.merge( this );
					item.updateQuickslot();
					if (Dungeon.hero != null && Dungeon.hero.isAlive()) {
						Badges.validateItemLevelAquired( this );
						Talent.onItemCollected(Dungeon.hero, item);
						if (isIdentified()) {
							Catalog.setSeen(getClass());
							Statistics.itemTypesDiscovered.add(getClass());
						}
					}
					if (TippedDart.lostDarts > 0){
						Dart d = new Dart();
						d.quantity(TippedDart.lostDarts);
						TippedDart.lostDarts = 0;
						if (!d.collect()){
							//have to handle this in an actor as we can't manipulate the heap during pickup
							Actor.add(new Actor() {
								{ actPriority = VFX_PRIO; }
								@Override
								protected boolean act() {
									Dungeon.level.drop(d, Dungeon.hero.pos).sprite.drop();
									Actor.remove(this);
									return true;
								}
							});
						}
					}
					return true;
				}
			}
		}

		if (Dungeon.hero != null && Dungeon.hero.isAlive()) {
			Badges.validateItemLevelAquired( this );
			Talent.onItemCollected( Dungeon.hero, this );
			if (isIdentified()){
				Catalog.setSeen(getClass());
				Statistics.itemTypesDiscovered.add(getClass());
			}
		}

		items.add( this );
		Dungeon.quickslot.replacePlaceholder(this);
		Collections.sort( items, itemComparator );
		updateQuickslot();
		return true;

	}
	
	public final boolean collect() {
		return collect( Dungeon.hero.belongings.backpack );
	}
	
	//returns a new item if the split was sucessful and there are now 2 items, otherwise null
	public Item split( int amount ){
		if (amount <= 0 || amount >= quantity()) {
			return null;
		} else {
			//pssh, who needs copy constructors?
			Item split = Reflection.newInstance(getClass());
			
			if (split == null){
				return null;
			}
			
			Bundle copy = new Bundle();
			this.storeInBundle(copy);
			split.restoreFromBundle(copy);
			split.quantity(amount);
			quantity -= amount;
			
			return split;
		}
	}

	public Item duplicate(){
		Item dupe = Reflection.newInstance(getClass());
		if (dupe == null){
			return null;
		}
		Bundle copy = new Bundle();
		this.storeInBundle(copy);
		dupe.restoreFromBundle(copy);
		return dupe;
	}
	
	public final Item detach( Bag container ) {
		
		if (quantity <= 0) {
			
			return null;
			
		} else
		if (quantity == 1) {

			if (stackable){
				Dungeon.quickslot.convertToPlaceholder(this);
			}

			return detachAll( container );
			
		} else {
			
			
			Item detached = split(1);
			updateQuickslot();
			if (detached != null) detached.onDetach( );
			return detached;
			
		}
	}
	
	public final Item detachAll( Bag container ) {
		Dungeon.quickslot.clearItem( this );

		for (Item item : container.items) {
			if (item == this) {
				container.items.remove(this);
				item.onDetach();
				container.grabItems(); //try to put more items into the bag as it now has free space
				updateQuickslot();
				return this;
			} else if (item instanceof Bag) {
				Bag bag = (Bag)item;
				if (bag.contains( this )) {
					return detachAll( bag );
				}
			}
		}

		updateQuickslot();
		return this;
	}
	
	public boolean isSimilar( Item item ) {
		return getClass() == item.getClass();
	}

	protected void onDetach(){}

	//returns the true level of the item, ignoring all modifiers aside from upgrades
	public final int trueLevel(){
		return level;
	}

	//returns the persistant level of the item, only affected by modifiers which are persistent (e.g. curse infusion)
	public int level(){
		return level;
	}
	
	//returns the level of the item, after it may have been modified by temporary boosts/reductions
	//note that not all item properties should care about buffs/debuffs! (e.g. str requirement)
	public int buffedLvl(){
		//only the hero can be affected by Degradation
		if (Dungeon.hero != null && Dungeon.hero.buff( Degrade.class ) != null
			&& (isEquipped( Dungeon.hero ) || Dungeon.hero.belongings.contains( this ))) {
			return Degrade.reduceLevel(level());
		} else {
			return level();
		}
	}

	public Item level( int value ){
		level = value;

		updateQuickslot();

		return this;
	}

	public Item uncurse() {
		cursed = false;
		return this;
	}

	public Item reinforce() {
		reinforced = true;
		return this;
	}

	public boolean isReinforced() {
		return reinforced;
	}
	
	public Item upgrade() {
		
		this.level++;

		updateQuickslot();
		
		return this;
	}
	
	final public Item upgrade( int n ) {
		for (int i=0; i < n; i++) {
			upgrade();
		}
		
		return this;
	}
	
	public Item degrade() {
		
		this.level--;
		
		return this;
	}
	
	final public Item degrade( int n ) {
		for (int i=0; i < n; i++) {
			degrade();
		}
		
		return this;
	}
	
	public int visiblyUpgraded() {
		return levelKnown ? level() : 0;
	}

	public int buffedVisiblyUpgraded() {
		return levelKnown ? buffedLvl() : 0;
	}
	
	public boolean visiblyCursed() {
		return cursed && cursedKnown;
	}
	
	public boolean isUpgradable() {
		return true;
	}
	
	public boolean isIdentified() {
		return levelKnown && cursedKnown;
	}
	
	public boolean isEquipped( Hero hero ) {
		return false;
	}

	public final Item identify(){
		return identify(true);
	}

	public Item identify( boolean byHero ) {

		if (byHero && Dungeon.hero != null && Dungeon.hero.isAlive()){
			Catalog.setSeen(getClass());
			Statistics.itemTypesDiscovered.add(getClass());
		}

		levelKnown = true;
		cursedKnown = true;
		Item.updateQuickslot();
		
		return this;
	}
	
	public void onHeroGainExp( float levelPercent, Hero hero ){
		//do nothing by default
	}
	
	public static void evoke( Hero hero ) {
		hero.sprite.emitter().burst( Speck.factory( Speck.EVOKE ), 5 );
	}

	public String title() {

		String name = name();

		if (visiblyUpgraded() != 0)
			name = Messages.format( TXT_TO_STRING_LVL, name, visiblyUpgraded()  );

		if (quantity > 1)
			name = Messages.format( TXT_TO_STRING_X, name, quantity );

		return name;

	}
	
	public String name() {
		return trueName();
	}
	
	public final String trueName() {
		return Messages.get(this, "name");
	}
	
	public IconEntry image() {
		return image;
	}
	
	public ItemSprite.Glowing glowing() {
		return null;
	}

	public Emitter emitter() { return null; }
	
	public String info() {
		String info = desc();
		if (Dungeon.hero != null) {
			Notes.CustomRecord note = Notes.findCustomRecord(customNoteID);
			if (note != null) {
				//we swap underscore(0x5F) with low macron(0x2CD) here to avoid highlighting in the item window
				info = Messages.get(this, "custom_note", note.title().replace('_', 'ˍ')) + "\n\n" + info;
			} else {
				note = Notes.findCustomRecord(getClass());
				if (note != null) {
					//we swap underscore(0x5F) with low macron(0x2CD) here to avoid highlighting in the item window
					info = Messages.get(this, "custom_note_type", note.title().replace('_', 'ˍ')) + "\n\n" + info;
				}
			}
		}
		if (reinforced) info += "\n\n" + Messages.get(Item.class, "reinforced");
		return info;
	}
	
	public String desc() {
		return Messages.get(this, "desc");
	}
	
	public int quantity() {
		return quantity;
	}
	
	public Item quantity( int value ) {
		quantity = value;
		return this;
	}

	//item's value in gold coins
	public int value() {
		return 0;
	}

	//item's value in energy crystals
	public int energyVal() {
		return 0;
	}
	
	public Item virtual(){
		Item item = Reflection.newInstance(getClass());
		if (item == null) return null;
		
		item.quantity = 0;
		item.level = level;
		return item;
	}
	
	public Item random() {
		return this;
	}
	
	public String status() {
		return quantity != 1 ? Integer.toString( quantity ) : null;
	}

	//SPSXPD: 右上角等级槽的自定义文本（默认无；魂石用它显示宠物等级）
	public String slotLevelText() {
		return null;
	}

	public static void updateQuickslot() {
		GameScene.updateItemDisplays = true;
	}
	
	private static final String QUANTITY		= "quantity";
	private static final String LEVEL			= "level";
	private static final String LEVEL_KNOWN		= "levelKnown";
	private static final String CURSED			= "cursed";
	private static final String REINFORCED		= "reinforced";
	private static final String CURSED_KNOWN	= "cursedKnown";
	private static final String QUICKSLOT		= "quickslotpos";
	private static final String KEPT_LOST       = "kept_lost";
	private static final String CUSTOM_NOTE_ID = "custom_note_id";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( QUANTITY, quantity );
		bundle.put( LEVEL, level );
		bundle.put( LEVEL_KNOWN, levelKnown );
		bundle.put( CURSED, cursed );
		bundle.put( REINFORCED, reinforced );
		bundle.put( CURSED_KNOWN, cursedKnown );
		if (Dungeon.quickslot.contains(this)) {
			bundle.put( QUICKSLOT, Dungeon.quickslot.getSlot(this) );
		}
		bundle.put( KEPT_LOST, keptThoughLostInvent );
		if (customNoteID != -1)     bundle.put(CUSTOM_NOTE_ID, customNoteID);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		quantity	= bundle.getInt( QUANTITY );
		levelKnown	= bundle.getBoolean( LEVEL_KNOWN );
		cursedKnown	= bundle.getBoolean( CURSED_KNOWN );
		
		int level = bundle.getInt( LEVEL );
		if (level > 0) {
			upgrade( level );
		} else if (level < 0) {
			degrade( -level );
		}
		
		cursed	= bundle.getBoolean( CURSED );
		reinforced = bundle.getBoolean( REINFORCED );

		//only want to populate slots when restoring belongings
		if (Belongings.bundleRestoring) {
			if (bundle.contains(QUICKSLOT)) {
				Dungeon.quickslot.setSlot(bundle.getInt(QUICKSLOT), this);
			}
		}

		keptThoughLostInvent = bundle.getBoolean( KEPT_LOST );
		if (bundle.contains(CUSTOM_NOTE_ID))    customNoteID = bundle.getInt(CUSTOM_NOTE_ID);
	}

	public int targetingPos( Hero user, int dst ){
		return throwPos( user, dst );
	}

	public int throwPos( Hero user, int dst){
		return new Ballistica( user.pos, dst, Ballistica.PROJECTILE ).collisionPos;
	}

	public void throwSound(){
		Sample.INSTANCE.play(Assets.Sounds.MISS, 0.6f, 0.6f, 1.5f);
	}
	
	public void cast( final Hero user, final int dst ) {
		
		final int cell = throwPos( user, dst );
		user.sprite.zap( cell );
		user.busy();

		throwSound();

		Char enemy = Actor.findChar( cell );
		QuickSlotButton.target(enemy);
		
		final float delay = castDelay(user, cell);

		if (enemy != null) {
			((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
					reset(user.sprite,
							enemy.sprite,
							this,
							new Callback() {
						@Override
						public void call() {
							curUser = user;
							Item i = EndlessAmmo.isActive(user) ? Item.this.duplicate() : Item.this.detach(user.belongings.backpack);
							if (i != null) i.onThrow(cell);
							if (curUser.hasTalent(Talent.IMPROVISED_PROJECTILES)
									&& !(Item.this instanceof MissileWeapon)
									&& curUser.buff(Talent.ImprovisedProjectileCooldown.class) == null){
								if (enemy != null && enemy.alignment != curUser.alignment){
									Sample.INSTANCE.play(Assets.Sounds.HIT);
									Buff.affect(enemy, Blindness.class, 1f + curUser.pointsInTalent(Talent.IMPROVISED_PROJECTILES));
									Buff.affect(curUser, Talent.ImprovisedProjectileCooldown.class, 50f);
								}
							}
							if (user.buff(Talent.LethalMomentumTracker.class) != null){
								user.buff(Talent.LethalMomentumTracker.class).detach();
								user.next();
							} else {
								user.spendAndNext(delay);
							}
						}
					});
		} else {
			((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
					reset(user.sprite,
							cell,
							this,
							new Callback() {
						@Override
						public void call() {
							curUser = user;
							Item i = EndlessAmmo.isActive(user) ? Item.this.duplicate() : Item.this.detach(user.belongings.backpack);
							user.spend(delay);
							if (i != null) i.onThrow(cell);
							user.next();
						}
					});
		}
	}
	
	public float castDelay( Char user, int cell ){
		return TIME_TO_THROW;
	}

	public float pickupDelay(){
		return TIME_TO_PICK_UP;
	}
	
	protected static Hero curUser = null;
	protected static Item curItem = null;
	public void setCurrent( Hero hero ){
		curUser = hero;
		curItem = this;
	}

	protected static CellSelector.Listener thrower = new CellSelector.Listener() {
		@Override
		public void onSelect( Integer target ) {
			if (target != null) {
				curItem.cast( curUser, target );
			}
		}
		@Override
		public String prompt() {
			return Messages.get(Item.class, "prompt");
		}
	};
}
