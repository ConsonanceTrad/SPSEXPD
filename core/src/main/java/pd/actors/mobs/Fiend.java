/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Buff;
import pd.actors.buffs.STRDown;
import pd.actors.buffs.ShadowCurse;
import pd.actors.buffs.Silent;
import pd.actors.buffs.Terror;
import pd.actors.damagetype.DamageType;
import pd.effects.Speck;
import pd.items.Generator;
import pd.items.quest.AdventureJournal;
import pd.items.wands.fusion.WandOfBlood;
import pd.mechanics.Ballistica;
import pd.scenes.GameScene;
import pd.sprites.FiendSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Fiend extends Mob {

	private static final float TIME_TO_ZAP = 2f;
	private static final float SPAWN_DELAY = 6f;
	private static final String LEGACY_DEPTH = "legacy_depth";
	private int legacyDepth = effectiveLegacyDepth();

	private static int effectiveLegacyDepth() {
		int destination = AdventureJournal.destinationForBranch(Dungeon.branch);
		if (destination == 9) return 35;
		if (destination == 22) return 85;
		return legacyDungeonDepth();
	}

	{
		spriteClass = FiendSprite.class;
		baseSpeed = 1.5f;
		viewDistance = 4;
		HP = HT = 80 + legacyDepth * Random.NormalIntRange(2, 5);
		defenseSkill = 2;
		EXP = 20;
		loot = Generator.Category.SCROLL;
		lootChance = 0.15f;
		properties.add(Property.DEMONIC);
		properties.add(Property.ELEMENT);
		immunities.add(DamageType.Dark.class);
		resistances.add(WandOfBlood.class);
	}

	@Override public int damageRoll() { return Random.NormalIntRange(legacyDepth / 2, legacyDepth); }
	@Override public int attackSkill(Char target) { return 50; }
	@Override public int drRoll() { return Random.NormalIntRange(5, 10); }

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEGACY_DEPTH, legacyDepth);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(LEGACY_DEPTH)) legacyDepth = bundle.getInt(LEGACY_DEPTH);
	}

	@Override
	protected boolean canAttack(Char enemy) {
		if (buff(Silent.class) != null) {
			return Dungeon.level.adjacent(pos, enemy.pos) && !isCharmedBy(enemy);
		}
		return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	@Override
	protected boolean doAttack(Char enemy) {
		if (Dungeon.level.adjacent(pos, enemy.pos)) return super.doAttack(enemy);
		if (sprite != null && (sprite.visible || enemy.sprite != null && enemy.sprite.visible)) {
			sprite.zap(enemy.pos);
			return false;
		}
		zap();
		return true;
	}

	private void zap() {
		spend(TIME_TO_ZAP);
		if (enemy != null && enemy.isAlive() && hit(this, enemy, true)) {
			if (enemy == Dungeon.hero && Random.Int(5) == 0) Buff.prolong(enemy, STRDown.class, 5f);
			enemy.damage(legacyZapDamage(), this);
		} else if (enemy != null && enemy.sprite != null) {
			enemy.sprite.showStatus(0xFFFFFF, enemy.defenseVerb());
		}
	}

	public void onZapComplete() {
		zap();
		next();
	}

	int legacyZapDamage() { return Random.Int(20, 45); }

	@Override
	public boolean add(Buff buff) {
		if (buff instanceof ShadowCurse) {
			if (HP < HT) {
				HP = Math.min(HT, HP + HT / 10);
				if (sprite != null) sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
			}
			return false;
		}
		if (buff instanceof Amok || buff instanceof Terror) {
			damage(Random.NormalIntRange(1, HT * 2 / 3), buff);
			return false;
		}
		return super.add(buff);
	}

	public static void spawnAround(int center) {
		if (Dungeon.level == null) return;
		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = center + offset;
			if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell]
					&& Actor.findChar(cell) == null) spawnAt(cell);
		}
	}

	public static void spawnAroundChance(int center) {
		if (Dungeon.level == null) return;
		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = center + offset;
			if (Dungeon.level.insideMap(cell) && Dungeon.level.distance(center, cell) == 1
					&& Dungeon.level.passable[cell] && Actor.findChar(cell) == null
					&& Random.Float() < 0.75f) {
				spawnAt(cell);
			}
		}
	}

	public static Fiend spawnAt(int pos) {
		if (Dungeon.level == null || !Dungeon.level.insideMap(pos)
				|| !Dungeon.level.passable[pos] || Actor.findChar(pos) != null) return null;
		Fiend fiend = new Fiend();
		fiend.pos = pos;
		fiend.state = fiend.HUNTING;
		GameScene.add(fiend, SPAWN_DELAY);
		return fiend;
	}
}
