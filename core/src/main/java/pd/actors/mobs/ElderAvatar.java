/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Assets;
import pd.Challenges;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.DarkGas;
import pd.actors.blobs.ToxicGas;
import pd.actors.blobs.effectblobs.ElectriShock;
import pd.actors.buffs.Amok;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.AttackDown;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Chill;
import pd.actors.buffs.Disarm;
import pd.actors.buffs.EnergyArmor;
import pd.actors.buffs.Frost;
import pd.actors.buffs.GlassShield;
import pd.actors.buffs.GrowSeed;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.items.Generator;
import pd.items.Item;
import pd.items.StoneOre;
import pd.items.armor.Armor;
import pd.items.armor.normalarmor.BaseArmor;
import pd.items.armor.normalarmor.RubberArmor;
import pd.items.armor.normalarmor.WoodenArmor;
import pd.items.artifacts.AlienBag;
import pd.items.bombs.MiniBomb;
import pd.items.wands.WandOfDisintegration;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ElderAvatarSprite;
import pd.sprites.GolemSprite;
import pd.sprites.MonkSprite;
import pd.sprites.MusketeerSprite;
import pd.sprites.ObeliskSprite;
import pd.sprites.WarlockSprite;
import pd.ui.BossHealthBar;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.Bundle;
import render.utils.Random;

/** SPS-PD's elder-avatar city boss and its four low-health reinforcement waves. */
public class ElderAvatar extends Mob {
	private int waves;
	private int obeliskId = -1;
	private boolean obeliskSpawned;

	{
		spriteClass = ElderAvatarSprite.class;
		HP = HT = 600;
		EXP = 50;
		defenseSkill = 25;
		baseSpeed = 1f;
		properties.add(Property.ALIEN);
		properties.add(Property.BOSS);
		resistances.add(ToxicGas.class);
		resistances.add(Amok.class);
		resistances.add(Vertigo.class);
		resistances.add(WandOfDisintegration.class);
		immunities.add(Paralysis.class);
		immunities.add(Charm.class);
	}

