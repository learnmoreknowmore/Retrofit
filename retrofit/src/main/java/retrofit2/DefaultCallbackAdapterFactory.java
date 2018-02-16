/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrofit2;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.annotation.Nullable;

/** Creates callback adapters for the built-in {@link Callback} type. */
final class DefaultCallbackAdapterFactory extends CallbackAdapter.Factory {
  static final CallbackAdapter.Factory INSTANCE = new DefaultCallbackAdapterFactory();

  @Nullable @Override
  public CallbackAdapter<?, ?> get(Type callbackType, Type returnType, Annotation[] annotations,
      Retrofit retrofit) {
    if (getRawType(callbackType) != Callback.class || returnType != void.class) {
      return null;
    }

    if (!(callbackType instanceof ParameterizedType)) {
      throw new IllegalArgumentException(
          "Callback must be parameterized as Callback<Foo> or Callback<? extends Foo>");
    }

    final Type responseType = Utils.getParameterUpperBound(0, (ParameterizedType) callbackType);
    return new CallbackAdapter<Object, Callback<Object>>() {
      @Override public Type responseType() {
        return responseType;
      }

      @Override public Object adapt(Call<Object> call, Callback<Object> callback) {
        call.enqueue(callback);
        return null;
      }
    };
  }
}
