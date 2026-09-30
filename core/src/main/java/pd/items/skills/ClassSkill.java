/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.skills;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.SkillRecharge;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.items.Item;
import pd.items.Heap;
import pd.items.bags.Bag;
import pd.messages.Messages;
import pd.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import pd.Assets;
import pd.actors.mobs.Mob;
import pd.effects.particles.ElmoParticle;
import com.watabou.noosa.audio.Sample;

/** SPS-PD 0.9.8's reusable, class-specific skill item. */
public abstract class ClassSkill extends Item {

	public static final String AC_SPECIAL = "SPECIAL";
	public static final String AC_SPECIAL_TWO = "SPECIAL_TWO";
	public static final String AC_SPECIAL_THREE = "SPECIAL_THREE";
	public static final String AC_SPECIAL_FOUR = "SPECIAL_FOUR";

	private static final String COOLDOWN_PROGRESS = "colddown";
	private static final String CHARGE = "charge";

	private static int charge;
	private float cooldownProgress;
	private SkillCharger skillCharger;

	{
		defaultAction = AC_SPECIAL;
		stackable = true;
		unique = true;
	}

	public static ClassSkill createFor(HeroClass heroClass) {
		if (heroClass == null) return null;
		switch (heroClass) {
			case WARRIOR: return new WarriorSkill();
			case MAGE: return new MageSkill();
			case ROGUE: return new RogueSkill();
			case HUNTRESS: return new HuntressSkill();
			case PERFORMER: return new PerformerSkill();
			case SOLDIER: return new SoldierSkill();
			case FOLLOWER: return new FollowerSkill();
			case ASCETIC: return new AsceticSkill();
			default: return null;
		}
	}

	public static int remainingCooldown() {
		return charge;
	}

	public static void resetCooldown() {
		charge = 0;
	}

	protected final void addCooldown(int amount) {
		charge = Math.max(0, charge + amount);
		updateQuickslot();
	}

	@Override
	public boolean collect(Bag container) {
		if (!super.collect(container)) return false;
		if (container.owner != null) charge(container.owner);
		return true;
	}

	public void charge(Char owner) {
		if (skillCharger == null) skillCharger = new SkillCharger();
		skillCharger.attachTo(owner);
	}

	@Override
	protected void onDetach() {
		if (skillCharger != null) {
			skillCharger.detach();
			skillCharger = null;
		}
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		if (!coolingDown(hero)) {
			actions.add(AC_SPECIAL);
			if (hero.lvl > 20) actions.add(AC_SPECIAL_TWO);
			if (hero.lvl > 30) actions.add(AC_SPECIAL_THREE);
			if (hero.lvl > 40) actions.add(AC_SPECIAL_FOUR);
		}
		return actions;
	}

	@Override
	public String defaultAction() {
		return Dungeon.hero != null && !coolingDown(Dungeon.hero) ? AC_SPECIAL : null;
	}

	private static boolean coolingDown(Hero hero) {
		return charge > 0 && hero.buff(SkillRecharge.class) == null;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!isSkillAction(action)) {
			super.execute(hero, action);
			return;
		}
		if (coolingDown(hero)) {
			GLog.w(Messages.get(ClassSkill.class, "cooldown"));
			return;
		}
		int requiredLevel = requiredLevel(action);
		if (hero.lvl <= requiredLevel) {
			GLog.w(Messages.get(ClassSkill.class, "level_required", requiredLevel + 1));
			return;
		}
		curUser = hero;
		Invisibility.dispel(hero);
		if (AC_SPECIAL.equals(action)) doSpecial();
		else if (AC_SPECIAL_TWO.equals(action)) doSpecial2();
		else if (AC_SPECIAL_THREE.equals(action)) doSpecial3();
		else doSpecial4();
	}

	private static boolean isSkillAction(String action) {
		return AC_SPECIAL.equals(action) || AC_SPECIAL_TWO.equals(action)
				|| AC_SPECIAL_THREE.equals(action) || AC_SPECIAL_FOUR.equals(action);
	}

	private static int requiredLevel(String action) {
		if (AC_SPECIAL_TWO.equals(action)) return 20;
		if (AC_SPECIAL_THREE.equals(action)) return 30;
		if (AC_SPECIAL_FOUR.equals(action)) return 40;
		return -1;
	}

	public abstract void doSpecial();
	public abstract void doSpecial2();
	public abstract void doSpecial3();
	public abstract void doSpecial4();

	protected final boolean visibleMob(Mob mob, int range) {
		return Dungeon.level != null && mob.pos >= 0 && mob.pos < Dungeon.level.heroFOV.length
				&& Dungeon.level.heroFOV[mob.pos]
				&& Dungeon.level.distance(curUser.pos, mob.pos) <= range;
	}

	protected final void dropAtHero(Item item) {
		if (item != null && Dungeon.level != null) {
			Heap heap = Dungeon.level.drop(item, curUser.pos);
			if (heap.sprite != null) heap.sprite.drop(curUser.pos);
		}
	}

	protected final void finishSkillCast() {
		if (curUser.sprite != null) {
			curUser.spend(1f);
			curUser.busy();
			curUser.sprite.centerEmitter().burst(ElmoParticle.FACTORY, 4);
			curUser.sprite.operate(curUser.pos);
		} else {
			curUser.spendAndNext(1f);
		}
		Sample.INSTANCE.play(Assets.Sounds.READ);
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 0; }

	@Override
	public String info() {
		return desc() + "\n\n" + Messages.get(ClassSkill.class, "charge", charge);
	}

	@Override public String status() { return Integer.toString(charge); }

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(COOLDOWN_PROGRESS, cooldownProgress);
		bundle.put(CHARGE, charge);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		cooldownProgress = Math.max(0f, bundle.getFloat(COOLDOWN_PROGRESS));
		charge = Math.max(0, bundle.getInt(CHARGE));
	}

	protected class SkillCharger extends Buff {
		@Override
		public boolean act() {
			if (charge > 0) {
				int energy = Dungeon.hero == null || Dungeon.hero.belongings.armor() == null ? 1
						: Dungeon.hero.belongings.armor().energyFactor(Dungeon.hero);
				cooldownProgress += Math.max(1, energy);
				if (target.buff(SkillRecharge.class) != null) cooldownProgress += 20f;
				if (cooldownProgress >= 20f) {
					cooldownProgress = 0f;
					charge--;
					updateQuickslot();
				}
			}
			spend(TICK);
			return true;
		}
	}
}
