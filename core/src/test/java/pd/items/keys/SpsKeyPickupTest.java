package pd.items.keys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.actors.buffs.Buff;
import pd.items.artifacts.SkeletonKey;
import pd.journal.Notes;
import render.noosa.Game;

/** 回归：学者任务钥匙（SpsSkeletonKey）拾取不得崩溃（曾因 HUD 钥匙显示未登记槽位而 NPE 闪退）。 */
public final class SpsKeyPickupTest {

	public static void main(String[] args) {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new com.badlogic.gdx.ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		try {
			Dungeon.depth = 1;
			Dungeon.branch = 0;
			Notes.reset();   //模拟 Dungeon.init 的日志初始化
			Hero hero = new Hero();
			Dungeon.hero = hero;
			hero.pos = 0;

			check(new SpsSkeletonKey(1).doPickUp(hero, 0), "SpsSkeletonKey 应可拾取");
			check(Notes.keyCount(new SpsSkeletonKey(1)) >= 0, "SpsSkeletonKey 日志登记应可用");

			check(new GoldenSkeletonKey(1).doPickUp(hero, 0), "GoldenSkeletonKey 应可拾取");

			Buff.affect(hero, SkeletonKey.KeyReplacementTracker.class);
			check(new SpsSkeletonKey(1).doPickUp(hero, 0), "带钥匙追踪 buff 时应可拾取");
			hero.buff(SkeletonKey.KeyReplacementTracker.class).processExcessKeys();

			System.out.println("SPS钥匙拾取测试通过：任务钥匙拾取、日志登记与钥匙追踪均无崩溃。");
		} finally {
			Actor.clear();
			Dungeon.level = null;
			Dungeon.hero = null;
			app.exit();
		}
	}

	private static void check(boolean cond, String msg) {
		if (!cond) throw new AssertionError(msg);
	}

	private SpsKeyPickupTest() { }
}
