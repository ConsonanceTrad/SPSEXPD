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

package pd.items.equipment.armor;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Badges;
import pd.Challenges;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicImmune;
import pd.actors.buffs.Momentum;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.HeroSubClass;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.rogue.ShadowClone;
import pd.actors.hero.spells.AuraOfProtection;
import pd.actors.hero.spells.BodyForm;
import pd.actors.hero.spells.HolyWard;
import pd.actors.hero.spells.LifeLinkSpell;
import pd.actors.mobs.npcs.PrismaticImage;
import pd.effects.Speck;
import pd.items.BrokenSeal;
import pd.items.EquipableItem;
import pd.items.Item;
import pd.items.equipment.armor.curses.AntiEntropy;
import pd.items.equipment.armor.curses.Bulk;
import pd.items.equipment.armor.curses.Corrosion;
import pd.items.equipment.armor.curses.Displacement;
import pd.items.equipment.armor.curses.Metabolism;
import pd.items.equipment.armor.curses.Multiplicity;
import pd.items.equipment.armor.curses.Overgrowth;
import pd.items.equipment.armor.curses.Stench;
import pd.items.equipment.armor.glyphs.AdaptGlyph;
import pd.items.equipment.armor.glyphs.Affection;
import pd.items.equipment.armor.glyphs.AntiMagic;
import pd.items.equipment.armor.glyphs.Brimstone;
import pd.items.equipment.armor.glyphs.Camouflage;
import pd.items.equipment.armor.glyphs.Changeglyph;
import pd.items.equipment.armor.glyphs.Crystalglyph;
import pd.items.equipment.armor.glyphs.Darkglyph;
import pd.items.equipment.armor.glyphs.Earthglyph;
import pd.items.equipment.armor.glyphs.Electricityglyph;
import pd.items.equipment.armor.glyphs.Energyglyph;
import pd.items.equipment.armor.glyphs.Entanglement;
import pd.items.equipment.armor.glyphs.Fireglyph;
import pd.items.equipment.armor.glyphs.Flow;
import pd.items.equipment.armor.glyphs.Iceglyph;
import pd.items.equipment.armor.glyphs.Lightglyph;
import pd.items.equipment.armor.glyphs.Obfuscation;
import pd.items.equipment.armor.glyphs.Potential;
import pd.items.equipment.armor.glyphs.RecoilGlyph;
import pd.items.equipment.armor.glyphs.Repulsion;
import pd.items.equipment.armor.glyphs.Revivalglyph;
import pd.items.equipment.armor.glyphs.Stone;
import pd.items.equipment.armor.glyphs.Swiftness;
import pd.items.equipment.armor.glyphs.Testglyph;
import pd.items.equipment.armor.glyphs.Thorns;
import pd.items.equipment.armor.glyphs.Viscosity;
import pd.items.equipment.bags.Bag;
import pd.items.equipment.rings.RingOfArcana;
import pd.items.equipment.trinkets.ParchmentScrap;
import pd.items.equipment.trinkets.ShardOfOblivion;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.HeroSprite;
import pd.sprites.ItemSprite;
import pd.utils.GLog;
import pd.windows.WndOptions;
import render.noosa.particles.Emitter;
import render.utils.math.Random;
import render.utils.serialize.Bundlable;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import pd.messages.InlineText;

