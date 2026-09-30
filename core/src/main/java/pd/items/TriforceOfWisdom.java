package pd.items;
import pd.Dungeon;
public class TriforceOfWisdom extends TriforcePiece {
	@Override protected void collected() { Dungeon.triforceOfWisdom = true; }
}
