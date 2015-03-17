package name.ltp.ujint.javac;

import com.sun.source.tree.*;
import com.sun.source.util.*;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

class Visitor extends TreePathScanner<Void, Void>
{
	static final String u = name.ltp.ujint.u.class.getCanonicalName();

	final JavacTask task;

	final Types types;
	final Trees trees;
	final SourcePositions sourcePositions;

	final TypeMirror utype;

	CompilationUnitTree currCompUnit;

	Visitor(JavacTask task)
	{
		this.task = task;

		types = task.getTypes();
		trees = Trees.instance(task);
		sourcePositions = trees.getSourcePositions();

		Elements elements = task.getElements();
		utype = elements.getTypeElement(u).asType();
	}

	@Override
	public Void visitCompilationUnit(CompilationUnitTree tree, Void p)
	{
		currCompUnit = tree;

		return super.visitCompilationUnit(tree, p);
	}

	@Override
	public Void visitExpressionStatement(ExpressionStatementTree node, Void p)
	{
		return super.visitExpressionStatement(node, p);
	}

	static class UJLiteral extends JCTree.JCLiteral
	{
		public UJLiteral(TypeTag typeTag, Object o)
		{
			super(typeTag, o);
		}
	}

	@Override
	public Void visitBinary(BinaryTree node, Void p)
	{
		ExpressionTree left = node.getLeftOperand();
		ExpressionTree right = node.getRightOperand();
		Tree.Kind kind = node.getKind();

		brk:
		if(kind == Tree.Kind.DIVIDE
				&& left.getKind() == Tree.Kind.PARENTHESIZED
				&& right.getKind() == Tree.Kind.INT_LITERAL)
		{
			TreePath tp = new TreePath(getCurrentPath(), left);
			if(tp.getLeaf().getKind() != Tree.Kind.PARENTHESIZED)
				break brk;

			JCTree.JCParens parens = (JCTree.JCParens)tp.getLeaf();
			if(parens.getExpression().getKind() != Tree.Kind.TYPE_CAST)
				break brk;

			JCTree.JCTypeCast cast = (JCTree.JCTypeCast)parens.getExpression();
			if(cast.getKind() != Tree.Kind.TYPE_CAST
					|| cast.getType().getKind() != Tree.Kind.ANNOTATED_TYPE)
				break brk;

			JCTree.JCAnnotatedType type = (JCTree.JCAnnotatedType)cast.getType();
			if(type.getKind() != Tree.Kind.ANNOTATED_TYPE
					|| type.getUnderlyingType().getKind() != Tree.Kind.PRIMITIVE_TYPE
					|| ((JCTree.JCPrimitiveTypeTree)type.getUnderlyingType()).getPrimitiveTypeKind() != TypeKind.INT
					// TODO: Nullptr? The type is null and the name is simply "u".
					/*|| !types.isSameType(utype, trees.getTypeMirror(new TreePath(tp, type.getAnnotations().get(0))))*/
					|| !utype.toString().equals(type.getAnnotations().get(0).getAnnotationType().toString()))
				break brk;

			int fold = Integer.divideUnsigned(
				(Integer)((JCTree.JCLiteral)cast.getExpression()).getValue()
				, (Integer)((JCTree.JCLiteral)right).getValue());

			UJLiteral l = new UJLiteral(((JCTree.JCLiteral)right).typetag, fold);
			((JCTree.JCMethodInvocation)getCurrentPath().getParentPath().getLeaf())
				.args = List.from(new JCTree.JCExpression[]{l});
		}

		return super.visitBinary(node, p);
	}
}
