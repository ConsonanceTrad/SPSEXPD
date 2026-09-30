package pd.items.food;
import pd.sprites.ItemSpriteSheet;
public class PetFood extends Food {
	{ image = ItemSpriteSheet.PET_FOOD; energy = 10f; }
	@Override public int value() { return quantity; }
}