public class Armor extends EquipableItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Armor.class)
			.t("ac_detach", "拆卸")
			.t("detach_seal", "你将纹章从护甲上摘了下来。")
			.t("equip_cursed", "这件护甲生疼地勒住了你。")
			.t("identify", "你对你的护甲已经足够熟悉并且可以因此将其完全鉴定。")
			.t("hardening_gone", "这件护甲上的硬化效果已被损耗！")
			.t("incompatible", "不同属性的魔法相冲突，消除了护甲上的刻印！")
			.t("curr_absorb", "这件_%1$d阶_护甲能抵挡_%2$d~%3$d点伤害_并且需要_%4$d点力量_来正常使用。")
			.t("avg_absorb", "一般情况下这件_%1$d阶_护甲能抵挡_%2$d~%3$d点伤害_并且需要_%4$d点力量_来正常使用。")
			.t("too_heavy", "由于你的力量不足，使用这件护甲会降低你的移动、闪避以及防御能力。")
			.t("probably_too_heavy", "这套护甲对你来说或许太重了。")
			.t("excess_str", "你的额外力量使你穿戴着这件护甲时_更易闪避_。")
			.t("evasion", "这件护甲强化了_闪避_。")
			.t("defense", "这件护甲强化了_防御_。")
			.t("inscribed", "这件护甲刻有_%s_。")
			.t("glyph_hardened", "它被_硬化_了。")
			.t("hardened_no_glyph", "这件护甲已被_硬化_，但目前没有携带刻印。")
			.t("cursed_worn", "这套护甲被诅咒了，你无法卸下它。")
			.t("cursed", "你能感觉到这套护甲里潜伏着一股充满恶意的魔力。")
			.t("weak_cursed", "虽然这件护甲有诅咒，但是你可以脱下它。")
			.t("not_cursed", "这件护甲没有被诅咒。")
			.t("seal_attached", "战士的破损纹章被贴附于这件护甲上，当战士将要受伤至生命值半数以下时，纹章会为战士提供_%d点护盾_。")
			.t("seal_transfer", "战士的破损纹章必须贴附在他当前穿戴的护甲上方可生效。你是否愿意将破损纹章从卸下的护甲上自动剥离，并贴附到新装备的护甲上？")
			.t("seal_transfer_yes", "是")
			.t("seal_transfer_no", "否")
			.t("$glyph.glyph", "刻印")
			.t("$glyph.killed", "%s杀死了你...")
			.t("$glyph.rankings_desc", "死于刻印")
			.t("$glyph.discover_hint", "你可在地牢中概率找到带有该效果的物品，或尝试自行使物品获得该效果。");
	}




	protected static final String AC_DETACH       = "DETACH";
	
	public enum Augment {
		EVASION (2f , -1f),
		DEFENSE (-2f, 1f),
		NONE	(0f   ,  0f);
		
		private float evasionFactor;
		private float defenceFactor;
		
		Augment(float eva, float df){
			evasionFactor = eva;
			defenceFactor = df;
		}
		
		public int evasionFactor(int level){
			return Math.round((2 + level) * evasionFactor);
		}
		
		public int defenseFactor(int level){
			return Math.round((2 + level) * defenceFactor);
		}
	}
	
	public Augment augment = Augment.NONE;
	
	public Glyph glyph;
	public boolean glyphHardened = false;
	public boolean curseInfusionBonus = false;
	public boolean masteryPotionBonus = false;
	
	protected BrokenSeal seal;
	
	public int tier;
	
	private static final int USES_TO_ID = 10;
	private float usesLeftToID = USES_TO_ID;
	private float availableUsesToID = USES_TO_ID/2f;
	
	public Armor( int tier ) {
		this.tier = tier;
	}
	
	private static final String USES_LEFT_TO_ID = "uses_left_to_id";
	private static final String AVAILABLE_USES  = "available_uses";
	private static final String GLYPH			= "glyph";
	private static final String GLYPH_HARDENED	= "glyph_hardened";
	private static final String CURSE_INFUSION_BONUS = "curse_infusion_bonus";
	private static final String MASTERY_POTION_BONUS = "mastery_potion_bonus";
	private static final String SEAL            = "seal";
	private static final String AUGMENT			= "augment";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( USES_LEFT_TO_ID, usesLeftToID );
		bundle.put( AVAILABLE_USES, availableUsesToID );
		bundle.put( GLYPH, glyph );
		bundle.put( GLYPH_HARDENED, glyphHardened );
		bundle.put( CURSE_INFUSION_BONUS, curseInfusionBonus );
		bundle.put( MASTERY_POTION_BONUS, masteryPotionBonus );
		bundle.put( SEAL, seal);
		bundle.put( AUGMENT, augment);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		usesLeftToID = bundle.getInt( USES_LEFT_TO_ID );
		availableUsesToID = bundle.getInt( AVAILABLE_USES );
		inscribe((Glyph) bundle.get(GLYPH));
		glyphHardened = bundle.getBoolean(GLYPH_HARDENED);
		curseInfusionBonus = bundle.getBoolean( CURSE_INFUSION_BONUS );
		masteryPotionBonus = bundle.getBoolean( MASTERY_POTION_BONUS );
		seal = (BrokenSeal)bundle.get(SEAL);
		
		augment = bundle.getEnum(AUGMENT, Augment.class);
	}

	@Override
	public void reset() {
		super.reset();
		usesLeftToID = USES_TO_ID;
		availableUsesToID = USES_TO_ID/2f;
		//armor can be kept in bones between runs, the seal cannot.
		seal = null;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (seal != null) actions.add(AC_DETACH);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {

		super.execute(hero, action);

		if (action.equals(AC_DETACH) && seal != null){
			BrokenSeal detaching = detachSeal();
			GLog.i( Messages.get(Armor.class, "detach_seal") );
			hero.sprite.operate(hero.pos);
			if (!detaching.collect()){
				Dungeon.level.drop(detaching, hero.pos);
			}
			updateQuickslot();
		}
	}

	@Override
	public boolean collect(Bag container) {
		if(super.collect(container)){
			if (Dungeon.hero != null && Dungeon.hero.isAlive() && isIdentified() && glyph != null){
				Catalog.setSeen(glyph.getClass());
				Statistics.itemTypesDiscovered.add(glyph.getClass());
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Item identify(boolean byHero) {
		if (glyph != null && byHero && Dungeon.hero != null && Dungeon.hero.isAlive()){
			Catalog.setSeen(glyph.getClass());
			Statistics.itemTypesDiscovered.add(glyph.getClass());
		}
		return super.identify(byHero);
	}

	public void setIDReady(){
		usesLeftToID = -1;
	}

	public boolean readyToIdentify(){
		return !isIdentified() && usesLeftToID <= 0;
	}

	@Override
	public boolean doEquip( Hero hero ) {

		// 15/25% chance
		if (hero.heroClass != HeroClass.CLERIC && hero.hasTalent(Talent.HOLY_INTUITION)
				&& cursed && !cursedKnown
				&& Random.Int(20) < 1 + 2*hero.pointsInTalent(Talent.HOLY_INTUITION)){
			cursedKnown = true;
			GLog.p(Messages.get(this, "curse_detected"));
			return false;
		}

		detach(hero.belongings.backpack);

		Armor oldArmor = hero.belongings.armor;
		if (hero.belongings.armor == null || hero.belongings.armor.doUnequip( hero, true, false )) {
			
			hero.belongings.armor = this;
			
			cursedKnown = true;
			if (cursed) {
				equipCursed( hero );
				GLog.n( Messages.get(Armor.class, "equip_cursed") );
			}
			
			((HeroSprite)hero.sprite).updateArmor();
			activate(hero);
			Talent.onItemEquipped(hero, this);
			hero.spend( timeToEquip( hero ) );

			if (Dungeon.hero.heroClass == HeroClass.WARRIOR && checkSeal() == null){
				BrokenSeal seal = oldArmor != null ? oldArmor.checkSeal() : null;
				if (seal != null && (!cursed || (seal.getGlyph() != null && seal.getGlyph().curse()))){

					GameScene.show(new WndOptions(new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0),
							Messages.titleCase(seal.trueName()),
							Messages.get(Armor.class, "seal_transfer"),
							Messages.get(Armor.class, "seal_transfer_yes"),
							Messages.get(Armor.class, "seal_transfer_no")){
						@Override
						protected void onSelect(int index) {
							super.onSelect(index);
							if (index == 0){
								seal.affixToArmor(Armor.this, oldArmor);
								updateQuickslot();
							}
							super.hide();
						}

						@Override
						public void hide() {
							//do nothing, must press button
						}
					});
				} else {
					hero.next();
				}
			} else {
				hero.next();
			}
			return true;
			
		} else {
			
			collect( hero.belongings.backpack );
			return false;
			
		}
	}

	@Override
	public void activate(Char ch) {
		if (seal != null) Buff.affect(ch, BrokenSeal.WarriorShield.class).setArmor(this);
	}

	public void deactivate(Char ch) {
		if (ch instanceof Hero) {
			BrokenSeal.WarriorShield sealBuff = ch.buff(BrokenSeal.WarriorShield.class);
			if (sealBuff != null) sealBuff.setArmor(null);
		}
	}

	public boolean equipSecondary(Hero hero) {
		detach(hero.belongings.backpack);

		if (hero.belongings.secondArmor == null
				|| hero.belongings.secondArmor.doUnequip(hero, true, false)) {
			hero.belongings.secondArmor = this;
			cursedKnown = true;
			if (cursed) {
				equipCursed(hero);
				GLog.n(Messages.get(Armor.class, "equip_cursed"));
			}
			Talent.onItemEquipped(hero, this);
			hero.spendAndNext(timeToEquip(hero));
			return true;
		}

		collect(hero.belongings.backpack);
		return false;
	}

	public void affixSeal(BrokenSeal seal){
		this.seal = seal;
		if (seal.level() > 0){
			//doesn't trigger upgrading logic such as affecting curses/glyphs
			int newLevel = trueLevel()+1;
			level(newLevel);
			Badges.validateItemLevelAquired(this);
		}
		if (seal.getGlyph() != null){
			inscribe(seal.getGlyph());
		}
		if (Dungeon.hero != null && Dungeon.hero.belongings.armor == this){
			Buff.affect(Dungeon.hero, BrokenSeal.WarriorShield.class).setArmor(this);
		}
	}

	public BrokenSeal detachSeal(){
		if (seal != null){

			if (Dungeon.hero != null && Dungeon.hero.belongings.armor == this) {
				BrokenSeal.WarriorShield sealBuff = Dungeon.hero.buff(BrokenSeal.WarriorShield.class);
				if (sealBuff != null) sealBuff.setArmor(null);
			}

			BrokenSeal detaching = seal;
			seal = null;

			if (detaching.level() > 0){
				degrade();
			}
			if (detaching.canTransferGlyph()){
				inscribe(null);
			} else {
				detaching.setGlyph(null);
			}
			return detaching;
		} else {
			return null;
		}
	}

	public BrokenSeal checkSeal(){
		return seal;
	}

	@Override
	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {
		boolean second = hero.belongings.secondArmor == this;
		if (second) hero.belongings.secondArmor = null;

		if (super.doUnequip( hero, collect, single )) {

			if (!second) {
				hero.belongings.armor = null;
				if (hero.sprite instanceof HeroSprite) ((HeroSprite)hero.sprite).updateArmor();
				deactivate(hero);
			}

			return true;

		} else {
			if (second) hero.belongings.secondArmor = this;
			return false;

		}
	}
	
	@Override
	public boolean isEquipped( Hero hero ) {
		return hero != null && (hero.belongings.armor() == this || hero.belongings.secondArmor() == this);
	}

	public final int DRMax(){
		return DRMax(buffedLvl());
	}

	public int DRMax(int lvl){
		if (Dungeon.isChallenged(Challenges.NO_ARMOR)){
			return 1 + tier + lvl + augment.defenseFactor(lvl);
		}

		int max = tier * (2 + lvl) + augment.defenseFactor(lvl);
		if (lvl > max){
			return ((lvl - max)+1)/2;
		} else {
			return max;
		}
	}

	public final int DRMin(){
		return DRMin(buffedLvl());
	}

	public int DRMin(int lvl){
		if (Dungeon.isChallenged(Challenges.NO_ARMOR)){
			return 0;
		}

		int max = DRMax(lvl);
		if (lvl >= max){
			return (lvl - max);
		} else {
			return lvl;
		}
	}

	//This exists so we can test what a char's base evasion would be without armor affecting it
	//more ugly static vars yaaay~
	public static boolean testingNoArmDefSkill = false;
	
	public float evasionFactor( Char owner, float evasion ){
		if (testingNoArmDefSkill) return evasion;
		
		if (hasGlyph(Stone.class, owner) && !Stone.testingEvasion()){
			return 0;
		}
		
		if (owner instanceof Hero){
			int aEnc = STRReq() - ((Hero) owner).STR();
			if (aEnc > 0) evasion /= Math.pow(1.5, aEnc);
			
			Momentum momentum = owner.buff(Momentum.class);
			if (momentum != null){
				evasion += momentum.evasionBonus(((Hero) owner).lvl, Math.max(0, -aEnc));
			}
		}
		
		return evasion + augment.evasionFactor(buffedLvl());
	}
	
	public float speedFactor( Char owner, float speed ){
		
		if (owner instanceof Hero) {
			int aEnc = STRReq() - ((Hero) owner).STR();
			if (aEnc > 0) speed /= Math.pow(1.2, aEnc);
		}
		
		return speed;
		
	}

	/** Legacy SPS armor hooks. Shattered armor keeps neutral values. */
	public float stealthFactor(Hero owner) {
		return 1f;
	}

	public int energyFactor(Hero owner) {
		return 1;
	}

	public int damageReductionFactor(Hero owner, int damageReduction) {
		int encumbrance = STRReq() - owner.STR();
		return encumbrance > 0 ? damageReduction - 2 * encumbrance : damageReduction;
	}
	
	@Override
	public int level() {
		int level = super.level();
		//TODO warrior's seal upgrade should probably be considered here too
		// instead of being part of true level
		if (curseInfusionBonus) level += 1 + level/6;
		return level;
	}
	
	@Override
	public Item upgrade() {
		return upgrade( false );
	}
	
	public Item upgrade( boolean inscribe ) {

		if (inscribe){
			if (glyph == null){
				inscribe( Glyph.random() );
			}
		} else if (glyph != null) {
			//chance to lose harden buff is 10/20/40/80/100% when upgrading from +6/7/8/9/10
			if (glyphHardened) {
				if (level() >= 6 && Random.Float(10) < Math.pow(2, level()-6)){
					glyphHardened = false;
				}

			//chance to remove curse is a static 33%
			} else if (hasCurseGlyph()){
				if (Random.Int(3) == 0) inscribe(null);

			//otherwise chance to lose glyph is 10/20/40/80/100% when upgrading from +4/5/6/7/8
			} else {

				//the chance from +4/5, and then +6 can be set to 0% with metamorphed runic transference
				int lossChanceStart = 4;
				if (Dungeon.hero != null && Dungeon.hero.heroClass != HeroClass.WARRIOR && Dungeon.hero.hasTalent(Talent.RUNIC_TRANSFERENCE)){
					lossChanceStart += 1+Dungeon.hero.pointsInTalent(Talent.RUNIC_TRANSFERENCE);
				}

				if (level() >= lossChanceStart && Random.Float(10) < Math.pow(2, level()-4)) {
					inscribe(null);
				}
			}
		}
		
		cursed = false;

		if (seal != null && seal.level() == 0)
			seal.upgrade();

		return super.upgrade();
	}
	
	public int proc( Char attacker, Char defender, int damage ) {

		if (defender.buff(MagicImmune.class) == null) {
			Glyph trinityGlyph = null;
			//only when it's the hero or a char that uses the hero's armor
			if (Dungeon.hero.buff(BodyForm.BodyFormBuff.class) != null
					&& (defender == Dungeon.hero || defender instanceof PrismaticImage || defender instanceof ShadowClone.ShadowAlly)){
				trinityGlyph = Dungeon.hero.buff(BodyForm.BodyFormBuff.class).glyph();
				if (glyph != null && trinityGlyph != null && trinityGlyph.getClass() == glyph.getClass()){
					trinityGlyph = null;
				}
			}

			if (defender instanceof Hero && isEquipped((Hero) defender)
					&& defender.buff(HolyWard.HolyArmBuff.class) != null){
				if (glyph != null &&
						(((Hero) defender).subClass == HeroSubClass.PALADIN || hasCurseGlyph())){
					damage = glyph.proc( this, attacker, defender, damage );
				}
				if (trinityGlyph != null){
					damage = trinityGlyph.proc( this, attacker, defender, damage );
				}
				int blocking = ((Hero) defender).subClass == HeroSubClass.PALADIN ? 3 : 1;
				damage -= Math.round(blocking * Glyph.genericProcChanceMultiplier(defender));

			} else {
				if (glyph != null) {
					damage = glyph.proc(this, attacker, defender, damage);
				}
				if (trinityGlyph != null){
					damage = trinityGlyph.proc( this, attacker, defender, damage );
				}
				//so that this effect procs for allies using this armor via aura of protection
				if (defender.alignment == Dungeon.hero.alignment
						&& Dungeon.hero.buff(AuraOfProtection.AuraBuff.class) != null
						&& (Dungeon.level.distance(defender.pos, Dungeon.hero.pos) <= 2 || defender.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null)
						&& Dungeon.hero.buff(HolyWard.HolyArmBuff.class) != null) {
					int blocking = Dungeon.hero.subClass == HeroSubClass.PALADIN ? 3 : 1;
					damage -= Math.round(blocking * Glyph.genericProcChanceMultiplier(defender));
				}
			}
			damage = Math.max(damage, 0);
		}
		
		if (!levelKnown && defender == Dungeon.hero) {
			float uses = Math.min( availableUsesToID, Talent.itemIDSpeedFactor(Dungeon.hero, this) );
			availableUsesToID -= uses;
			usesLeftToID -= uses;
			if (usesLeftToID <= 0) {
				if (ShardOfOblivion.passiveIDDisabled()){
					if (usesLeftToID > -1){
						GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready"), name());
					}
					setIDReady();
				} else {
					identify();
					GLog.p(Messages.get(Armor.class, "identify"));
					Badges.validateItemLevelAquired(this);
				}
			}
		}
		
		return damage;
	}
	
	@Override
	public void onHeroGainExp(float levelPercent, Hero hero) {
		levelPercent *= Talent.itemIDSpeedFactor(hero, this);
		if (!levelKnown && isEquipped(hero) && availableUsesToID <= USES_TO_ID/2f) {
			//gains enough uses to ID over 0.5 levels
			availableUsesToID = Math.min(USES_TO_ID/2f, availableUsesToID + levelPercent * USES_TO_ID);
		}
	}
	
	@Override
	public String name() {
		if (isEquipped(Dungeon.hero) && !hasCurseGlyph() && Dungeon.hero.buff(HolyWard.HolyArmBuff.class) != null
			&& (Dungeon.hero.subClass != HeroSubClass.PALADIN || glyph == null)){
				return Messages.get(HolyWard.class, "glyph_name", super.name());
			} else {
				return glyph != null && (cursedKnown || !glyph.curse()) ? glyph.name( super.name() ) : super.name();

		}
	}
	
	@Override
	public String info() {
		String info = super.info();
		
		if (levelKnown) {

			info += "\n\n" + Messages.get(Armor.class, "curr_absorb", tier, DRMin(), DRMax(), STRReq());
			
			if (Dungeon.hero != null && STRReq() > Dungeon.hero.STR()) {
				info += " " + Messages.get(Armor.class, "too_heavy");
			}
		} else {
			info += "\n\n" + Messages.get(Armor.class, "avg_absorb", tier, DRMin(0), DRMax(0), STRReq(0));

			if (Dungeon.hero != null && STRReq(0) > Dungeon.hero.STR()) {
				info += " " + Messages.get(Armor.class, "probably_too_heavy");
			}
		}

		switch (augment) {
			case EVASION:
				info += " " + Messages.get(Armor.class, "evasion");
				break;
			case DEFENSE:
				info += " " + Messages.get(Armor.class, "defense");
				break;
			case NONE:
		}

		if (isEquipped(Dungeon.hero) && !hasCurseGlyph() && Dungeon.hero.buff(HolyWard.HolyArmBuff.class) != null
				&& (Dungeon.hero.subClass != HeroSubClass.PALADIN || glyph == null)){
			info += "\n\n" + Messages.capitalize(Messages.get(Armor.class, "inscribed", Messages.get(HolyWard.class, "glyph_name", Messages.get(Glyph.class, "glyph"))));
			info += " " + Messages.get(HolyWard.class, "glyph_desc");
		} else if (glyph != null  && (cursedKnown || !glyph.curse())) {
			info += "\n\n" +  Messages.capitalize(Messages.get(Armor.class, "inscribed", glyph.name()));
			if (glyphHardened) info += " " + Messages.get(Armor.class, "glyph_hardened");
			info += " " + glyph.desc();
		} else if (glyphHardened){
			info += "\n\n" + Messages.get(Armor.class, "hardened_no_glyph");
		}
		
		if (cursed && isEquipped( Dungeon.hero )) {
			info += "\n\n" + Messages.get(Armor.class, "cursed_worn");
		} else if (cursedKnown && cursed) {
			info += "\n\n" + Messages.get(Armor.class, "cursed");
		} else if (!isIdentified() && cursedKnown){
			if (glyph != null && glyph.curse()) {
				info += "\n\n" + Messages.get(Armor.class, "weak_cursed");
			} else {
				info += "\n\n" + Messages.get(Armor.class, "not_cursed");
			}
		}

		if (seal != null) {
			info += "\n\n" + Messages.get(Armor.class, "seal_attached", seal.maxShield(tier, level()));
		}
		
		return info;
	}

	@Override
	public Emitter emitter() {
		if (seal == null) return super.emitter();
		Emitter emitter = new Emitter();
		emitter.pos(image.w(0)/2f + 2f, image.h(0)/3f);
		emitter.fillTarget = false;
		emitter.pour(Speck.factory( Speck.RED_LIGHT ), 0.6f);
		return emitter;
	}

	@Override
	public Item random() {
		//+0: 75% (3/4)
		//+1: 20% (4/20)
		//+2: 5%  (1/20)
		int n = 0;
		if (Random.Int(4) == 0) {
			n++;
			if (Random.Int(5) == 0) {
				n++;
			}
		}
		level(n);

		//we use a separate RNG here so that variance due to things like parchment scrap
		//does not affect levelgen
		Random.pushGenerator(Random.Long());

			//30% chance to be cursed
			//15% chance to be inscribed
			float effectRoll = Random.Float();
			if (effectRoll < 0.3f * ParchmentScrap.curseChanceMultiplier()) {
				inscribe(Glyph.randomCurse());
				cursed = true;
			} else if (effectRoll >= 1f - (0.15f * ParchmentScrap.enchantChanceMultiplier())){
				inscribe();
			}

		Random.popGenerator();

		return this;
	}

	public int STRReq(){
		return STRReq(level());
	}

	public int STRReq(int lvl){
		int req = STRReq(tier, lvl);
		if (masteryPotionBonus){
			req -= 2;
		}
		return req;
	}

	protected static int STRReq(int tier, int lvl){
		lvl = Math.max(0, lvl);

		//strength req decreases at +1,+3,+6,+10,etc.
		return (8 + Math.round(tier * 2)) - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
	}
	
	@Override
	public int value() {
		if (seal != null) return 0;

		int price = 20 * tier;
		if (hasGoodGlyph()) {
			price *= 1.5;
		}
		if (cursedKnown && (cursed || hasCurseGlyph())) {
			price /= 2;
		}
		if (levelKnown && level() > 0) {
			price *= (level() + 1);
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}

	public Armor inscribe( Glyph glyph ) {
		if (glyph == null || !glyph.curse()) curseInfusionBonus = false;
		this.glyph = glyph;
		updateQuickslot();
		//the hero needs runic transference to actually transfer, but we still attach the glyph here
		// in case they take that talent in the future
		if (seal != null){
			seal.setGlyph(glyph);
		}
		if (glyph != null && isIdentified() && Dungeon.hero != null
				&& Dungeon.hero.isAlive() && Dungeon.hero.belongings.contains(this)){
			Catalog.setSeen(glyph.getClass());
			Statistics.itemTypesDiscovered.add(glyph.getClass());
		}
		return this;
	}

	public Armor inscribe() {

		Class<? extends Glyph> oldGlyphClass = glyph != null ? glyph.getClass() : null;
		Glyph gl = Glyph.random( oldGlyphClass );

		return inscribe( gl );
	}

	public boolean hasGlyph(Class<?extends Glyph> type, Char owner) {
		if (owner.buff(MagicImmune.class) != null) {
			return false;
		} else if (glyph != null
				&& !glyph.curse()
				&& owner instanceof Hero
				&& isEquipped((Hero) owner)
				&& owner.buff(HolyWard.HolyArmBuff.class) != null
				&& ((Hero) owner).subClass != HeroSubClass.PALADIN){
			return false;
		} else if (owner.buff(BodyForm.BodyFormBuff.class) != null
				&& owner.buff(BodyForm.BodyFormBuff.class).glyph() != null
				&& owner.buff(BodyForm.BodyFormBuff.class).glyph().getClass().equals(type)){
			return true;
		} else if (glyph != null) {
			return glyph.getClass() == type;
		} else {
			return false;
		}
	}

	//these are not used to process specific glyph effects, so magic immune doesn't affect them
	public boolean hasGoodGlyph(){
		return glyph != null && !glyph.curse();
	}

	public boolean hasCurseGlyph(){
		return glyph != null && glyph.curse();
	}

	private static ItemSprite.Glowing HOLY = new ItemSprite.Glowing( 0xFFFF00 );

	@Override
	public ItemSprite.Glowing glowing() {
		if (isEquipped(Dungeon.hero) && !hasCurseGlyph() && Dungeon.hero.buff(HolyWard.HolyArmBuff.class) != null
				&& (Dungeon.hero.subClass != HeroSubClass.PALADIN || glyph == null)){
			return HOLY;
		} else {
			return glyph != null && (cursedKnown || !glyph.curse()) ? glyph.glowing() : null;
		}
	}
	
	public static abstract class Glyph implements Bundlable {
		
		public static final Class<?>[] common = new Class<?>[]{
				Changeglyph.class, Crystalglyph.class, Darkglyph.class, Earthglyph.class,
				Electricityglyph.class, Fireglyph.class, Iceglyph.class, Lightglyph.class,
				Revivalglyph.class, Testglyph.class, AdaptGlyph.class, RecoilGlyph.class,
				Energyglyph.class };

		public static final Class<?>[] uncommon = new Class<?>[]{};

		public static final Class<?>[] rare = new Class<?>[]{};

		public static final float[] typeChances = new float[]{
				100,
				0,
				0
		};

		public static final Class<?>[] curses = new Class<?>[]{
				AntiEntropy.class, Corrosion.class, Displacement.class, Metabolism.class,
				Multiplicity.class, Stench.class, Overgrowth.class, Bulk.class
		};
		
		public abstract int proc( Armor armor, Char attacker, Char defender, int damage );

		protected float procChanceMultiplier( Char defender ){
			return genericProcChanceMultiplier( defender );
		}

		public static float genericProcChanceMultiplier( Char defender ){
			float multi = RingOfArcana.enchantPowerMultiplier(defender);

			if (Dungeon.hero.alignment == defender.alignment
					&& Dungeon.hero.buff(AuraOfProtection.AuraBuff.class) != null
					&& (Dungeon.level.distance(defender.pos, Dungeon.hero.pos) <= 2 || defender.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null)){
				multi += 0.25f + 0.25f*Dungeon.hero.pointsInTalent(Talent.AURA_OF_PROTECTION);
			}

			return multi;
		}
		
		public String name() {
			if (!curse())
				return name( Messages.get(this, "glyph") );
			else
				return name( Messages.get(Item.class, "curse"));
		}
		
		public String name( String armorName ) {
			return Messages.get(this, "name", armorName);
		}

		public String desc() {
			return Messages.get(this, "desc");
		}

		public boolean curse() {
			return false;
		}
		
		@Override
		public void restoreFromBundle( Bundle bundle ) {
		}

		@Override
		public void storeInBundle( Bundle bundle ) {
		}
		
		public abstract ItemSprite.Glowing glowing();

		@SuppressWarnings("unchecked")
		public static Glyph random( Class<? extends Glyph> ... toIgnore ) {
			switch(Random.chances(typeChances)){
				case 0: default:
					return randomCommon( toIgnore );
				case 1:
					return randomUncommon( toIgnore );
				case 2:
					return randomRare( toIgnore );
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Glyph randomCommon( Class<? extends Glyph> ... toIgnore ){
			ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(common));
			glyphs.removeAll(Arrays.asList(toIgnore));
			if (glyphs.isEmpty()) {
				return random();
			} else {
				return (Glyph) Reflection.newInstance(Random.element(glyphs));
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Glyph randomUncommon( Class<? extends Glyph> ... toIgnore ){
			ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(uncommon));
			glyphs.removeAll(Arrays.asList(toIgnore));
			if (glyphs.isEmpty()) {
				return random();
			} else {
				return (Glyph) Reflection.newInstance(Random.element(glyphs));
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Glyph randomRare( Class<? extends Glyph> ... toIgnore ){
			ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(rare));
			glyphs.removeAll(Arrays.asList(toIgnore));
			if (glyphs.isEmpty()) {
				return random();
			} else {
				return (Glyph) Reflection.newInstance(Random.element(glyphs));
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Glyph randomCurse( Class<? extends Glyph> ... toIgnore ){
			ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(curses));
			glyphs.removeAll(Arrays.asList(toIgnore));
			if (glyphs.isEmpty()) {
				return random();
			} else {
				return (Glyph) Reflection.newInstance(Random.element(glyphs));
			}
		}
		
	}
}
