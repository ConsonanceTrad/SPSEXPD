package com.shatteredpixel.shatteredpixeldungeon.items;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
public class TriforceOfCourage extends TriforcePiece {
	@Override protected void collected() { Dungeon.triforceOfCourage = true; }
}
