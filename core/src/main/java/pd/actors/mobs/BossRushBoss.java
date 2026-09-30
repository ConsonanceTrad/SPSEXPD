/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.CorruptGas;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Sleep;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.items.scrolls.ScrollOfPsionicBlast;
import pd.levels.BossRushLevel;
import pd.scenes.GameScene;
import pd.ui.BossHealthBar;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

/** Shared state and safe stage hand-off for the eight ultimate bosses. */
public abstract class BossRushBoss extends Mob {

	protected static final float SPAWN_DELAY = 2f;
	protected int breaks;

	{
		baseSpeed = 0.75f;
		HP = HT = 1000;
		EXP = 20;
		defenseSkill = 5;
		properties.add(Property.BOSS);
		resistances.add(ToxicGas.class);
		resistances.add(Poison.class);
		immunities.add(Terror.class);
		immunities.add(Amok.class);
		immunities.add(Charm.class);
		immunities.add(Sleep.class);
		immunities.add(Burning.class);
		immunities.add(ToxicGas.class);
		immunities.add(ScrollOfPsionicBlast.class);
		immunities.add(Vertigo.class);
		immunities.add(Paralysis.class);
		immunities.add(Bleeding.class);
		immunities.add(CorruptGas.class);
	}

	@Override
	public int damageRoll() {
		int level = Dungeon.hero == null ? 1 : Dungeon.hero.lvl;
		return Random.NormalIntRange(level / 2, level);
	}

	@Override
	public int attackSkill(Char target) {
		return 100;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 2);
	}

	@Override
	public float speed() {
		return breaks == 3 ? 2f * super.speed() : super.speed();
	}

	@Override
	protected boolean act() {
		if (usesHealthPhases() && 3 - breaks > 4 * HP / HT) {
			breaks++;
			onPhaseChanged(breaks);
			spend(TICK);
			return true;
		}
		return super.act();
	}

	protected void onPhaseChanged(int phase) {
	}

	protected boolean usesHealthPhases() {
		return true;
	}

	@Override
	public void damage(int damage, Object source) {
		if (createsCorruptGas() && damage > 15) {
			GameScene.add(Blob.seed(pos, 30, CorruptGas.class));
		}
		super.damage(Math.min(damage, damageCap()), source);
	}

	protected boolean createsCorruptGas() {
		return true;
	}

	protected int damageCap() {
		return 20;
	}

	protected abstract Class<? extends BossRushBoss> nextBoss();

	@Override
	public void die(Object cause) {
		super.die(cause);
		if (Dungeon.level instanceof BossRushLevel) {
			((BossRushLevel) Dungeon.level).advance(this, nextBoss());
		}
	}

	public static <T extends BossRushBoss> T spawn(Class<T> type, int pos) {
		if (!(Dungeon.level instanceof BossRushLevel)) return null;
		T boss = Reflection.newInstance(type);
		if (boss == null) return null;
		boss.pos = ((BossRushLevel) Dungeon.level).safeSpawnCell(pos);
		boss.state = boss.HUNTING;
		GameScene.add(boss, SPAWN_DELAY);
		BossHealthBar.assignBoss(boss);
		return boss;
	}

	private static final String BREAKS = "breaks";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(BREAKS, breaks);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		breaks = bundle.getInt(BREAKS);
		if (state != SLEEPING) BossHealthBar.assignBoss(this);
	}
}
