/**
 * Copyright (c) 2016 Peter A McGill
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
**/
package org.pmg.jms.sudoku.genmodel;

import com.google.inject.Provider;
import org.pmg.jms.genconfig.JmsClientScope;

/**
 * Guice scope to define the required application scope for JMS integration
 * @author peter
 */
public class SudokuModelScope extends JmsClientScope {
    
private static final Provider<Object> SEEDED_KEY_PROVIDER =
      new Provider<Object>() {
        @Override
        public Object get() {
          throw new IllegalStateException("If you got here then it means that" +
              " your code asked for scoped object which should have been" +
              " explicitly seeded in this scope by calling" +
              " SimpleScope.seed(), but was not.");
        }
      };

  /**
   * Returns a provider that always throws exception complaining that the object
   * in question must be seeded before it can be injected.
   *
   * @return typed provider
   */
  @SuppressWarnings({"unchecked"})
  public static <T> Provider<T> seededKeyProvider() {
    return (Provider<T>) SEEDED_KEY_PROVIDER;
  }

}
