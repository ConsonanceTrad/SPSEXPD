/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.effects;

import pd.Dungeon;
import pd.items.unique.items.Metronome;
import pd.tiles.DungeonTilemap;
import render.noosa.ColorBlock;
import render.noosa.ui.Component;

/**
 * SPSEXPD: 节拍器标出的「下一拍」格子高亮。
 *
 * <p>用四个描边条画一个方框（不透明的橙金色）。四条边的尺寸**直接取常量**
 * {@link DungeonTilemap#SIZE}，不依赖 {@code width()/height()}——那两个在第一帧
 * 还是 0（相机尚未挂上、setSize 未跑），会导致描边被缩成 1×1 而看不见。</p>
 */
public class MetronomeMark extends Component {

	//SPSEXPD: 必须是 8 位 ARGB（aa/rr/gg/bb）——TextureCache.createSolid 按 ARGB 解析，
	//漏掉 alpha 字节（如 0xFF8A00）会被当成 alpha=0x00 的全透明色，表现为「描边看不见」。
	//alpha 取 0x66（≈40% 不透明度），让高亮格更好地融入地面背景。
	private static final int COLOR = 0x66FF8A00;   // 橙金色，40% 不透明
	private static final float EDGE = 2f;

	private final ColorBlock top = new ColorBlock( DungeonTilemap.SIZE, EDGE, COLOR );
	private final ColorBlock bottom = new ColorBlock( DungeonTilemap.SIZE, EDGE, COLOR );
	private final ColorBlock left = new ColorBlock( EDGE, DungeonTilemap.SIZE, COLOR );
	private final ColorBlock right = new ColorBlock( EDGE, DungeonTilemap.SIZE, COLOR );

	public MetronomeMark() {
		super();
		add( top );
		add( bottom );
		add( left );
		add( right );
	}

	/** 子条位置只跟 x/y 有关，尺寸用常量，所以每次 setPos 重摆都安全。 */
	private void placeEdges() {
		float s = DungeonTilemap.SIZE;

		top.x = x;
		top.y = y;

		bottom.x = x;
		bottom.y = y + s - EDGE;

		left.x = x;
		left.y = y;

		right.x = x + s - EDGE;
		right.y = y;
	}

	@Override
	public void update() {
		super.update();

		int cell = Metronome.currentBeatCell();
		if (cell < 0 || Dungeon.level == null || Dungeon.hero == null
				|| !Metronome.equipped( Dungeon.hero ) ) {
			visible = false;
			return;
		}
		visible = true;

		render.utils.geom.PointF p = DungeonTilemap.tileToWorld( cell );
		x = p.x;
		y = p.y;
		placeEdges();
	}
}
