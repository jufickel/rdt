package de.steinpfeffer.rdt;

import static java.lang.String.format;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * The main {@link Activity}.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
public final class MainActivity extends Activity {

    private final class DecimalNumberFormattingOnFocusChangeListener implements OnFocusChangeListener {

        /**
         * Constructs a new
         * {@link DecimalNumberFormattingOnFocusChangeListener}.
         */
        public DecimalNumberFormattingOnFocusChangeListener() {

        }

        @Override
        public void onFocusChange(final View v, final boolean hasFocus) {
            final EditText editText = (EditText) v;
            if (hasFocus) {
                setNumberAsPlainText(editText);
            } else {
                setNumberAsFormattedText(editText);
            }
        }

        private void setNumberAsPlainText(final EditText editText) {
            final Editable text = editText.getText();
            final String textAsString = text.toString();
            if (!textAsString.isEmpty()) {
                final Number parsedNumber = tryToParse(textAsString);
                final String numberAsPlainText = String.valueOf(parsedNumber.doubleValue());
                editText.setText(numberAsPlainText);
            }
        }

        private void setNumberAsFormattedText(final EditText editText) {
            final Editable text = editText.getText();
            final String textAsString = text.toString();
            if (!textAsString.isEmpty()) {
                final String formattedNumber = formatTextAsDecimalNumber(textAsString);
                editText.setText(formattedNumber);
            }
        }

        private String formatTextAsDecimalNumber(final String numberAsText) {
            final Number parsedNumber = tryToParse(numberAsText);
            return decimalFormat.format(parsedNumber);
        }

    }

    private final Set<TextView> dirtyTextViews;
    private final Set<TextView> requiredViewsForCalculation;
    private final DecimalFormat decimalFormat;

    private EditText valueExtreme;
    private EditText unitExtreme;
    private EditText valueMeansB;
    private EditText unitMeansB;
    private EditText valueMeansC;
    private EditText unitMeansC;
    private EditText valueResult;
    private EditText unitResult;
    private Button buttonCalculate;
    private Button buttonReset;

    /**
     * Constructs a new {@link MainActivity} object.
     */
    public MainActivity() {
        dirtyTextViews = new HashSet<TextView>();
        requiredViewsForCalculation = new HashSet<TextView>();
        decimalFormat = (DecimalFormat) DecimalFormat.getInstance();
        decimalFormat.setMinimumFractionDigits(2);
        valueExtreme = null;
        unitExtreme = null;
        valueMeansB = null;
        unitMeansB = null;
        valueMeansC = null;
        unitMeansC = null;
        valueResult = null;
        unitResult = null;
        buttonCalculate = null;
        buttonReset = null;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_form);

        final OnFocusChangeListener decimalNumberFormatter = new DecimalNumberFormattingOnFocusChangeListener();

        valueExtreme = getTextViewAndAddResettableWatcher(R.id.value_extreme);
        valueExtreme.setOnFocusChangeListener(decimalNumberFormatter);

        unitExtreme = getTextViewAndAddResettableWatcher(R.id.unit_extreme);
        valueMeansB = getTextViewAndAddResettableWatcher(R.id.value_means_b);
        valueMeansB.setOnFocusChangeListener(decimalNumberFormatter);
        unitMeansB = getTextViewAndAddResettableWatcher(R.id.unit_means_b);
        valueMeansC = getTextViewAndAddResettableWatcher(R.id.value_means_c);
        valueMeansC.setOnFocusChangeListener(decimalNumberFormatter);

        unitMeansC = getTextViewById(R.id.unit_means_c);
        valueResult = getTextViewById(R.id.value_result);
        unitResult = getTextViewById(R.id.unit_result);

        buttonCalculate = (Button) findViewById(R.id.entry_form_button_calculate);
        buttonCalculate.setOnClickListener(createCalculateButtonOnClickListener());
        buttonReset = (Button) findViewById(R.id.entry_form_button_reset);
        buttonReset.setOnClickListener(createResetButtonOnClickListener());

        requiredViewsForCalculation.add(valueExtreme);
        requiredViewsForCalculation.add(valueMeansB);
        requiredViewsForCalculation.add(valueMeansC);
    }

    @SuppressWarnings("unchecked")
    private <T extends TextView> T getTextViewAndAddResettableWatcher(final int id) {
        final TextView textView = getTextViewById(id);
        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(final Editable text) {
                if (0 < text.length()) {
                    markTextViewAsDirty(textView);
                } else {
                    markTextViewAsClean(textView);
                }
                setValueOfRelatedView(textView);
                enableOrDisableResetButton();
                enableOrDisableCalculateButton();
            }

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
                // Nothing to do.
            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                // Nothing to do.
            }
        };
        textView.addTextChangedListener(textWatcher);
        return (T) textView;
    }

    @SuppressWarnings("unchecked")
    private <T extends TextView> T getTextViewById(final int id) {
        final View result = findViewById(id);
        return (T) result;
    }

    private void markTextViewAsDirty(final TextView t) {
        dirtyTextViews.add(t);
    }

    private void markTextViewAsClean(final TextView t) {
        dirtyTextViews.remove(t);
    }

    private void setValueOfRelatedView(final TextView changedTextView) {
        if (changedTextView.equals(unitExtreme)) {
            unitMeansC.setText(changedTextView.getText());
        } else if (changedTextView.equals(unitMeansB)) {
            unitResult.setText(changedTextView.getText());
        }
    }

    private void enableOrDisableResetButton() {
        buttonReset.setEnabled(!dirtyTextViews.isEmpty());
    }

    private void enableOrDisableCalculateButton() {
        buttonCalculate.setEnabled(areRequiredValuesSet());
    }

    private boolean areRequiredValuesSet() {
        return dirtyTextViews.containsAll(requiredViewsForCalculation);
    }

    private OnClickListener createCalculateButtonOnClickListener() {
        return new OnClickListener() {
            @Override
            public void onClick(final View calculateButton) {
                calculateAndShowResult();
                calculateButton.setEnabled(false);
            }
        };
    }

    private void calculateAndShowResult() {
        final BigDecimal a = getTextAsBigDecimal(valueExtreme);
        final BigDecimal b = getTextAsBigDecimal(valueMeansB);
        final BigDecimal c = getTextAsBigDecimal(valueMeansC);

        final RuleOfThreeCalculator calculator = new RuleOfThreeCalculator(a, b, c);
        final BigDecimal result = calculator.calculate(MathContext.DECIMAL64);

        final String resultToShow = decimalFormat.format(result.doubleValue());
        valueResult.setText(resultToShow);
        valueResult.requestFocus();
    }

    private BigDecimal getTextAsBigDecimal(final TextView textView) {
        final CharSequence text = textView.getText();
        final Number parsedNumber = tryToParse(text.toString());
        return BigDecimal.valueOf(parsedNumber.doubleValue());
    }

    private Number tryToParse(final String numberAsText) {
        try {
            return decimalFormat.parse(numberAsText);
        } catch (final ParseException e) {
            System.err.println(format("Unable to parse '%s'.", numberAsText));
            if (numberAsText.isEmpty()) {
                return Double.valueOf(0.0D);
            }
            return Double.valueOf(numberAsText);
        }
    }

    private OnClickListener createResetButtonOnClickListener() {
        return new OnClickListener() {
            @Override
            public void onClick(final View resetButton) {
                resetAllTexts();
                valueExtreme.requestFocus();
            }
        };
    }

    private void resetAllTexts() {
        valueExtreme.setText("");
        unitExtreme.setText("");
        valueMeansB.setText("");
        unitMeansB.setText("");
        valueMeansC.setText("");
        valueResult.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
