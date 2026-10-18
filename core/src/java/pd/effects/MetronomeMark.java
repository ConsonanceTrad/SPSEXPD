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

	private static final int COLOR = 0xFF8A00;   // 橙金色，不透明
	private static final float EDGE = 2f;

	private final ColorBlock top = new ColorBlock( DungeonTilemap.SIZE, EDGE, COLOR );
	private final ColorBlock bottom = new ColorBlock( DungeonTilemap.SIZE, EDGE, COLOR );
	private final ColorBlock left = new ColorBlock( EDGE, DungeonTilemap.SIZE, COLOR );
	private final ColorBlock right = new ColorBlock( EDGE, DungeonTilemap.SIZE, COLOR );

	private int lastCell = Integer.MIN_VALUE;

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

	private boolean drawLogged = false;

	/**
	 * SPSEXPD: 诊断——确认这个 Gizmo 真的走到了 draw()，以及四条描边各自的
	 * 位置/尺寸/可见性。只有 draw() 被调用过，才能排除"没渲染"。
	 */
	@Override
	public void draw() {
		if (!drawLogged && visible) {
			drawLogged = true;
			System.err.println("[SPSEXPD] MetronomeMark.draw: self x=" + (int)x + " y=" + (int)y
					+ " visible=" + visible
					+ " | top(" + (int)top.x + "," + (int)top.y + " " + (int)top.width() + "x" + (int)top.height()
					+ " vis=" + top.visible + ")"
					+ " | bottom(" + (int)bottom.x + "," + (int)bottom.y + " " + (int)bottom.width() + "x" + (int)bottom.height() + ")"
					+ " | left(" + (int)left.x + "," + (int)left.y + " " + (int)left.width() + "x" + (int)left.height() + ")"
					+ " | right(" + (int)right.x + "," + (int)right.y + " " + (int)right.width() + "x" + (int)right.height() + ")"
					+ " | camera=" + (camera == null ? "null" : "ok"));
		}
		super.draw();
	}

	@Override
	public void update() {
		super.update();

		int cell = Metronome.currentBeatCell();
		if (cell != lastCell) {
			System.err.println( "[SPSEXPD] MetronomeMark: cell=" + cell
					+ " x=" + (int)x + " y=" + (int)y
					+ " camera=" + (camera == null ? "null" : "ok") );
			lastCell = cell;
		}

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
