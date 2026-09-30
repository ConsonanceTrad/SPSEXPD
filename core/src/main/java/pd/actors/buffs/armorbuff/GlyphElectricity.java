/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs.armorbuff;

import pd.actors.blobs.Electricity;
import pd.actors.blobs.effectblobs.ElectriShock;
import pd.actors.buffs.Locked;
import pd.actors.buffs.Shocked;
import pd.actors.damagetype.DamageType;
import pd.actors.mobs.Shell;
import pd.items.wands.WandOfLightning;

public class GlyphElectricity extends ArmorGlyphBuff {
	{
		resistances.add(Shell.class);
		immunities.add(WandOfLightning.class);
		immunities.add(Shocked.class);
		immunities.add(ElectriShock.class);
		immunities.add(Electricity.class);
		immunities.add(Locked.class);
		immunities.add(DamageType.Shock.class);
	}
}
