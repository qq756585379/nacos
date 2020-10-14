
package com.alibaba.nacos.api.selector;

public class NoneSelector extends AbstractSelector {

    public NoneSelector() {
        super(SelectorType.none.name());
    }
}
