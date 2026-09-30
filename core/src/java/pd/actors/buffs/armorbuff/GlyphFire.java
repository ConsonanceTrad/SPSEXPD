/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs.armorbuff;

import pd.actors.blobs.Fire;
import pd.actors.blobs.TarGas;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Hot;
import pd.actors.buffs.Tar;
import pd.actors.damagetype.DamageType;
import pd.actors.mobs.FireElemental;

public class GlyphFire extends ArmorGlyphBuff {
	{
		immunities.add(Burning.class);
		immunities.add(Fire.class);
		immunities.add(Tar.class);
		immunities.add(TarGas.class);
		immunities.add(Hot.class);
		immunities.add(DamageType.Fire.class);
		resistances.add(FireElemental.class);
	}
}
