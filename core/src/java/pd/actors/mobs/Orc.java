/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.Statistics;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.CorruptGas;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.items.Ankh;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.OrcSprite;
import pd.utils.GLog;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Orc extends Mob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Orc.class)
			.t("name", "猪人")
			.t("desc", "凶悍的猪面战士，受到重创时会释放腐化气体。")
			.t("killcount", "已击败猪人：%d");
	}


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
