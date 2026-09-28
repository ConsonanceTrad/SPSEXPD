package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.BlueWraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfLich;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ManySkeleton;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Zombie;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.TriforceOfCourage;
import com.watabou.utils.Random;

public class TriangleCLevel extends SpsTriangleLevel {
	{ color1 = 0x48763c; color2 = 0x59994a; viewDistance = 3; }
	@Override public String tilesTex() { return Assets.Environment.SPS_TILES_SKELETON; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_PRISON; }
	@Override protected boolean rewardCollected() { return Dungeon.triforceOfCourage; }
	@Override protected Item reward() { return new TriforceOfCourage(); }
	@Override protected int legacyTrialDepth() { return 31; }
	@Override public Mob createMob() {
		switch (Random.chances(new float[]{1f, 0.1f, 0.3f, 0.3f})) {
			case 1: return new DwarfLich();
			case 2: return new Zombie();
			case 3: return new ManySkeleton();
			default: return new BlueWraith();
		}
	}
}
