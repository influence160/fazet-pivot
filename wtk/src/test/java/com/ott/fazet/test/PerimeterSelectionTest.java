package com.ott.fazet.test;

import org.apache.pivot.wtk.Point;
import org.apache.pivot.wtk.Span;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.ott.fazet.wtk.PerimeterSelection;

import static junit.framework.Assert.*;

public class PerimeterSelectionTest{
	
	private PerimeterSelection perimeterSelection = new PerimeterSelection();
	
	public PerimeterSelectionTest(){
		perimeterSelection = createPerimeterSelection();
	}
	
	private PerimeterSelection createPerimeterSelection(){
		PerimeterSelection ps = new PerimeterSelection();
		ps.addPerimeter(new Span(2, 5), new Span(4, 5));
		ps.addPerimeter(new Span(1, 1), new Span(2, 2));
		ps.addPerimeter(new Span(0, 0), new Span(7, 7));
		ps.addPerimeter(new Span(3, 4), new Span(2, 4));
		ps.addPerimeter(new Span(2, 5), new Span(4, 5));
		ps.addPerimeter(new Span(1, 1), new Span(0, 5));
		return ps;
	}

	@Test
	@Category(PerimeterSelectionTest.class)
	public void testContainsPoint() {
		PerimeterSelection perimeterSelection = createPerimeterSelection();
		assertTrue(perimeterSelection.containsPoint(1, 2));
		assertFalse(perimeterSelection.containsPoint(2, 1));
		assertTrue(perimeterSelection.containsPoint(4, 4));
		assertTrue(perimeterSelection.containsPoint(4, 3));
		assertFalse(perimeterSelection.containsPoint(2, 3));
		assertFalse(perimeterSelection.containsPoint(0, 0));
		assertFalse(perimeterSelection.containsPoint(new Point(5, 2)));
	}

	@Test
	@Category(PerimeterSelectionTest.class)
	public void testInsertPoint() {
		PerimeterSelection ps = createPerimeterSelection();
		ps.insertPoint(new Point(3, 7));
		assertTrue(ps.containsPoint(new Point(3, 7)));
	}
	
	@Test
	@Category(PerimeterSelectionTest.class)
	public void testRemovePoint() {
		PerimeterSelection ps = createPerimeterSelection();
		ps.removePoints(3, 4, 2, 2);
		assertFalse(ps.containsPoint(new Point(3, 4)));
		assertFalse(ps.containsPoint(new Point(4, 5)));
	}
	
	@Test
	@Category(PerimeterSelectionTest.class)
	public void testAddPerimeter() {
		PerimeterSelection ps = createPerimeterSelection();
		ps.addPerimeter(new Span(0, 5), new Span(2, 2));
		assertTrue(ps.containsPoint(new Point(2, 2)));
	}
	
	public static void main(String[] args) {
		PerimeterSelectionTest test = new PerimeterSelectionTest();
		test.testContainsPoint();
//		test.testInsertPoint();
//		test.testRemovePoint();
//		test.testAddPerimeter();
		
	}
}
