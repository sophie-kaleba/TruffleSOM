package trufflesom.primitives.reflection;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.Specialization;

import bd.primitives.Primitive;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.nodes.RootNode;
import trufflesom.interpreter.nodes.dispatch.CachedDispatchNode;
import trufflesom.interpreter.nodes.nary.BinaryExpressionNode;
import trufflesom.interpreter.nodes.nary.UnaryExpressionNode;
import trufflesom.vmobjects.SAbstractObject;
import trufflesom.vmobjects.SArray;
import trufflesom.vmobjects.SClass;

import java.util.List;


public class ClassPrims {

  @GenerateNodeFactory
  @Primitive(className = "Class", primitive = "name")
  public abstract static class NamePrim extends UnaryExpressionNode {
    @Specialization
    public final SAbstractObject doSClass(final SClass receiver) {
      return receiver.getName();
    }
  }

  @GenerateNodeFactory
  @Primitive(className = "Class", primitive = "superclass")
  public abstract static class SuperClassPrim extends UnaryExpressionNode {
    @Specialization
    public final SAbstractObject doSClass(final SClass receiver) {
      return receiver.getSuperClass();
    }
  }

  @GenerateNodeFactory
  @Primitive(className = "Class", primitive = "methods")
  public abstract static class InstanceInvokablesPrim extends UnaryExpressionNode {
    @Specialization
    public final SArray doSClass(final SClass receiver) {
      return receiver.getInstanceInvokables();
    }
  }

  @GenerateNodeFactory
  @Primitive(className = "Class", primitive = "fields")
  public abstract static class InstanceFieldsPrim extends UnaryExpressionNode {
    @Specialization
    public final SArray doSClass(final SClass receiver) {
      return receiver.getInstanceFields();
    }
  }

  @GenerateNodeFactory
  @Primitive(className = "Class", primitive = "cleanDispatchChain:")
  public abstract static class CleanDispatchChainPrim extends BinaryExpressionNode {
    @Specialization
    public final SAbstractObject doSClass(final SClass receiver, final boolean enabled) {

      if (enabled) {
        RootNode r = receiver.getInstanceInvokable(1).getCallTarget().getRootNode();
        List<CachedDispatchNode> allCached = NodeUtil.findAllNodeInstances(r, CachedDispatchNode.class);

        for (int i = 0; i < allCached.size(); i++) {
          CachedDispatchNode n = allCached.get(i);
          if (n.getParent().toString().equals("GMsgSend(x)")) {
            n.replace(allCached.get(i + 3));
            return receiver;
          }
        }
      }
      return receiver;
    }

  }
}
