package name.ltp.ujint.javac.test;

import name.ltp.ujint.u;

import static java.lang.System.out;

public class Test
{
	public static void main(String... args)
	{
		out.println("-----------------------------");

//		@u int k = -4;
//		out.println("" + Integer.toUnsignedLong(k / 2));

		out.println("" + Integer.toUnsignedLong(((@u int) -4) / 2));
//		out.println("" + Integer.toUnsignedLong(((@name.ltp.ujint.u int) -4) / 2));
		out.println("" + Integer.toUnsignedLong(((int) -4) / 2));
		out.println("" + Integer.toUnsignedLong(4 / 2));
	}
}
