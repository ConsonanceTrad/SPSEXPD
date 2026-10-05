package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.blobs.Blob;
import pd.actors.blobs.effectblobs.Fire;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DBurning;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.scenes.GameScene;
import pd.messages.InlineText;
import pd.atlas.items.ConsumPotionSeedBasicPotionDict;

public class JackOLantern extends Pill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(JackOLantern.class)
			.t("name", "火焰磷菌瓶")
			.t("desc", "存储着大量富磷的菌类，向外界泼洒时会引起难以扑灭的大火甚至使其变成炼狱。");
	}



	{ image = ConsumPotionSeedBasicPotionDict.FIRE_PHOSPHORUS_FRUIT; }
	public JackOLantern() { this(1); }
	public JackOLantern(int value) { quantity = value; }
	@Override protected void onUse(Hero hero) {
		for (Mob mob : mobs()) {
			GameScene.add(Blob.seed(mob.pos, 3, Fire.class));
			Buff.affect(mob, DBurning.class).set(8f);
		}
	}
}
