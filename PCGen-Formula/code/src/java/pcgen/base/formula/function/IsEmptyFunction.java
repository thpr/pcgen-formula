/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.formula.function;

import java.util.Arrays;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.util.FormatManager;

/**
 * IsEmptyFunction calculates the a Boolean based on whether a given array is empty.
 */
public class IsEmptyFunction implements FormulaFunction
{

	@Override
	public String getFunctionName()
	{
		return "ISEMPTY";
	}

	@Override
	public final FormatManager<?> allowArgs(SemanticsVisitor visitor, Node[] args,
		FormulaSemantics semantics)
	{
		int argCount = args.length;
		if (argCount != 1)
		{
			semantics.setInvalid("Function " + getFunctionName()
				+ " received incorrect # of arguments, expected: 1 got " + argCount + " "
				+ Arrays.asList(args));
			return null;
		}
		Node node = args[0];
		@SuppressWarnings("PMD.PrematureDeclaration")
		FormatManager<?> format = (FormatManager<?>) node.jjtAccept(visitor,
			semantics.getWith(FormulaSemantics.ASSERTED, null));
		if (!semantics.isValid())
		{
			return null;
		}
		if (!format.getManagedClass().isArray())
		{
			semantics.setInvalid("Parse Error: Invalid Value Format: "
				+ format.getIdentifierType() + " found in " + node.getClass().getName()
				+ " found in location requiring an "
				+ "ARRAY (class cannot be evaluated)");
			return null;
		}
		return FormatUtilities.BOOLEAN_MANAGER;
	}

	@Override
	public final Boolean evaluate(EvaluateVisitor visitor, Node[] args,
		EvaluationManager manager)
	{
		Object[] solution = (Object[]) args[0].jjtAccept(visitor,
			manager.getWith(EvaluationManager.ASSERTED, null));
		return (solution.length == 0);
	}

	@Override
	public Boolean isStatic(StaticVisitor visitor, Node[] args)
	{
		return (Boolean) args[0].jjtAccept(visitor, null);
	}

	@Override
	public FormatManager<?> getDependencies(DependencyVisitor visitor,
		DependencyManager manager, Node[] args)
	{
		args[0].jjtAccept(visitor, manager.getWith(DependencyManager.ASSERTED, null));
		return FormatUtilities.BOOLEAN_MANAGER;
	}
}