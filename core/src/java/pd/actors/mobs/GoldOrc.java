/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.CorruptGas;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.scenes.GameScene;
import pd.sprites.GoldOrcSprite;
import render.utils.math.Random;

public class GoldOrc extends Mob {

	{
		spriteClass = GoldOrcSprite.class;
		state = SLEEPING;
		HP = HT = 500;
		defenseSkill = 35;
		EXP = 25;
		properties.add(Property.DEMONIC);
		properties.add(Property.ORC);
		immunities.add(Amok.class);
		immunities.add(Terror.class);
		immunities.add(CorruptGas.class);
		immunities.add(Vertigo.class);
	}

	@Override public int damageRoll() { return Random.NormalIntRange(55, 115); }
	@Override public int attackSkill(Char target) { return 55; }
	@Override public float attackDelay() { return 1.5f; }
	@Override public int drRoll() { return Random.NormalIntRange(16, 32); }

	@Override
	public void damage(int damage, Object source) {
		if (damage > HT / 8) GameScene.add(Blob.seed(pos, 30, CorruptGas.class));
		super.damage(damage, source);
	}
}
