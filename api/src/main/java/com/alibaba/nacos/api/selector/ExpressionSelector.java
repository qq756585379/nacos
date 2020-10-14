
package com.alibaba.nacos.api.selector;

public class ExpressionSelector extends AbstractSelector {

    /**
     * Label expression of this selector.
     */
    private String expression;

    public ExpressionSelector() {
        super(SelectorType.label.name());
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
