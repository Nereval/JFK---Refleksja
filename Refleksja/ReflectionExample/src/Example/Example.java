package Example;

import Callable.Description;
import Callable.ICallable;

@Description(description = "Klasa implementuje metodê ³¹cz¹c¹ dwa stringi w jeden.")
public class Example implements ICallable {

	@Override
	public String Call(String arg0, String arg1) {
		String result = "Wynik ³¹czenia: " + arg0 + arg1;
		return result;
	}


}
