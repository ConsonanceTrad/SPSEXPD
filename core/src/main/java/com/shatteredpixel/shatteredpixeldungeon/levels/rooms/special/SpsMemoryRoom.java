/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.MemoryFire;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;

/** Hidden overgrown SPS chamber containing the memory fire. */
public class SpsMemoryRoom extends SpecialRoom {

	@Override
	public void paint(Level level) {
		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.HIGH_GRASS);

		Point center = center();
		Door door = entrance();
		if (door.x == left || door.x == right) {
			Point turn = Painter.drawInside(level, this, door,
					Math.max(0, Math.abs(door.x - center.x) - 2), Terrain.EMPTY_SP);
			for (; turn.y != center.y; turn.y += turn.y < center.y ? 1 : -1) {
				Painter.set(level, turn, Terrain.EMPTY_SP);
			}
		} else {
			Point turn = Painter.drawInside(level, this, door,
					Math.max(0, Math.abs(door.y - center.y) - 2), Terrain.EMPTY_SP);
			for (; turn.x != center.x; turn.x += turn.x < center.x ? 1 : -1) {
				Painter.set(level, turn, Terrain.EMPTY_SP);
			}
		}

		Painter.fill(level, center.x - 1, center.y - 1, 3, 3, Terrain.EMBERS);
		Painter.set(level, center, Terrain.PEDESTAL);
		Blob.seed(level.pointToCell(center), 5 + Dungeon.legacyDepth() * 5, MemoryFire.class, level);
		door.set(Door.Type.HIDDEN);
	}
}
