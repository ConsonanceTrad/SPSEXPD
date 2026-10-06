/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Hex;
import pd.actors.mobs.Mob;
import pd.mechanics.Ballistica;
import pd.sprites.AbiSprite;
import pd.sprites.CharSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

/** The original Abbey companion summoned by Alfred's whistle. */
public class Abi extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Abi.class)
			.t("name", "阿比")
			.t("desc", "由阿飞制作的哨子召唤出的精灵。");
	}



	{
		spriteClass = AbiSprite.class;
		properties.add(Property.BEAST);
		flying = false;
		cooldown = 5;
		updateStats(true);
	}

	@Override protected Kind kind() { return Kind.ABI; }

	@Override
	public void updateStats(boolean refill) {
		int oldHT = HT;
		HT = 150 + 3 * petLevel();
		defenseSkill = petLevel();
		if (refill) HP = HT;
		else if (HT > oldHT) HP = Math.min(HT, HP + HT - oldHT);
	}

	@Override public int damageRoll() {
		return Random.NormalIntRange((int) (5 + petLevel() * 0.5f), (int) (5 + petLevel() * 1.5f));
	}
	@Override public int drRoll() { return Random.IntRange(petLevel(), petLevel() * 3); }
	@Override public int attackSkill(Char target) { return petLevel() + 5; }

	@Override
	protected boolean canAttack(Char enemy) {
		if (cooldown > 1) return super.canAttack(enemy);
		return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	@Override
	protected boolean doAttack(Char enemy) {
		if (cooldown > 1) return super.doAttack(enemy);
		if (Random.Int(20) == 0) {
			superAttack();
			return true;
		}
		if (sprite != null && (sprite.visible || enemy.sprite != null && enemy.sprite.visible)) {
			sprite.zap(enemy.pos);
			return false;
		}
		zap();
		return true;
	}

	private void superAttack() {
		spend(TICK);
		int damage = damageRoll() * 3;
		for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
			if (mob.isAlive() && !(mob instanceof LegacyPet)) mob.damage(damage, this);
		}
		cooldown = 5;
	}

	/** 掉落型/无目标技能：施法目标是自身 */
	@Override protected Char castTarget() { return this; }

	@Override public boolean hasAbility() { return true; }

	/** 技能是全场范围强击（CD 5） */
	@Override protected boolean castOn(Char target) {
		if (cooldown > 0 || Dungeon.level == null) return false;
		superAttack();
		return true;
	}

	private void zap() {
		spend(TICK);
		if (enemy == null || !enemy.isAlive()) return;
		if (hit(this, enemy, true)) {
			int damage = damageRoll();
			if (Random.Int(5) == 1) {
				enemy.damage(damage * 2, this);
				Buff.affect(enemy, Hex.class, 6f);
			} else {
				enemy.damage(damage, this);
				Buff.affect(enemy, Blindness.class, 6f);
			}
			cooldown = 5;
		} else if (enemy.sprite != null) {
			enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
		}
	}

	public void onZapComplete() { zap(); next(); }

	@Override
	public int attackProc(Char enemy, int damage) {
		if (cooldown > 0) cooldown--;
		return damage;
	}
}
