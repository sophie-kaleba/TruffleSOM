package trufflesom.interpreter.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

import bd.inlining.ScopeAdaptationVisitor;
import bd.tools.nodes.Invocation;
import trufflesom.compiler.Variable.AccessNodeState;
import trufflesom.compiler.Variable.Argument;
import trufflesom.interpreter.SArguments;
import trufflesom.vmobjects.SSymbol;


public abstract class ArgumentReadNode {

  public static class LocalArgumentReadNode extends ExpressionNode
      implements Invocation<SSymbol> {
    protected final int      argumentIndex;
    protected final Argument arg;

    public LocalArgumentReadNode(final Argument arg) {
      assert arg.index >= 0;
      this.arg = arg;
      this.argumentIndex = arg.index;
    }

    /** Only to be used in primitives. */
    public LocalArgumentReadNode(final boolean useInPrim, final int idx) {
      assert idx >= 0;
      this.arg = null;
      this.argumentIndex = idx;
      assert useInPrim;
    }

    @Override
    public final Object executeGeneric(final VirtualFrame frame) {
      return SArguments.arg(frame, argumentIndex);
    }

    @Override
    public void replaceAfterScopeChange(final ScopeAdaptationVisitor inliner) {
      inliner.updateRead(arg, this, 0);
    }

    @Override
    public SSymbol getInvocationIdentifier() {
      return arg.name;
    }
  }

  public static class LocalArgumentWriteNode extends ExpressionNode {
    protected final int      argumentIndex;
    protected final Argument arg;

    @Child protected ExpressionNode valueNode;

    public LocalArgumentWriteNode(final Argument arg, final ExpressionNode valueNode) {
      assert arg.index >= 0;
      this.arg = arg;
      this.argumentIndex = arg.index;
      this.valueNode = valueNode;
    }

    /** Only to be used in primitives. */
    public LocalArgumentWriteNode(final boolean useInPrim, final int idx,
        final ExpressionNode valueNode) {
      assert idx >= 0;
      this.arg = null;
      this.argumentIndex = idx;
      assert useInPrim;
      this.valueNode = valueNode;
    }

    @Override
    public final Object executeGeneric(final VirtualFrame frame) {
      Object value = valueNode.executeGeneric(frame);
      SArguments.setArg(frame, argumentIndex, value);
      return value;
    }

    @Override
    public void replaceAfterScopeChange(final ScopeAdaptationVisitor inliner) {
      inliner.updateWrite(arg, this, valueNode, 0);
    }
  }

  public static class NonLocalArgumentReadNode extends ContextualNode
      implements Invocation<SSymbol> {
    protected final int      argumentIndex;
    protected final Argument arg;

    public NonLocalArgumentReadNode(final Argument arg, final int contextLevel) {
      super(contextLevel);
      assert contextLevel > 0;
      this.arg = arg;
      this.argumentIndex = arg.index;
    }

    @Override
    public final Object executeGeneric(final VirtualFrame frame) {
      return SArguments.arg(determineContext(frame), argumentIndex);
    }

    @Override
    public void replaceAfterScopeChange(final ScopeAdaptationVisitor inliner) {
      inliner.updateRead(arg, this, contextLevel);
    }

    @Override
    public SSymbol getInvocationIdentifier() {
      return arg.name;
    }
  }

  public static class NonLocalArgumentWriteNode extends ContextualNode {
    protected final int      argumentIndex;
    protected final Argument arg;

    @Child protected ExpressionNode valueNode;

    public NonLocalArgumentWriteNode(final Argument arg, final int contextLevel,
        final ExpressionNode valueNode) {
      super(contextLevel);
      assert contextLevel > 0;
      this.arg = arg;
      this.argumentIndex = arg.index;

      this.valueNode = valueNode;
    }

    @Override
    public final Object executeGeneric(final VirtualFrame frame) {
      Object value = valueNode.executeGeneric(frame);
      SArguments.setArg(determineContext(frame), argumentIndex, value);
      return value;
    }

    @Override
    public void replaceAfterScopeChange(final ScopeAdaptationVisitor inliner) {
      inliner.updateWrite(arg, this, valueNode, contextLevel);
    }
  }

  public static final class LocalSuperReadNode extends LocalArgumentReadNode
      implements ISuperReadNode {

    private final SSymbol holderClass;
    private final boolean classSide;

    public LocalSuperReadNode(final Argument arg, final SSymbol holderClass,
        final boolean classSide) {
      super(arg);
      this.holderClass = holderClass;
      this.classSide = classSide;
    }

    @Override
    public SSymbol getHolderClass() {
      return holderClass;
    }

    @Override
    public boolean isClassSide() {
      return classSide;
    }

    @Override
    public void replaceAfterScopeChange(final ScopeAdaptationVisitor inliner) {
      inliner.updateSuperRead(arg, this, new AccessNodeState(holderClass, classSide), 0);
    }
  }

  public static final class NonLocalSuperReadNode extends
      NonLocalArgumentReadNode implements ISuperReadNode {

    private final SSymbol holderClass;
    private final boolean classSide;

    public NonLocalSuperReadNode(final Argument arg, final int contextLevel,
        final SSymbol holderClass, final boolean classSide) {
      super(arg, contextLevel);
      this.holderClass = holderClass;
      this.classSide = classSide;
    }

    @Override
    public SSymbol getHolderClass() {
      return holderClass;
    }

    @Override
    public void replaceAfterScopeChange(final ScopeAdaptationVisitor inliner) {
      inliner.updateSuperRead(arg, this, new AccessNodeState(holderClass, classSide),
          contextLevel);
    }

    @Override
    public boolean isClassSide() {
      return classSide;
    }
  }
}
