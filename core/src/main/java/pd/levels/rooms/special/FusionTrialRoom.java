/*
 * Layout inspired by the challenge rooms in Special Surprise Pixel Dungeon and
 * the compact set-piece rooms in Magic Ling Pixel Dungeon.
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.levels.rooms.special;

import pd.Dungeon;
import pd.items.Heap;
import pd.items.Item;
import pd.items.keys.IronKey;
import pd.items.weapon.melee.fusion.Harp;
import pd.items.weapon.melee.fusion.ReedPipe;
import pd.items.weapon.melee.fusion.RitualBlade;
import pd.items.weapon.melee.fusion.VerdantGuard;
import pd.items.weapon.melee.fusion.WarDrum;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.painters.Painter;
import pd.levels.traps.ExplosiveTrap;
import pd.levels.traps.FlashingTrap;
import pd.levels.traps.GrippingTrap;
import pd.levels.traps.PoisonDartTrap;
import pd.levels.traps.Trap;
import pd.levels.traps.WarpingTrap;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public class FusionTrialRoom extends SpecialRoom {

	@Override
	public int minWidth() {
		return 7;
	}

	@Override
	public int maxWidth() {
		return 9;
	}

	@Override
	public int minHeight() {
		return 7;
	}

	@Override
	public int maxHeight() {
		return 9;
	}

	@Override
	public void paint(Level level) {
		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.EMPTY);

		Point center = center();
		Painter.drawLine(level, pointInside(entrance(), 1), center, Terrain.EMPTY);
		Painter.set(level, center, Terrain.PEDESTAL);

		Class<? extends Trap> trapClass = trapForDepth();
		for (int dx : new int[]{-1, 1}) {
			for (int dy : new int[]{-1, 1}) {
				int cell = level.pointToCell(new Point(center.x + dx, center.y + dy));
				Painter.set(level, cell, Terrain.TRAP);
				level.setTrap(Reflection.newInstance(trapClass).reveal(), cell);
			}
		}

		level.drop(prize(), level.pointToCell(center)).type = Heap.Type.CHEST;
		entrance().set(Door.Type.LOCKED);
		level.addItemToSpawn(new IronKey(Dungeon.depth));
	}

	private static Class<? extends Trap> trapForDepth() {
		switch (Math.min(4, Dungeon.depth / 5)) {
			case 0:
				return GrippingTrap.class;
			case 1:
				return PoisonDartTrap.class;
			case 2:
				return ExplosiveTrap.class;
			case 3:
				return FlashingTrap.class;
			default:
				return WarpingTrap.class;
		}
	}

	private static Item prize() {
		switch (Math.min(4, Dungeon.depth / 5)) {
			case 0:
				return Random.Int(2) == 0 ? new RitualBlade() : new ReedPipe();
			case 1:
				return Random.Int(2) == 0 ? new VerdantGuard() : new ReedPipe();
			case 2:
				return Random.Int(2) == 0 ? new VerdantGuard() : new WarDrum();
			case 3:
				return Random.Int(2) == 0 ? new WarDrum() : new Harp();
			default:
				return new Harp();
		}
	}
}
