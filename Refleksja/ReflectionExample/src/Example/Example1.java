package Example;

import Callable.Description;
import Callable.ICallable;

@Description(description = "Klasa implementuje metod� zlicz�j�c� d�ugo�� dw�ch string�w.")
public class Example1 implements ICallable {

	@Override
	public String Call(String arg0, String arg1) {
		int sum = arg0.length() + arg1.length();
		String result = "Liczba znak�w wynosi: " + sum;
		return result;
	}

}
