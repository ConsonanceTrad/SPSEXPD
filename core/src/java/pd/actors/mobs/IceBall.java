/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.sprites.SnowballSprite;
import pd.messages.InlineText;

public class IceBall extends Mob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(IceBall.class)
			.t("name", "大雪球")
			.t("desc", "巨大的冰球滚过来了。");
	}


	{
		spriteClass = SnowballSprite.class;
		baseSpeed = 0.5f;
		HP = HT = 10;
		defenseSkill = 0;
		EXP = 1;
		maxLvl = 1;
		properties.add(Property.ICY);
		properties.add(Property.INORGANIC);
		properties.add(Property.ELEMENT);
		properties.add(Property.MECH);
	}

	@Override protected boolean canAttack(Char enemy) { return false; }
	@Override protected Char chooseEnemy() { return null; }
	@Override public int damageRoll() { return 100; }
	@Override public int attackSkill(Char target) { return 1000; }
	@Override public int drRoll() { return 0; }
	@Override public boolean add(Buff buff) { return false; }

	@Override public void die(Object cause) {
		int origin = pos;
		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = origin + offset;
			if (!Dungeon.level.insideMap(cell) || !Dungeon.level.passable[cell]
					|| Actor.findChar(cell) != null) continue;
			Mob mob = SpsChallengeMobPool.randomIceBallSpawn();
			mob.pos = cell;
			mob.state = mob.HUNTING;
			GameScene.add(mob, 1f);
			if (mob.sprite != null) mob.sprite.jump(origin, cell, () -> { });
		}
		super.die(cause);
	}
}
