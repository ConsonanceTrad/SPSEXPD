/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Assets;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.mobs.Mob;
import pd.levels.CellFlags;
import pd.scenes.GameScene;
import pd.sprites.ShadowRatSprite;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import pd.messages.InlineText;

public class DarkFallen extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(DarkFallen.class)
			.t("name", "暗影降临")
			.t("darkliver.name", "夜影")
			.t("darkliver.desc", "和时间相关，只在晚上攻击。");
	}


	@Override
	public boolean act() {
		if (Dungeon.level != null && !Dungeon.shopOnLevel() && !hasDarkLiver()) {
			DarkLiver liver = new DarkLiver();
			int pos = Dungeon.level.randomRespawnCell(liver);
			if (pos != -1) {
				liver.pos = pos;
				GameScene.add(liver, 1f);
				Sample.INSTANCE.play(Assets.Sounds.BURNING);
			}
		}
		spend(TICK);
		return true;
	}

	private boolean hasDarkLiver() {
		for (Mob mob : Dungeon.level.mobs()) {
			if (mob instanceof DarkLiver && mob.isAlive()) return true;
		}
		return false;
	}

	public static class DarkLiver extends Mob {

		{
			spriteClass = ShadowRatSprite.class;
			HP = HT = 10 + Statistics.spsDays;
			defenseSkill = Statistics.spsDays;
			EXP = 1;
			viewDistance = 3;
			flying = true;
			state = HUNTING;
			properties.add(Property.UNKNOW);
		}

		@Override
		protected boolean canAttack(Char enemy) {
			return Statistics.spsNight()
					&& Dungeon.level.adjacent(pos, enemy.pos)
					&& !isCharmedBy(enemy);
		}

		@Override
		public void move(int step, boolean travelling) {
			super.move(step, travelling);
			Buff.prolong(this, HiddenShadow.class, 3f);
		}

		@Override
		protected boolean getCloser(int target) {
			return Statistics.spsNight() ? super.getCloser(target) : getFurther(target);
		}

		@Override
		protected boolean act() {
			recoverWhileSeparated();
			return super.act();
		}

		protected void recoverWhileSeparated() {
			if (enemy == null || !Dungeon.level.adjacent(pos, enemy.pos)) {
				HP = Math.min(HT, HP + 5);
			}
		}

		@Override
		public int attackProc(Char enemy, int damage) {
			Buff.detach(this, HiddenShadow.class);
			if (Random.Int(5) == 0) {
				Buff.affect(enemy, Terror.class, 2f).object = id();
			}
			return super.attackProc(enemy, damage);
		}

		@Override
		public int damageRoll() {
			return Random.NormalIntRange(Statistics.spsDays, 4 * Statistics.spsDays);
		}

		@Override
		public int attackSkill(Char target) {
			return Statistics.spsDays + 5;
		}

		@Override
		public int drRoll() {
			return Statistics.spsDays;
		}

		@Override
		public void die(Object cause) {
			revealSurroundings();
			super.die(cause);
			Dungeon.observe();
		}

		protected void revealSurroundings() {
			if (Dungeon.level == null) return;
			for (int cell = 0; cell < Dungeon.level.length(); cell++) {
				if (Dungeon.level.distance(cell, pos) < 3 && Dungeon.level.discoverable[cell]) {
					Dungeon.level.mapped[cell] = true;
					CellFlags.discover( Dungeon.level, cell);
				}
			}
			GameScene.updateFog(pos, 3);
		}
	}
}
