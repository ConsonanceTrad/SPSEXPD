/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ForeverShadow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DummySprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** Summons the original stationary, noisy training doll. */
public class ScrollOfDummy extends Scroll {
	@Override public void doRead() {
		detach(curUser.belongings.backpack);
		spawnDummy(curUser.pos, 30);
		identify();
		Sample.INSTANCE.play(Assets.Sounds.READ);
		readAnimation();
		Buff.affect(curUser, Invisibility.class, 5f);
	}
	@Override
	public void empoweredRead() {
		detach(curUser.belongings.backpack);
		spawnDummy(curUser.pos, 50);
		identify();
		Sample.INSTANCE.play(Assets.Sounds.READ);
		readAnimation();
		Buff.affect(curUser, ForeverShadow.class, 5f);
	}
	private static MiniDummy spawnDummy(int center, int hp) {
		if (Dungeon.level == null) return null;
		ArrayList<Integer> cells = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = center + offset;
			if (cell >= 0 && cell < Dungeon.level.length() && Actor.findChar(cell) == null
					&& (Dungeon.level.passable[cell] || Dungeon.level.avoid[cell])) cells.add(cell);
		}
		if (cells.isEmpty()) return null;
		MiniDummy dummy = new MiniDummy();
		dummy.HP = dummy.HT = hp;
		GameScene.add(dummy);
		int destination = Random.element(cells);
		if (dummy.sprite != null) {
			ScrollOfTeleportation.appear(dummy, destination);
		} else {
			dummy.pos = destination;
			Dungeon.level.occupyCell(dummy);
		}
		return dummy;
	}
	public static class MiniDummy extends Mob {
		{
			spriteClass = DummySprite.class;
			alignment = Alignment.ALLY;
			state = WANDERING;
			HP = HT = 40;
			defenseSkill = 0;
			EXP = 0;
			properties.add(Property.UNKNOW);
		}
		@Override protected boolean act() {
			decay();
			return super.act();
		}
		private void decay() { damage(1, this); }
		@Override public int drRoll() { return 0; }
		@Override protected boolean getCloser(int target) { return true; }
		@Override protected boolean getFurther(int target) { return true; }
		@Override protected Char chooseEnemy() { return null; }
		@Override public int defenseProc(Char enemy, int damage) {
			HP++;
			return super.defenseProc(enemy, damage);
		}
		@Override public void damage(int damage, Object source) {
			super.damage(damage > 0 ? 2 : damage, source);
		}
	}
	@Override public int value() { return isKnown() ? 30 * quantity : super.value(); }
}
