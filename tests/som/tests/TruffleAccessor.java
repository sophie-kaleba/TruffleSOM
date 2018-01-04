package som.tests;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.vm.PolyglotEngine.Language;
import com.oracle.truffle.api.vm.PolyglotEngine.Value;

import som.vm.Universe;
import som.vmobjects.SClass;
import som.vmobjects.SObject;


public class TruffleAccessor {

  public static boolean isDynamicObject(final Value actualResult)
      throws ReflectiveOperationException {
    Method m = actualResult.getClass().getDeclaredMethod("value");
    m.setAccessible(true);
    Object o = m.invoke(actualResult);
    return o instanceof DynamicObject;
  }

  public static String getSomClassName(final Value actualResult) throws NoSuchMethodException,
      IllegalAccessException, InvocationTargetException, NoSuchFieldException {
    DynamicObject o = toObject(actualResult);

    Universe universe = getContext(actualResult);

    String actual = SClass.getName(o, universe).getString();
    return actual;
  }

  public static String getSomObjectClassName(final Value obj)
      throws ReflectiveOperationException {
    DynamicObject o = toObject(obj);
    Universe universe = getContext(obj);
    String actual = SClass.getName(SObject.getSOMClass(o), universe).getString();
    return actual;
  }

  private static DynamicObject toObject(final Value actualResult)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    Method m = actualResult.getClass().getDeclaredMethod("value");
    m.setAccessible(true);
    DynamicObject o = (DynamicObject) m.invoke(actualResult);
    return o;
  }

  private static Universe getContext(final Value actualResult)
      throws NoSuchFieldException, IllegalAccessException {
    Field f = actualResult.getClass().getSuperclass().getDeclaredField("language");
    f.setAccessible(true);
    Language l = (Language) f.get(actualResult);
    Field ctxF = l.getClass().getDeclaredField("context");
    ctxF.setAccessible(true);
    Universe universe = (Universe) ctxF.get(l);
    return universe;
  }
}
