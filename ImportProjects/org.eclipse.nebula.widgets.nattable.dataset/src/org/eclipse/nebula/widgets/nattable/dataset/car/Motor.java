/*****************************************************************************
 * Copyright (c) 2018 Dirk Fauth.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.nebula.widgets.nattable.dataset.car;

import java.util.List;

public class Motor {

    private String identifier;
    private String capacity;
    private String capacityUnit;
    private Integer maximumSpeed;

    private List<Feedback> feedbacks;

    public Motor(String identifier, String capacity, String capacityUnit, Integer maximumSpeed) {
        this.identifier = identifier;
        this.capacity = capacity;
        this.capacityUnit = capacityUnit;
        this.maximumSpeed = maximumSpeed;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getCapacity() {
        return this.capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getCapacityUnit() {
        return this.capacityUnit;
    }

    public void setCapacityUnit(String capacityUnit) {
        this.capacityUnit = capacityUnit;
    }

    public Integer getMaximumSpeed() {
        return this.maximumSpeed;
    }

    public void setMaximumSpeed(Integer maximumSpeed) {
        this.maximumSpeed = maximumSpeed;
    }

    public List<Feedback> getFeedbacks() {
        return this.feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }
}
