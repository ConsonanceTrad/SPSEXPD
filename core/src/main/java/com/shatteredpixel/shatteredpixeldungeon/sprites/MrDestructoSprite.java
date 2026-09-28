package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.TextureFilm;

public class MrDestructoSprite extends MobSprite {
	private int attackPos;

	public MrDestructoSprite() {
		this(0);
	}

	protected MrDestructoSprite(int offset) {
		texture(Assets.Sprites.MRDESTRUCTO);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(2, true);
		idle.frames(frames, offset+1, offset+2, offset+3, offset+4);
		run = new Animation(12, true);
		run.frames(frames, offset+2, offset+3, offset+4);
		attack = new Animation(15, false);
		attack.frames(frames, offset+1, offset+5);
		die = new Animation(8, false);
		die.frames(frames, offset+1, offset, offset+6);
		play(idle);
	}

	@Override
	public void attack(int pos) {
		attackPos = pos;
		super.attack(pos);
	}

	@Override
	public void onComplete(Animation anim) {
		if (anim == attack && parent != null && ch != null
				&& (Dungeon.level.heroFOV[ch.pos] || Dungeon.level.heroFOV[attackPos])) {
			parent.add(new Beam.DeathRay(center(), DungeonTilemap.raisedTileCenterToWorld(attackPos)));
		}
		super.onComplete(anim);
	}
}
