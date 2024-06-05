package com.ethan.common.lang;

import java.util.Comparator;

/**
 * {@code Prioritized} interface can be implemented by objects that
 * should be sorted, for example the tasks in executable queue.
 *
 * @author Huang Z.Y.
 */
public interface Prioritized extends Comparable<Prioritized> {

    /**
     * The {@link Comparator} of {@link Prioritized}
     */
    Comparator<Object> COMPARATOR = (one, two) -> {
        boolean b1 = one instanceof Prioritized;
        boolean b2 = two instanceof Prioritized;
        if (b1 && !b2) { // one is Prioritized, two is not
            return -1;
        } else if (b2 && !b1) { // two is Prioritized, one is not
            return 1;
        } else if (b1 && b2) { //  one and two both are Prioritized
            return ((Prioritized) one).compareTo((Prioritized) two);
        } else { // no different
            return 0;
        }
    };
    
}
