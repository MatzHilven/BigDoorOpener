/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.bigdoorsopener.core.events;

import de.eldoria.bigdoorsopener.conditions.DoorCondition;
import de.eldoria.bigdoorsopener.door.ConditionalDoor;
import de.eldoria.bigdoorsopener.door.conditioncollections.ConditionBag;

public class ConditionModifiedEvent extends ConditionBagModifiedEvent {
    /**
     * Create a new ConditionBagModified Event.
     *
     * @param conditionBag bag which was modified.
     * @param condition condition which was affected by this event
     */
    public ConditionModifiedEvent(ConditionalDoor door, ConditionBag conditionBag, DoorCondition condition) {
        super(door, conditionBag, condition);
    }
}
