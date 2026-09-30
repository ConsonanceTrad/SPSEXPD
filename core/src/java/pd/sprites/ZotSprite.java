/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.items.trinkets.RatSkull;
import pd.levels.CellFlags;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import render.noosa.TextureFilm;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.math.Random;

/** Original 18px Zot animation and explosive ranged attack. */
public class ZotSprite extends MobSprite {

	private final Animation cast;

	public ZotSprite() {
		texture(Assets.Sprites.SPS_ZOT);
		TextureFilm frames = new TextureFilm(texture, 18, 18);
		idle = new Animation(2, true); idle.frames(frames, 0, 0, 0, 1, 0);
		run = new Animation(8, false); run.frames(frames, 0, 1, 2);
		attack = new Animation(8, false); attack.frames(frames, 0, 2, 2);
		cast = new Animation(8, false); cast.frames(frames, 2, 3, 4);
		die = new Animation(8, false); die.frames(frames, 0, 5, 6, 7, 8, 9, 8);
		play(run.clone());
	}

	@Override
	public void move(int from, int to) {
		place(to);
		play(run);
		turnTo(from, to);
		isMoving = true;
		if (Dungeon.level.water[to]) GameScene.ripple(to);
		ch.onMotionComplete();
	}

	@Override
	public void attack(final int cell) {
		if (!Dungeon.level.adjacent(cell, ch.pos)) {
			((MissileSprite) parent.recycle(MissileSprite.class)).reset(this, cell,
					new RatSkull(), new Callback() {
						@Override public void call() { ch.onAttackComplete(); }
					});
			play(cast);
			turnTo(ch.pos, cell);
			explode(cell);
		} else {
			super.attack(cell);
		}
	}

	private void explode(int cell) {
		Sample.INSTANCE.play(Assets.Sounds.BLAST, 2f);
		boolean terrainAffected = false;
		for (int offset : PathFinder.NEIGHBOURS9) {
			int target = cell + offset;
			if (!Dungeon.level.insideMap(target)) continue;
			if (Dungeon.level.flamable[target]) {
				CellFlags.destroy( Dungeon.level, target);
				GameScene.updateMap(target);
				terrainAffected = true;
			}
			Char targetChar = Actor.findChar(target);
			if (targetChar == Dungeon.hero) {
				int min = target == cell ? Dungeon.scalingDepth() + 5 : 1;
				int max = Dungeon.scalingDepth() + 10;
				int damage = Random.NormalIntRange(min, max) - Math.max(0, targetChar.drRoll());
				if (damage > 0) targetChar.damage(damage, ch);
			}
		}
		if (terrainAffected) Dungeon.observe();
	}

	@Override
	public void onComplete(Animation animation) {
		if (animation == run) {
			isMoving = false;
			idle();
		} else {
			super.onComplete(animation);
		}
	}
}
