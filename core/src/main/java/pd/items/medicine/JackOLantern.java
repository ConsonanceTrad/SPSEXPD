package pd.items.medicine;

import pd.actors.blobs.Blob;
import pd.actors.blobs.effectblobs.Fire;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DBurning;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.sprites.ItemSpriteSheet;
import pd.scenes.GameScene;

public class JackOLantern extends Pill {
	{ image = ItemSpriteSheet.MUSHROOM_LANTERN; }
	public JackOLantern() { this(1); }
	public JackOLantern(int value) { quantity = value; }
	@Override protected void onUse(Hero hero) {
		for (Mob mob : mobs()) {
			GameScene.add(Blob.seed(mob.pos, 3, Fire.class));
			Buff.affect(mob, DBurning.class).set(8f);
		}
	}
}
