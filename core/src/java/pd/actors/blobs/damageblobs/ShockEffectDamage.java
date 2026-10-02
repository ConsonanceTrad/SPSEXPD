/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs.damageblobs;

import pd.actors.blobs.SpsElementalDamage;
import pd.actors.damagetype.DamageType;
import pd.effects.BlobEmitter;
import pd.effects.particles.EnergyParticle;
import pd.items.Heap;
import pd.messages.InlineText;

public class ShockEffectDamage extends SpsElementalDamage {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(ShockEffectDamage.class)
			.t("desc", "这片区域会持续造成雷电伤害。");
	}

	@Override protected Object damageSource() { return DamageType.SHOCK_DAMAGE; }
	@Override protected void affectHeap(Heap heap) { heap.shockhit(); }
	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(EnergyParticle.FACTORY, 0.1f, 0);
	}
}