	@Override public int damageRoll() {
		return Dungeon.isChallenged(Challenges.TEST_TIME)
				? Random.NormalIntRange(0, 1)
				: Random.NormalIntRange(25, 40);
	}
	@Override public int attackSkill(Char target) { return 58; }
	@Override public int drRoll() { return Random.NormalIntRange(8, 12); }
	@Override protected boolean canAttack(Char enemy) { return HP > 49 && Dungeon.level.distance(pos, enemy.pos) <= 3; }

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(5) == 0) new MiniBomb().explode(enemy.pos);
		if (Random.Int(8) == 0) {
			Charm charm = Buff.affect(enemy, Charm.class, Random.IntRange(3, 5));
			charm.object = id();
			if (enemy.sprite != null) enemy.sprite.centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 5);
			Sample.INSTANCE.play(Assets.Sounds.CHARMS);
		}
		if (enemy == Dungeon.hero && Random.Int(10) == 0) disarmArmor(Dungeon.hero);
		return damage;
	}

	private void disarmArmor(Hero hero) {
		Armor armor = hero.belongings.armor;
		if (armor == null || armor.cursed || armor instanceof WoodenArmor
				|| armor instanceof RubberArmor || armor instanceof BaseArmor) return;
		hero.belongings.armor = null;
		Dungeon.level.drop(armor, hero.pos).sprite.drop();
		GLog.w(Messages.get(this, "disarm"));
	}

	@Override
	protected boolean act() {
		if (!obeliskSpawned && spawnObelisk()) return true;
		int breaks = obeliskBreaks();
		if (HP < 50 && waves < 4 && waves == breaks) {
			summonWave(waves++);
			Buff.affect(this, EnergyArmor.class).level(100);
			if (sprite != null) sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.4f, 2);
			Sample.INSTANCE.play(Assets.Sounds.CHALLENGE);
			return true;
		}
		return super.act();
	}

	private boolean spawnObelisk() {
		if (Dungeon.level == null) return false;
		int cell = SpsCityBossLevelCell.well();
		if (!Dungeon.level.insideMap(cell) || Actor.findChar(cell) != null) return false;
		Obelisk obelisk = new Obelisk();
		obelisk.pos = cell;
		obelisk.ownerId = id();
		obeliskId = obelisk.id();
		obeliskSpawned = true;
		GameScene.add(obelisk);
		return true;
	}

	private Obelisk findObelisk() {
		Actor actor = Actor.findById(obeliskId);
		if (actor instanceof Obelisk && ((Obelisk) actor).isAlive()) return (Obelisk) actor;
		if (Dungeon.level != null) for (Mob mob : Dungeon.level.mobs) {
			if (mob instanceof Obelisk && ((Obelisk) mob).ownerId == id() && mob.isAlive()) {
				obeliskId = mob.id();
				return (Obelisk) mob;
			}
		}
		return null;
	}

	private int obeliskBreaks() {
		Obelisk obelisk = findObelisk();
		return obelisk == null ? 4 : obelisk.breaks;
	}

	private void summonWave(int wave) {
		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = pos + offset;
			if (!Dungeon.level.insideMap(cell) || !Dungeon.level.passable[cell] || Actor.findChar(cell) != null) continue;
			Mob mob;
			switch (wave) {
				case 0: mob = new TheHunter(); break;
				case 1: mob = new TheWarlock(); break;
				case 2: mob = new TheMonk(); break;
				default: mob = new TheMech(); break;
			}
			mob.pos = cell;
			mob.state = mob.HUNTING;
			GameScene.add(mob, 2f);
		}
	}

	@Override
	public void damage(int damage, Object src) {
		Obelisk obelisk = findObelisk();
		if (damage > HP && obelisk != null && obelisk.HP > 10) {
			HP = 30;
			damage = 0;
		}
		super.damage(damage, src);
	}

	@Override public void notice() { super.notice(); BossHealthBar.assignBoss(this); yell(Messages.get(this, "notice")); }
	@Override public void die(Object cause) {
		super.die(cause);
		pd.items.weapon.rockcode.RockCode.dropForPerformer(
				new pd.items.weapon.rockcode.Alink());
		SpsCityBossRewards.grant(pos, 4900, 10000, rareLoot(), commonLoot());
		yell(Messages.get(this, "die"));
	}

	static Item rareLoot() { return new AlienBag().identify(); }
	static Item commonLoot() { return Generator.random(Generator.Category.GUNWEAPON); }

	private static final String WAVES = "waves", OBELISK_ID = "obelisk_id", OBELISK_SPAWNED = "obelisk_spawned";
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(WAVES, waves); bundle.put(OBELISK_ID, obeliskId); bundle.put(OBELISK_SPAWNED, obeliskSpawned); }
	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		waves = Math.max(0, Math.min(4, bundle.getInt(WAVES)));
		obeliskId = bundle.getInt(OBELISK_ID);
		obeliskSpawned = bundle.contains(OBELISK_SPAWNED) ? bundle.getBoolean(OBELISK_SPAWNED) : obeliskId > 0;
		if (state != SLEEPING) BossHealthBar.assignBoss(this);
	}

	private static abstract class ElderMinion extends Mob {
		{
			EXP = 0;
			state = WANDERING;
			properties.add(Property.ALIEN);
			properties.add(Property.BOSS);
			immunities.add(Charm.class);
			immunities.add(Amok.class);
			immunities.add(Paralysis.class);
		}
		@Override public int drRoll() { return 5; }
	}

	public static class TheHunter extends ElderMinion {
		{ spriteClass = MusketeerSprite.class; HP = HT = 100; defenseSkill = 15; baseSpeed = 3f; viewDistance = 6; }
		@Override public int damageRoll() { return Random.NormalIntRange(20, 35); }
		@Override public int attackSkill(Char target) { return 60; }
		@Override protected boolean canAttack(Char enemy) {
			return buff(Disarm.class) == null
					&& new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos;
		}
		@Override public int attackProc(Char enemy, int damage) {
			Buff.affect(this, Disarm.class, 5f);
			Buff.affect(enemy, ArmorBreak.class, 3f).level(30);
			return damage;
		}
		@Override public void damage(int damage, Object src) {
			super.damage(damage, src);
			if (src instanceof ToxicGas) ((ToxicGas) src).clear(pos);
		}
	}

	public static class TheWarlock extends ElderMinion {
		{ spriteClass = WarlockSprite.class; HP = HT = 150; defenseSkill = 15; }
		@Override public int damageRoll() { return Random.NormalIntRange(25, 45); }
		@Override public int attackSkill(Char target) { return 60; }
		@Override public int attackProc(Char enemy, int damage) {
			Buff.prolong(enemy, Vertigo.class, 3f);
			Buff.prolong(enemy, AttackDown.class, 3f).level(20);
			return damage;
		}
		@Override public void damage(int damage, Object src) { super.damage(damage / 2, src); }
	}

	public static class TheMonk extends ElderMinion {
		{ spriteClass = MonkSprite.class; HP = HT = 100; defenseSkill = 30; baseSpeed = 2f; viewDistance = 5; }
		@Override public float attackDelay() { return 0.5f; }
		@Override public int damageRoll() { return Random.NormalIntRange(15, 30); }
		@Override public int attackSkill(Char target) { return 60; }
		@Override public int attackProc(Char enemy, int damage) {
			if (Random.Int(6) == 0) Buff.affect(this, GlassShield.class).turns(1);
			Buff.prolong(enemy, Blindness.class, 3f);
			return damage;
		}
		@Override public void damage(int damage, Object src) {
			super.damage(damage, src);
			if (src instanceof ToxicGas) ((ToxicGas) src).clear(pos);
		}
	}

	public static class TheMech extends ElderMinion {
		private boolean shieldAdded;
		{
			spriteClass = GolemSprite.class;
			HP = HT = 200;
			defenseSkill = 15;
			baseSpeed = 0.5f;
			properties.add(Property.MECH);
			immunities.add(Burning.class);
			immunities.add(Frost.class);
			immunities.add(Chill.class);
			immunities.add(ElectriShock.class);
			immunities.add(ToxicGas.class);
			immunities.add(DarkGas.class);
			immunities.add(GrowSeed.class);
		}
		@Override public int damageRoll() { return Random.NormalIntRange(45, 70); }
		@Override public int attackSkill(Char target) { return 30; }
		@Override public int drRoll() { return 10; }
		@Override protected boolean act() {
			if (!shieldAdded) {
				Buff.affect(this, EnergyArmor.class).level(100);
				shieldAdded = true;
				return true;
			}
			return super.act();
		}
		@Override public int attackProc(Char enemy, int damage) {
			Buff.affect(enemy, Burning.class).reignite(enemy, 3f);
			return damage;
		}
		private static final String SHIELD_ADDED = "shield_added";
		@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put(SHIELD_ADDED, shieldAdded); }
		@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); shieldAdded = b.getBoolean(SHIELD_ADDED); }
	}

	public static class Obelisk extends Mob {
		private int ownerId = -1;
		private int breaks;
		{
			spriteClass = ObeliskSprite.class;
			HP = HT = 1000;
			EXP = 10;
			defenseSkill = 0;
			state = PASSIVE;
			alignment = Alignment.NEUTRAL;
			loot = StoneOre.class;
			lootChance = 0.05f;
			properties.add(Property.UNKNOW);
			properties.add(Property.BOSS);
		}
		@Override public void beckon(int cell) { }
		@Override public boolean add(Buff buff) { return false; }
		@Override public int damageRoll() { return 0; }
		@Override public int attackSkill(Char target) { return 0; }
		@Override public int drRoll() { return 0; }
		@Override protected boolean act() {
			ElderAvatar owner = owner();
			if (breaks < 3 && 3 - breaks > 4 * HP / HT) {
				breaks++;
				if (owner != null && owner.isAlive()) owner.HP = owner.HT;
				return true;
			}
			return super.act();
		}
		private ElderAvatar owner() { Actor a = Actor.findById(ownerId); return a instanceof ElderAvatar ? (ElderAvatar) a : null; }
		@Override public void damage(int damage, Object src) {
			ElderAvatar owner = owner();
			if (owner != null && owner.isAlive() && owner.HP > 50) {
				yell(Messages.get(this, "impossible"));
				return;
			}
			super.damage(damage, src);
		}
		private static final String OWNER_ID = "owner_id", BREAKS = "breaks";
		@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put(OWNER_ID, ownerId); b.put(BREAKS, breaks); }
		@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); ownerId = b.getInt(OWNER_ID); breaks = Math.max(0, Math.min(3, b.getInt(BREAKS))); }
	}

	/** Keeps the old fixed throne-room coordinate out of generic mob logic. */
	private static final class SpsCityBossLevelCell {
		private static int well() { return 3 * 48 + 23; }
	}
}
