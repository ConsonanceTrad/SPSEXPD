package com.shatteredpixel.shatteredpixeldungeon.actors.blobs.effectblobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LightShootAttack;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class HolyLight extends SpsEffectBlob {
	@Override protected void affect(Char target) { Buff.affect(target, LightShootAttack.class).level(5); }
	@Override protected void affect(Heap heap) { heap.lighthit(); }
	@Override public void use(BlobEmitter emitter) { super.use(emitter); emitter.start(ShaftParticle.FACTORY, 1f, 0); }
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
