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
