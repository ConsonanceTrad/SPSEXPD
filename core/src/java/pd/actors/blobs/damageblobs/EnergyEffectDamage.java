/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs.damageblobs;

import pd.actors.blobs.SpsElementalDamage;
import pd.actors.damagetype.DamageType;
import pd.effects.BlobEmitter;
import pd.effects.Speck;
import pd.messages.InlineText;

public class EnergyEffectDamage extends SpsElementalDamage {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(EnergyEffectDamage.class)
			.t("desc", "这片区域会持续造成能量伤害。");
	}

	@Override protected Object damageSource() { return DamageType.ENERGY_DAMAGE; }
	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.pour(Speck.factory(Speck.CONFUSION), 0.6f);
	}
}
