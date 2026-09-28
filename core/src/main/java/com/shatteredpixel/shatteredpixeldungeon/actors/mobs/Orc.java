/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorruptGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.OrcSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class Orc extends Mob {

	{
		spriteClass = OrcSprite.class;
		state = SLEEPING;
		HP = HT = 400;
		defenseSkill = 30;
		EXP = 10;
		maxLvl = 40;
		properties.add(Property.DEMONIC);
		properties.add(Property.ORC);
		immunities.add(Amok.class);
		immunities.add(Terror.class);
		immunities.add(CorruptGas.class);
		immunities.add(Vertigo.class);
	}

	@Override public int damageRoll() { return Random.NormalIntRange(50, 90); }
	@Override public int attackSkill(Char target) { return 35; }
	@Override public float attackDelay() { return 1.5f; }
	@Override public int drRoll() { return Random.NormalIntRange(16, 32); }

	@Override
	public void damage(int damage, Object source) {
		if (damage > HT / 8) GameScene.add(Blob.seed(pos, 30, CorruptGas.class));
		super.damage(damage, source);
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		Statistics.orcsKilled++;
		GLog.w(Messages.get(this, "killcount", Statistics.orcsKilled));
		if (Statistics.orcsKilled % 10 == 0) Dungeon.level.drop(new Ankh(), pos).sprite.drop();
	}
}
