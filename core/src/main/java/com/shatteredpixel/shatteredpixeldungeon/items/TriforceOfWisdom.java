package com.shatteredpixel.shatteredpixeldungeon.items;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
public class TriforceOfWisdom extends TriforcePiece {
	@Override protected void collected() { Dungeon.triforceOfWisdom = true; }
}
