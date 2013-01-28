/*
 * Copyright (c) 2008 - 2012 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Symbol.java

package org.bson.types;

import java.io.Serializable;

/**
 * Class to hold a BSON symbol object, which is an interned string in Ruby
 */
public class Symbol implements Serializable {

    private final String symbol;

    private static final long serialVersionUID = 1326269319883146072L;

    public Symbol(final String s) {
        symbol = s;
    }

    public String getSymbol() {
        return symbol;
    }

    /**
     * Will compare equal to a String that is equal to the String that this holds
     *
     * @param o the Symbol to compare this to
     * @return true if parameter o is the same as this Symbol
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Symbol symbol1 = (Symbol) o;

        if (!symbol.equals(symbol1.symbol)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return symbol.hashCode();
    }

    @Override
    public String toString() {
        return symbol;
    }
}