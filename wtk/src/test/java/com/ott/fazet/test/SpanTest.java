package com.ott.fazet.test;

import java.util.Comparator;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.wtk.Span;

public class SpanTest {
	
    private ArrayList<Span> selectedRanges = new ArrayList<Span>();

    // Comparator that determines the index of the first intersecting range.
    private static final Comparator<Span> START_COMPARATOR = new Comparator<Span>() {
        @Override
        public int compare(Span range1, Span range2) {
        	System.out.println("compare " + range1 + " and " + range2 + " = " + (range1.end - range2.start));
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
    
    public static void main(String[] args) {
    	SpanTest test = new SpanTest();
    	test.testStartComparator();
    }
    
    public void testStartComparator(){
    	ArrayList<Span> ranges = createSelectedRanges();
    	Span span = new Span(3, 6);
    	int i = ArrayList.binarySearch(ranges, span, START_COMPARATOR);
    	System.out.println(i);
    }
    
    private ArrayList<Span> createSelectedRanges(){
    	ArrayList<Span> ranges = new ArrayList<Span>();
    	ranges.add(new Span(0, 1));
    	ranges.add(new Span(5, 8));
    	return ranges;
    }
}
