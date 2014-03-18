/*
 * Copyright 2014 Juergen Fickel (steinpfeffer@gmx.de)
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
package de.steinpfeffer.rdt;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Calculates the result by the <a href=
 * "https://en.wikipedia.org/wiki/Cross-multiplication#Rule_of_Three"
 * >Rule of Three</a>.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
public final class RuleOfThreeCalculator {

    private final BigDecimal a;
    private final BigDecimal b;
    private final BigDecimal c;

    /**
     * Constructs a new {@link RuleOfThreeCalculator} object.
     * 
     * @param a the value <em>a</em>.
     * @param b the value <em>b</em>.
     * @param c the value <em>c</em>.
     */
    public RuleOfThreeCalculator(final BigDecimal a, final BigDecimal b, final BigDecimal c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    /**
     * Calculates <em>x</em> by the Rule of Three.
     * 
     * @param mathContext the {@link MathContext} which determines the
     *        precision.
     * @return the calculated result for <em>x</em>.
     */
    public BigDecimal calculate(final MathContext mathContext) {
        final BigDecimal result = c.multiply(b).divide(a, mathContext);
        return result.stripTrailingZeros();
    }

    @Override
    public String toString() {
        final byte initialCapacity = 64;
        return new StringBuilder(initialCapacity).append(getClass().getName()).append(" [a=").append(a).append(", b=")
                .append(b).append(", c=").append(c).append("]").toString();
    }

}
