/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.CorruptGas;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.hero.Hero;
import pd.items.scrolls.ScrollOfPsionicBlast;
import pd.scenes.GameScene;
import pd.sprites.ErrorSprite;
import com.watabou.utils.Random;

/** The hostile body produced by the legacy Nightmare Virus challenge. */
public class Virus extends Mob {

	{
		spriteClass = ErrorSprite.class;
		Hero hero = Dungeon.hero;
		HP = HT = Math.max(1, hero == null ? 1 : hero.HT / 5);
		EXP = 0;
		defenseSkill = hero == null ? 0 : hero.defenseSkill(this);
		properties.add(Property.UNKNOW);
		properties.add(Property.BOSS);
		immunities.add(Burning.class);
		immunities.add(ToxicGas.class);
		immunities.add(ScrollOfPsionicBlast.class);
		immunities.add(CorruptGas.class);
	}

	@Override
	public int damageRoll() {
		int level = Dungeon.hero == null ? 1 : Dungeon.hero.lvl;
		return Random.NormalIntRange(level / 2, level);
	}

	@Override
	public int attackSkill(Char target) {
		return Dungeon.hero == null ? 1 : Dungeon.hero.attackSkill(target);
	}

	@Override
	protected boolean act() {
		damage(1, this);
		return isAlive() && super.act();
	}

	@Override public int drRoll() { return 0; }
	@Override public float speed() { return 1f; }
	@Override public boolean add(Buff buff) { return false; }
	@Override protected boolean spawnsNightmareVirusOnDeath() { return false; }

	@Override
	public void die(Object cause) {
		releaseCorruptGas();
		super.die(cause);
	}

	void releaseCorruptGas() {
		if (Dungeon.level != null && Dungeon.level.insideMap(pos)) {
			GameScene.add(Blob.seed(pos, 20, CorruptGas.class));
		}
	}
}
