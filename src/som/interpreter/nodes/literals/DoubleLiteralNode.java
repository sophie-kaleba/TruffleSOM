package som.interpreter.nodes.literals;

import com.oracle.truffle.api.frame.VirtualFrame;


public final class DoubleLiteralNode extends LiteralNode {
  private final double value;

  public DoubleLiteralNode(final double value) {
    this.value = value;
  }

  @Override
  public double executeDouble(final VirtualFrame frame) {
    return value;
  }

  @Override
  public Object executeGeneric(final VirtualFrame frame) {
    return value;
  }
}
