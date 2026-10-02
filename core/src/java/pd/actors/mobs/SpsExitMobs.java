/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.CorruptGas;
import pd.actors.blobs.DarkGas;
import pd.actors.blobs.SandStorm;
import pd.actors.blobs.StenchGas;
import pd.actors.blobs.SwampGas;
import pd.actors.blobs.ToxicGas;
import pd.actors.blobs.damageblobs.EarthEffectDamage;
import pd.actors.blobs.damageblobs.FireEffectDamage;
import pd.actors.blobs.damageblobs.IceEffectDamage;
import pd.actors.buffs.Amok;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.BeOld;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Charm;
import pd.actors.buffs.DBurning;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.EnergyArmor;
import pd.actors.buffs.FrostIce;
import pd.actors.buffs.Locked;
import pd.actors.buffs.MagicArmor;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Roots;
import pd.actors.buffs.ShieldArmor;
import pd.actors.buffs.Silent;
import pd.actors.buffs.Sleep;
import pd.actors.buffs.StoneIce;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.actors.damagetype.DamageType;
import pd.effects.Speck;
import pd.effects.Wound;
import pd.items.Generator;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.Item;
import pd.items.KindOfWeapon;
import pd.items.StoneOre;
import pd.items.equipment.armor.normalarmor.WoodenArmor;
import pd.items.equipment.artifacts.CapeOfThorns;
import pd.items.equipment.artifacts.HornOfPlenty;
import pd.items.consum.food.MysteryMeat;
import pd.items.consum.food.meatfood.Meat;
import pd.items.consum.food.staplefood.NormalRation;
import pd.items.consum.food.staplefood.OverpricedRation;
import pd.items.consum.food.vegetable.NutVegetable;
import pd.items.consum.potions.PotionOfFrost;
import pd.items.consum.potions.PotionOfToxicGas;
import pd.items.consum.scrolls.ScrollOfLullaby;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.wands.WandOfAcid;
import pd.items.equipment.wands.WandOfFreeze;
import pd.items.equipment.wands.fusion.WandOfFlow;
import pd.items.equipment.weapon.enchantments.EnchantmentIce2;
import pd.items.equipment.weapon.enchantments.EnchantmentIce;
import pd.items.equipment.weapon.melee.normalweapon.FightGloves;
import pd.items.equipment.weapon.melee.normalweapon.Handaxe;
import pd.items.equipment.weapon.melee.normalweapon.Knuckles;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.sprites.AcidicSprite;
import pd.sprites.AlbinoSprite;
import pd.sprites.BanditSprite;
import pd.sprites.SeniorSprite;
import pd.sprites.ShieldedSprite;
import pd.sprites.SpsExitSprites;
import pd.sprites.SuccubusSprite;
import render.noosa.audio.Sample;
import render.utils.data.BArray;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

/** Isolated SPS-PD 0.9.8 variants used by ordinary-floor exit rooms. */
public final class SpsExitMobs {
	private SpsExitMobs() { }

	public interface ExitGuard { }

	public static Mob randomForDepth(int depth) {
		switch (depth) {
			case 2: return new GuardAlbino();
			case 3: return Random.chances(new float[]{1, 0.5f}) == 0
					? new GuardAlbino() : new GuardVagrant();
			case 4: return Random.chances(new float[]{0.5f, 1}) == 0
					? new GuardAlbino() : new GuardVagrant();
			case 7: return Random.chances(new float[]{0.5f, 1}) == 0
					? new GuardBandit() : new GuardVagrant();
			case 8: return Random.chances(new float[]{1, 0.5f}) == 0
					? new GuardBandit() : new ExBambooMob();
			case 9: return Random.Int(2) == 0 ? new GuardBandit() : new ExBambooMob();
			case 12: return Random.chances(new float[]{1, 0.5f}) == 0
					? new BombBug() : new Shielded();
			case 13: return Random.Int(2) == 0 ? new BombBug() : new Shielded();
			case 14: return Random.chances(new float[]{0.5f, 1}) == 0
					? new BombBug() : new Shielded();
			case 17: case 18: case 19: return new GuardSenior();
			case 22: return new FireSuccubus();
			case 23: case 24: return Random.Int(2) == 0
					? new GuardAcidic() : new FireSuccubus();
			default: return null;
		}
	}

