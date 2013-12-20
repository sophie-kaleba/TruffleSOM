/**
 * Copyright (c) 2013 Stefan Marr, stefan.marr@vub.ac.be
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package som.interpreter;

import som.vmobjects.SObject;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.Frame;

public final class Arguments extends com.oracle.truffle.api.Arguments {

  private final Object self;
  private final FrameOnStackMarker onStackMarker;
  private final Object[] upvalues;

  @CompilationFinal private final Object[] arguments;


  public Arguments(final Object self, final Object[] arguments, final int numUpvalues, final SObject nilObject) {
    this.self = self;
    this.onStackMarker = new FrameOnStackMarker();
    this.arguments = arguments;

    if (numUpvalues > 0) {
      upvalues = new Object[numUpvalues];
      for (int i = 0; i < numUpvalues; i++) {
        upvalues[i] = nilObject;
      }
    } else {
      upvalues = null;
    }
  }

  public Object getSelf() {
    return self;
  }

  public FrameOnStackMarker getFrameOnStackMarker() {
    return onStackMarker;
  }

  public Object[] getUpvalues() {
    return upvalues;
  }

  public Object getArgument(final int i) {
    return arguments[i];
  }

  public static Arguments get(final Frame frame) {
    return frame.getArguments(Arguments.class);
  }

}
