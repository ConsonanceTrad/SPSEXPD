/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs.damageblobs;

import pd.actors.blobs.SpsElementalDamage;
import pd.actors.damagetype.DamageType;
import pd.effects.BlobEmitter;
import pd.effects.particles.ShaftParticle;
import pd.items.Heap;
import pd.messages.InlineText;

public class LightEffectDamage extends SpsElementalDamage {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(LightEffectDamage.class)
			.t("desc", "这片区域会持续造成光明伤害。");
	}

	@Override protected Object damageSource() { return DamageType.LIGHT_DAMAGE; }
	@Override protected void affectHeap(Heap heap) { heap.lighthit(); }
	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(ShaftParticle.FACTORY, 1f, 0);
	}
}