	/** The nine equal-weight guards used by an SPS tent outside a shop floor. */
	public static Mob randomTentGuard() {
		switch (Random.Int(9)) {
			case 0: return new GuardAlbino();
			case 1: return new GuardBandit();
			case 2: return new Shielded();
			case 3: return new BombBug();
			case 4: return new GuardSenior();
			case 5: return new GuardAcidic();
			case 6: return new GuardVagrant();
			case 7: return new FireSuccubus();
			default: return new ExBambooMob();
		}
	}

	private static void seedAround(int center, int amount, Class<? extends Blob> type, boolean includeCenter) {
		int[] offsets = includeCenter ? PathFinder.NEIGHBOURS9 : PathFinder.NEIGHBOURS8;
		for (int offset : offsets) {
			int cell = center + offset;
			if (Dungeon.level.insideMap(cell)) GameScene.add(Blob.seed(cell, amount, type));
		}
	}

	private static int cap(int damage, int health) {
		return Math.min(damage, Math.max(1, health / 6));
	}

	private static void dropBonus(Mob mob, Item item) {
		if (Dungeon.level == null || item == null) return;
		Heap heap = Dungeon.level.drop(item, mob.pos);
		if (heap != null && heap.sprite != null) heap.sprite.drop();
	}

	public static class GuardAlbino extends Mob implements ExitGuard {
		{
			spriteClass = AlbinoSprite.class;
			HP = HT = 10 + legacyDepthAdjustment(0) * Random.NormalIntRange(1, 3);
			defenseSkill = 3 + legacyDepthAdjustment(1);
			EXP = 1; maxLvl = 4; loot = Meat.class; lootChance = 1f;
			properties.add(Property.BEAST); properties.add(Property.DEMONIC);
			resistances.add(Wand.class); immunities.add(Amok.class); immunities.add(Terror.class);
			immunities.add(CorruptGas.class); immunities.add(Vertigo.class); immunities.add(SandStorm.class);
		}
		@Override protected boolean act() { seedAround(pos, 2, SandStorm.class, true); return super.act(); }
		@Override public void damage(int damage, Object src) { GameScene.add(Blob.seed(pos, 15, CorruptGas.class)); super.damage(damage, src); }
		@Override public int damageRoll() { return Random.NormalIntRange(1, 5 + legacyDepthAdjustment(0)); }
		@Override public int attackSkill(Char target) { return 5 + legacyDepthAdjustment(0); }
		@Override public int drRoll() { return 1; }
		@Override public Item SupercreateLoot() { return Generator.random(Generator.Category.HIGHFOOD); }
	}

	public static class GuardVagrant extends ExVagrant implements ExitGuard { }

