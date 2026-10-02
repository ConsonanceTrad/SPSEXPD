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

public class JackOLantern extends Pill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(JackOLantern.class)
			.t("name", "灯笼球")
			.t("desc", "这种菌类含有大量的磷。当它受到外界挤压时它会将会把体内的磷成分以白磷的形式释放，从而引起大火甚至使其变成炼狱。\n使用_1份水，1份蔬菜，1份火焰花种子_炼金");
	}

	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	public JackOLantern() { this(1); }
	public JackOLantern(int value) { quantity = value; }
	@Override protected void onUse(Hero hero) {
		for (Mob mob : mobs()) {
			GameScene.add(Blob.seed(mob.pos, 3, Fire.class));
			Buff.affect(mob, DBurning.class).set(8f);
		}
	}
}
