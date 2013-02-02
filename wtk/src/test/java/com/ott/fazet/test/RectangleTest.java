package com.ott.fazet.test;

import java.util.Comparator;

import org.apache.pivot.collections.immutable.ImmutableList;
import org.apache.pivot.wtk.Span;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.*;
import com.ott.fazet.wtk.Rectangle;

public class RectangleTest {
	
    // Comparator that determines if two ranges intersect.
    private static final Comparator<Span> SPAN_INTERSECTION_COMPARATOR = new Comparator<Span>() {
        @Override
        public int compare(Span range1, Span range2) {
            return (range1.start > range2.end) ? 1 : (range2.start > range1.end) ? -1 : 0;
        }
    };

    // Comparator that determines if two ranges intersect.
    private static final Comparator<Rectangle> RECTANGLE_INTERSECTION_COMPARATOR = new Comparator<Rectangle>() {
        @Override
        public int compare(Rectangle perimeter1, Rectangle perimeter2) {
        	int intersection = SPAN_INTERSECTION_COMPARATOR.compare(perimeter1.x, perimeter2.x);
        	if(intersection == 0){
            	intersection = SPAN_INTERSECTION_COMPARATOR.compare(perimeter1.y, perimeter2.y);
        	}
        	return intersection;
        }
    };
    
    @Test
    @Category(Rectangle.class)
    public void testSpanIntersection(){
    	Span x, y;
    	x = new Span(1, 3);
    	y = new Span(4, 6);
    	assertEquals(-1, SPAN_INTERSECTION_COMPARATOR.compare(x, y));
    	assertEquals(1, SPAN_INTERSECTION_COMPARATOR.compare(y, x));
    	assertEquals(0, SPAN_INTERSECTION_COMPARATOR.compare(x, new Span(3, 4)));
    }
    
    @Test
    @Category(Rectangle.class)
    public void testRectangleIntersection(){
    	Rectangle x, y;
    	x = new Rectangle(new Span(2, 2), new Span(2, 4));
    	y = new Rectangle(new Span(1, 3), new Span(2, 2));
    	assertEquals(0, RECTANGLE_INTERSECTION_COMPARATOR.compare(x, y));
    	assertEquals(0, RECTANGLE_INTERSECTION_COMPARATOR.compare(y, x));
    	Rectangle z = new Rectangle(new Span(1, 4), new Span(0, 1));
    	assertEquals(1, RECTANGLE_INTERSECTION_COMPARATOR.compare(x, z));
    	assertEquals(-1, RECTANGLE_INTERSECTION_COMPARATOR.compare(z, x));
    }
    
    
    /*
     * Test substraction
     */

    @Test
    @Category(Rectangle.class)
    public void testSubstractionNonIntersected(){
    	Rectangle x, y;
    	x = new Rectangle(new Span(1, 3), new Span(1, 5));
    	y = new Rectangle(new Span(4, 4), new Span(2, 4));
    	ImmutableList<Rectangle> substraction = x.substract(y);
    	assertEquals(1, substraction.getLength());
    	assertEquals(x, substraction.get(0));
    }
    
    @Test
    @Category(Rectangle.class)
    public void testSubstractionEquals(){
    	Rectangle x, y;
    	x = new Rectangle(new Span(1, 3), new Span(1, 5));
    	y = new Rectangle(new Span(1, 3), new Span(1, 5));
    	ImmutableList<Rectangle> substraction = x.substract(y);
    	assertEquals(0, substraction.getLength());
    }

    @Test
    @Category(Rectangle.class)
    public void testSubstractionIntersected1(){
    	Rectangle x, y;
    	x = new Rectangle(new Span(1, 3), new Span(1, 5));
    	y = new Rectangle(new Span(2, 5), new Span(4, 5));
    	ImmutableList<Rectangle> substraction = x.substract(y);
    	assertEquals(2, substraction.getLength());
    	assertEquals(new Rectangle(new Span(1, 1), new Span(1, 5)), substraction.get(0));
    	assertEquals(new Rectangle(new Span(2, 3), new Span(1, 3)), substraction.get(1));
    }

    @Test
    @Category(Rectangle.class)
    public void testSubstractionIncluded1(){
    	Rectangle x, y;
    	x = new Rectangle(new Span(1, 3), new Span(1, 5));
    	y = new Rectangle(new Span(2, 2), new Span(2, 4));
    	ImmutableList<Rectangle> substraction = x.substract(y);
    	assertEquals(4, substraction.getLength());
    	assertEquals(new Rectangle(new Span(1, 1), new Span(1, 5)), substraction.get(0));
    	assertTrue(substraction.indexOf(new Rectangle(new Span(2, 2), new Span(1, 1))) != -1);
    	assertTrue(substraction.indexOf(new Rectangle(new Span(2, 2), new Span(5, 5))) != -1);
    	assertTrue(substraction.indexOf(new Rectangle(new Span(3, 3), new Span(1, 5))) != -1);
    }
    
}
