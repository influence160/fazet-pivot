package com.ott.fazet.wtk;

import java.util.Comparator;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.collections.immutable.ImmutableList;
import org.apache.pivot.wtk.Point;
import org.apache.pivot.wtk.Span;

/**
 * Class for managing a set of indexed range selections.
 */
public class PerimeterSelection {
	
	private static enum Axe{
		X,
		Y
	}
	
    // The coalesced selected ranges
    private ArrayList<Rectangle> selectedPerimeters = new ArrayList<Rectangle>();

    // Comparator that determines the index of the first intersecting range.
    private static final Comparator<Span> START_COMPARATOR = new Comparator<Span>() {
        @Override
        public int compare(Span range1, Span range2) {
            return (range1.end - range2.start);
        }
    };

    // Comparator that determines the index of the last intersecting range.
    private static final Comparator<Span> END_COMPARATOR = new Comparator<Span>() {
        @Override
        public int compare(Span range1, Span range2) {
            return (range1.start - range2.end);
        }
    };

    // Comparator that determines if two ranges intersect.
    private static final Comparator<Span> INTERSECTION_COMPARATOR = new Comparator<Span>() {
        @Override
        public int compare(Span range1, Span range2) {
            return (range1.start > range2.end) ? 1 : (range2.start > range1.end) ? -1 : 0;
        }
    };

    // Comparator that determines the index of the first intersecting range.
    private static final Comparator<Rectangle> X_START_COMPARATOR = new Comparator<Rectangle>() {
        @Override
        public int compare(Rectangle perimeter1, Rectangle perimeter2) {
            return START_COMPARATOR.compare(perimeter1.x, perimeter2.x);
        }
    };
    
    // Comparator that determines the index of the first intersecting range.
    private static final Comparator<Rectangle> Y_START_COMPARATOR = new Comparator<Rectangle>() {
        @Override
        public int compare(Rectangle perimeter1, Rectangle perimeter2) {
            return START_COMPARATOR.compare(perimeter1.y, perimeter2.y);
        }
    };

    // Comparator that determines the index of the last intersecting range.
    private static final Comparator<Rectangle> X_END_COMPARATOR = new Comparator<Rectangle>() {
        @Override
        public int compare(Rectangle perimeter1, Rectangle perimeter2) {
            return END_COMPARATOR.compare(perimeter1.x, perimeter2.x);
        }
    };

    // Comparator that determines the index of the last intersecting range.
    private static final Comparator<Rectangle> Y_END_COMPARATOR = new Comparator<Rectangle>() {
        @Override
        public int compare(Rectangle perimeter1, Rectangle perimeter2) {
            return END_COMPARATOR.compare(perimeter1.y, perimeter2.y);
        }
    };

    // Comparator that determines if two ranges intersect.
    private static final Comparator<Rectangle> RECTANGLE_INTERSECTION_COMPARATOR = new Comparator<Rectangle>() {
        @Override
        public int compare(Rectangle perimeter1, Rectangle perimeter2) {
        	int intersection = INTERSECTION_COMPARATOR.compare(perimeter1.x, perimeter2.x);
        	if(intersection == 0){
            	intersection = INTERSECTION_COMPARATOR.compare(perimeter1.y, perimeter2.y);
        	}
        	return intersection;
        }
    };

	
    /**
     * Adds a perimeter to the selection, merging and removing intersecting ranges
     * as needed.
     *
     * @param start
     * @param end
     *
     * @return
     * A rectangle containing the perimeter that were added.
     */
    public Sequence<Rectangle> addPerimeter(Span x, Span y) {
    	
        ArrayList<Rectangle> addedPerimeters = new ArrayList<Rectangle>();
        final Rectangle perimeter = normalize(x, y);
        final int n = selectedPerimeters.getLength();
        
        ArrayList<Rectangle> rectanglesToAdd = new ArrayList<Rectangle>();
        rectanglesToAdd.add(perimeter);
        for (int k = 0; k < selectedPerimeters.getLength(); k++) {
        	Rectangle selectedPerimeter = selectedPerimeters.get(k);
        	for (int h = 0; h < rectanglesToAdd.getLength(); h++) {
        		Rectangle toAdd = rectanglesToAdd.get(h);
            	if (toAdd.intersects(selectedPerimeter)) {
            		if (selectedPerimeter.contains(toAdd)) {
            			rectanglesToAdd.remove(toAdd);
            			h--;
            		} else if (toAdd.contains(selectedPerimeter)) {
            			selectedPerimeters.remove(selectedPerimeter);
            			k--;
            			break;
            			//addedPerimeters.add(toAdd);
            		} else {
            			ImmutableList<Rectangle> substraction = toAdd.substract(selectedPerimeter);
        				rectanglesToAdd.remove(toAdd);
            			for (Rectangle rect : substraction) {
            				rectanglesToAdd.add(rect);
            			}
            		}
            	}
        	}
        }

		for (Rectangle rect : rectanglesToAdd) {
			addedPerimeters.add(rect);
		}
		
		if (rectanglesToAdd.getLength() > 0) {
			for (Rectangle rect : rectanglesToAdd) {
				selectedPerimeters.add(rect);
			}
		}
		
        return addedPerimeters;
    }

