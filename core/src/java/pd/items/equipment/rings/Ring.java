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

import pd.atlas.IconEntry;

import pd.atlas.items.EquipmentJewelleryRingDict;
import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Badges;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.EnhancedRings;
import pd.actors.buffs.MagicImmune;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.hero.spells.SpiritForm;
import pd.items.Generator;
import pd.items.Item;
import pd.items.ItemStatusHandler;
import pd.items.KindofMisc;
import pd.items.equipment.trinkets.ShardOfOblivion;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import pd.messages.InlineText;

public class Ring extends KindofMisc {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Ring.class)
			.t("diamond", "钻石戒指")
			.t("opal", "蛋白石戒指")
			.t("garnet", "石榴石戒指")
			.t("ruby", "红宝石戒指")
			.t("amethyst", "紫水晶戒指")
			.t("topaz", "黄玉戒指")
			.t("onyx", "黑曜石戒指")
			.t("tourmaline", "碧玺戒指")
			.t("emerald", "绿宝石戒指")
			.t("sapphire", "蓝宝石戒指")
			.t("quartz", "石英戒指")
			.t("agate", "玛瑙戒指")
			.t("equip_cursed", "这枚戒指突然紧缩，箍住了你的手指！")
			.t("unknown_desc", "这枚金属环镶嵌着一颗在黑暗中闪烁光芒的大块宝石。谁知道戴上后会有什么效果？")
			.t("known", "这是一枚%s。")
			.t("identify", "你对你的戒指已经足够熟悉并且可以因此将其完全鉴定。")
			.t("cursed_worn", "由于这个戒指被诅咒，你无力将其除下。")
			.t("curse_known", "你能感觉到这件戒指里潜伏着一股充满恶意的魔力。")
			.t("not_cursed", "这枚戒指没有被诅咒。")
			.t("starstone", "星石戒指")
			.t("moonstone", "月石戒指");
	}

	
	protected Buff buff;
	protected Class<? extends RingBuff> buffClass;

	private static final LinkedHashMap<String, IconEntry> gems = new LinkedHashMap<String, IconEntry>() {
		{
			put("garnet",SpecificPlaceHolderDict.SOMETHING_0);
			put("ruby",EquipmentJewelleryRingDict.RING_RUBY_0);
			put("topaz",EquipmentJewelleryRingDict.RING_ELEMENTS_0);
			put("emerald",EquipmentJewelleryRingDict.RING_ENERGY_0);
			put("onyx",EquipmentJewelleryRingDict.RING_ONYX_0);
			put("opal",EquipmentJewelleryRingDict.RING_FORCE_0);
			put("tourmaline",EquipmentJewelleryRingDict.RING_FUROR_0);
			put("sapphire",EquipmentJewelleryRingDict.RING_SAPPHIRE_0);
			put("amethyst",EquipmentJewelleryRingDict.RING_MIGHT_0);
			put("quartz",EquipmentJewelleryRingDict.RING_SHARPSHOOT_0);
			put("agate",EquipmentJewelleryRingDict.RING_TENACITY_0);
			put("diamond",EquipmentJewelleryRingDict.RING_DIAMOND_0);
			put("starstone",EquipmentJewelleryRingDict.RING_MIGHT_0);
			put("moonstone",EquipmentJewelleryRingDict.RING_FORCE_0);
		}
	};
	
	private static ItemStatusHandler<Ring> handler;
	
	private String gem;
	
	//rings cannot be 'used' like other equipment, so they ID purely based on exp
	private float levelsToID = 1;
	
	@SuppressWarnings("unchecked")
	public static void initGems() {
		handler = new ItemStatusHandler<>( (Class<? extends Ring>[])Generator.Category.RING.classes, gems );
	}

	public static void clearGems(){
		handler = null;
	}
	
	public static void save( Bundle bundle ) {
		handler.save( bundle );
	}

	public static void saveSelectively( Bundle bundle, ArrayList<Item> items ) {
		handler.saveSelectively( bundle, items );
	}
	
	@SuppressWarnings("unchecked")
	public static void restore( Bundle bundle ) {
		handler = new ItemStatusHandler<>( (Class<? extends Ring>[])Generator.Category.RING.classes, gems, bundle );
	}
	
	public Ring() {
		super();
		reset();
	}

	//anonymous rings are always IDed, do not affect ID status,
	//and their sprite is replaced by a placeholder if they are not known,
	//useful for items that appear in UIs, or which are only spawned for their effects
	protected boolean anonymous = false;
	public void anonymize(){
		if (!isKnown()) image = EquipmentJewelleryRingDict.RING_HOLDER;
		anonymous = true;
	}
	
	public void reset() {
		super.reset();
		levelsToID = 1;
		if (handler != null && handler.contains(this)){
			image = handler.image(this);
			gem = handler.label(this);
		} else {
			image = SpecificPlaceHolderDict.SOMETHING_0;
			gem = "garnet";
		}
	}
	
	public void activate( Char ch ) {
		if (buff != null){
			buff.detach();
			buff = null;
		}
		buff = buff();
		buff.attachTo( ch );
	}

	@Override
	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {
		if (super.doUnequip( hero, collect, single )) {

			if (buff != null) {
				buff.detach();
				buff = null;
			}

			return true;

		} else {

			return false;

		}
	}
	
	public boolean isKnown() {
		return anonymous || (handler != null && handler.isKnown( this ));
	}
	
	public void setKnown() {
		if (!anonymous && handler != null) {
			if (!isKnown()) {
				handler.know(this);
			}

			if (Dungeon.hero != null && Dungeon.hero.isAlive()) {
				Catalog.setSeen(getClass());
				Statistics.itemTypesDiscovered.add(getClass());
			}
		}
	}
	
	@Override
	public String name() {
		return isKnown() ? super.name() : Messages.get(Ring.class, gem);
	}

	@Override
	public String desc() {
		return isKnown() ? super.desc() : Messages.get(this, "unknown_desc");
	}
	
	@Override
	public String info(){

		//skip custom notes if anonymized and un-Ided
		String desc;
		if (anonymous && (handler == null || !handler.isKnown( this ))){
			desc = desc();

		} else {
			desc = super.info();
		}

		if (cursed && isEquipped( Dungeon.hero )) {
			desc += "\n\n" + Messages.get(Ring.class, "cursed_worn");
			
		} else if (cursed && cursedKnown) {
			desc += "\n\n" + Messages.get(Ring.class, "curse_known");
			
		} else if (!isIdentified() && cursedKnown){
			desc += "\n\n" + Messages.get(Ring.class, "not_cursed");
			
		}
		
		if (isKnown()) {
			desc += "\n\n" + statsInfo();
		}
		
		return desc;
	}
	
	protected String statsInfo(){
		return "";
	}

	public String upgradeStat1(int level){
		return null;
	}

	public String upgradeStat2(int level){
		return null;
	}

	public String upgradeStat3(int level){
		return null;
	}
	
	@Override
	public Item upgrade() {
		super.upgrade();
		
		if (Random.Int(3) == 0) {
			cursed = false;
		}
		
		return this;
	}
	
	@Override
	public boolean isIdentified() {
		return super.isIdentified() && isKnown();
	}
	
	@Override
	public Item identify( boolean byHero ) {
		setKnown();
		levelsToID = 0;
		return super.identify(byHero);
	}

	public void setIDReady(){
		levelsToID = -1;
	}

	public boolean readyToIdentify(){
		return !isIdentified() && levelsToID <= 0;
	}
	
	@Override
	public Item random() {
		//+0: 66.67% (2/3)
		//+1: 26.67% (4/15)
		//+2: 6.67%  (1/15)
		int n = 0;
		if (Random.Int(3) == 0) {
			n++;
			if (Random.Int(5) == 0){
				n++;
			}
		}
		level(n);
		
		//30% chance to be cursed
		if (Random.Float() < 0.3f) {
			cursed = true;
		}
		
		return this;
	}
	
	public static HashSet<Class<? extends Ring>> getKnown() {
		return handler.known();
	}
	
	public static HashSet<Class<? extends Ring>> getUnknown() {
		return handler.unknown();
	}
	
	public static boolean allKnown() {
		return handler != null && handler.known().size() == Generator.Category.RING.classes.length;
	}
	
	@Override
	public int value() {
		int price = 75;
		if (cursed && cursedKnown) {
			price /= 2;
		}
		if (levelKnown) {
			if (level() > 0) {
				price *= (level() + 1);
			} else if (level() < 0) {
				price /= (1 - level());
			}
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}
	
	protected RingBuff buff() {
		return null;
	}

	private static final String LEVELS_TO_ID    = "levels_to_ID";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( LEVELS_TO_ID, levelsToID );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		levelsToID = bundle.getFloat( LEVELS_TO_ID );
	}
	
	public void onHeroGainExp( float levelPercent, Hero hero ){
		if (isIdentified() || !isEquipped(hero)) return;
		levelPercent *= Talent.itemIDSpeedFactor(hero, this);
		//becomes IDed after 1 level
		levelsToID -= levelPercent;
		if (levelsToID <= 0){
			if (ShardOfOblivion.passiveIDDisabled()){
				if (levelsToID > -1){
					GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready"), name());
				}
				setIDReady();
			} else {
				identify();
				GLog.p(Messages.get(Ring.class, "identify"));
				Badges.validateItemLevelAquired(this);
			}
		}
	}

	@Override
	public int buffedLvl() {
		int lvl = super.buffedLvl();
		if (Dungeon.hero.buff(EnhancedRings.class) != null){
			lvl++;
		}
		return lvl;
	}

	public static int getBonus(Char target, Class<?extends RingBuff> type){
		if (target.buff(MagicImmune.class) != null) return 0;
		int bonus = 0;
		for (RingBuff buff : target.buffs(type)) {
			bonus += buff.level();
		}
		SpiritForm.SpiritFormBuff spiritForm = target.buff(SpiritForm.SpiritFormBuff.class);
		if (bonus == 0
				&& spiritForm != null
				&& spiritForm.ring() != null
				&& spiritForm.ring().buffClass == type){
			bonus += spiritForm.ring().soloBonus();
		}
		return bonus;
	}

	public static int getBuffedBonus(Char target, Class<?extends RingBuff> type){
		if (target.buff(MagicImmune.class) != null) return 0;
		int bonus = 0;
		for (RingBuff buff : target.buffs(type)) {
			bonus += buff.buffedLvl();
		}
		if (bonus == 0
				&& target.buff(SpiritForm.SpiritFormBuff.class) != null
				&& target.buff(SpiritForm.SpiritFormBuff.class).ring() != null
				&& target.buff(SpiritForm.SpiritFormBuff.class).ring().buffClass == type){
			bonus += target.buff(SpiritForm.SpiritFormBuff.class).ring().soloBuffedBonus();
		}
		return bonus;
	}

	//just used for ring descriptions
	public int soloBonus(){
		if (cursed){
			return Math.min( 0, Ring.this.level()-2 );
		} else {
			return Ring.this.level()+1;
		}
	}

	//just used for ring descriptions
	public int soloBuffedBonus(){
		if (cursed){
			return Math.min( 0, Ring.this.buffedLvl()-2 );
		} else {
			return Ring.this.buffedLvl()+1;
		}
	}

	//just used for ring descriptions
	public int combinedBonus(Hero hero){
		int bonus = 0;
		if (hero.belongings.ring() instanceof Ring && hero.belongings.ring().getClass() == getClass()){
			bonus += ((Ring)hero.belongings.ring()).soloBonus();
		}
		if (hero.belongings.misc() != null && hero.belongings.misc().getClass() == getClass()){
			bonus += ((Ring)hero.belongings.misc()).soloBonus();
		}
		return bonus;
	}

	//just used for ring descriptions
	public int combinedBuffedBonus(Hero hero){
		int bonus = 0;
		if (hero.belongings.ring() instanceof Ring && hero.belongings.ring().getClass() == getClass()){
			bonus += ((Ring)hero.belongings.ring()).soloBuffedBonus();
		}
		if (hero.belongings.misc() != null && hero.belongings.misc().getClass() == getClass()){
			bonus += ((Ring)hero.belongings.misc()).soloBuffedBonus();
		}
		return bonus;
	}

	public class RingBuff extends Buff {

		@Override
		public boolean attachTo( Char target ) {
			if (super.attachTo( target )) {
				//if we're loading in and the hero has partially spent a turn, delay for 1 turn
				if (target instanceof Hero && Dungeon.hero == null && cooldown() == 0 && target.cooldown() > 0) {
					spend(TICK);
				}
				return true;
			}
			return false;
		}

		@Override
		public boolean act() {
			spend( TICK );
			return true;
		}

		public int level(){
			return Ring.this.soloBonus();
		}

		public int buffedLvl(){
			return Ring.this.soloBuffedBonus();
		}

	}
}
