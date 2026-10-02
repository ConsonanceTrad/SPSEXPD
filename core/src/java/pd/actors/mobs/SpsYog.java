/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Fire;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Roots;
import pd.actors.buffs.Silent;
import pd.actors.buffs.Sleep;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.effects.CellEmitter;
import pd.effects.Pushing;
import pd.effects.Speck;
import pd.effects.particles.ShadowParticle;
import pd.items.Elevator;
import pd.items.Item;
import pd.items.PuddingCup;
import pd.items.specific.keys.SpsSkeletonKey;
import pd.items.consum.scrolls.ScrollOfPsionicBlast;
import pd.items.equipment.weapon.enchantments.EnchantmentDark;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.sprites.SpsYogSprites;
import pd.ui.BossHealthBar;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

/** SPS-PD 0.9.8's final main-dungeon Yog encounter. */
public class SpsYog extends Mob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(SpsYog.class)
			.t("name", "Yog-Dzewa")
			.t("desc", "Yog-Dzewa是一位来自混沌领域的上古之神，在远古战争后被封印于矮人城市下方。")
			.t("notice", "希望的存在只是一种幻觉……")
			.t("die", "我……永生……")
			.t("blink", "Yog消失了！")
			.t("burningfist.name", "火焰之拳")
			.t("burningfist.desc", "由活体火焰构成的巨拳。")
			.t("rottingfist.name", "大地之拳")
			.t("rottingfist.desc", "由大地与腐化物构成的巨拳。")
			.t("infectingfist.name", "酸蚀之拳")
			.t("infectingfist.desc", "会散播剧毒气体的腐蚀巨拳。")
			.t("pinningfist.name", "剧毒之拳")
			.t("pinningfist.desc", "能够远程攻击并束缚目标的巨拳。")
			.t("larva.name", "古神幼虫")
			.t("larva.desc", "从受伤古神体内脱落的幼虫。");
	}

	private int breaks;
	private boolean fistsSpawned;

	{
		spriteClass = SpsYogSprites.Yog.class;
		HP = HT = 2000;
		EXP = 50;
		defenseSkill = 0;
		state = PASSIVE;
		properties.add(Property.UNKNOW);
		properties.add(Property.BOSS);
		resistances.add(Burning.class);
		resistances.add(ToxicGas.class);
		resistances.add(EnchantmentDark.class);
		resistances.add(ScrollOfPsionicBlast.class);
		resistances.add(Amok.class);
		resistances.add(Terror.class);
		resistances.add(Charm.class);
		resistances.add(Sleep.class);
		resistances.add(Vertigo.class);
	}

	@Override public int damageRoll() { return Random.NormalIntRange(54, 96); }
	@Override public int attackSkill(Char target) { return 36; }
	@Override public int drRoll() { return 30 * fistCount(); }
	@Override public void beckon(int cell) { }

	@Override protected boolean act() {
		if (!fistsSpawned) {
			spawnFists();
			return true;
		}
		if (4 - breaks > 5 * HP / HT) {
			breaks++;
			teleportSafely();
			if (breaks == 4 && !hasYearBeast()) spawnYearBeast();
			return true;
		}
		return super.act();
	}

	private void teleportSafely() {
		ArrayList<Integer> cells = openCells(true, false);
		if (cells.isEmpty()) cells = openCells(false, false);
		if (cells.isEmpty()) return;
		int old = pos;
		int destination = Random.element(cells);
		move(destination, false);
		if (sprite != null) {
			sprite.place(destination);
			CellEmitter.get(old).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
		}
		yell(Messages.get(this, "blink"));
	}

	private ArrayList<Integer> openCells(boolean hiddenOnly, boolean awayFromHero) {
		ArrayList<Integer> result = new ArrayList<>();
		if (Dungeon.level == null) return result;
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			if (!Dungeon.level.passable[cell] || Actor.findChar(cell) != null) continue;
			if (hiddenOnly && Dungeon.level.heroFOV != null && Dungeon.level.heroFOV[cell]) continue;
			if (awayFromHero && Dungeon.hero != null && Dungeon.level.adjacent(cell, Dungeon.hero.pos)) continue;
			result.add(cell);
		}
		return result;
	}

	private boolean hasYearBeast() {
		if (Dungeon.level != null) for (Mob mob : Dungeon.level.mobs().all()) if (mob instanceof YearBeast && mob.isAlive()) return true;
		return false;
	}

	private void spawnYearBeast() {
		ArrayList<Integer> cells = openCells(false, true);
		if (cells.isEmpty()) cells = openCells(false, false);
		if (cells.isEmpty()) return;
		YearBeast.spawnAt(Random.element(cells));
	}

	public void spawnFists() {
		if (Dungeon.level == null || fistCount() > 0) { fistsSpawned = true; return; }
		ArrayList<Integer> cells = openCells(true, false);
		if (cells.size() < 4) cells = openCells(false, false);
		Random.shuffle(cells);
		Fist[] fists = createFists();
		for (int i = 0; i < fists.length && i < cells.size(); i++) {
			fists[i].ownerId = id();
			fists[i].pos = cells.get(i);
			fists[i].state = fists[i].HUNTING;
			GameScene.add(fists[i]);
		}
		fistsSpawned = true;
	}

	protected Fist[] createFists() {
		return new Fist[]{new RottingFist(), new BurningFist(), new PinningFist(), new InfectingFist()};
	}

	protected Larva createLarva() {
		return new Larva();
	}

	private int fistCount() {
		int count = 0;
		if (Dungeon.level != null) for (Mob mob : Dungeon.level.mobs().all()) if (mob instanceof Fist && ((Fist) mob).ownerId == id() && mob.isAlive()) count++;
		return count;
	}

	@Override public int defenseProc(Char enemy, int damage) {
		ArrayList<Integer> cells = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = pos + offset;
			if (Dungeon.level.insideMap(cell) && (Dungeon.level.passable[cell] || Dungeon.level.avoid[cell]) && Actor.findChar(cell) == null) cells.add(cell);
		}
		if (!cells.isEmpty()) {
			Larva larva = createLarva(); larva.ownerId = id(); larva.pos = Random.element(cells); larva.state = larva.HUNTING;
			GameScene.add(larva); Actor.addDelayed(new Pushing(larva, pos, larva.pos), -1);
		}
		if (Dungeon.level != null) for (Mob mob : Dungeon.level.mobs().all()) if ((mob instanceof Fist || mob instanceof Larva) && enemy != null) mob.aggro(enemy);
		if (fistsSpawned && fistCount() == 0) {
			spawnFists();
			if (sprite != null) sprite.emitter().burst(ShadowParticle.UP, 2);
			damage(50, this);
		}
		return super.defenseProc(enemy, damage);
	}

	@Override public void damage(int damage, Object src) {
		if (src instanceof Hero && damage > HP) damage = 1;
		super.damage(damage, src);
	}

	@Override public Item SupercreateLoot() { return new PuddingCup(); }

	@Override public void notice() { super.notice(); BossHealthBar.assignBoss(this); yell(Messages.get(this, "notice")); }
	@Override public void die(Object cause) {
		super.die(cause);
		Cleanup cleanup = new Cleanup();
		if (Dungeon.level == null) return;
		for (Mob mob : new ArrayList<>(Dungeon.level.mobs().all())) {
			if ((mob instanceof Fist && ((Fist) mob).ownerId == id())
					|| (mob instanceof Larva && ((Larva) mob).ownerId == id())
					|| mob instanceof Eye) mob.die(cleanup);
		}
		Dungeon.level.unseal();
		GameScene.bossSlain();
		dropReward(new Elevator());
		dropReward(new SpsSkeletonKey(Dungeon.depth));
		yell(Messages.get(this, "die"));
	}

	private void dropReward(Item item) {
		pd.items.Heap heap = Dungeon.level.drop(item, pos);
		if (heap != null && heap.sprite != null) heap.sprite.drop();
	}

	private static final String BREAKS = "breaks", FISTS_SPAWNED = "fists_spawned";
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(BREAKS, breaks); bundle.put(FISTS_SPAWNED, fistsSpawned); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); breaks = Math.max(0, Math.min(4, bundle.getInt(BREAKS))); fistsSpawned = bundle.getBoolean(FISTS_SPAWNED); if (state != SLEEPING) BossHealthBar.assignBoss(this); }

	protected static abstract class Fist extends Mob {
		int ownerId = -1;
		{
			EXP = 0;
			defenseSkill = 25;
			state = WANDERING;
			properties.add(Property.ELEMENT);
			properties.add(Property.BOSS);
			resistances.add(ToxicGas.class);
			resistances.add(EnchantmentDark.class);
			resistances.add(Amok.class);
			resistances.add(Sleep.class);
			resistances.add(Terror.class);
			resistances.add(Vertigo.class);
		}
		protected boolean legacyRangedAttack(Char enemy, boolean roots) {
			spend(attackDelay());
			if (Char.hit(this, enemy, true)) {
				int damage = damageRoll();
				enemy.damage(damage, this);
				if (roots && Random.Int(10) == 0) Buff.prolong(enemy, Roots.class, 20f);
				if (enemy.sprite != null) {
					if (sprite != null) enemy.sprite.bloodBurstA(sprite.center(), damage);
					enemy.sprite.flash();
				}
				if (!enemy.isAlive() && enemy == Dungeon.hero) Dungeon.fail(this);
				return true;
			}
			if (enemy.sprite != null) enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
			return false;
		}
		private static final String OWNER_ID = "owner_id";
		@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put(OWNER_ID, ownerId); }
		@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); ownerId = b.getInt(OWNER_ID); }
	}

	public static class RottingFist extends Fist {
		{ spriteClass = SpsYogSprites.Rotting.class; HP = HT = 1500; resistances.add(Poison.class); }
		@Override public int damageRoll() { return Random.NormalIntRange(44, 76); }
		@Override public int attackSkill(Char target) { return 36; }
		@Override public int drRoll() { return 35; }
		@Override public int attackProc(Char enemy, int damage) {
			if (Random.Int(3) == 0) {
				Buff.affect(enemy, Ooze.class).set(10f);
				if (enemy.sprite != null) enemy.sprite.burst(0xFF000000, 5);
			}
			return damage;
		}
		@Override protected boolean act() {
			if (Dungeon.level.water[pos] && HP < HT) {
				if (sprite != null) sprite.emitter().burst(ShadowParticle.UP, 2);
				HP += 50;
			}
			return super.act();
		}
	}

	public static class BurningFist extends Fist {
		{
			spriteClass = SpsYogSprites.Burning.class;
			HP = HT = 1000;
			resistances.add(Burning.class);
			resistances.add(Fire.class);
			resistances.add(ScrollOfPsionicBlast.class);
		}
		@Override public int damageRoll() { return Random.NormalIntRange(40, 52); }
		@Override public int attackSkill(Char target) { return 36; }
		@Override public int drRoll() { return 25; }
		@Override protected boolean canAttack(Char enemy) {
			if (buff(Silent.class) != null) return Dungeon.level.adjacent(pos, enemy.pos) && !isCharmedBy(enemy);
			return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
		}
		@Override public boolean attack(Char enemy, float dmgMulti, float dmgBonus, float accMulti) {
			return Dungeon.level.adjacent(pos, enemy.pos)
					? super.attack(enemy, dmgMulti, dmgBonus, accMulti) : legacyRangedAttack(enemy, false);
		}
		@Override protected boolean act() {
			for (int offset : PathFinder.NEIGHBOURS9) { int cell = pos + offset; if (Dungeon.level.insideMap(cell)) GameScene.add(Blob.seed(cell, 2, Fire.class)); }
			return super.act();
		}
	}

	public static class InfectingFist extends Fist {
		{ spriteClass = SpsYogSprites.Infecting.class; HP = HT = 1500; resistances.add(ToxicGas.class); resistances.add(Poison.class); }
		@Override public int damageRoll() { return Random.NormalIntRange(44, 86); }
		@Override public int attackSkill(Char target) { return 36; }
		@Override public int drRoll() { return 35; }
		@Override public int attackProc(Char enemy, int damage) {
			if (Random.Int(2) == 0) {
				Buff.affect(enemy, Poison.class).set(Random.Int(7, 9));
				state = FLEEING;
			}
			return damage;
		}
		@Override protected boolean act() { GameScene.add(Blob.seed(pos, 30, ToxicGas.class)); return super.act(); }
	}

	public static class PinningFist extends Fist {
		{
			spriteClass = SpsYogSprites.Pinning.class;
			HP = HT = 1000;
			resistances.add(Burning.class);
			resistances.add(ScrollOfPsionicBlast.class);
		}
		@Override public int damageRoll() { return Random.NormalIntRange(30, 42); }
		@Override public int attackSkill(Char target) { return 36; }
		@Override public int drRoll() { return 25; }
		@Override protected boolean canAttack(Char enemy) {
			if (buff(Silent.class) != null) return Dungeon.level.adjacent(pos, enemy.pos) && !isCharmedBy(enemy);
			return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
		}
		@Override protected boolean getCloser(int target) { return state == HUNTING && enemySeen ? getFurther(target) : super.getCloser(target); }
		@Override public boolean attack(Char enemy, float dmgMulti, float dmgBonus, float accMulti) {
			return Dungeon.level.adjacent(pos, enemy.pos)
					? super.attack(enemy, dmgMulti, dmgBonus, accMulti) : legacyRangedAttack(enemy, true);
		}
	}

	public static class Larva extends Mob {
		int ownerId = -1;
		{ spriteClass = SpsYogSprites.Larva.class; HP = HT = 25; defenseSkill = 20; EXP = 0; state = HUNTING; properties.add(Property.UNKNOW); }
		@Override public int damageRoll() { return Random.NormalIntRange(15, 20); }
		@Override public int attackSkill(Char target) { return 30; }
		@Override public int drRoll() { return 8; }
		private static final String OWNER_ID = "owner_id";
		@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put(OWNER_ID, ownerId); }
		@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); ownerId = b.getInt(OWNER_ID); }
	}

	private static final class Cleanup { }
}
