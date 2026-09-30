/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.BoxStar;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Chill;
import pd.actors.buffs.Frost;
import pd.actors.buffs.Slow;
import pd.actors.buffs.StoneIce;
import pd.levels.BossRushLevel;
import pd.levels.GroundItems;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.SpearTrap;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.sprites.IceRabbitSprite;
import pd.sprites.SpsFireRabbitSprite;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

/** FrostNova's first form. */
public class UIcecorps extends BossRushBoss {
	protected int timeToIce;

	{
		spriteClass = IceRabbitSprite.class;
		baseSpeed = 0.75f;
		HP = HT = 1500;
		EXP = 0;
		properties.add(Property.ICY);
		properties.add(Property.ORC);
		immunities.add(Chill.class);
		immunities.add(Frost.class);
	}

	@Override protected boolean usesHealthPhases() { return false; }

	@Override
	protected boolean canAttack(Char enemy) {
		return Dungeon.level.distance(pos, enemy.pos) <= 2;
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(2) == 0) Buff.affect(enemy, StoneIce.class).level(3f);
		enemy.damage(damageRoll() / 2, Frost.class);
		return damage / 2;
	}

	@Override
	protected boolean act() {
		if (++timeToIce > 20) {
			timeToIce = 0;
			Mob minion = createFireRabbit();
			minion.pos = Dungeon.level instanceof BossRushLevel
					? ((BossRushLevel) Dungeon.level).safeSpawnCell(pos) : pos;
			minion.state = minion.HUNTING;
			GameScene.add(minion);
		}
		return super.act();
	}

	protected Mob createFireRabbit() {
		return new pd.actors.mobs.FireRabbit();
	}

	@Override
	public void damage(int damage, Object source) {
		BoxStar star = buff(BoxStar.class);
		float starDuration = 0f;
		if (star != null && !(source instanceof StoneIce)) damage = 0;
		if (source instanceof StoneIce) {
			damage = 10;
			if (star != null) {
				starDuration = star.cooldown();
				star.detach();
			}
		}
		if (damage > 40) damage = Random.Int(10, 40);
		super.damage(damage, source);
		if (starDuration > 0f && isAlive()) Buff.affect(this, BoxStar.class, starDuration);
	}

	@Override protected int damageCap() { return 40; }
	@Override protected boolean createsCorruptGas() { return false; }

	@Override
	public void move(int step, boolean travelling) {
		super.move(step, travelling);
		freezeNearby(step);
	}

	protected void freezeNearby(int step) {
		int cell = step + PathFinder.NEIGHBOURS8[Random.Int(PathFinder.NEIGHBOURS8.length)];
		if (!Dungeon.level.insideMap(cell)) return;
		if (Dungeon.level.heroFOV[cell]) {
			if (Dungeon.level.water[cell]) {
				GroundItems.setTrap( Dungeon.level, new SpearTrap().reveal(), cell);
				Level.set(cell, Terrain.TRAP, Dungeon.level);
				GameScene.updateMap(cell);
			} else if (Dungeon.level.map[cell] == Terrain.EMPTY) {
				Level.set(cell, Terrain.WATER, Dungeon.level);
				GameScene.updateMap(cell);
			}
		}
		Char ch = Actor.findChar(cell);
		if (ch != null && ch != this) Buff.prolong(ch, Slow.class, 5f);
	}

	@Override protected Class<? extends BossRushBoss> nextBoss() { return UIcecorps2.class; }

	private static final String ICE_TIMER = "ice_timer";
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(ICE_TIMER, timeToIce); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); timeToIce = bundle.getInt(ICE_TIMER); }

	public static class FireRabbit extends Mob {
		{
			spriteClass = SpsFireRabbitSprite.class;
			HP = HT = 80;
			defenseSkill = 8;
			EXP = 5;
			properties.add(Property.FIERY);
			properties.add(Property.BOSS_MINION);
		}
		@Override public int damageRoll() { return Random.NormalIntRange(6, 8); }
		@Override public int attackSkill(Char target) { return 12; }
		@Override public int drRoll() { return Random.NormalIntRange(5, 10); }
		@Override protected boolean canAttack(Char enemy) { return Dungeon.level.distance(pos, enemy.pos) <= 3; }
		@Override public int attackProc(Char enemy, int damage) {
			if (Random.Int(3) == 0) Buff.affect(enemy, Burning.class).reignite(this, 4f);
			if (Dungeon.level.distance(pos, enemy.pos) == 3) return 0;
			return damage;
		}
	}
}
