package trufflesom.primitives.basics;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.profiles.ValueProfile;

import bd.primitives.Primitive;
import trufflesom.interpreter.nodes.nary.UnaryExpressionNode;
import trufflesom.vmobjects.SArray;
import trufflesom.vmobjects.SArray.ArrayType;
import trufflesom.vmobjects.SSymbol;


@GenerateNodeFactory
@ImportStatic(ArrayType.class)
@Primitive(className = "Array", primitive = "length")
@Primitive(className = "String", primitive = "length")
@Primitive(selector = "length", receiverType = String.class, inParser = false)
public abstract class LengthPrim extends UnaryExpressionNode {

  private final ValueProfile storageType = ValueProfile.createClassProfile();

  @Specialization(guards = "isEmptyType(receiver)")
  public final long doEmptySArray(final SArray receiver) {
    return receiver.getEmptyStorage(storageType);
  }

  @Specialization(guards = "isPartiallyEmptyType(receiver)")
  public final long doPartialEmptySArray(final SArray receiver) {
    return receiver.getPartiallyEmptyStorage(storageType).getLength();
  }

  @Specialization(guards = "isObjectType(receiver)")
  public final long doObjectSArray(final SArray receiver) {
    return receiver.getObjectStorage(storageType).length;
  }

  @Specialization(guards = "isLongType(receiver)")
  public final long doLongSArray(final SArray receiver) {
    return receiver.getLongStorage(storageType).length;
  }

  @Specialization(guards = "isDoubleType(receiver)")
  public final long doDoubleSArray(final SArray receiver) {
    return receiver.getDoubleStorage(storageType).length;
  }

  @Specialization(guards = "isBooleanType(receiver)")
  public final long doBooleanSArray(final SArray receiver) {
    return receiver.getBooleanStorage(storageType).length;
  }

  public abstract long executeEvaluated(SArray receiver);

  @Specialization
  public final long doString(final String receiver) {
    return receiver.length();
  }

  @Specialization
  public final long doSSymbol(final SSymbol receiver) {
    return receiver.getString().length();
  }
}
