/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.BeOld;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Silent;
import pd.mechanics.Ballistica;
import pd.items.BossRush;
import pd.messages.Messages;
import pd.sprites.NewDragon01Sprite;
import pd.sprites.CharSprite;
import watabou.utils.Random;

/** The original fixed guardian dragon in Dolya town. */
public class AdultDragonViolet extends Mob {

	private static final float TIME_TO_ZAP = 1f;

	{
		spriteClass = NewDragon01Sprite.class;
		baseSpeed = 1.5f;
		HP = HT = 8000;
		EXP = 10;
		defenseSkill = 40;
		loot = new BossRush();
		lootChance = 1f;
		properties.add(Property.BOSS);
		properties.add(Property.DRAGON);
		resistances.add(ToxicGas.class);
		resistances.add(Poison.class);
	}

	@Override
	public int damageRoll() {
		return Random.Int(60, 80);
	}

	@Override
	public int attackSkill(Char target) {
		return 50;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(20, 50);
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		yell(Messages.get(this, "die"));
	}

	@Override
	protected boolean canAttack(Char enemy) {
		if (buff(Silent.class) != null) return super.canAttack(enemy);
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
		if (enemy == null || !enemy.isAlive()) return;
		yell(Messages.get(this, "atk"));
		if (hit(this, enemy, true)) {
			enemy.damage(damageRoll(), this);
			Buff.affect(enemy, BeOld.class).set(20f);
		} else if (enemy.sprite != null) {
			enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
		}
	}

	public void onZapComplete() {
		zap();
		next();
	}
}
