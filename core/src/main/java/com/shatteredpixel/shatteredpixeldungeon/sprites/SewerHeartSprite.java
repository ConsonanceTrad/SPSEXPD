/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.SewerHeart;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EnergyParticle;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.particles.Emitter;

public class SewerHeartSprite extends MobSprite {
	private int zapPos;
	private final Animation charging;
	private final Emitter chargeParticles;
	private Emitter cloud;

	public SewerHeartSprite() {
		texture(Assets.Sprites.SPS_SEWER_HEART);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(1, true); idle.frames(frames, 0);
		run = new Animation(1, true); run.frames(frames, 0);
		attack = new Animation(10, false); attack.frames(frames, 0, 1);
		zap = attack.clone();
		charging = attack.clone();
		die = new Animation(8, false); die.frames(frames, 1, 2, 3, 4, 5, 6, 7, 7, 7);
		chargeParticles = centerEmitter();
		chargeParticles.autoKill = false;
		chargeParticles.pour(EnergyParticle.FACTORY, 0.05f);
		chargeParticles.on = false;
		play(idle);
	}

	@Override public void link(Char ch) {
		super.link(ch);
		if (((SewerHeart)ch).beamCharged()) play(charging);
		cloud = emitter();
		cloud.pour(Speck.factory(Speck.TOXIC), 0.7f);
	}

	@Override public void turnTo(int from, int to) { }

	@Override public void update() {
		super.update();
		chargeParticles.pos(center());
		chargeParticles.visible = visible;
		if (cloud != null) cloud.visible = visible;
	}

	@Override public void die() {
		super.die();
		if (cloud != null) cloud.on = false;
	}

	public void charge(int cell) {
		turnTo(ch.pos, cell);
		play(charging);
	}

	@Override public void play(Animation animation) {
		chargeParticles.on = animation == charging;
		super.play(animation);
	}

	@Override public void zap(int cell) {
		zapPos = cell;
		super.zap(cell);
	}

	@Override public void onComplete(Animation animation) {
		super.onComplete(animation);
		if (animation == zap) {
			idle();
			Char target = Actor.findChar(zapPos);
			if (parent != null) parent.add(new Beam.LightRay(center(), target == null || target.sprite == null
					? DungeonTilemap.raisedTileCenterToWorld(zapPos) : target.sprite.center()));
			((SewerHeart)ch).deathGaze();
			ch.next();
		} else if (animation == die) {
			chargeParticles.killAndErase();
		}
	}
}
