/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Frost;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Sleep;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.items.Heap;
import pd.items.KindOfWeapon;
import pd.items.armor.Armor;
import pd.items.eggs.YearPetEgg;
import pd.items.quest.AdventureJournal;
import pd.items.wands.Wand;
import pd.items.wands.WandOfBlastWave;
import pd.items.weapon.melee.special.FireCracker;
import pd.items.weapon.missiles.MoneyPack;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.sprites.BeastYearSprite;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** The original Spring Festival year beast and its turn-scaled combat rules. */
public class YearBeast2 extends Mob {

	private static final String TIMES = "times";
	private static final String GLASS_HITS = "glass_hits";
	private int times;
	private int glassHits;

	{
		spriteClass = BeastYearSprite.class;
		baseSpeed = 1.5f;
		HP = HT = 1000;
		EXP = 0;
		defenseSkill = 30;
		viewDistance = 6;
		flying = true;
		resistances.add(ToxicGas.class);
		resistances.add(Poison.class);
		immunities.add(Charm.class);
		immunities.add(Sleep.class);
		immunities.add(Terror.class);
		immunities.add(Vertigo.class);
		immunities.add(Burning.class);
	}

	@Override
	protected boolean act() {
		times++;
		return super.act();
	}

	@Override
	public int damageRoll() {
		return Math.round(Random.NormalIntRange(40, 60) * (1f + times * 0.01f));
	}

	@Override
	public int attackSkill(Char target) {
		return 40;
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return Dungeon.level.distance(pos, enemy.pos) <= 2;
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(2) == 0) Buff.affect(enemy, Burning.class).reignite(enemy, 3f);
		else Buff.affect(enemy, Frost.class, Frost.DURATION);

		if (Random.Int(5) == 0) Buff.affect(enemy, Charm.class, 4f).object = id();
		if (Random.Int(5) == 0) {
			int opposite = enemy.pos + enemy.pos - pos;
			Ballistica trajectory = new Ballistica(enemy.pos, opposite, Ballistica.MAGIC_BOLT);
			WandOfBlastWave.throwChar(enemy, trajectory, 1, false, false, this);
			Buff.affect(enemy, Vertigo.class, 3f);
		}
		if (enemy == Dungeon.hero && Random.Int(10) == 0) disarm(Dungeon.hero);
		return damage;
	}

	private void disarm(Hero hero) {
		if (Random.Int(2) == 0) {
			KindOfWeapon weapon = hero.belongings.weapon();
			if (weapon != null && !weapon.cursed && !fixedEquipment(weapon)) {
				hero.belongings.weapon = null;
				Dungeon.quickslot.clearItem(weapon);
				weapon.updateQuickslot();
				Dungeon.level.drop(weapon, hero.pos).sprite.drop();
				GLog.w(Messages.get(this, "disarm"));
			}
		} else {
			Armor armor = hero.belongings.armor();
			if (armor != null && !armor.cursed && !fixedEquipment(armor)) {
				hero.belongings.armor = null;
				Dungeon.level.drop(armor, hero.pos).sprite.drop();
				GLog.w(Messages.get(this, "disarm"));
			}
		}
	}

	//固定装备（免疫缴械）：类名显式名单（这些武器/护甲改名需同步本表）
	private static final Set<String> FIXED_EQUIPMENT = new HashSet<>(Arrays.asList(
			"Knuckles", "FightGloves", "WoodenArmor", "RubberArmor", "BaseArmor"));

	private boolean fixedEquipment(Object item) {
		return item != null && FIXED_EQUIPMENT.contains(item.getClass().getSimpleName());
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		if (damage > 200 && glassHits == 0) glassHits = 3;
		return super.defenseProc(enemy, damage);
	}

	@Override
	public void damage(int damage, Object source) {
		if (source instanceof Wand) damage /= 3;
		if (source instanceof FireCracker || source instanceof MoneyPack) times = 0;
		if (times > 50) times -= 3;
		float multiplier = 1f - times * 0.01f;
		if (multiplier < 0) return;
		damage = (int) Math.ceil(damage * multiplier);
		if (glassHits > 0 && damage >= 10) {
			damage = 10;
			glassHits--;
		}
		super.damage(damage, source);
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		Heap heap = Dungeon.level.drop(new YearPetEgg(), pos);
		if (heap != null && heap.sprite != null) heap.sprite.drop();
		if (Dungeon.branch == AdventureJournal.branchFor(6)) AdventureJournal.complete(6);
	}

	@Override
	public void notice() {
		super.notice();
		yell(Messages.get(this, "notice"));
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TIMES, times);
		bundle.put(GLASS_HITS, glassHits);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		times = bundle.getInt(TIMES);
		glassHits = bundle.getInt(GLASS_HITS);
	}
}
