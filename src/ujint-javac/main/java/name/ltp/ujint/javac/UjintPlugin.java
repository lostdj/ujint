package name.ltp.ujint.javac;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;

/*
javac -processorpath out/iji/artifacts/ujint.jar -Xplugin:name.ltp.ujint.javac.UjintPlugin \
-d out/iji/test/ujint-javac/ -cp out/iji/artifacts/ujint.jar \
src/ujint-javac/test/java/name/ltp/ujint/javac/test/Test.java \
&& java -cp out/iji/test/ujint-javac/ name.ltp.ujint.javac.test.Test
*/

public class UjintPlugin implements Plugin
{
	@Override
	public String getName()
	{
		return UjintPlugin.class.getName();
	}

	@Override
	public void init(JavacTask task, String[] args)
	{
		task.addTaskListener(new Listener(task));
	}
}
