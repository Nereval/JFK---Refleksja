package Example;

import Callable.Description;
import Callable.ICallable;

@Description(description = "Klasa implementuje metod� ��cz�c� dwa stringi w jeden.")
public class Example implements ICallable {

	@Override
	public String Call(String arg0, String arg1) {
		String result = "Wynik ��czenia: " + arg0 + arg1;
		return result;
	}


}
