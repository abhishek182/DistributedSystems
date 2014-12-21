import java.util.Random;

public class RandomRange {

	public static int getRandomInteger(int aStart, int aEnd) {
		Random aRandom = new Random();
		long range = (long) aEnd - (long) aStart + 1;
		long fraction = (long) (range * aRandom.nextDouble());
		int randomNumber = (int) (fraction + aStart);
		return randomNumber;
	}

}