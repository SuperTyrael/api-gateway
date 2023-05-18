package org.brown.mliang21.gateway.config.center.api;

import org.brown.mliang21.common.config.Rule;

import java.util.List;

public interface RulesChangeListener {
    void onRulesChange(List<Rule> rules);
}
