package som.primitives.reflection;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;

import bd.primitives.Primitive;
import som.interpreter.nodes.nary.UnaryExpressionNode;
import som.vmobjects.SAbstractObject;
import som.vmobjects.SArray;
import som.vmobjects.SClass;


public class ClassPrims {

  @GenerateNodeFactory
  @ImportStatic(SClass.class)
  @Primitive(className = "Class", primitive = "name")
  public abstract static class NamePrim extends UnaryExpressionNode {
    @Specialization(guards = "isSClass(receiver)")
    public final SAbstractObject doSClass(final DynamicObject receiver) {
      CompilerAsserts.neverPartOfCompilation();
      return SClass.getName(receiver);
    }
  }

  @GenerateNodeFactory
  @ImportStatic(SClass.class)
  @Primitive(className = "Class", primitive = "superclass")
  public abstract static class SuperClassPrim extends UnaryExpressionNode {
    @Specialization(guards = "isSClass(receiver)")
    public final Object doSClass(final DynamicObject receiver) {
      CompilerAsserts.neverPartOfCompilation();
      return SClass.getSuperClass(receiver);
    }
  }

  @GenerateNodeFactory
  @ImportStatic(SClass.class)
  @Primitive(className = "Class", primitive = "methods")
  public abstract static class InstanceInvokablesPrim extends UnaryExpressionNode {
    @Specialization(guards = "isSClass(receiver)")
    public final SArray doSClass(final DynamicObject receiver) {
      CompilerAsserts.neverPartOfCompilation();
      return SClass.getInstanceInvokables(receiver);
    }
  }

  @GenerateNodeFactory
  @ImportStatic(SClass.class)
  @Primitive(className = "Class", primitive = "fields")
  public abstract static class InstanceFieldsPrim extends UnaryExpressionNode {
    @Specialization(guards = "isSClass(receiver)")
    public final SArray doSClass(final DynamicObject receiver) {
      CompilerAsserts.neverPartOfCompilation();
      return SClass.getInstanceFields(receiver);
    }
  }
}
