package Example;

import Callable.Description;
import Callable.ICallable;

@Description(description = "Klasa implementuje metodê zlicz¹j¹c¹ d³ugoœæ dwóch stringów.")
public class Example1 implements ICallable {

	@Override
	public String Call(String arg0, String arg1) {
		int sum = arg0.length() + arg1.length();
		String result = "Liczba znaków wynosi: " + sum;
		return result;
	}

}
