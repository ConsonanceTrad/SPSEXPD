/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.plants;

import pd.Assets;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.levels.Level;
import pd.messages.InlineText;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Reflection;

/**
 * SPSEXPD: 踩踏高草的收获——迷你种子。
 *
 * <p>无论种在地上还是花盆里，都只会长出一株**野生植物**（从下面这批里随机一种），
 * 不会像普通种子那样长成果丛（Ex* 形态）。
 */
public class MiniSeed extends Plant.Seed {

	/** 只会长出的野生植物（不含果丛 Ex* 形态）。 */
	private static final Class<? extends Plant>[] WILD = new Class[]{
			Firebloom.class, Icecap.class, Sorrowmoss.class, Blindweed.class, Dreamfoil.class,
			Earthroot.class, Fadeleaf.class, Sungrass.class, Stormvine.class, Swiftthistle.class
	};

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(MiniSeed.class)
			.t("name", "迷你种子")
			.t("desc", "一小把分不出品种的草籽。种下去只会长出一株野生植物，不会结成能反复收获的果丛。");
	}

	{
		image = SpecificPlaceHolderDict.SEED_HOLDER_0;
		//兜底物种：未覆写的种植路径也不会拿到 null
		plantClass = Firebloom.class;
	}

	@Override
	public Plant couch( int pos, Level level ) {
		return spawn( Random.element( WILD ), pos, level );
	}

	/** SPSEXPD: 种进花盆时同样只长野生植物（不走 explantClass 果丛）。 */
	@Override
	public Plant excouch( int pos, Level level ) {
		return couch( pos, level );
	}

	private Plant spawn( Class<? extends Plant> type, int pos, Level level ) {
		if (level != null && level.heroFOV != null && level.heroFOV[pos]) {
			Sample.INSTANCE.play( Assets.Sounds.PLANT );
		}
		Plant plant = Reflection.newInstance( type );
		plant.pos = pos;
		//SPSEXPD: 记录植物来自哪种种子，供花盆种植等场景校验/回推
		plant.seedClass = getClass();
		return plant;
	}
}
