/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.normalarmor;

import pd.atlas.IconEntry;

import pd.Challenges;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.armor.glyphs.Stone;
import pd.items.equipment.armor.specialarmor.AsceticArmor;
import pd.items.equipment.armor.specialarmor.FollowerArmor;
import pd.items.equipment.armor.specialarmor.HuntressArmor;
import pd.items.equipment.armor.specialarmor.MageArmor;
import pd.items.equipment.armor.specialarmor.PerformerArmor;
import pd.items.equipment.armor.specialarmor.RogueArmor;
import pd.items.equipment.armor.specialarmor.SoldierArmor;
import pd.items.equipment.armor.specialarmor.WarriorArmor;
import pd.items.equipment.rings.RingOfEvasion;
import pd.messages.Messages;

/** Shared implementation of SPS-PD's defense, dexterity, stealth and energy armor stats. */
public class NormalArmor extends Armor {

	public final float DEX;
	public final float STE;
	public final int ENG;
	private final int baseMin;
	private final int baseMax;
	private final int strengthOffset;
	private final int minGrowth;
	private final int maxGrowth;

	protected NormalArmor(int tier, float dex, float stealth, int energy,
			int baseMin, int baseMax, int strengthOffset, int minGrowth, int maxGrowth, IconEntry image) {
		super(tier);
		DEX = dex;
		STE = stealth;
		ENG = energy;
		this.baseMin = baseMin;
		this.baseMax = baseMax;
		this.strengthOffset = strengthOffset;
		this.minGrowth = minGrowth;
		this.maxGrowth = maxGrowth;
		this.image = image;
	}

	@Override
	public int DRMin(int level) {
		if (Dungeon.isChallenged(Challenges.NO_ARMOR)) return super.DRMin(level);
		return Math.max(0, baseMin + minGrowth * Math.max(0, level));
	}

	@Override
	public int DRMax(int level) {
		if (Dungeon.isChallenged(Challenges.NO_ARMOR)) return super.DRMax(level);
		return Math.max(0, baseMax + maxGrowth * Math.max(0, level));
	}

	@Override
	public int STRReq(int level) {
		return Math.max(0, 8 + tier * 2 + strengthOffset);
	}

	@Override
	public float evasionFactor(Char owner, float evasion) {
		if (testingNoArmDefSkill) return evasion;
		if (hasGlyph(Stone.class, owner) && !Stone.testingEvasion()) return 0;
		if (owner instanceof Hero) {
			int encumbrance = STRReq() - ((Hero)owner).STR();
			if (encumbrance > 0) evasion /= Math.pow(1.5, encumbrance);
		}
		return evasion * (DEX + RingOfEvasion.dexterityBonus(owner));
	}

	@Override public float speedFactor(Char owner, float speed) { return speed; }

	@Override
	public float stealthFactor(Hero owner) {
		int encumbrance = STRReq() - owner.STR();
		float stealth = STE + RingOfEvasion.stealthBonus(owner);
		return encumbrance > 0 ? (float)(stealth / Math.pow(1.5, encumbrance)) : stealth;
	}

	@Override public int energyFactor(Hero owner) { return ENG; }

	@Override
	public int damageReductionFactor(Hero owner, int damageReduction) {
		int encumbrance = STRReq() - owner.STR();
		return encumbrance > 0
				? Math.max(damageReduction * (1 - encumbrance / 3), 0)
				: damageReduction - encumbrance;
	}

	@Override
	public String info() {
		return super.info() + "\n\n" + Messages.get(NormalArmor.class, "sps_stats",
				Messages.decimalFormat("#.##", DEX), Messages.decimalFormat("#.##", STE), ENG);
	}

	public Item safeUpgrade() { return upgrade(glyph != null); }

	@Override
	public int value() {
		int price = 100;
		if (glyph != null) price = (int)(price * 1.5f);
		if (cursed && cursedKnown) price /= 2;
		if (levelKnown && level() > 0) price *= level() + 1;
		else if (levelKnown && level() < 0) price /= 1 - level();
		return Math.max(1, price);
	}

	public static NormalArmor upgrade(Hero owner) {
		if (owner == null || owner.heroClass == null) return null;
		switch (owner.heroClass) {
			case WARRIOR: return new WarriorArmor();
			case MAGE: return new MageArmor();
			case ROGUE: return new RogueArmor();
			case HUNTRESS: return new HuntressArmor();
			case PERFORMER: return new PerformerArmor();
			case SOLDIER: return new SoldierArmor();
			case FOLLOWER: return new FollowerArmor();
			case ASCETIC: return new AsceticArmor();
			default: return null;
		}
	}
}
