/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs.damageblobs;

import pd.actors.blobs.SpsElementalDamage;
import pd.actors.damagetype.DamageType;
import pd.effects.BlobEmitter;
import pd.effects.particles.BlackFlameParticle;
import pd.items.Heap;
import pd.messages.InlineText;

public class DarkEffectDamage extends SpsElementalDamage {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(DarkEffectDamage.class)
			.t("desc", "这片区域会持续造成黑暗伤害。");
	}



	@Override protected Object damageSource() { return DamageType.DARK_DAMAGE; }
	@Override protected void affectHeap(Heap heap) { heap.darkhit(); }
	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(BlackFlameParticle.FACTORY, 0.03f, 0);
	}
}
