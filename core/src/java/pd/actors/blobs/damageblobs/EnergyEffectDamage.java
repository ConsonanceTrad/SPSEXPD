/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs.damageblobs;

import pd.actors.blobs.SpsElementalDamage;
import pd.actors.damagetype.DamageType;
import pd.effects.BlobEmitter;
import pd.effects.Speck;

public class EnergyEffectDamage extends SpsElementalDamage {
	@Override protected Object damageSource() { return DamageType.ENERGY_DAMAGE; }
	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.pour(Speck.factory(Speck.CONFUSION), 0.6f);
	}
}