    /**
     * Removes a range from the selection, truncating and removing intersecting
     * ranges as needed.
     *
     * @param start
     * @param end
     *
     * @return
     * A sequence containing the ranges that were removed.
     */
    public Sequence<Rectangle> removePerimeter(Span x, Span y) {
        ArrayList<Rectangle> removedPerimeters = new ArrayList<Rectangle>();

        Rectangle perimeter = normalize(x, y);
        assert(perimeter.x.start >= 0);
        assert(perimeter.y.start >= 0);

        int n = selectedPerimeters.getLength();

        if (n > 0) {
            for (int k = 0; k < selectedPerimeters.getLength(); k++) {
            	Rectangle selectedPerimeter = selectedPerimeters.get(k);
            	if (perimeter.intersects(selectedPerimeter)) {
            		if (perimeter.contains(selectedPerimeter)) {
            			selectedPerimeters.remove(selectedPerimeter);
            			removedPerimeters.add(selectedPerimeter);
            			k--;
            			//addedPerimeters.add(toAdd);
            		} else {
            			ImmutableList<Rectangle> substraction = selectedPerimeter.substract(perimeter);
        				removedPerimeters.add(selectedPerimeter.intersect(perimeter));
        				selectedPerimeters.remove(selectedPerimeter);
        				for (Rectangle rect : substraction) {
        					selectedPerimeters.insert(rect, k);
            			}
        				k += substraction.getLength() - 1;
            			if (selectedPerimeter.contains(perimeter)) {
            				break;
            			}
            		}
            	}
            }
        }

        return removedPerimeters;
    }

    /**
     * Clears the selection.
     */
    public void clear() {
        selectedPerimeters.clear();
    }

    /**
     * Returns the perimeter at a given index.
     *
     * @param index
     */
    public Rectangle get(int index) {
        return selectedPerimeters.get(index);
    }

    /**
     * Returns the number of perimeter in the selection.
     */
    public int getLength() {
        return selectedPerimeters.getLength();
    }

    /**
     * Returns an immutable wrapper around the selected perimeters.
     */
    public ImmutableList<Rectangle> getSelectedPerimeters() {
        return new ImmutableList<Rectangle>(selectedPerimeters);
    }

    /**
     * Determines the index of a range in the selection.
     *
     * @param range
     *
     * @return
     * The index of the range, if it exists in the selection; <tt>-1</tt>,
     * otherwise.
     */
    public int indexOf(Rectangle perimeter) {
        assert (perimeter != null);

        int index = -1;
        int i = ArrayList.binarySearch(selectedPerimeters, perimeter, RECTANGLE_INTERSECTION_COMPARATOR);

        if (i >= 0) {
            index = (perimeter.equals(selectedPerimeters.get(i))) ? i : -1;
        }

        return index;
    }

    /**
     * Tests for the presence of an index in the selection.
     *
     * @param index
     *
     * @return
     * <tt>true</tt> if the index is selected; <tt>false</tt>, otherwise.
     */
    public boolean containsPoint(int x, int y) {
    	return containsPoint(new Point(x, y));
    }

    /**
     * Tests for the presence of an index in the selection.
     *
     * @param index
     *
     * @return
     * <tt>true</tt> if the index is selected; <tt>false</tt>, otherwise.
     */
    public boolean containsPoint(Point point) {
        Rectangle rectangle = new Rectangle(point);
        //int i = ArrayList.binarySearch(selectedPerimeters, range, RECTANGLE_INTERSECTION_COMPARATOR);
        for (Rectangle selectedPerimeter : selectedPerimeters) {
        	if (selectedPerimeter.contains(rectangle)) {
        		return true;
        	}
        }
        //return (i == 0);
        return false;
    }

    /**
     * Inserts an index into the span sequence (e.g. when items are inserted
     * into the model data).
     *
     * @param index
     *
     * @return
     * The number of ranges that were updated.
     */
    public int insertPoint(Point point) {
    	Span x = new Span(point.x, point.x);
    	Span y = new Span(point.y, point.y);
    	Sequence<Rectangle> inserted = addPerimeter(x, y);//TODO optimisation
        int updated = (inserted != null && inserted.getLength() > 0) ? 1 : 0;

        return updated;
    }

    /**
     * Removes a range of indexes from the span sequence (e.g. when items
     * are removed from the model data).
     *
     * @param index
     * @param count
     *
     * @return
     * The number of ranges that were updated.
     */
    public int removePoints(int x, int y, int countX, int countY) {
        // Clear any selections in the given range
    	Span spanX = new Span(x, (x + countX) - 1);
    	Span spanY = new Span(y, (y + countY) - 1);
        Sequence<Rectangle> removed = removePerimeter(spanX, spanY);//TODO optimisation
        int updated = removed.getLength();

        //TODO
//        // Decrement any subsequent selection indexes
//        Span range = new Span(index);
//        int i = ArrayList.binarySearch(selectedRanges, range, INTERSECTION_COMPARATOR);
//        assert (i < 0) : "i should be negative, since index should no longer be selected";
//
//        i = -(i + 1);
//
//        // Determine the number of ranges to modify
//        int n = selectedRanges.getLength();
//        while (i < n) {
//            Span selectedRange = selectedRanges.get(i);
//            selectedRanges.update(i, new Span(selectedRange.start - count, selectedRange.end - count));
//            updated++;
//            i++;
//        }

        return updated;
    }
    
    /**
     * Ensures that the start value is less than or equal to the end value.
     *
     * @param start
     * @param end
     *
     * @return
     * A span containing the normalized range.
     */
    public static Span normalize(int start, int end) {
        return new Span(Math.min(start, end), Math.max(start, end));
    }
    
    public static Span normalize(Span span){
    	return normalize(span.start, span.end);
    }
    
    public static Rectangle normalize(Span x, Span y) {
    	return new Rectangle(normalize(x), normalize(y));
    }
}
