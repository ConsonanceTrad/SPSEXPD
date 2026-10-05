/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.mobs.npcs.TownNpc;
import render.noosa.TextureFilm;

/** Loads each town resident's original standalone sprite sheet. */
public class TownNpcSprite extends MobSprite {

	@Override
	public void link(Char ch) {
		TownNpc.Spec spec = ((TownNpc) ch).spec();
		texture(spec.asset);
		TextureFilm frames = new TextureFilm(texture, spec.frameWidth, spec.frameHeight);
		int requested = spec == TownNpc.Spec.OLD_NEW_STWIST
				? (Dungeon.gnollMission ? 0 : 8)
				: spec.firstFrame;
		//SPSEXPD: 按图集实际帧数收敛帧号——越界帧会取到 null，而空帧会让精灵更新直接崩溃
		int frameCount = Math.max(1,
				(texture.width / spec.frameWidth) * (texture.height / spec.frameHeight));
		int first = Math.min(requested, frameCount - 1);
		if (requested + 3 >= frameCount) {
			System.err.println("[SPSEXPD] town npc sprite frames short: asset=" + spec.asset
					+ " requested=" + requested + " frames=" + frameCount
					+ " size=" + texture.width + "x" + texture.height
					+ " cell=" + spec.frameWidth + "x" + spec.frameHeight);
		}
		int f1 = nextFrame(first, frameCount);
		int f2 = nextFrame(f1, frameCount);
		int f3 = nextFrame(f2, frameCount);
		idle = new Animation(8, true);
		idle.frames(frames, first, first, first, f1, f1, f2, f2, f3, f3);
		run = new Animation(12, true);
		run.frames(frames, first);
		attack = new Animation(12, false);
		attack.frames(frames, first);
		die = new Animation(12, false);
		die.frames(frames, first);
		play(idle);
		super.link(ch);
	}

	//SPSEXPD: 取下一个存在的帧号，超出图集帧数时停留在当前帧
	private static int nextFrame(int prev, int frameCount) {
		return prev + 1 < frameCount ? prev + 1 : prev;
	}
}
