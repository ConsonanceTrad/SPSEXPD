package com.shatteredpixel.shatteredpixeldungeon.items;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
public class TriforceOfPower extends TriforcePiece {
	@Override protected void collected() { Dungeon.triforceOfPower = true; }
}
