/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.effects.particles.SparkParticle;
import pd.messages.Messages;
import pd.sprites.OtiluckStoneSprite;
import render.noosa.Camera;
import render.utils.math.Random;

/** Invulnerable lightning statue powered by the corrupted Otiluke mirror. */
public class LitTower extends Mob {

	public static class LightningBolt {
	}

	{
		spriteClass = OtiluckStoneSprite.class;
		HP = HT = 600;
		defenseSkill = 1000;
		EXP = 25;
		state = PASSIVE;
		properties.add(Property.INORGANIC);
		properties.add(Property.MECH);
		properties.add(Property.MAGICER);
		properties.add(Property.BOSS);
	}

	private boolean otilukeAlive() {
		for (Mob mob : Dungeon.level.mobs()) if (mob instanceof Otiluke && mob.isAlive()) return true;
		return false;
	}

	@Override
	protected boolean act() {
		if (Dungeon.hero != null && Dungeon.hero.isAlive()
				&& Dungeon.level.distance(pos, Dungeon.hero.pos) < 5 && otilukeAlive()) {
			yell(Messages.get(this, "zap"));
			if (sprite != null && sprite.visible) sprite.zap(Dungeon.hero.pos);
			Dungeon.hero.damage(Random.IntRange(100, 199), new LightningBolt());
			if (Dungeon.hero.sprite != null) {
				Dungeon.hero.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
				Dungeon.hero.sprite.flash();
			}
			Camera.main.shake(2, 0.3f);
		}
		spend(TICK);
		return true;
	}

	@Override
	public int damageRoll() {
		return 0;
	}

	@Override
	public int attackSkill(Char target) {
		return 100;
	}

	@Override
	public int drRoll() {
		return 1000;
	}

	@Override
	public void damage(int damage, Object source) {
	}

	@Override
	public boolean add(Buff buff) {
		return false;
	}

	@Override
	public void beckon(int cell) {
	}
}
