package som.primitives.arrays;

import som.interpreter.Invokable;
import som.interpreter.nodes.dispatch.AbstractDispatchNode;
import som.interpreter.nodes.dispatch.UninitializedValuePrimDispatchNode;
import som.interpreter.nodes.nary.BinaryExpressionNode;
import som.primitives.BlockPrims.ValuePrimitiveNode;
import som.primitives.LengthPrim;
import som.vm.constants.Nil;
import som.vmobjects.SArray;
import som.vmobjects.SArray.ArrayType;
import som.vmobjects.SBlock;
import som.vmobjects.SObject;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;

@NodeChild(value = "length", type = LengthPrim.class, executeWith = "receiver")
public abstract class PutAllNode extends BinaryExpressionNode
  implements ValuePrimitiveNode  {
  @Child private AbstractDispatchNode block;

  public PutAllNode() {
    super(null);
    block = new UninitializedValuePrimDispatchNode();
  }

  @Override
  public void adoptNewDispatchListHead(final AbstractDispatchNode node) {
    block = insert(node);
  }

  protected final boolean notABlock(final SArray rcvr, final Object value) {
    return !(value instanceof SBlock);
  }

  protected final boolean valueIsNil(final SArray rcvr, final SObject value) {
    return value == Nil.nilObject;
  }

  public final boolean isEmptyType(final SArray receiver) {
    return receiver.getType() == ArrayType.EMPTY;
  }

  public final boolean isPartiallyEmptyType(final SArray receiver) {
    return receiver.getType() == ArrayType.PARTIAL_EMPTY;
  }

  public final boolean isObjectType(final SArray receiver) {
    return receiver.getType() == ArrayType.OBJECT;
  }

  @Specialization(guards = {"isEmptyType", "valueIsNil"})
  public SArray doPutNilInEmptyArray(final SArray rcvr, final SObject nil,
      final long length) {
    // NO OP
    return rcvr;
  }

  @Specialization(guards = {"valueIsNil"}, contains = {"doPutNilInEmptyArray"})
  public SArray doPutNilInOtherArray(final SArray rcvr, final SObject nil,
      final long length) {
    rcvr.transitionToEmpty(length);
    return rcvr;
  }

  private void evalBlockForRemaining(final VirtualFrame frame,
      final SBlock block, final long length, final Object[] storage) {
    for (int i = SArray.FIRST_IDX + 1; i < length; i++) {
      storage[i] = this.block.executeDispatch(frame, new Object[] {block});
    }
  }
  @Specialization
  public SArray doPutEvalBlock(final VirtualFrame frame, final SArray rcvr,
      final SBlock block, final long length) {
    if (length <= 0) {
      return rcvr;
    }

    try {
      Object result = this.block.executeDispatch(frame, new Object[] {block});
      Object[] newStorage = new Object[(int) length];
      newStorage[0] = result;
      evalBlockForRemaining(frame, block, length, newStorage);
      rcvr.transitionToObject(newStorage);
    } finally {
      if (CompilerDirectives.inInterpreter()) {
        reportLoopCount(length);
      }
    }
    return rcvr;
  }

  protected final void reportLoopCount(final long count) {
    if (count == 0) {
      return;
    }

    CompilerAsserts.neverPartOfCompilation("reportLoopCount");
    Node current = getParent();
    while (current != null && !(current instanceof RootNode)) {
      current = current.getParent();
    }
    if (current != null) {
      ((Invokable) current).propagateLoopCountThroughoutLexicalScope(count);
    }
  }

  @Specialization(guards = "notABlock")
  public SArray doPutObject(final SArray rcvr, final Object value,
      final long length) {
    assert !(value instanceof SBlock);
    rcvr.transitionToObjectWithAll(length, value);
    return rcvr;
  }
}
