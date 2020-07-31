/*
 *     Copyright (C) 2020 rsouth (https://github.com/rsouth)
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.brokn.sequence.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Interaction {

    private final Lane fromLane;
    private final Lane toLane;
    private final String message;
    private final int index;
    private final InteractionType type;
    private final boolean isSynchronous;

    public enum InteractionType {
        Message,
        Reply
    }

    public Interaction(Lane fromLane, Lane toLane, String message, int index, InteractionType type, boolean isSynchronous) {
        this.fromLane = fromLane;
        this.toLane = toLane;
        this.message = message;
        this.index = index;
        this.type = type;
        this.isSynchronous = isSynchronous;
    }

    public Lane getFromLane() {
        return fromLane;
    }

    public Lane getToLane() {
        return toLane;
    }

    public String getMessage() {
        return message;
    }

    public int getIndex() {
        return index;
    }

    public InteractionType getInteractionType() {
        return type;
    }


    public boolean isSynchronous() {
        return isSynchronous;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Interaction that = (Interaction) o;
        return index == that.index &&
                isSynchronous == that.isSynchronous &&
                Objects.equal(fromLane, that.fromLane) &&
                Objects.equal(toLane, that.toLane) &&
                Objects.equal(message, that.message) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fromLane, toLane, message, index, type, isSynchronous);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("fromLane", fromLane)
                .add("toLane", toLane)
                .add("message", message)
                .add("index", index)
                .add("type", type)
                .add("isSynchronous", isSynchronous)
                .toString();
    }
}