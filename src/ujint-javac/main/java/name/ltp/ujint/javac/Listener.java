package name.ltp.ujint.javac;

import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;

class Listener implements TaskListener
{
	final Visitor visitor;

	Listener(JavacTask task)
	{
		visitor = new Visitor(task);
	}

	@Override
	public void finished(TaskEvent taskEvent)
	{
		;
	}

	@Override
	public void started(TaskEvent taskEvent)
	{
		if(taskEvent.getKind() == TaskEvent.Kind.ENTER)
			visitor.scan(taskEvent.getCompilationUnit(), null);
	}
}