	public static class GuardBandit extends Mob implements ExitGuard {
		private static final String BREAKS = "breaks";
		private static final String SKILL_USED = "skill_used";
		private int breaks;
		private boolean skillUsed;
		{
			spriteClass = BanditSprite.class;
			HP = HT = 80 + legacyDepthAdjustment(0) * Random.NormalIntRange(3, 5);
			defenseSkill = 8 + legacyDepthAdjustment(0); EXP = 5; maxLvl = 20;
			baseSpeed = 1f; loot = NutVegetable.class; lootChance = 0.1f;
			properties.add(Property.GOBLIN); properties.add(Property.ELF);
			immunities.add(pd.actors.buffs.Blindness.class);
			immunities.add(DarkGas.class);
		}
		@Override protected boolean act() {
			if (2 - breaks > 3 * HP / HT) { breaks++; skillUsed = false; return true; }
			seedAround(pos, 10, DarkGas.class, true);
			return super.act();
		}
		@Override protected boolean canAttack(Char enemy) { return Dungeon.level.distance(pos, enemy.pos) <= 2; }
		@Override public int damageRoll() { return Random.NormalIntRange(1, 7 + legacyDepthAdjustment(0)); }
		@Override public float attackDelay() { return 0.5f; }
		@Override public int attackSkill(Char target) { return 12; }
		@Override public int drRoll() { return Random.NormalIntRange(0, 3); }
		@Override public int attackProc(Char enemy, int damage) {
			if (!skillUsed && enemy == Dungeon.hero) {
				skillUsed = true;
				int amount = Math.max(0, Dungeon.gold / 20);
				Buff.affect(this, EnergyArmor.class).level(Dungeon.gold / 40);
				Dungeon.gold = Math.max(0, Dungeon.gold - amount);
			}
			if (skillUsed && Random.Int(3) == 1) Buff.affect(enemy, Poison.class).set(Random.IntRange(2, 3));
			return damage;
		}
		@Override public void damage(int damage, Object src) { super.damage(cap(damage, HT), src); }
		@Override public Item SupercreateLoot() { return Generator.random(Generator.Category.MELEEWEAPON); }
		@Override public void rollToDropLoot() { super.rollToDropLoot(); if (Random.Float() < 0.05f) dropBonus(this, Generator.random(Generator.Category.BERRY)); }
		@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put(BREAKS, breaks); b.put(SKILL_USED, skillUsed); }
		@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); breaks = b.getInt(BREAKS); skillUsed = b.getBoolean(SKILL_USED); }
	}

	public static class GuardBamboo extends Mob implements ExitGuard {
		private static final String SKILL_USED = "skill_used";
		private boolean skillUsed;
		{
			spriteClass = SpsExitSprites.ExBamboo.class;
			HP = HT = 80; defenseSkill = 0; EXP = 1; loot = NutVegetable.class; lootChance = 0.4f;
			properties.add(Property.PLANT); resistances.add(Roots.class);
			weaknesses.add(ToxicGas.class); weaknesses.add(Ooze.class);
			weaknesses.add(SwampGas.class); weaknesses.add(Wand.class);
			weaknesses.add(Poison.class);
			immunities.add(DamageType.Earth.class); immunities.add(EarthEffectDamage.class);
		}
		@Override protected boolean act() {
			if (isAlive()) seedAround(pos, 3, EarthEffectDamage.class, false);
			if (1 > 2 * HP / HT && !skillUsed && isAlive()) {
				skillUsed = true; HP = HT;
				PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.solid, null), 2);
				for (int cell = 0; cell < PathFinder.distance.length; cell++) {
					if (PathFinder.distance[cell] < Integer.MAX_VALUE) {
						Char ch = Actor.findChar(cell);
						if (ch != null && ch.isAlive()) Buff.prolong(ch, Roots.class, 10f);
					}
				}
				if (sprite != null) sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
			}
			return super.act();
		}
		@Override protected boolean getCloser(int target) { return true; }
		@Override protected boolean getFurther(int target) { return true; }
		@Override public int damageRoll() { return Random.NormalIntRange(6, 24); }
		@Override public int attackSkill(Char target) { return 20; }
		@Override public int drRoll() { return Random.NormalIntRange(5, 10); }
		@Override public int attackProc(Char enemy, int damage) {
			tryDefenseBuff();
			tryDefenseBuff();
			return super.attackProc(enemy, damage);
		}
		protected void tryDefenseBuff() { if (Random.Int(3) == 0) Buff.affect(this, DefenceUp.class, 3f).level(20); }
		@Override public int defenseProc(Char enemy, int damage) {
			retaliate(enemy, damage);
			retaliate(enemy, damage);
			return super.defenseProc(enemy, damage);
		}
		protected void retaliate(Char enemy, int damage) {
			int reflected = Random.IntRange(0, Math.max(0, damage)) - enemy.drRoll();
			if (reflected > 0) { enemy.damage(reflected, this); Wound.hit(enemy); }
		}
		@Override public void damage(int damage, Object src) { if (1 > 2 * HP / HT && !skillUsed) damage = 0; super.damage(Math.min(damage, Math.max(1, HT / 3)), src); }
		@Override public Item SupercreateLoot() { return Generator.random(Generator.Category.ARMOR); }
		@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put(SKILL_USED, skillUsed); }
		@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); skillUsed = b.getBoolean(SKILL_USED); }
	}

	public static class GuardBombBug extends Mob implements ExitGuard {
		private static final String BREAKS = "breaks";
		private int breaks;
		{
			spriteClass = SpsExitSprites.BombBug.class;
			HP = HT = 80 + legacyDepthAdjustment(0) * Random.NormalIntRange(2, 5);
			defenseSkill = 15 + legacyDepthAdjustment(0); baseSpeed = 1.5f; EXP = 9; maxLvl = 25;
			loot = StoneOre.class; lootChance = 0.3f;
			properties.add(Property.BEAST); properties.add(Property.ICY);
			resistances.add(DamageType.Ice.class); resistances.add(WandOfFlow.class);
			resistances.add(WandOfFreeze.class);
			immunities.add(FrostIce.class); immunities.add(EnchantmentIce.class);
			immunities.add(EnchantmentIce2.class);
		}
		@Override protected boolean act() { if (1 - breaks > 2 * HP / HT) { breaks++; spawnAround(pos); return true; } return super.act(); }
		@Override public int damageRoll() { return Random.NormalIntRange(10, 15 + legacyDepthAdjustment(0)); }
		@Override public int attackSkill(Char target) { return 16 + legacyDepthAdjustment(0); }
		@Override public int drRoll() { return Random.NormalIntRange(2, 5); }
		@Override public int attackProc(Char enemy, int damage) { if (Random.Int(5) == 0) Buff.affect(enemy, FrostIce.class).level(5); return damage; }
		@Override public void damage(int damage, Object src) { damage = cap(damage, HT); if (Random.Int(8) == 0) freezeNeighbours(10); super.damage(damage, src); }
		@Override public void die(Object cause) {
			super.die(cause);
			freezeNeighbours(10);
			if (Dungeon.level != null && Dungeon.level.insideMap(pos) && Dungeon.level.heroFOV[pos]) {
				Sample.INSTANCE.play(Assets.Sounds.BLAST);
			}
		}
		private void freezeNeighbours(int level) { for (int offset : PathFinder.NEIGHBOURS8) { int cell = pos + offset; if (!Dungeon.level.insideMap(cell)) continue; Char ch = Actor.findChar(cell); if (ch != null && ch.isAlive()) Buff.affect(ch, StoneIce.class).level(level); } }
		private static void spawnAround(int center) { for (int offset : PathFinder.NEIGHBOURS4) { int cell = center + offset; if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell] && Actor.findChar(cell) == null) { BombBug mob = new BombBug(); mob.pos = cell; mob.state = mob.HUNTING; GameScene.add(mob, 1f); } } }
		@Override public Item SupercreateLoot() { return Random.oneOf(new PotionOfFrost(), new WandOfFreeze()); }
		@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put(BREAKS, breaks); }
		@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); breaks = b.getInt(BREAKS); }
	}

	public static class GuardShielded extends Mob implements ExitGuard {
		private static final String BREAKS = "breaks";
		private static final String ENRAGED = "enraged";
		private int breaks;
		private boolean enraged;
		{
			spriteClass = ShieldedSprite.class;
			HP = HT = 120 + legacyDepthAdjustment(0) * Random.NormalIntRange(1, 2);
			defenseSkill = 20 + legacyDepthAdjustment(0); EXP = 8; maxLvl = 25;
			loot = Gold.class; lootChance = 0.5f; properties.add(Property.ORC);
			weaknesses.add(Wand.class); immunities.add(Terror.class);
		}
		@Override protected boolean act() { if (2 - breaks > 3 * HP / HT) { breaks++; Buff.affect(this, ShieldArmor.class).level(legacyDepthAdjustment(0) * 3); Buff.affect(this, MagicArmor.class).level(legacyDepthAdjustment(0) * 3); return true; } return super.act(); }
		@Override public int damageRoll() { return enraged ? Random.NormalIntRange(25 + legacyDepthAdjustment(0), 40 + legacyDepthAdjustment(0)) : Random.NormalIntRange(5 + legacyDepthAdjustment(0), 25 + legacyDepthAdjustment(0)); }
		@Override public float attackDelay() { return 1.5f; }
		@Override public int attackSkill(Char target) { return 10 + legacyDepthAdjustment(1); }
		@Override public int drRoll() { return Random.NormalIntRange(10, 30); }
		@Override public int attackProc(Char enemy, int damage) { if (Random.Int(3) == 0) { int opposite = enemy.pos + enemy.pos - pos; if (Dungeon.level.insideMap(opposite)) { WandOfFlow.throwChar(enemy, new Ballistica(enemy.pos, opposite, Ballistica.MAGIC_BOLT), 2); Buff.prolong(enemy, Vertigo.class, 3f); } } return damage; }
		@Override public int defenseProc(Char enemy, int damage) { if (HP > damage && Random.Int(2) == 0) attack(enemy); return damage; }
		@Override public void damage(int damage, Object src) { super.damage(cap(damage, HT), src); if (isAlive() && !enraged && HP < HT / 4) { enraged = true; Buff.affect(this, DefenceUp.class, 3f).level(70); spend(TICK); } }
		@Override public Item SupercreateLoot() { return Random.oneOf(new WoodenArmor(), new Handaxe(), new CapeOfThorns()); }
		public static Generator.Category secondaryLootCategory() { return Generator.Category.RANGEWEAPON; }
		@Override public void rollToDropLoot() { super.rollToDropLoot(); if (Dungeon.hero != null && legacyLootLevelEligible() && Random.Float() < legacySecondaryLootChance(0.5f)) dropBonus(this, Generator.randomUsingDefaults(secondaryLootCategory())); }
		@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put(BREAKS, breaks); b.put(ENRAGED, enraged); }
		@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); breaks = b.getInt(BREAKS); enraged = b.getBoolean(ENRAGED); }
	}

	public static class GuardSenior extends Mob implements ExitGuard {
		private static final String SKILL_USED = "skill_used";
		private boolean skillUsed;
		{
			spriteClass = SeniorSprite.class;
			HP = HT = 160 + legacyDepthAdjustment(0) * Random.NormalIntRange(3, 5);
			defenseSkill = 30 + legacyDepthAdjustment(1); EXP = 14; maxLvl = 30;
			loot = NormalRation.class; lootChance = 0.1f;
			properties.add(Property.DWARF); immunities.add(Amok.class); immunities.add(Terror.class);
		}
		@Override protected boolean act() { if (1 > 2 * HP / HT && !skillUsed) { skillUsed = true; Buff.affect(this, AttackUp.class, 10f).level(50); Buff.affect(this, DefenceUp.class, 10f).level(75); HP = HT; return true; } return super.act(); }
		@Override public int damageRoll() { return Random.NormalIntRange(32, 56 + legacyDepthAdjustment(0)); }
		@Override public float attackDelay() { return 0.5f; }
		@Override public int attackSkill(Char target) { return 30 + legacyDepthAdjustment(1); }
		@Override public int drRoll() { return Random.NormalIntRange(2, 12); }
		@Override public int attackProc(Char enemy, int damage) {
			Buff.affect(enemy, DBurning.class).set(2f);
			if (Random.Int(12) == 0 && enemy == Dungeon.hero) {
				KindOfWeapon weapon = Dungeon.hero.belongings.weapon;
				if (weapon != null && !(weapon instanceof Knuckles || weapon instanceof FightGloves)
						&& !weapon.cursed) {
					Dungeon.hero.belongings.weapon = null;
					Dungeon.quickslot.clearItem(weapon);
					weapon.updateQuickslot();
					Dungeon.level.drop(weapon, Dungeon.hero.pos).sprite.drop();
				}
			}
			return damage;
		}
		@Override public void damage(int damage, Object src) { super.damage(cap(damage, HT), src); }
		@Override public Item SupercreateLoot() { return Random.oneOf(Generator.random(Generator.Category.HIGHFOOD), new HornOfPlenty()); }
		@Override public void rollToDropLoot() { super.rollToDropLoot(); if (Random.Float() < 0.4f) dropBonus(this, new OverpricedRation()); }
		@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put(SKILL_USED, skillUsed); }
		@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); skillUsed = b.getBoolean(SKILL_USED); }
	}

	public static class GuardFireSuccubus extends Mob implements ExitGuard {
		private static final String BLINK_DELAY = "blink_delay";
		private int blinkDelay;
		{
			spriteClass = SuccubusSprite.class;
			HP = HT = 150 + legacyDepthAdjustment(0) * Random.NormalIntRange(5, 7);
			defenseSkill = 30 + legacyDepthAdjustment(1); viewDistance = 8;
			EXP = 14; maxLvl = 35; loot = ScrollOfLullaby.class; lootChance = 0.05f;
			properties.add(Property.DEMONIC);
			immunities.add(Sleep.class); immunities.add(FireEffectDamage.class);
		}
		@Override protected boolean act() { seedAround(pos, 2, FireEffectDamage.class, true); return super.act(); }
		@Override public int damageRoll() { return Random.NormalIntRange(15, 25 + legacyDepthAdjustment(0)); }
		@Override public int attackSkill(Char target) { return 42 + legacyDepthAdjustment(1); }
		@Override public int drRoll() { return Random.NormalIntRange(5, 10); }
		@Override public int attackProc(Char enemy, int damage) { Buff.affect(this, DefenceUp.class, 10f).level(30); GameScene.add(Blob.seed(enemy.pos, 3, IceEffectDamage.class)); if (Random.Int(3) == 0) Buff.affect(enemy, Charm.class, Random.IntRange(3, 7)).object = id(); if (buff(BeOld.class) == null) HP = Math.min(HT, HP + damage); return damage; }
		@Override public void damage(int damage, Object src) { Buff.affect(this, AttackUp.class, 10f).level(30); super.damage(cap(damage, HT), src); }
		@Override protected boolean getCloser(int target) {
			if (fieldOfView != null && fieldOfView[target] && Dungeon.level.distance(pos, target) > 2
					&& blinkDelay <= 0 && buff(Silent.class) == null && !rooted) {
				blink(target); spend(-1 / speed()); return true;
			}
			blinkDelay--;
			return super.getCloser(target);
		}
		private void blink(int target) {
			Ballistica route = new Ballistica(pos, target, Ballistica.PROJECTILE);
			int cell = route.collisionPos;
			if (Actor.findChar(cell) != null && cell != pos && route.dist > 0) cell = route.path.get(route.dist - 1);
			if (!Dungeon.level.insideMap(cell) || Dungeon.level.avoid[cell]) {
				ArrayList<Integer> candidates = new ArrayList<>();
				for (int offset : PathFinder.NEIGHBOURS8) {
					int candidate = route.collisionPos + offset;
					if (Dungeon.level.insideMap(candidate) && Dungeon.level.passable[candidate]
							&& Actor.findChar(candidate) == null) candidates.add(candidate);
				}
				if (candidates.isEmpty()) { blinkDelay = 5; return; }
				cell = Random.element(candidates);
			}
			ScrollOfTeleportation.appear(this, cell);
			blinkDelay = 5;
		}
		@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put(BLINK_DELAY, blinkDelay); }
		@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); blinkDelay = b.getInt(BLINK_DELAY); }
		@Override public void rollToDropLoot() { super.rollToDropLoot(); if (Random.Float() < 0.1f) dropBonus(this, new MysteryMeat()); }
	}

	public static class GuardAcidic extends Mob implements ExitGuard {
		{
			spriteClass = AcidicSprite.class;
			HP = HT = 180 + legacyDepthAdjustment(0) * Random.NormalIntRange(1, 3);
			defenseSkill = 24 + legacyDepthAdjustment(1); viewDistance = 8;
			EXP = 17; maxLvl = 35; properties.add(Property.BEAST); properties.add(Property.DEMONIC);
			immunities.add(StenchGas.class);
		}
		@Override protected boolean act() { GameScene.add(Blob.seed(pos, 30, StenchGas.class)); return super.act(); }
		@Override protected boolean canAttack(Char enemy) { return buff(Locked.class) != null ? Dungeon.level.adjacent(pos, enemy.pos) : Dungeon.level.distance(pos, enemy.pos) <= 2; }
		@Override protected boolean getCloser(int target) { return state == HUNTING ? enemySeen && getFurther(target) : super.getCloser(target); }
		@Override public int damageRoll() { return Random.NormalIntRange(20, 52 + legacyDepthAdjustment(0)); }
		@Override public int attackSkill(Char target) { return 36 + legacyDepthAdjustment(1); }
		@Override public int drRoll() { return Random.NormalIntRange(10, 20); }
		@Override public int attackProc(Char enemy, int damage) { if (Random.Int(2) == 0) Buff.prolong(enemy, pd.actors.buffs.Cripple.class, pd.actors.buffs.Cripple.DURATION); return damage; }
		@Override public int defenseProc(Char enemy, int damage) { int reflected = Random.IntRange(0, Math.max(0, damage / 2)) - enemy.drRoll(); if (reflected > 0) { enemy.damage(reflected, this); Wound.hit(enemy); } return damage; }
		@Override public Item SupercreateLoot() { return Random.oneOf(new PotionOfToxicGas(), new WandOfAcid()); }
	}
}
