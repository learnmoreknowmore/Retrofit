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

/**
 * Adapts a {@link Call} with response type {@code ResponseT} into the type of {@code CallbackT}.
 * Instances are created by {@linkplain Factory a factory} which is
 * {@linkplain Retrofit.Builder#addCallbackAdapterFactory(Factory) installed} into the
 * {@link Retrofit} instance.
 */
public interface CallbackAdapter<ResponseT, CallbackT> {
  /**
   * Returns the value type that this adapter uses when converting the HTTP response body to a Java
   * object. For example, the response type for {@code Call<Repo>} is {@code Repo}. This type
   * is used to prepare the {@code call} passed to {@code #adapt}.
   */
  Type responseType();

  /**
   * Enqueues {@code call} and propagates the result to {@code callback}.
   *
   * @return A value corresponding to the {@code returnType} passed to the factory's
   * {@link Factory#get get} method. Almost always should be null.
   */
  Object adapt(Call<ResponseT> call, CallbackT callback);

  /**
   * Creates {@link CallbackAdapter} instances based on the return type of {@linkplain
   * Retrofit#create(Class) the service interface} methods.
   */
  abstract class Factory {
    /**
     * Returns a call adapter for interface methods that return {@code returnType}, or null if it
     * cannot be handled by this factory.
     */
    public abstract @Nullable CallbackAdapter<?, ?> get(Type callbackType, Type returnType,
        Annotation[] annotations, Retrofit retrofit);

    /**
     * Extract the upper bound of the generic parameter at {@code index} from {@code type}. For
     * example, index 1 of {@code Map<String, ? extends Runnable>} returns {@code Runnable}.
     */
    protected static Type getParameterUpperBound(int index, ParameterizedType type) {
      return Utils.getParameterUpperBound(index, type);
    }

    /**
     * Extract the lower bound of the generic parameter at {@code index} from {@code type}. For
     * example, index 1 of {@code Map<String, ? super Runnable>} returns {@code Runnable}.
     */
    protected static Type getParameterLowerBound(int index, ParameterizedType type) {
      return Utils.getParameterLowerBound(index, type);
    }

    /**
     * Extract the raw class type from {@code type}. For example, the type representing
     * {@code List<? extends Runnable>} returns {@code List.class}.
     */
    protected static Class<?> getRawType(Type type) {
      return Utils.getRawType(type);
    }
  }
}
