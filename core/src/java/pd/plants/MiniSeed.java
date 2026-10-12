/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.plants;

import pd.Assets;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.levels.Level;
import pd.messages.InlineText;
import pd.messages.Messages;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Reflection;

/**
 * SPSEXPD: 迷你种子——营养不良的劣质草籽。
 *
 * <p>无论种在地上还是花盆里，都只会长出一株**野生植物**（不会像普通种子那样长成果丛、
 * 也没有精心种植的额外产出）。品种化子类通过 {@code plantClass} 指定自己长出的野生植物，
 * 见 {@link MiniSeeds}。
 *
 * <p>本类自身仍可实例化：旧存档里可能存有高草产出的「分不出品种」的迷你种子实例，
 * 那种情况按老规矩随机长出一株野生植物。
 */
public class MiniSeed extends Plant.Seed {

	/** 旧档的通用迷你种子：随机长出的野生植物（不含果丛 Ex* 形态）。 */
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
	}

	/** SPSEXPD: 迷你种子判定——劣质草籽，各玩法用它把自己排除在外。 */
	public static boolean isMini( Item item ) {
		return item instanceof MiniSeed;
	}

	@Override
	public Plant couch( int pos, Level level ) {
		Class<? extends Plant> type = plantClass != null ? plantClass : Random.element( WILD );
		return spawn( type, pos, level );
	}

	/** SPSEXPD: 种进花盆时同样只长野生植物（不走 explantClass 果丛）。 */
	@Override
	public Plant excouch( int pos, Level level ) {
		return couch( pos, level );
	}

	/** SPSEXPD: 迷你种子用自己的文案（父类取的是 plantClass 那株植物的描述）。 */
	@Override
	public String desc() {
		return Messages.get( getClass(), "desc" );
	}

	@Override
	public int value() {
		return 5 * quantity;
	}

	@Override
	public int energyVal() {
		return 1 * quantity;
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
