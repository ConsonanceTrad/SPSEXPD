/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs.damageblobs;

import pd.actors.blobs.SpsElementalDamage;
import pd.actors.damagetype.DamageType;
import pd.effects.BlobEmitter;
import pd.effects.particles.FlameParticle;
import pd.items.Heap;
import pd.messages.InlineText;

public class FireEffectDamage extends SpsElementalDamage {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(FireEffectDamage.class)
			.t("desc", "这片区域会持续造成火焰伤害。");
	}

	@Override protected Object damageSource() { return DamageType.FIRE_DAMAGE; }
	@Override protected void affectHeap(Heap heap) { heap.firehit(); }
	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(FlameParticle.FACTORY, 0.03f, 0);
	}
}
