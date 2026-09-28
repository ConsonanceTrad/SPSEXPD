/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorruptGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArmorBreak;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BeCorrupt;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DefenceUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GlassShield;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GrowSeed;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silent;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EnergyParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.StoneOre;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMending;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows.GlassFruit;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SpsHallsSprites;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public final class SpsHallsMobs {
	private SpsHallsMobs() { }

	public static class DemonGoo extends Mob {
		private static final String GENERATION = "generation";
		private int generation;

		{
			spriteClass = SpsHallsSprites.DemonGoo.class;
			HP = HT = 300 + legacyDepthAdjustment(0) * Random.NormalIntRange(4, 7);
			defenseSkill = 10 + legacyDepthAdjustment(1);
			baseSpeed = 2f;
			viewDistance = Light.DISTANCE;
			EXP = 10;
			maxLvl = 35;
			loot = StoneOre.class;
			lootChance = 1f;
			properties.add(Property.DEMONIC);
			properties.add(Property.ELEMENT);
			resistances.add(ToxicGas.class);
			immunities.add(Roots.class);
		}

		@Override protected boolean act() {
			boolean result = super.act();
			if (!flying && Dungeon.level.water[pos]) {
				if (HP < HT) {
					HP++;
					if (sprite != null) sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
				} else if (HP == HT && HT < 200) {
					HT += 5;
					HP = HT;
					if (sprite != null) sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
				}
			}
			return result;
		}

		@Override public int damageRoll() { return Random.NormalIntRange(30 + legacyDepthAdjustment(1), 60 + legacyDepthAdjustment(1)); }
		@Override public int attackSkill(Char target) { return 35 + legacyDepthAdjustment(1); }
		@Override public int drRoll() { return Random.NormalIntRange(10, 15); }

		@Override public int defenseProc(Char enemy, int damage) {
			if (HP >= damage + 2) {
				ArrayList<Integer> candidates = new ArrayList<>();
				for (int offset : PathFinder.NEIGHBOURS4) {
					int cell = pos + offset;
					if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell] && Actor.findChar(cell) == null) candidates.add(cell);
				}
				if (!candidates.isEmpty()) {
					GLog.n(Messages.get(this, "divide"));
					DemonGoo clone = split();
					clone.pos = Random.element(candidates);
					clone.state = clone.HUNTING;
					GameScene.add(clone, 1f);
					clone.HP = Math.max(1, (HP - damage) / 2);
					Actor.add(new Pushing(clone, pos, clone.pos));
					Dungeon.level.occupyCell(clone);
					HP -= clone.HP;
				}
			}
			return super.defenseProc(enemy, damage);
		}

		protected DemonGoo newSplit() {
			return new DemonGoo();
		}

		private DemonGoo split() {
			DemonGoo clone = newSplit();
			clone.generation = generation + 1;
			if (buff(Burning.class) != null) Buff.affect(clone, Burning.class).reignite(clone, 3f);
			if (buff(Poison.class) != null) Buff.affect(clone, Poison.class).set(2f);
			return clone;
		}

		@Override public int attackProc(Char enemy, int damage) {
			damage = super.attackProc(enemy, damage);
			if (Random.Int(3) == 0) {
				Buff.affect(enemy, Ooze.class).set(10f);
				if (enemy.sprite != null) enemy.sprite.burst(0x000000, 5);
			}
			return damage;
		}

		@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(GENERATION, generation); }
		@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); generation = bundle.getInt(GENERATION); }
	}

	public static class ThiefImp extends Mob {
		private static final String ITEM = "item";
		public Item item;

		{
			spriteClass = SpsHallsSprites.ThiefImp.class;
			HP = HT = 200 + legacyDepthAdjustment(0) * Random.NormalIntRange(4, 7);
			defenseSkill = 20 + legacyDepthAdjustment(1);
			EXP = 13;
			maxLvl = 35;
			flying = true;
			loot = com.shatteredpixel.shatteredpixeldungeon.items.Generator.Category.BERRY;
			lootChance = 0.05f;
			FLEEING = new Fleeing();
			properties.add(Property.DEMONIC);
		}

		@Override public int damageRoll() { return Random.NormalIntRange(15, 25 + legacyDepthAdjustment(0)); }
		@Override public int attackSkill(Char target) { return 30 + legacyDepthAdjustment(0); }
		@Override public int drRoll() { return Random.NormalIntRange(10, 20); }
		@Override public Item createLoot() { return new Gold(Random.NormalIntRange(100, 250)); }

		@Override public int attackProc(Char enemy, int damage) {
			damage = super.attackProc(enemy, damage);
			if (item == null && enemy instanceof com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
					&& steal((com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero)enemy)) state = FLEEING;
			return damage;
		}

		@Override public int defenseProc(Char enemy, int damage) {
			if (state == FLEEING) Dungeon.level.drop(new Gold(), pos).sprite.drop();
			return super.defenseProc(enemy, damage);
		}

		protected boolean steal(com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero hero) {
			Item toSteal = hero.belongings.randomUnequipped();
			if (toSteal == null || toSteal.unique || toSteal.level() >= 1) return false;
			GLog.w(Messages.get(this, "stole", toSteal.name()));
			if (!toSteal.stackable) Dungeon.quickslot.convertToPlaceholder(toSteal);
			Item.updateQuickslot();
			item = toSteal.detach(hero.belongings.backpack);
			if (item instanceof Honeypot) item = ((Honeypot)item).shatter(this, pos);
			else if (item instanceof Honeypot.ShatteredPot) ((Honeypot.ShatteredPot)item).pickupPot(this);
			return true;
		}

		@Override public void die(Object cause) {
			super.die(cause);
			if (item != null) {
				Dungeon.level.drop(item, pos).sprite.drop();
				if (item instanceof Honeypot.ShatteredPot) ((Honeypot.ShatteredPot)item).dropPot(this, pos);
				item = null;
			}
		}

		@Override public String description() {
			String result = super.description();
			if (item != null) result += Messages.get(this, "carries", item.name());
			return result;
		}

		@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(ITEM, item); }
		@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); item = (Item)bundle.get(ITEM); }

		private class Fleeing extends Mob.Fleeing {
			@Override protected void nowhereToRun() {
				if (buff(Terror.class) == null) {
					if (sprite != null) sprite.showStatus(CharSprite.NEGATIVE, Messages.get(Mob.class, "rage"));
					state = HUNTING;
				} else {
					super.nowhereToRun();
				}
			}
		}
	}

	public static class DemonFlower extends Mob {
		private static final String DEBUFF_COUNTER = "debuff_counter";
		private static final int DEBUFF_DELAY = 6;
		private int debuffCounter = DEBUFF_DELAY;

		{
			spriteClass = SpsHallsSprites.DemonFlower.class;
			HP = HT = 450;
			defenseSkill = 5;
			EXP = 25;
			maxLvl = 35;
			loot = com.shatteredpixel.shatteredpixeldungeon.items.Generator.Category.SEED;
			lootChance = 0.5f;
			properties.add(Property.DEMONIC);
			resistances.add(ToxicGas.class);
			resistances.add(Poison.class);
			immunities.add(GrowSeed.class);
		}

		@Override public int damageRoll() { return Random.NormalIntRange(26, 37); }
		@Override public int attackSkill(Char target) { return 35 + legacyDepthAdjustment(1); }
		@Override public float attackDelay() { return 0.33f; }
		@Override public int drRoll() { return Random.NormalIntRange(0, 5); }

		@Override protected boolean doAttack(Char enemy) {
			debuffCounter--;
			if (debuffCounter <= 0 && Dungeon.level.adjacent(pos, enemy.pos) && buff(Silent.class) == null) {
				debuffCounter = DEBUFF_DELAY;
				Buff.prolong(enemy, AttackDown.class, 5f).level(30);
				Buff.prolong(enemy, ArmorBreak.class, 5f).level(30);
				yell(Messages.get(this, "debuff"));
				spend(TICK);
				return true;
			}
			return super.doAttack(enemy);
		}

		@Override public int defenseProc(Char enemy, int damage) {
			if (damage < HT / 9 && buff(DefenceUp.class) == null) Buff.affect(this, DefenceUp.class, 3f).level(damage);
			return super.defenseProc(enemy, damage);
		}

		@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(DEBUFF_COUNTER, debuffCounter); }
		@Override public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			debuffCounter = bundle.contains(DEBUFF_COUNTER) ? Math.max(1, bundle.getInt(DEBUFF_COUNTER)) : DEBUFF_DELAY;
		}
	}

	public static class Sufferer extends Mob {
		{
			spriteClass = SpsHallsSprites.Sufferer.class;
			HP = HT = 180 + legacyDepthAdjustment(0) * Random.NormalIntRange(4, 7);
			defenseSkill = 16 + legacyDepthAdjustment(1);
			EXP = 16;
			maxLvl = 35;
			loot = com.shatteredpixel.shatteredpixeldungeon.items.Generator.Category.SCROLL;
			lootChance = 0.35f;
			properties.add(Property.DEMONIC);
			properties.add(Property.MAGICER);
			properties.add(Property.HUMAN);
			immunities.add(Amok.class);
			immunities.add(Terror.class);
			immunities.add(Sleep.class);
		}

		@Override public int damageRoll() {
			int first = 7 + legacyDepthAdjustment(0);
			int second = 10 + legacyDepthAdjustment(1);
			return Random.NormalIntRange(Math.min(first, second), Math.max(first, second));
		}
		@Override public int attackSkill(Char target) { return 34 + legacyDepthAdjustment(1); }
		@Override public int drRoll() { return Random.NormalIntRange(0, 10); }

		@Override public int attackProc(Char enemy, int damage) {
			if (Random.Int(3) == 0) Buff.affect(enemy, BeCorrupt.class).level(20);
			enemy.damage(damageRoll(), DamageType.DARK_DAMAGE);
			return 0;
		}

		@Override public int defenseProc(Char enemy, int damage) {
			if (damage > 50 && buff(GlassShield.class) == null) Buff.affect(this, GlassShield.class).turns(4);
			return super.defenseProc(enemy, damage);
		}
	}

	public static class DemonRabbit extends LegacyDualLootMob {
		private static final String CHARGED = "charged";
		private boolean charged;

		{
			spriteClass = SpsHallsSprites.DemonRabbit.class;
			HP = HT = 200 + legacyDepthAdjustment(0) * Random.NormalIntRange(3, 5);
			defenseSkill = 30 + legacyDepthAdjustment(1);
			baseSpeed = 0.4f;
			EXP = 12;
			maxLvl = 30;
			setupLegacyDualLoot(GlassFruit.class, 0.2f, PotionOfMending.class, 0.1f);
			properties.add(Property.DEMONIC);
			immunities.add(Amok.class);
			immunities.add(Terror.class);
			immunities.add(Bleeding.class);
		}

		@Override protected boolean act() {
			if (!enemySeen) charged = false;
			return super.act();
		}

		@Override protected boolean doAttack(Char enemy) {
			if (enemySeen && state != SLEEPING && paralysed == 0 && !charged) {
				charged = true;
				if (sprite != null && Dungeon.level.heroFOV[pos]) sprite.centerEmitter().burst(EnergyParticle.FACTORY, 15);
				spend(attackDelay());
				return true;
			}
			charged = false;
			return super.doAttack(enemy);
		}

		@Override protected boolean canAttack(Char enemy) {
			return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
		}

		@Override public void onAttackComplete() {
			int oldPos = pos;
			if (enemy != null && getFurther(enemy.pos)) moveSprite(oldPos, pos);
			spend(attackDelay());
			super.onAttackComplete();
		}

		@Override public int damageRoll() { return Random.NormalIntRange(5, 10); }
		@Override public int attackSkill(Char target) { return 25 + legacyDepthAdjustment(1); }
		@Override public int drRoll() { return Random.NormalIntRange(3, 5); }
		@Override public int attackProc(Char enemy, int damage) {
			GameScene.add(Blob.seed(enemy.pos, 8, CorruptGas.class));
			charged = false;
			return super.attackProc(enemy, damage);
		}

		@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGED, charged); }
		@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charged = bundle.getBoolean(CHARGED); }
	}
}
