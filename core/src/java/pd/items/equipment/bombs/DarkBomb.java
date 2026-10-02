/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bombs;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.ShadowCurse;
import pd.actors.buffs.Terror;
import pd.effects.CellEmitter;
import pd.effects.particles.SmokeParticle;
import pd.mechanics.pathfind.PathFinder;
import render.utils.math.Random;
import pd.messages.InlineText;

public class DarkBomb extends Bomb {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DarkBomb.class)
			.t("name", "暗黑炸弹")
			.t("desc", "这枚炸弹会散布恐惧与暗影诅咒，并对生命体造成巨额伤害。");
	}


	{ image = SpecificPlaceHolderDict.SOMETHING_0; }

	@Override
	public void explode(int cell) {
		super.explode(cell);
		if (Dungeon.level == null || !Dungeon.level.insideMap(cell)) return;
		for (int offset : PathFinder.NEIGHBOURS9) {
			int target = cell + offset;
			if (!Dungeon.level.insideMap(target)) continue;
			if (Dungeon.level.heroFOV[target]) CellEmitter.get(target).burst(SmokeParticle.FACTORY, 4);
			Char ch = Actor.findChar(target);
			if (ch == null || !ch.isAlive()) continue;
			Terror terror = Buff.affect(ch, Terror.class, Terror.DURATION);
			if (Dungeon.hero != null) terror.object = Dungeon.hero.id();
			Buff.affect(ch, ShadowCurse.class);
			boolean living = dealsHeavyDamageTo(ch);
			ch.damage(Random.NormalIntRange(living ? 200 : 50, living ? 400 : 100), this);
		}
		Dungeon.observe();
	}

	public static boolean dealsHeavyDamageTo(Char ch) {
		return Char.hasProp(ch, Char.Property.HUMAN)
				|| Char.hasProp(ch, Char.Property.PLANT)
				|| Char.hasProp(ch, Char.Property.ORC)
				|| Char.hasProp(ch, Char.Property.TROLL)
				|| Char.hasProp(ch, Char.Property.DWARF)
				|| Char.hasProp(ch, Char.Property.BEAST)
				|| Char.hasProp(ch, Char.Property.ELF)
				|| Char.hasProp(ch, Char.Property.GOBLIN)
				|| Char.hasProp(ch, Char.Property.BOSS)
				|| Char.hasProp(ch, Char.Property.MINIBOSS);
	}

	@Override public DarkBomb random() { return this; }
	@Override public int value() { return 20 * quantity; }
}
