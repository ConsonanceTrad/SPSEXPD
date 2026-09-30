package pd.atlas;

/**
 * 图集条目：某张图集里一个图标的全部帧。
 *
 * 字段说明：
 * <ul>
 *   <li>{@code atlas} —— 图集在 assets 下的相对路径（与 {@code pd.Assets} 中的常量同值）</li>
 *   <li>{@code rects} —— 帧矩形，扁平的 int 数组，每 4 个元素一帧：x, y, w, h（像素坐标）</li>
 * </ul>
 *
 * 单帧图标只有 4 个元素；变体族 / 动画帧按顺序排列，索引即帧序号。
 * 本类只描述"图集里的位置"，不负责渲染，也不含任何图标语义。
 */
public final class IconEntry {

	public final String atlas;
	private final int[] rects;

	public IconEntry(String atlas, int[] rects) {
		this.atlas = atlas;
		this.rects = rects;
	}

	/** 帧数（至少 1） */
	public int frames() {
		return rects.length / 4;
	}

	public int x(int frame) {
		return rects[frame * 4];
	}

	public int y(int frame) {
		return rects[frame * 4 + 1];
	}

	public int w(int frame) {
		return rects[frame * 4 + 2];
	}

	public int h(int frame) {
		return rects[frame * 4 + 3];
	}
}
