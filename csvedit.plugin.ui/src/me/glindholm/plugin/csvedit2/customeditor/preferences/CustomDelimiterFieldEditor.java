
package me.glindholm.plugin.csvedit2.customeditor.preferences;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class CustomDelimiterFieldEditor extends ComboFieldEditor {
    private Combo combo;
    private static final String[] delItems = { ",", ";", "|", "tab" };

    public CustomDelimiterFieldEditor(final String name, final String labelText, final Composite parent) {
        super(name, labelText, new String[][] {}, parent);
        createControl(parent);
        combo.setTextLimit(3);
    }

    @Override
    protected void adjustForNumColumns(final int numColumns) {
        final GridData gd = (GridData) combo.getLayoutData();
        gd.horizontalSpan = numColumns - 1;
        gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
    }

    @Override
    protected void doFillIntoGrid(final Composite parent, final int numColumns) {
        getLabelControl(parent);
        if (combo == null) {
            combo = new Combo(parent, SWT.DROP_DOWN | SWT.BORDER);
            combo.setItems(delItems);
            combo.addModifyListener(e -> valueChanged());
            combo.addVerifyListener(e -> {
                if (e.text.length() == 0 || //
                        !"tab".equals(e.text) && e.text.length() > 1) {
                    e.doit = false;
                }
            });
        }
        final GridData gd = new GridData();
        gd.horizontalSpan = numColumns - 1;
        gd.horizontalAlignment = GridData.FILL;
        combo.setLayoutData(gd);
    }

    @Override
    protected void doLoad() {
        updateComboForValue(getPreferenceStore().getString(getPreferenceName()));
    }

    @Override
    protected void doLoadDefault() {
        updateComboForValue(getPreferenceStore().getDefaultString(getPreferenceName()));
    }

    @Override
    protected void doStore() {
        String value = combo.getText();
        if ("tab".equals(value)) {
            value = "\t";
        }
        getPreferenceStore().setValue(getPreferenceName(), value);
    }

    @Override
    public int getNumberOfControls() {
        return 2;
    }

    @Override
    public void setEnabled(final boolean enabled, final Composite parent) {
        //        super.setEnabled(enabled, parent);
        combo.setEnabled(enabled);
        super.getLabelControl(parent).setEnabled(enabled);
    }

    private void updateComboForValue(final String value) {
        if (combo != null) {
            if ("\t".equals(value)) {
                combo.setText("tab");
            } else {
                combo.setText(value);
            }
        }
    }

    private void valueChanged() {
        setPresentsDefaultValue(false);
        final boolean oldState = isValid();
        refreshValidState();
        final boolean newState = isValid();
        if (newState != oldState) {
            fireStateChanged(IS_VALID, oldState, newState);
        }
        String newValue = combo.getText();
        if ("tab".equals(newValue)) {
            newValue = "\t";
        }
        if (!newValue.equals(getPreferenceStore().getString(getPreferenceName()))) {
            fireValueChanged(VALUE, getPreferenceStore().getString(getPreferenceName()), newValue);
        }
    }
}
